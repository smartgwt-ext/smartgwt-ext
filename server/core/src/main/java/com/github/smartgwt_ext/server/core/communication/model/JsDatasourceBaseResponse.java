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

/**
 * @author Andreas Berger
 * @created 09.01.2013
 */
public class JsDatasourceBaseResponse {
	private Integer status;
	private Integer queueStatus;
	private JsDatasourceOperationType operation;

	/**
	 * Gets the value of the status property.
	 *
	 * @return possible object is {@link StatusCode }
	 */
	public Integer getStatus() {
		return status;
	}

	/**
	 * Sets the value of the status property.
	 *
	 * @param value allowed object is {@link StatusCode }
	 */
	public void setStatus(Integer value) {
		this.status = value;
	}

	public Integer getQueueStatus() {
		return queueStatus;
	}

	public void setQueueStatus(Integer queueStatus) {
		this.queueStatus = queueStatus;
	}

	@JsonIgnore
	public JsDatasourceOperationType getOperation() {
		return operation;
	}

	public void setOperation(JsDatasourceOperationType operation) {
		this.operation = operation;
	}
}
