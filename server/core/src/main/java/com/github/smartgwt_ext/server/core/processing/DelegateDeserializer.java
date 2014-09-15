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

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Andreas Berger
 * @created 26.11.12 - 19:39
 */
class DelegateDeserializer extends BaseSettableProperty {
	private final List<BeanPropertyDefinition> path;
	private final PasswordHandler passwordHandler;

	DelegateDeserializer(DelegateDeserializer delegate, JsonDeserializer<?> deser) {
		super(delegate, deser);
		this.passwordHandler = delegate.passwordHandler;
		this.path = delegate.path;
	}

	private DelegateDeserializer(String name, JavaType type, List<BeanPropertyDefinition> path,
			PasswordHandler passwordHandler) {
		super(name, type);
		setter = path.get(path.size() - 1).getSetter();
		this.path = path;
		this.passwordHandler = passwordHandler;
	}

	public static DelegateDeserializer getInstance(
			EnhancedObjectMapper enhancedObjectMapper,
			SettableBeanProperty prop,
			BeanDescription beanDesc,
			String[] path, DeserializationConfig config) {
		String name = null;
		final List<BeanPropertyDefinition> propertyPath = new ArrayList<BeanPropertyDefinition>();
		JavaType currentType = prop.getType();
		BeanDescription currentBeanDesc = null;
		outer:
		for (String s : path) {
			if (name == null) {
				name = s;
				currentBeanDesc = beanDesc;
			} else {
				name += "_" + s;
				currentBeanDesc = config.introspect(currentType);
			}
			for (BeanPropertyDefinition currentProperty : currentBeanDesc.findProperties()) {
				if (currentProperty.getName().equals(s)) {
					propertyPath.add(currentProperty);
					currentType = config.constructType(currentProperty.getAccessor().getRawType());
					continue outer;
				}
			}
			throw new IllegalStateException("not found");
		}
		BeanPropertyDefinition lastProperty = propertyPath.get(propertyPath.size() - 1);
		PasswordHandler passwordHandler = enhancedObjectMapper.getPasswordHandler(
				currentBeanDesc.getType(),
				lastProperty.getName());
		return new DelegateDeserializer(name, currentType, propertyPath, passwordHandler);
	}

	@Override
	public SettableBeanProperty withValueDeserializer(JsonDeserializer<?> deser) {
		return new DelegateDeserializer(this, deser);
	}

	@Override
	public SettableBeanProperty withName(PropertyName newName) {
		throw new UnsupportedOperationException("not implemented yet");
	}

	@Override
	public Object setAndReturn(Object instance, Object value) throws IOException {
		if (passwordHandler != null && value != null) {
			if (PasswordSerializer.PASSWORD_UNCHANGED.equals(value)) {
				return null;
			}
			value = passwordHandler.transform((String) value);
		}
		Object currentInstance = instance;
		for (int i = 0; i < path.size() - 1; i++) {
			BeanPropertyDefinition path = this.path.get(i);
			Object nextInstance = path.getGetter().getValue(currentInstance);
			if (nextInstance == null) {
				if (value == null) {
					// wenn nix gesetzt werden muss, dann brauch der Pfad auch nicht erzeugt werden
					return null;
				}
				try {
					nextInstance = path.getAccessor().getRawType().newInstance();
					path.getSetter().setValue(currentInstance, nextInstance);
				} catch (Exception e) {
					throw new IOException(e);
				}
			}
			currentInstance = nextInstance;
		}
		path.get(path.size() - 1).getSetter().setValue(currentInstance, value);
		return value;
	}
}
