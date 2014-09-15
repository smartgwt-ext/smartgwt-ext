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

import com.github.smartgwt_ext.server.core.HasIdField;
import com.github.smartgwt_ext.server.core.annotations.GenerateUiInformation;

import javax.persistence.Id;

/**
 * @author Andreas Berger
 * @created 13.09.14 - 23:14
 */
@GenerateUiInformation
public class Delegate implements HasIdField {
	@Id
	private Integer id;

	private ReduceToDisplayField name;

	private Delegate delegate2;

	public Delegate() {
	}

	public Delegate(Integer id, ReduceToDisplayField name) {
		this.id = id;
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public ReduceToDisplayField getName() {
		return name;
	}

	public void setName(ReduceToDisplayField name) {
		this.name = name;
	}

	public Delegate getDelegate2() {
		return delegate2;
	}

	public void setDelegate2(Delegate delegate2) {
		this.delegate2 = delegate2;
	}
}
