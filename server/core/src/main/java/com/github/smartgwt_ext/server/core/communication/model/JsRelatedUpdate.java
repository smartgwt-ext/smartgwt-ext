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
package com.github.smartgwt_ext.server.core.communication.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andreas Berger
 * @created 09.01.2013
 */
public class JsRelatedUpdate<T> {
	private JsDatasourceOperationType operationType;

	private boolean invalidateCache;

	private String dataSource;

	private List<T> data;

	public JsRelatedUpdate() {
	}

	public JsRelatedUpdate(String datasource, JsDatasourceOperationType operation,
			T object) {
		this.dataSource = datasource;
		this.operationType = operation;
		addData(object);
	}

	public JsDatasourceOperationType getOperationType() {
		return operationType;
	}

	public void setOperationType(JsDatasourceOperationType operationType) {
		this.operationType = operationType;
	}

	public boolean isInvalidateCache() {
		return invalidateCache;
	}

	public void setInvalidateCache(boolean invalidateCache) {
		this.invalidateCache = invalidateCache;
	}

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}

	public void addData(T value) {
		if (data == null) {
			this.data = new ArrayList<T>();
		}
		this.data.add(value);
	}
}
