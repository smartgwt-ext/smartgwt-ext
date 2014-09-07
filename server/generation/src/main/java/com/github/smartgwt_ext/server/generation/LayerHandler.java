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

import com.github.smartgwt_ext.server.introspection.BeanInformation;

/**
 * Override to customize layer generation.
 *
 * @author Andreas Berger
 * @created 05.09.14 - 12:43
 */
public class LayerHandler {
	private String sourcePackage;
	private String destinationPackage;

	public LayerHandler(String sourcePackage, String destinationPackage) {
		this.sourcePackage = sourcePackage;
		this.destinationPackage = destinationPackage;
	}

	public String resolveDataSourceName(BeanInformation<?> beanInformation) {
		return beanInformation.getSimpleName();
	}

	/**
	 * @param beanInformation
	 * @return the full qualified name of the GWT-Overlay Type
	 */
	public String resolveLayerName(BeanInformation<?> beanInformation) {
		String result = beanInformation.getName().replace(sourcePackage, destinationPackage);
		int pos = result.lastIndexOf('.');
		return result.substring(0, pos) + ".Js" + result.substring(pos + 1);
	}
}
