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

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andreas Berger
 * @created 09.01.2013
 */
public class JsDatasourceRequest {

	protected String dataSource;
	protected JsDatasourceOperationType operationType;
	protected String operationId;
	protected Integer startRow;
	protected Integer endRow;
	protected List<JsSortSpecifier> sortBy;
	protected String textMatchStyle;
	protected JsonNode data;
	protected JsonNode oldValues;

	/**
	 * Gets the value of the dataSource property.
	 *
	 * @return possible object is {@link String }
	 */
	public String getDataSource() {
		return dataSource;
	}

	/**
	 * Sets the value of the dataSource property.
	 *
	 * @param value allowed object is {@link String }
	 */
	public void setDataSource(String value) {
		this.dataSource = value;
	}

	/**
	 * Gets the value of the operationType property.
	 *
	 * @return possible object is {@link JsDatasourceOperationType }
	 */
	public JsDatasourceOperationType getOperationType() {
		return operationType;
	}

	/**
	 * Sets the value of the operationType property.
	 *
	 * @param value allowed object is {@link JsDatasourceOperationType }
	 */
	public void setOperationType(JsDatasourceOperationType value) {
		this.operationType = value;
	}

	/**
	 * Gets the value of the operationId property.
	 *
	 * @return possible object is {@link String }
	 */
	public String getOperationId() {
		return operationId;
	}

	/**
	 * Sets the value of the operationId property.
	 *
	 * @param value allowed object is {@link String }
	 */
	public void setOperationId(String value) {
		this.operationId = value;
	}

	/**
	 * Gets the value of the startRow property.
	 *
	 * @return possible object is {@link Long }
	 */
	public Integer getStartRow() {
		return startRow;
	}

	/**
	 * Sets the value of the startRow property.
	 *
	 * @param value allowed object is {@link Long }
	 */
	public void setStartRow(Integer value) {
		this.startRow = value;
	}

	/**
	 * Gets the value of the endRow property.
	 *
	 * @return possible object is {@link Long }
	 */
	public Integer getEndRow() {
		return endRow;
	}

	/**
	 * Sets the value of the endRow property.
	 *
	 * @param value allowed object is {@link Long }
	 */
	public void setEndRow(Integer value) {
		this.endRow = value;
	}

	/**
	 * Sets the value of the sortBy property.
	 *
	 * @param value allowed object is {@link String }
	 */
	public void setSortBy(String[] value) {
		if (value == null || value.length == 0) {
			sortBy = null;
			return;
		}
		sortBy = new ArrayList<JsSortSpecifier>(value.length);
		for (String sort : value) {
			if (sort.startsWith("-")) {
				sortBy.add(new JsSortSpecifier(sort.substring(1), JsSortSpecifier.Direction.DESC));
			} else {
				sortBy.add(new JsSortSpecifier(sort, JsSortSpecifier.Direction.ASC));
			}
		}
	}

	public List<JsSortSpecifier> getSortSpecifier() {
		return sortBy;
	}

	/**
	 * Gets the value of the textMatchStyle property.
	 *
	 * @return possible object is {@link String }
	 */

	public String getTextMatchStyle() {
		return textMatchStyle;
	}

	/**
	 * Sets the value of the textMatchStyle property.
	 *
	 * @param value allowed object is {@link String }
	 */
	public void setTextMatchStyle(String value) {
		this.textMatchStyle = value;
	}

	/**
	 * Gets the value of the data property.
	 *
	 * @return possible object is {@link JsDatasourceRequest.Data }
	 */
	public JsonNode getData() {
		return data;
	}

	/**
	 * Sets the value of the data property.
	 *
	 * @param value allowed object is {@link JsDatasourceRequest.Data }
	 */
	public void setData(JsonNode value) {
		this.data = value;
	}

	public JsonNode getOldValues() {
		return oldValues;
	}

	public void setOldValues(JsonNode oldValues) {
		this.oldValues = oldValues;
	}
}
