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
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.github.smartgwt_ext.server.introspection.PropertyInformationFromClass;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Andreas Berger
 * @created 26.11.12 - 19:39
 */
class ReduceToFieldDeserializer extends BaseSettableProperty {

	private PropertyInformationFromClass prop;

	private ReduceToFieldDeserializer(ReduceToFieldDeserializer delegate, JsonDeserializer<?> deser) {
		super(delegate, deser);
		this.prop = delegate.prop;
	}

	protected ReduceToFieldDeserializer(
			PropertyInformationFromClass prop,
			SettableBeanProperty src,
			BeanDescription beanDesc) {
		super(src, beanDesc);
		this.prop = prop;
	}

	@Override
	public SettableBeanProperty withValueDeserializer(JsonDeserializer<?> deser) {
		return new ReduceToFieldDeserializer(this, deser);
	}

	@Override
	public SettableBeanProperty withName(PropertyName propertyName) {
		throw new UnsupportedOperationException("not implemented yet");
	}

	@Override
	public Object deserializeSetAndReturn(JsonParser jp, DeserializationContext ctxt, Object instance)
			throws IOException {
		String txt;
		if (jp.getCurrentToken() == JsonToken.VALUE_NULL) {
			txt = null;
		} else {
			txt = jp.getText();
		}
		return setAndReturn(instance, txt);
	}

	@Override
	public Object setAndReturn(Object instance, Object value) throws IOException {
		Object middleMan = null;
		if (value != null) {
			try {
				middleMan = getter.getValue(instance);
				if (middleMan == null) {
					middleMan = getter.getRawReturnType().newInstance();
				}
				prop.getSetter().invoke(middleMan, value);
			} catch (InstantiationException e) {
				throw new IOException(e);
			} catch (IllegalAccessException e) {
				throw new IOException(e);
			} catch (InvocationTargetException e) {
				throw new IOException(e);
			}
		}
		getMember().setValue(instance, middleMan);
		return middleMan;
	}

}
