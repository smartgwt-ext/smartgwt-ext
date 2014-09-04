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

package com.github.smartgwt_ext.server.core.dao;

import java.util.List;

/**
 * @author Andreas Berger
 * @created 24.10.12 - 18:23
 */
public class FetchResult<T> {
	private int totalRows;
	private List<T> data;

	public FetchResult(int totalRows, List<T> data) {
		this.totalRows = totalRows;
		this.data = data;
	}

	public int getTotalRows() {
		return totalRows;
	}

	public List<T> getData() {
		return data;
	}
}
