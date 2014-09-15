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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.smartgwt_ext.server.core.HasIdField;

import java.io.IOException;

/**
 * @author Andreas Berger
 * @created 08.09.14 - 21:58
 */
class HasIdFieldDeserializer extends JsonDeserializer<Object> {
	private Class<? extends HasIdField> entityClass;
	private EnhancedObjectMapper enhancedObjectMapper;

	public HasIdFieldDeserializer(EnhancedObjectMapper enhancedObjectMapper,
			Class<? extends HasIdField> entityClass) {
		this.enhancedObjectMapper = enhancedObjectMapper;
		this.entityClass = entityClass;
	}

	@Override
	public Object deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		switch (jp.getCurrentToken()) {
			case VALUE_NUMBER_INT:
				return enhancedObjectMapper.getEntityById(jp.getNumberValue(), entityClass);
			case VALUE_STRING:
				return enhancedObjectMapper.getEntityById(jp.getText(), entityClass);
			case VALUE_NULL:
				return null;
		}
		throw new IllegalArgumentException("ID of type " + jp.getCurrentToken() + " is not supported");
	}
}
