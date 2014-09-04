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
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;

import java.io.IOException;
import java.lang.annotation.Annotation;

/**
 * @author Andreas Berger
 * @created 13.02.2013
 */
abstract class BaseSettableProperty extends SettableBeanProperty {

	protected JsonDeserializer<?> deser;
	protected SettableBeanProperty delegate;
	protected AnnotatedMethod getter;
	protected AnnotatedMethod setter;

	protected BaseSettableProperty(SettableBeanProperty src, BeanDescription beanDesc) {
		super(src);
		this.delegate = src;
		for (BeanPropertyDefinition p : beanDesc.findProperties()) {
			if (p.getName().equals(_propName.getSimpleName())) {
				getter = p.getGetter();
				setter = p.getSetter();
				break;
			}
		}
		if (getter == null) {
			throw new NullPointerException(
					"Getter must be defined: " + beanDesc.getBeanClass().getName() + "::" + _propName);
		}
		if (setter == null) {
			throw new NullPointerException(
					"Setter must be defined: " + beanDesc.getBeanClass().getName() + "::" + _propName);
		}
	}

	@Override
	public void deserializeAndSet(JsonParser jp, DeserializationContext ctxt, Object instance) throws IOException {
		deserializeSetAndReturn(jp, ctxt, instance);
	}

	@Override
	public Object deserializeSetAndReturn(JsonParser jp, DeserializationContext ctxt, Object instance)
			throws IOException {
		Object value;
		if (jp.getCurrentToken() == JsonToken.VALUE_NULL) {
			value = null;
		} else {
			value = deser.deserialize(jp, ctxt);
		}
		return setAndReturn(instance, value);
	}

	@Override
	public void set(Object instance, Object value) throws IOException {
		setAndReturn(instance, value);
	}

	@Override
	public SettableBeanProperty withValueDeserializer(JsonDeserializer<?> deser) {
		this.deser = deser;
		return this;
	}

	@Override
	public <A extends Annotation> A getAnnotation(Class<A> acls) {
		if (delegate == null) {
			return setter == null ? null : setter.getAnnotation(acls);
		} else {
			return delegate.getAnnotation(acls);
		}
	}

	@Override
	public AnnotatedMember getMember() {
		return delegate == null ? setter : delegate.getMember();
	}

}
