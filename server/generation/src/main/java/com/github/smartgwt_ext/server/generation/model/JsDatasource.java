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
package com.github.smartgwt_ext.server.generation.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

/**
 * // TODO Comment me!
 *
 * @author Andreas Berger
 */
@JsonPropertyOrder({"id", "fields"})
public class JsDatasource {

	@JsonProperty("ID")
	private String id;

	private List<JsDatasourceField> fields;

	/** @return the id */
	public String getId() {
		return id;
	}

	/** @param id the id to set */
	public void setId(String id) {
		this.id = id;
	}

	/** @return the fields */
	public List<JsDatasourceField> getFields() {
		return fields;
	}

	/** @param fields the fields to set */
	public void setFields(List<JsDatasourceField> fields) {
		this.fields = fields;
	}

}
