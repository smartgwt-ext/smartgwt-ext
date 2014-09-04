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
import java.util.Map;

/**
 * @author Andreas Berger
 * @created 09.01.2013
 */
public class JsDatasourceField {
	private String name;
	private String title;
	private String prompt;
	private Boolean primaryKey;
	private Boolean hidden;
	private Boolean required;
	private String type;
	private Integer length;
	private String foreignKey;
	private String displayField;
	private String editorType;
	private Map<String, Object> editorProperties;
	private Boolean canEdit;
	private Boolean canSave;
	private Map<String, Object> valueMap;
	private List<JsValidator> validators;
	private Boolean multiple;
	private String ecPath;

	/** @return the name */
	public String getName() {
		return name;
	}

	/** @param name the name to set */
	public void setName(String name) {
		this.name = name;
	}

	/** @return the title */
	public String getTitle() {
		return title;
	}

	/** @param title the title to set */
	public void setTitle(String title) {
		this.title = title;
	}

	/** @return the primaryKey */
	public Boolean getPrimaryKey() {
		return primaryKey;
	}

	/** @param primaryKey the primaryKey to set */
	public void setPrimaryKey(Boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	/** @return the hidden */
	public Boolean getHidden() {
		return hidden;
	}

	/** @param hidden the hidden to set */
	public void setHidden(Boolean hidden) {
		this.hidden = hidden;
	}

	/** @return the type */
	public String getType() {
		return type;
	}

	/** @param type the type to set */
	public void setType(String type) {
		this.type = type;
	}

	/** @return the prompt */
	public String getPrompt() {
		return prompt;
	}

	/** @param prompt the prompt to set */
	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	/** @return the required */
	public Boolean getRequired() {
		return required;
	}

	/** @param required the required to set */
	public void setRequired(Boolean required) {
		this.required = required;
	}

	/** @return the length */
	public Integer getLength() {
		return length;
	}

	/** @param length the length to set */
	public void setLength(Integer length) {
		this.length = length;
	}

	/** @return the validators */
	public List<JsValidator> getValidators() {
		return validators;
	}

	/** @param validators the validators to set */
	public void setValidators(List<JsValidator> validators) {
		this.validators = validators;
	}

	public void addValidator(JsValidator validator) {
		if (validators == null) {
			validators = new ArrayList<JsValidator>();
		}
		validators.add(validator);
	}

	/** @param foreignKey the foreignKey to set */
	public void setForeignKey(String foreignKey) {
		this.foreignKey = foreignKey;
	}

	/** @return the foreignKey */
	public String getForeignKey() {
		return foreignKey;
	}

	/** @param displayField the displayField to set */
	public void setDisplayField(String displayField) {
		this.displayField = displayField;
	}

	/** @return the displayField */
	public String getDisplayField() {
		return displayField;
	}

	/** @param editorType the editorType to set */
	public void setEditorType(String editorType) {
		this.editorType = editorType;
	}

	/** @return the editorType */
	public String getEditorType() {
		return editorType;
	}

	/** @param editorProperties the editorProperties to set */
	public void setEditorProperties(Map<String, Object> editorProperties) {
		this.editorProperties = editorProperties;
	}

	/** @return the editorProperties */
	public Map<String, Object> getEditorProperties() {
		return editorProperties;
	}

	/** @return the canEdit */
	public Boolean getCanEdit() {
		return canEdit;
	}

	/** @param canEdit the canEdit to set */
	public void setCanEdit(Boolean canEdit) {
		this.canEdit = canEdit;
	}

	/** @return the canSave */
	public Boolean getCanSave() {
		return canSave;
	}

	/** @param canSave the canSave to set */
	public void setCanSave(Boolean canSave) {
		this.canSave = canSave;
	}

	/** @param valueMap the valueMap to set */
	public void setValueMap(Map<String, Object> valueMap) {
		this.valueMap = valueMap;
	}

	/** @return the valueMap */
	public Map<String, Object> getValueMap() {
		return valueMap;
	}

	public void setMultiple(Boolean multiple) {
		this.multiple = multiple;
	}

	public Boolean isMultiple() {
		return multiple;
	}

	public void setEcPath(String ecPath) {
		this.ecPath = ecPath;
	}

	public String getEcPath() {
		return ecPath;
	}
}
