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
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
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
import com.github.smartgwt_ext.server.core.DelegateFieldInformation;
import com.github.smartgwt_ext.server.core.HasIdField;
import com.github.smartgwt_ext.server.core.annotations.DisplayField;
import com.github.smartgwt_ext.server.core.annotations.FieldFeatures;
import com.github.smartgwt_ext.server.core.annotations.GenerateUiInformation;
import com.github.smartgwt_ext.server.core.annotations.UiIgnore;
import com.github.smartgwt_ext.server.introspection.BeanInformation;
import com.github.smartgwt_ext.server.introspection.BeanInformationFactory;
import com.github.smartgwt_ext.server.introspection.BeanInformationFromClass;
import com.github.smartgwt_ext.server.introspection.PropertyInformation;
import com.github.smartgwt_ext.server.introspection.PropertyInformationFromClass;

import javax.persistence.Embedded;
import javax.persistence.Id;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Diese Klasse erweitert den JSON-Mapper, so dass er:
 * <ul>
 * <li>für Klassen, die {@link HasIdField} implementieren, automatisch die Id
 * zurück liefert und auch das Objekt aus der Id wieder herstellt</li>
 * <li>{@link java.util.Date} in das SmartGWT format überführt</li>
 * </ul>
 *
 * @author Andreas Berger
 * @created 24.10.12 - 13:45
 */
public abstract class EnhancedObjectMapper {
	private static final String SMARTGWT_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
	private ObjectMapper mapper;
	private DelegateFieldInformation delegateFieldInformation;

