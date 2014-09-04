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

import java.util.List;

/**
 * @author Andreas Berger
 * @created 09.01.2013
 */
public class JsDatasourceTransactionRequest {
	private int transactionNum;
	private List<JsDatasourceRequest> operations;

	/** @return the transactionNum */
	public int getTransactionNum() {
		return transactionNum;
	}

	/** @param transactionNum the transactionNum to set */
	public void setTransactionNum(int transactionNum) {
		this.transactionNum = transactionNum;
	}

	/** @return the operations */
	public List<JsDatasourceRequest> getOperations() {
		return operations;
	}

	/** @param operations the operations to set */
	public void setOperations(List<JsDatasourceRequest> operations) {
		this.operations = operations;
	}
}
