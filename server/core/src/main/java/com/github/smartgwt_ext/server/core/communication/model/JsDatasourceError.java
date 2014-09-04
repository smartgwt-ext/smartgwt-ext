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

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.smartgwt.client.rpc.RPCResponse;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Andreas Berger
 * @created 09.01.2013
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonTypeName("response")
public class JsDatasourceError extends JsDatasourceBaseResponse {

	protected Integer status;
	protected Integer queueStatus;
	protected Object data;
	protected Map<String, JsErrorMessage> errors;

	public JsDatasourceError(int status) {
		setStatus(status);
	}

	public JsDatasourceError(String message) {
		this(RPCResponse.STATUS_FAILURE);
		data = message;
	}

	/**
	 * Gets the value of the status property.
	 *
	 * @return possible object is {@link StatusCode }
	 */
	@Override
	public Integer getStatus() {
		return status;
	}

	/**
	 * Sets the value of the status property.
	 *
	 * @param value allowed object is {@link StatusCode }
	 */
	@Override
	public void setStatus(Integer value) {
		this.status = value;
	}

	@Override
	public Integer getQueueStatus() {
		return queueStatus;
	}

	@Override
	public void setQueueStatus(Integer queueStatus) {
		this.queueStatus = queueStatus;
	}

	public void addError(String field, String message) {
		if (errors == null) {
			this.errors = new LinkedHashMap<String, JsErrorMessage>();
		}
		this.errors.put(field, new JsErrorMessage(message));
	}

	public Map<String, JsErrorMessage> getErrors() {
		return errors;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}
