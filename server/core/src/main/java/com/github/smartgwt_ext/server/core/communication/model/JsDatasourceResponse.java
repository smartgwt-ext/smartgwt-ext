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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.smartgwt.client.rpc.RPCResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andreas Berger
 * @created 09.01.2013
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonTypeName("response")
public class JsDatasourceResponse<T> extends JsDatasourceBaseResponse {

	private Boolean invalidateCache;
	private List<T> data;
	private Integer startRow;
	private Integer endRow;
	private Integer totalRows;
	private List<JsRelatedUpdate<?>> relatedUpdates;

	public JsDatasourceResponse() {
	}

	public JsDatasourceResponse(int status) {
		setStatus(status);
	}

	public JsDatasourceResponse(List<T> updated) {
		this(RPCResponse.STATUS_SUCCESS);
		setData(updated);
	}

	public JsDatasourceResponse(T updated) {
		this(RPCResponse.STATUS_SUCCESS);
		addData(updated);
	}

	/**
	 * Gets the value of the invalidateCache property.
	 *
	 * @return possible object is {@link Boolean }
	 */
	public Boolean isInvalidateCache() {
		return invalidateCache;
	}

	/**
	 * Sets the value of the invalidateCache property.
	 *
	 * @param value allowed object is {@link Boolean }
	 */
	public void setInvalidateCache(Boolean value) {
		this.invalidateCache = value;
	}

	/**
	 * Gets the value of the data property.
	 *
	 * @return possible object is
	 *         {@link com.smartgwt.client.data.DSResponse.Data }
	 */
	public List<T> getData() {
		return data;
	}

	/**
	 * Sets the value of the data property.
	 *
	 * @param value allowed object is
	 * {@link com.smartgwt.client.data.DSResponse.Data }
	 */
	public void setData(List<T> value) {
		this.data = value;
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
	 * Gets the value of the totalRows property.
	 *
	 * @return possible object is {@link Long }
	 */
	public Integer getTotalRows() {
		return totalRows;
	}

	/**
	 * Sets the value of the totalRows property.
	 *
	 * @param value allowed object is {@link Long }
	 */
	public void setTotalRows(Integer value) {
		this.totalRows = value;
	}

	public void addData(T value) {
		if (data == null) {
			this.data = new ArrayList<T>();
		}
		this.data.add(value);
	}

	@JsonIgnore
	public T getFirstEntry() {
		return data == null ? null : data.get(0);
	}

	public List<JsRelatedUpdate<?>> getRelatedUpdates() {
		return relatedUpdates;
	}

	public void setRelatedUpdates(List<JsRelatedUpdate<?>> relatedUpdates) {
		this.relatedUpdates = relatedUpdates;
	}

	public void addRelatedUpdate(JsRelatedUpdate<?> value) {
		if (relatedUpdates == null) {
			this.relatedUpdates = new ArrayList<JsRelatedUpdate<?>>();
		}
		this.relatedUpdates.add(value);
	}
}
