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

package com.github.smartgwt_ext.server.core.processing.entities;

import com.github.smartgwt_ext.server.core.annotations.DisplayField;
import com.github.smartgwt_ext.server.core.annotations.GenerateUiInformation;

/**
 * @author Andreas Berger
 * @created 13.09.14 - 23:14
 */
@GenerateUiInformation(reduceToDisplayField = true)
public class ReduceToDisplayField {

	@DisplayField
	private String foo;

	public ReduceToDisplayField() {
	}

	public ReduceToDisplayField(String foo) {
		this.foo = foo;
	}

	public String getFoo() {
		return foo;
	}

	public void setFoo(String foo) {
		this.foo = foo;
	}
}
