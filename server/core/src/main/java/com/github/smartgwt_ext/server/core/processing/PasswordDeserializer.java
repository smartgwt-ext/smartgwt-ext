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
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;

import java.io.IOException;

/**
 * @author Andreas Berger
 * @created 13.02.2013
 */
public class PasswordDeserializer extends BaseSettableProperty {

	private final PasswordHandler passwordHandler;

	public PasswordDeserializer(PasswordDeserializer delegate, JsonDeserializer<?> deser) {
		super(delegate, deser);
		this.passwordHandler = delegate.passwordHandler;
	}

	protected PasswordDeserializer(
			SettableBeanProperty src,
			BeanDescription beanDesc,
			PasswordHandler passwordHandler
	) {
		super(src, beanDesc);
		this.passwordHandler = passwordHandler;
	}

	@Override
	public SettableBeanProperty withValueDeserializer(JsonDeserializer<?> deser) {
		return new PasswordDeserializer(this, deser);
	}

	@Override
	public SettableBeanProperty withName(PropertyName newName) {
		throw new UnsupportedOperationException("not implemented yet");
	}

	@Override
	public Object setAndReturn(Object instance, Object value) throws IOException {
		if (PasswordSerializer.PASSWORD_UNCHANGED.equals(value)) {
			return null;
		}
		String encryptedPassword = null;
		if (value != null) {
			encryptedPassword = passwordHandler.transform((String) value);
		}
		setter.setValue(instance, encryptedPassword);
		return null;
	}
}
