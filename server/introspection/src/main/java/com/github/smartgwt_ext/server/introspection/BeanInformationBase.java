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
package com.github.smartgwt_ext.server.introspection;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Andreas Berger
 */
public abstract class BeanInformationBase<T extends PropertyInformation<?>> implements BeanInformation<T> {

	private BeanInformation<T> superBean;

	private Map<String, T> properties;

	private Map<String, T> allProperties;

	protected BeanInformationBase() {
		properties = new LinkedHashMap<String, T>();
	}

	protected void addProperty(T prop) {
		if (allProperties != null) {
			throw new IllegalStateException("Not allowed");
		}
		properties.put(prop.getName(), prop);
	}

	protected static String getPropertyName(String name, int prefixLength) {
		return ("" + name.charAt(prefixLength)).toLowerCase() + name.substring(prefixLength + 1);
	}

	/**
	 * @return the properties
	 */
	@Override
	public Collection<T> getProperties() {
		return properties.values();
	}

	@Override
	public Collection<T> getAllProperties() {
		initAllProperties();
		return allProperties.values();
	}

	private void initAllProperties() {
		if (allProperties != null) {
			return;
		}
		allProperties = new LinkedHashMap<String, T>();
		if (getSuperBeanInformation() != null) {
			for (T prop : getSuperBeanInformation().getAllProperties()) {
				allProperties.put(prop.getName(), prop);
			}
		}
		allProperties.putAll(properties);
	}

	@Override
	public T getProperty(String name) {
		initAllProperties();
		return allProperties.get(name);
	}

	@Override
	public BeanInformation<T> getSuperBeanInformation() {
		if (superBean != null) {
			return superBean;
		}
		return superBean = initSuperBean();
	}

	protected abstract BeanInformation<T> initSuperBean();

}