	protected EnhancedObjectMapper(DelegateFieldInformation delegateFieldInformation) {
		this.delegateFieldInformation = delegateFieldInformation;
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
			@Override
			public BeanDeserializerBuilder updateBuilder(DeserializationConfig config, BeanDescription beanDesc,
					BeanDeserializerBuilder builder) {
				enhanceDeserializer(config, beanDesc, builder);
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
			@Override
			public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc,
					List<BeanPropertyWriter> beanProperties) {
				enhanceSerializer(config, beanDesc, beanProperties);
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

	private void enhanceSerializer(SerializationConfig config, BeanDescription beanDesc,
			List<BeanPropertyWriter> beanProperties) {
		Map<String, Collection<String[]>> delegates = getDefinedDelegatesByPropertyName(beanDesc);
		for (Iterator<BeanPropertyWriter> iterator = beanProperties.iterator(); iterator.hasNext(); ) {
			BeanPropertyWriter prop = iterator.next();

			FieldFeatures features = prop.getAnnotation(FieldFeatures.class);
			if (features != null) {
				if ((features.ignore() || prop.getAnnotation(UiIgnore.class) != null)
						&& prop.getAnnotation(Id.class) == null) {
					iterator.remove();
					continue;
				}
				if (features.password() != FieldFeatures.Password.NONE) {
					prop.assignSerializer(new PasswordSerializer());
					continue;
				}
			}
			Collection<String[]> paths = getDelegatesByProperty(delegates, prop, features);
			JsonSerializer delegateSerializer = null;
			if (!paths.isEmpty()) {
				delegateSerializer = new DelegateSerializer(this, prop, paths, config);
			}
			if (delegateSerializer != null) {
				prop.assignSerializer(delegateSerializer);
			}
			if (prop.getSerializer() != null) {
				continue;
			}
			GenerateUiInformation ui = prop.getPropertyType().getAnnotation(GenerateUiInformation.class);
			if (ui != null && ui.reduceToDisplayField()) {
				BeanInformationFromClass middleMan = BeanInformationFactory.createBeanInformation(
						prop.getPropertyType());
				prop.assignSerializer(new ReduceToFieldSerializer(middleMan.getAnnotatedProperty(DisplayField.class)));
			} else if (HasIdField.class.isAssignableFrom(prop.getPropertyType())) {
				if (features == null || !features.transferAll()) {
					HasIdFieldSerializer serializer = new HasIdFieldSerializer(
							EnhancedObjectMapper.this,
							prop.getName(),
							(Class<? extends HasIdField>) prop.getPropertyType());
					prop.assignSerializer(serializer);
				}
			}
		}
	}

	private void enhanceDeserializer(DeserializationConfig config, BeanDescription beanDesc,
			BeanDeserializerBuilder builder) {
		List<SettableBeanProperty> modified = new ArrayList<SettableBeanProperty>();
		Map<String, Collection<String[]>> delegates = getDefinedDelegatesByPropertyName(beanDesc);

		for (Iterator<SettableBeanProperty> iterator = builder.getProperties(); iterator.hasNext(); ) {
			SettableBeanProperty prop = iterator.next();

			FieldFeatures features = prop.getAnnotation(FieldFeatures.class);
			if (features != null) {
				if ((features.ignore() || prop.getAnnotation(UiIgnore.class) != null)
						&& prop.getAnnotation(Id.class) == null) {
					iterator.remove();
					continue;
				}
				if (features.immutable()) {
					iterator.remove();
					continue;
				}
				if (features.password() != FieldFeatures.Password.NONE) {
					PasswordHandler passwordHandler = getPasswordHandler(beanDesc.getType(), prop.getName());
					modified.add(new PasswordDeserializer(prop, beanDesc, passwordHandler));
					continue;
				}
			}
			Collection<String[]> paths = getDelegatesByProperty(delegates, prop, features);
			if (!paths.isEmpty()) {
				for (String[] path : paths) {
					modified.add(DelegateDeserializer.getInstance(this, prop, beanDesc, path, config));
				}
			}
			if (prop.getValueDeserializer() != null) {
				continue;
			}
			GenerateUiInformation ui = prop.getType().getRawClass().getAnnotation(GenerateUiInformation.class);
			if (ui != null && ui.reduceToDisplayField()) {
				BeanInformationFromClass middleMan = BeanInformationFactory.createBeanInformation(
						prop.getType().getRawClass());
				modified.add(new ReduceToFieldDeserializer(
						middleMan.getAnnotatedProperty(DisplayField.class),
						prop,
						beanDesc));
			} else if (HasIdField.class.isAssignableFrom(prop.getType().getRawClass())) {
				if (features == null || !features.transferAll()) {
					modified.add(prop.withValueDeserializer(new HasIdFieldDeserializer(EnhancedObjectMapper.this,
							(Class<? extends HasIdField>) prop.getType().getRawClass())));
				}
			}
		}
		for (SettableBeanProperty property : modified) {
			builder.addOrReplaceProperty(property, true);
		}
	}

	private Map<String, Collection<String[]>> getDefinedDelegatesByPropertyName(BeanDescription beanDesc) {
		if (delegateFieldInformation == null) {
			return Collections.emptyMap();
		}
		Map<Class<?>, Collection<String[]>> fields = delegateFieldInformation.getDelegateFields();
		if (fields == null) {
			return Collections.emptyMap();
		}
		Map<String, Collection<String[]>> delegatesOfClass = new HashMap<String, Collection<String[]>>();
		Class<?> clazz = beanDesc.getBeanClass();
		while (Object.class != clazz) {
			//noinspection SuspiciousMethodCalls
			Collection<String[]> d = fields.get(clazz);
			if (d != null) {
				for (String[] strings : d) {
					Collection<String[]> paths = delegatesOfClass.get(strings[0]);
					if (paths == null) {
						paths = new LinkedHashSet<String[]>();
						delegatesOfClass.put(strings[0], paths);
					}
					paths.add(strings);
				}
			}
			clazz = clazz.getSuperclass();
		}
		return delegatesOfClass;
	}

	private Collection<String[]> getDelegatesByProperty(Map<String, Collection<String[]>> delegates,
			BeanProperty beanProperty, FieldFeatures dto) {
		Collection<String[]> paths = new LinkedHashSet<String[]>();
		if (dto != null && dto.flatten() || beanProperty.getAnnotation(Embedded.class) != null) {
			collectDelegates(beanProperty.getName(),
					BeanInformationFactory.createBeanInformation(beanProperty.getType().getRawClass()),
					paths);
		}
		Collection<String[]> additionalPaths = delegates.get(beanProperty.getName());
		if (additionalPaths != null) {
			additionalPaths.addAll(paths);
		} else {
			additionalPaths = paths;
			delegates.put(beanProperty.getName(), additionalPaths);
		}
		return additionalPaths;
	}

	private void collectDelegates(String path, BeanInformation<?> beanInfo, Collection<String[]> paths) {
		for (PropertyInformation<?> property : beanInfo.getAllProperties()) {
			FieldFeatures dto = property.getAnnotation(FieldFeatures.class);
			if (dto == null) {
				continue;
			}
			if (dto.flatten() || property.getAnnotation(Embedded.class) != null) {
				collectDelegates(path + "_" + property.getName(), property.getBeanInformation(), paths);
			} else {
				paths.add((path + "_" + property.getName()).split("_"));
			}
		}
	}

	protected abstract <T extends HasIdField> T getEntityById(Serializable id, Class<T> entityClass);

	protected Serializable getIdOfEntity(HasIdField entity) {
		return entity.getId();
	}

	public PasswordHandler getPasswordHandler(JavaType currentType, String propertyName) {
		PropertyInformation<?> prop = getPropertyInformation(currentType, propertyName);
		FieldFeatures features = prop.getAnnotation(FieldFeatures.class);
		if (features == null || features.password() == null) {
			return null;
		}
		return getPasswordHandler(prop.getDeclaringTypeBeanInformation(), prop, features.password());
	}

	public static PropertyInformationFromClass getPropertyInformation(JavaType currentType, String propertyName) {
		BeanInformationFromClass bean = BeanInformationFactory.createBeanInformation(currentType.getRawClass());
		return bean.getProperty(propertyName);
	}

	protected abstract PasswordHandler getPasswordHandler(BeanInformation<?> bean, PropertyInformation<?> prop,
			FieldFeatures.Password password);
}
