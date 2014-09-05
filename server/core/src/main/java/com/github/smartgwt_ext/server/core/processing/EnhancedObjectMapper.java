/*
 * Copyright 2012-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.smartgwt_ext.server.core.processing;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBuilder;
import com.fasterxml.jackson.databind.deser.BeanDeserializerFactory;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
import com.fasterxml.jackson.databind.deser.DeserializerFactory;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.SerializerFactory;
import com.github.smartgwt_ext.server.core.HasIdField;
import com.github.smartgwt_ext.server.core.annotations.DisplayField;
import com.github.smartgwt_ext.server.core.annotations.FieldFeatures;
import com.github.smartgwt_ext.server.introspection.BeanInformation;
import com.github.smartgwt_ext.server.introspection.BeanInformationFactory;
import com.github.smartgwt_ext.server.introspection.PropertyInformation;
import com.github.smartgwt_ext.server.introspection.PropertyInformationFromClass;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

/**
 * Diese Klasse erweitert den JSON-Mapper, so dass er:
 * <ul>
 * <li>für Klassen, die {@link HasIdField} implementieren, automatisch die Id
 * zurück liefert und auch das Objekt aus der Id wieder herstellt</li>
 * <li>{@link Date} in das SmartGWT format überführt</li>
 * </ul>
 *
 * @author Andreas Berger
 * @created 24.10.12 - 13:45
 */
public abstract class EnhancedObjectMapper {
	private static final String SMARTGWT_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
	private ObjectMapper mapper;

	public static PropertyInformation<?> getDisplayField(BeanInformation<?> type) {
		for (PropertyInformation<?> prop : type.getAllProperties()) {
			if (prop.getAnnotation(DisplayField.class) != null) {
				return prop;
			}
		}
		return null;
	}

	public ObjectMapper getMapper() {
		if (mapper == null) {
			mapper = initMapper();
		}
		return mapper;
	}

	private ObjectMapper initMapper() {
		DeserializerFactory deserFact = BeanDeserializerFactory.instance;
		deserFact = deserFact.withDeserializerModifier(new BeanDeserializerModifier() {
			@SuppressWarnings("unchecked")
			@Override
			public BeanDeserializerBuilder updateBuilder(DeserializationConfig config, BeanDescription beanDesc,
					BeanDeserializerBuilder builder) {
				List<SettableBeanProperty> modified = new ArrayList<SettableBeanProperty>();
				for (Iterator<SettableBeanProperty> iterator = builder.getProperties(); iterator.hasNext(); ) {
					SettableBeanProperty prop = iterator.next();
					FieldFeatures features = prop.getAnnotation(FieldFeatures.class);
					if (features != null) {
						if (features.ignore()) {
							iterator.remove();
							continue;
						}
						if (features.password()) {
							modified.add(new PasswordDeserializer(prop, beanDesc));
							continue;
						}
					}
					if (prop.getValueDeserializer() != null) {
						continue;
					}
					if (HasIdField.class.isAssignableFrom(prop.getType().getRawClass())) {
						modified.add(prop.withValueDeserializer(new HasIdFieldDeserializer(
								(Class<? extends HasIdField>) prop.getType().getRawClass())));
					}
				}
				for (SettableBeanProperty property : modified) {
					builder.addOrReplaceProperty(property, true);
				}
				return super.updateBuilder(config, beanDesc, builder);
			}
		});
		DefaultDeserializationContext dc = new DefaultDeserializationContext.Impl(deserFact);

		ObjectMapper m = new ObjectMapper(null, null, dc);
		m.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		m.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		m.configure(SerializationFeature.INDENT_OUTPUT, true);
		m.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
		SimpleDateFormat sdf = getSmartGWTDateFormat();
		m.setDateFormat(sdf);

		SerializerFactory serFact = BeanSerializerFactory.instance.withSerializerModifier(new BeanSerializerModifier() {
			@SuppressWarnings("unchecked")
			@Override
			public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc,
					List<BeanPropertyWriter> beanProperties) {
				for (Iterator<BeanPropertyWriter> iterator = beanProperties.iterator(); iterator.hasNext(); ) {
					BeanPropertyWriter beanProperty = iterator.next();
					FieldFeatures features = beanProperty.getAnnotation(FieldFeatures.class);
					if (features != null) {
						if (features.ignore()) {
							iterator.remove();
							continue;
						}
						if (features.password()) {
							beanProperty.assignSerializer(new PasswordSerializer());
							continue;
						}
					}
					if (beanProperty.getSerializer() != null) {
						continue;
					}
					if (HasIdField.class.isAssignableFrom(beanProperty.getPropertyType())) {
						beanProperty.assignSerializer(new HasIdFieldSerializer(beanProperty.getName(),
								(Class<? extends HasIdField>) beanProperty.getPropertyType()));
					}
				}
				return super.changeProperties(config, beanDesc, beanProperties);
			}
		});
		m.setSerializerFactory(serFact);
		return m;
	}

	public static SimpleDateFormat getSmartGWTDateFormat() {
		SimpleDateFormat sdf = new SimpleDateFormat(SMARTGWT_DATE_PATTERN);
		Calendar utc = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
		sdf.setCalendar(utc);
		return sdf;
	}

	private class HasIdFieldSerializer extends JsonSerializer<Object> {

		private String displayFieldName;
		private PropertyInformationFromClass displayField;

		public HasIdFieldSerializer(String fieldName, Class<? extends HasIdField> entityClass) {
			displayField = (PropertyInformationFromClass)
					getDisplayField(BeanInformationFactory.createBeanInformation(entityClass));
			if (displayField != null) {
				displayFieldName = fieldName + "_" + displayField.getName();
			}
		}

		@Override
		public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider)
				throws IOException, JsonProcessingException {
			Serializable id = ((HasIdField) value).getId();
			if (id == null) {
				jgen.writeNull();
				writeDisplayField(value, jgen);
				return;
			}
			if (id instanceof Integer) {
				jgen.writeNumber((Integer) id);
				writeDisplayField(value, jgen);
				return;
			}
			if (id instanceof Long) {
				jgen.writeNumber((Long) id);
				writeDisplayField(value, jgen);
				return;
			}
			if (id instanceof String) {
				jgen.writeString((String) id);
				writeDisplayField(value, jgen);
				return;
			}
			throw new IllegalArgumentException("ID of type " + id.getClass() + " is not supported");
		}

		private void writeDisplayField(Object value, JsonGenerator jgen) throws IOException {
			if (displayField == null) {
				return;
			}
			jgen.writeFieldName(displayFieldName);
			try {
				jgen.writeString((String) displayField.getGetter().invoke(value));
			} catch (Exception e) {
				throw new IOException(e);
			}
		}
	}

	private class HasIdFieldDeserializer extends JsonDeserializer<Object> {
		private Class<? extends HasIdField> entityClass;

		public HasIdFieldDeserializer(Class<? extends HasIdField> entityClass) {
			this.entityClass = entityClass;
		}

		@Override
		public Object deserialize(JsonParser jp, DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			switch (jp.getCurrentToken()) {
				case VALUE_NUMBER_INT:
					return getEntityById(jp.getNumberValue(), entityClass);
				case VALUE_STRING:
					return getEntityById(jp.getText(), entityClass);
				case VALUE_NULL:
					return null;
			}
			throw new IllegalArgumentException("ID of type " + jp.getCurrentToken() + " is not supported");
		}
	}

	protected abstract <T extends HasIdField> T getEntityById(Serializable id, Class<T> entityClass);

}
