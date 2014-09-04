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
package com.github.smartgwt_ext.server.core.processing;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.smartgwt_ext.server.core.HasIdField;
import com.github.smartgwt_ext.server.core.communication.SmartGwtException;
import com.github.smartgwt_ext.server.core.communication.ValidationException;
import com.github.smartgwt_ext.server.core.communication.model.JsCriterion;
import com.github.smartgwt_ext.server.core.communication.model.JsDatasourceOperationType;
import com.github.smartgwt_ext.server.core.communication.model.JsDatasourceRequest;
import com.github.smartgwt_ext.server.core.communication.model.JsDatasourceResponse;
import com.github.smartgwt_ext.server.core.communication.model.JsDatasourceTransactionRequest;
import com.github.smartgwt_ext.server.core.communication.model.JsSortSpecifier;
import com.github.smartgwt_ext.server.core.dao.FetchResult;
import com.github.smartgwt_ext.server.core.dao.SmartGwtDAO;
import com.smartgwt.client.rpc.RPCResponse;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author Andreas Berger
 * @created 09.01.2013
 */
public abstract class SmartGwtRequestContext {
	private JsDatasourceTransactionRequest transaction;
	private JsDatasourceRequest request;

	private SmartGwtDAO dao;
	private ObjectMapper mapper;
	private EntityByDatasourceResolver entityResolver;

	/** @param entityResolver */
	public SmartGwtRequestContext(EntityByDatasourceResolver entityResolver) {
		this.entityResolver = entityResolver;
	}

	public void setTransaction(JsDatasourceTransactionRequest transaction) {
		this.transaction = transaction;
	}

	public void setRequest(JsDatasourceRequest request) {
		this.request = request;
	}

	public void setDao(SmartGwtDAO dao) {
		this.dao = dao;
	}

	public void setMapper(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	public JsDatasourceTransactionRequest getTransaction() {
		return transaction;
	}

	public JsDatasourceRequest getRequest() {
		return request;
	}

	public String getDataSource() {
		return request.getDataSource();
	}

	public JsDatasourceOperationType getOperationType() {
		return request.getOperationType();
	}

	public String getOperationId() {
		return request.getOperationId();
	}

	public Integer getStartRow() {
		return request.getStartRow();
	}

	public Integer getEndRow() {
		return request.getEndRow();
	}

	public List<JsSortSpecifier> getSortSpecifier() {
		return request.getSortSpecifier();
	}

	public JsonNode getData() {
		return request.getData();
	}

	public String getStringParam(String name) {
		JsonNode value = getData().get(name);
		if (value == null) {
			return null;
		}
		return value.asText();
	}

	public Number getNumberParam(String name) {
		JsonNode value = getData().get(name);
		if (value == null) {
			return null;
		}
		if (value.isLong()) {
			return value.asLong();
		}
		if (value.isInt()) {
			return value.asInt();
		}
		if (value.isDouble()) {
			return value.asDouble();
		}
		return null;
	}

	public JsonNode getOldValues() {
		return request.getOldValues();
	}

	public String getTextMatchStyle() {
		return request.getTextMatchStyle();
	}

	public SmartGwtDAO getDao() {
		return dao;
	}

	public <T extends HasIdField> JsDatasourceResponse<T> handleRequest() throws IOException, ValidationException,
			SmartGwtException {
		Class<T> clazz = entityResolver.getEntity(request.getDataSource());
		return this.<T>handleRequest(clazz);
	}

	public <T extends HasIdField> JsDatasourceResponse<T> handleRequest(Class<T> clazz)
			throws IOException, ValidationException, SmartGwtException {
		if (request.getOperationType() == JsDatasourceOperationType.fetch) {
			JsCriterion criteria = extractCriteria(request);

			FetchResult<T> result = dao.<T>find(clazz, criteria, request.getStartRow(), request.getEndRow(),
					request.getSortSpecifier());

			return this.<T>createResponse(result);

		} else if (request.getOperationType() == JsDatasourceOperationType.add
				|| request.getOperationType() == JsDatasourceOperationType.update) {

			T entity = initWithId(clazz, request.getData());
			if (request.getOperationType() == JsDatasourceOperationType.update) {
				T existingEntity = dao.getById(clazz, entity.getId());
				// TODO hibernate version comparison
				entity = updateObject(request.getData(), existingEntity);
				entity = dao.merge(entity);
			} else {
				entity = updateObject(request.getData(), entity);
				dao.save(entity);
			}

			return new JsDatasourceResponse<T>(entity);

		} else if (request.getOperationType() == JsDatasourceOperationType.remove) {

			T entity = getEntityById(clazz, request.getData());
			if (entity == null) {
				throw new SmartGwtException("Unbekannter Datensatz");
			}
			dao.remove(entity);

			return new JsDatasourceResponse<T>(entity);
		}
		throw new SmartGwtException("Diese Operation wird nicht unterstützt");
	}

	public <T> JsDatasourceResponse<T> createResponse(FetchResult<T> result) {
		JsDatasourceResponse<T> response = new JsDatasourceResponse<T>();
		response.setStartRow(request.getStartRow());
		int start = 0;
		if (request.getStartRow() != null) {
			start = request.getStartRow();
		}
		response.setEndRow(start + result.getData().size());
		response.setTotalRows(result.getTotalRows());
		response.setData(result.getData());
		response.setStatus(RPCResponse.STATUS_SUCCESS);
		return response;
	}

	public <T extends HasIdField> T getEntityById(Class<T> clazz, JsonNode node)
			throws IOException, SmartGwtException {
		HasIdField entity = initWithId(clazz, node);
		return dao.getById(clazz, entity.getId());
	}

	public <T extends HasIdField> T updateObject(JsonNode data, T object) throws IOException {
		return mapper.readerForUpdating(object).readValue(data);
	}

	public JsCriterion getRequetsCriteria() {
		try {
			return extractCriteria(request);
		} catch (IOException e) {
			return null;
		}
	}

	public JsCriterion extractCriteria(JsDatasourceRequest request) throws IOException {
		if (request.getData() == null) {
			return null;
		}
		return extractCriteria(request.getData(), request.getTextMatchStyle());
	}

	public JsCriterion extractCriteria(JsonNode criteria, String textMatchStyle) throws IOException {
		if (criteria == null) {
			return null;
		}
		JsCriterion crit = null;
		JsonNode constructor = criteria.get("_constructor");
		if (constructor != null && "AdvancedCriteria".equals(constructor.asText())) {
			crit = readObject(criteria, JsCriterion.class);
		} else {
			@SuppressWarnings("unchecked")
			Map<String, Serializable> filter = readObject(criteria, Map.class);
			for (Map.Entry<String, Serializable> entry : filter.entrySet()) {
				if (("__gwt_ObjectId").equals(entry.getKey())) {
					// Bug in chrome, der sendet immer dieses attribut mit
					continue;
				}
				if (crit == null) {
					crit = new JsCriterion();
				}
				JsCriterion.Operator op = JsCriterion.Operator.equals;
				if ("substring".equals(textMatchStyle)) {
					op = JsCriterion.Operator.iContains;
				}
				crit.addCriteria(new JsCriterion(entry.getKey(), op, entry.getValue()));
			}
		}
		return crit;
	}

	public <T> T readObject(JsonNode root, Class<T> clazz) throws IOException {
		JsonParser parser = root.traverse();
		parser.setCodec(mapper);
		return parser.readValueAs(clazz);
	}

	protected abstract <T extends HasIdField> T initWithId(Class<T> clazz, JsonNode data)
			throws IOException, SmartGwtException;

}
