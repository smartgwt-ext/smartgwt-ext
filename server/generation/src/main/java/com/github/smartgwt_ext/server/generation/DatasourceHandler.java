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

package com.github.smartgwt_ext.server.generation;

import com.github.smartgwt_ext.server.core.DelegateFieldInformation;
import com.github.smartgwt_ext.server.generation.model.JsDatasourceField;
import com.github.smartgwt_ext.server.introspection.BeanInformation;
import com.github.smartgwt_ext.server.introspection.PropertyInformation;

/**
 * @author Andreas Berger
 * @created 06.09.14 - 21:52
 */
public class DatasourceHandler {

	public String resolveDataSourceName(BeanInformation<?> beanInformation) {
		return beanInformation.getSimpleName();
	}

	public DelegateFieldInformation getDelegateFieldInformation() {
		return null;
	}

	public void handleCustomExtensions(PropertyInformation<?> prop, JsDatasourceField field) {
	}
}
