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

package com.github.smartgwt_ext.server.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Andreas Berger
 * @created 13.09.14 - 23:25
 */
public class DelegateFieldInformation {
	private Map<Class<?>, Collection<String[]>> delegateFields = new HashMap<Class<?>, Collection<String[]>>();

	public void addDelegateField(Class<?> clazz, String... path) {
		Collection<String[]> list = delegateFields.get(clazz);
		if (list == null) {
			list = new ArrayList<String[]>();
			delegateFields.put(clazz, list);
		}
		list.add(path);
	}

	public Map<Class<?>, Collection<String[]>> getDelegateFields() {
		return delegateFields;
	}
}
