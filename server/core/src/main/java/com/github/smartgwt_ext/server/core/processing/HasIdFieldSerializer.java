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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.github.smartgwt_ext.server.core.HasIdField;
import com.github.smartgwt_ext.server.core.annotations.DisplayField;
import com.github.smartgwt_ext.server.introspection.BeanInformationFactory;
import com.github.smartgwt_ext.server.introspection.PropertyInformationFromClass;

import java.io.IOException;
import java.io.Serializable;

/**
 * @author Andreas Berger
 * @created 08.09.14 - 21:58
 */
class HasIdFieldSerializer extends JsonSerializer<Object> {
	private EnhancedObjectMapper enhancedObjectMapper;
	private String displayFieldName;
	private PropertyInformationFromClass displayField;

	public HasIdFieldSerializer(
			EnhancedObjectMapper enhancedObjectMapper,
			String fieldName,
			Class<? extends HasIdField> entityClass) {
		this.enhancedObjectMapper = enhancedObjectMapper;
		displayField = BeanInformationFactory.createBeanInformation(entityClass).getAnnotatedProperty(
				DisplayField.class);
		if (displayField != null) {
			displayFieldName = fieldName + "_" + displayField.getName();
		}
	}

	@Override
	public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider)
			throws IOException, JsonProcessingException {
		Serializable id = enhancedObjectMapper.getIdOfEntity((HasIdField) value);
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
