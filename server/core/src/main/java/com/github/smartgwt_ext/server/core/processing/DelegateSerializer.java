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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.github.smartgwt_ext.server.core.HasIdField;
import com.github.smartgwt_ext.server.core.annotations.DisplayField;
import com.github.smartgwt_ext.server.core.annotations.GenerateUiInformation;
import com.github.smartgwt_ext.server.introspection.PropertyInformationFromClass;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Andreas Berger
 * @created 26.11.12 - 19:37
 */
class DelegateSerializer extends JsonSerializer<Object> {
	private EnhancedObjectMapper enhancedObjectMapper;
	private String name;
	private List<PropertyPath> paths;

	public DelegateSerializer(
			EnhancedObjectMapper enhancedObjectMapper,
			BeanPropertyWriter beanProperty,
			Collection<String[]> pathStrings,
			SerializationConfig config
	) {
		this.enhancedObjectMapper = enhancedObjectMapper;
		name = beanProperty.getName();
		this.paths = new ArrayList<PropertyPath>();
		for (String[] pathString : pathStrings) {
			PropertyPath path = new PropertyPath();
			this.paths.add(path);
			JavaType currentType = beanProperty.getType();
			BeanDescription beanDesc = null;
			outer:
			for (int i = 1; i < pathString.length; i++) {
				beanDesc = config.introspect(currentType);
				for (BeanPropertyDefinition prop : beanDesc.findProperties()) {
					if (prop.getName().equals(pathString[i])) {
						path.add(prop);
						currentType = config.constructType(prop.getAccessor().getRawType());
						continue outer;
					}
				}
				throw new IllegalStateException("not found");
			}
			if (beanDesc != null) {
				PropertyInformationFromClass prop = EnhancedObjectMapper
						.getPropertyInformation(beanDesc.getType(), path.get(path.size() - 1).getName());
				GenerateUiInformation ui = prop.getBeanInformation().getAnnotation(GenerateUiInformation.class);
				if (ui != null && ui.reduceToDisplayField()) {
					path.reduceField = prop.getBeanInformation().getAnnotatedProperty(DisplayField.class);
				}
				path.passwordProperty = enhancedObjectMapper.getPasswordHandler(
						beanDesc.getType(),
						path.get(path.size() - 1).getName()) != null;
			}
		}
	}

	@Override
	public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
		Serializable id = enhancedObjectMapper.getIdOfEntity((HasIdField) value);
		if (id != null) {
			jgen.writeObject(id);
		} else {
			jgen.writeNull();
		}
		for (PropertyPath path : paths) {
			String fieldName = name;
			Object v = value;
			for (BeanPropertyDefinition member : path) {
				if (v == null) {
					break;
				}
				fieldName += '_' + member.getName();
				v = member.getAccessor().getValue(v);
			}
			if (path.passwordProperty && v != null) {
				v = PasswordSerializer.PASSWORD_UNCHANGED;
			}
			if (v != null) {
				jgen.writeFieldName(fieldName);

				if (path.reduceField != null) {
					try {
						jgen.writeObject(path.reduceField.getGetter().invoke(v));
					} catch (IllegalAccessException e) {
						throw new IOException(e);
					} catch (InvocationTargetException e) {
						throw new IOException(e);
					}
				} else {
					jgen.writeObject(v);
				}
			}
		}
	}

	private static class PropertyPath extends ArrayList<BeanPropertyDefinition> {
		boolean passwordProperty;
		public PropertyInformationFromClass reduceField;
	}
}
