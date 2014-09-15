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
package com.github.smartgwt_ext.server.spring_binding.processing;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.smartgwt_ext.server.core.HasIdField;
import com.github.smartgwt_ext.server.core.annotations.FieldFeatures;
import com.github.smartgwt_ext.server.core.communication.SmartGwtException;
import com.github.smartgwt_ext.server.core.communication.ValidationException;
import com.github.smartgwt_ext.server.core.communication.model.JsDatasourceBaseResponse;
import com.github.smartgwt_ext.server.core.communication.model.JsDatasourceError;
import com.github.smartgwt_ext.server.core.communication.model.JsDatasourceOperationType;
import com.github.smartgwt_ext.server.core.communication.model.JsDatasourceRequest;
import com.github.smartgwt_ext.server.core.communication.model.JsDatasourceResponse;
import com.github.smartgwt_ext.server.core.communication.model.JsDatasourceTransactionRequest;
import com.github.smartgwt_ext.server.core.dao.SmartGwtDAO;
import com.github.smartgwt_ext.server.core.processing.CustomRequestHandler;
import com.github.smartgwt_ext.server.core.processing.EnhancedObjectMapper;
import com.github.smartgwt_ext.server.core.processing.EntityByDatasourceResolver;
import com.github.smartgwt_ext.server.core.processing.ErrorHandler;
import com.github.smartgwt_ext.server.core.processing.PasswordHandler;
import com.github.smartgwt_ext.server.core.processing.SmartGwtRequestContext;
import com.github.smartgwt_ext.server.core.processing.SmartGwtRequestProcessor;
import com.github.smartgwt_ext.server.core.processing.WrappedException;
import com.github.smartgwt_ext.server.introspection.BeanInformation;
import com.github.smartgwt_ext.server.introspection.PropertyInformation;
import com.smartgwt.client.rpc.RPCResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Andreas Berger
 * @created 10.01.2013
 */
@Repository
public class SmartGwtRequestProcessorImpl implements SmartGwtRequestProcessor {

	private static final String RESPONSE = "response";

	private static final Logger LOGGER = LoggerFactory.getLogger(SmartGwtRequestProcessorImpl.class);

	private ObjectMapper mapper;

	@Autowired
	private SmartGwtDAO dao;

	private TransactionTemplate transactionTemplate;

	public SmartGwtRequestProcessorImpl() {
		EnhancedObjectMapper em = new EnhancedObjectMapper(null) {

			@Override
			protected <T extends HasIdField> T getEntityById(Serializable id, Class<T> entityClass) {
				return dao.getById(entityClass, id);
			}

			@Override
			protected PasswordHandler getPasswordHandler(BeanInformation<?> bean, PropertyInformation<?> prop,
					FieldFeatures.Password password) {
				return null;
			}
		};
		mapper = em.getMapper();
	}

	@Autowired
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		transactionTemplate = new TransactionTemplate(transactionManager);
	}

	/**
	 * @param dao the dao to set
	 */
	public void setDao(SmartGwtDAO dao) {
		this.dao = dao;
	}

	@Override
	public void handleServletRequest(
			HttpServletRequest req,
			HttpServletResponse resp,
			EntityByDatasourceResolver entityResolver,
			CustomRequestHandler customRequestHandler,
			ErrorHandler errorHandler) throws IOException {
		JsonNode node = mapper.readTree(req.getInputStream());

		String response = handleNode(node, customRequestHandler, entityResolver, errorHandler);

		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(response);
	}

	@Override
	public String handleNode(
			JsonNode node,
			CustomRequestHandler customRequestHandler,
			EntityByDatasourceResolver entityResolver,
			ErrorHandler errorHandler) throws IOException {
		LOGGER.debug("REQUEST: " + node.toString());

		String response = handleRequest(node, customRequestHandler, entityResolver, errorHandler);

		LOGGER.debug("RESPONSE: " + response);

		return response;
	}

	private String handleRequest(
			final JsonNode node,
			final CustomRequestHandler customRequestHandler,
			final EntityByDatasourceResolver entityResolver,
			final ErrorHandler errorHandler)
			throws IOException {
		final JsonNode tx = node.get("transaction");
		if (tx != null) {

			final List<Map<String, JsDatasourceBaseResponse>> data = new ArrayList<Map<String, JsDatasourceBaseResponse>>();
			int queueStatus = RPCResponse.STATUS_SUCCESS;
			JsDatasourceError error;
			try {
				error = transactionTemplate.execute(new TransactionCallback<JsDatasourceError>() {

					@Override
					public JsDatasourceError doInTransaction(TransactionStatus status) {
						try {
							JsDatasourceError error = null;
							JsDatasourceTransactionRequest transaction = readObject(tx,
									JsDatasourceTransactionRequest.class);
							for (JsDatasourceRequest request : transaction.getOperations()) {
								JsDatasourceBaseResponse response;
								try {
									response = handleRequest(request, transaction, customRequestHandler,
											entityResolver);
								} catch (Exception e) {

									response = error = handleException(e, errorHandler);
								}
								response.setOperation(request.getOperationType());
								Map<String, JsDatasourceBaseResponse> r = new HashMap<String, JsDatasourceBaseResponse>();
								r.put(RESPONSE, response);
								data.add(r);
							}
							return error;
						} catch (Exception e) {
							throw new WrappedException(e);
						}
					}
				});
				if (error != null) {
					queueStatus = RPCResponse.STATUS_FAILURE;
				}
			} catch (RuntimeException e) {
				queueStatus = RPCResponse.STATUS_FAILURE;
				error = handleException(e, errorHandler);
			}
			if (error != null) {
				for (Map<String, JsDatasourceBaseResponse> response : data) {
					JsDatasourceBaseResponse resp = response.get(RESPONSE);
					if (resp instanceof JsDatasourceError || resp.getOperation() == JsDatasourceOperationType.fetch) {
						continue;
					}
					response.put(RESPONSE, error);
				}
			}
			for (Map<String, JsDatasourceBaseResponse> response : data) {
				JsDatasourceBaseResponse resp = response.get(RESPONSE);
				resp.setQueueStatus(queueStatus);
			}

			return getResponseString(data);
		} else {
			JsDatasourceBaseResponse response;
			try {
				response = transactionTemplate.execute(new TransactionCallback<JsDatasourceBaseResponse>() {

					@Override
					public JsDatasourceBaseResponse doInTransaction(TransactionStatus status) {
						try {
							return handleRequest(readObject(node, JsDatasourceRequest.class), null,
									customRequestHandler, entityResolver);
						} catch (Exception e) {
							throw new WrappedException(e);
						}
					}
				});
			} catch (Exception e) {
				response = handleException(e, errorHandler);
			}
			return getResponseString(response);
		}
	}

	private JsDatasourceError handleException(Exception ex, ErrorHandler errorHandler) {
		if (ex instanceof WrappedException) {
			ex = (Exception) ex.getCause();
		}
		try {
			JsDatasourceError error = errorHandler.handleException(ex);
			if (error != null) {
				return error;
			}
			throw ex;
		} catch (SmartGwtException e) {
			LOGGER.debug("", e);
			return new JsDatasourceError(e.getLocalizedMessage());
		} catch (ConstraintViolationException e) {
			JsDatasourceError response = new JsDatasourceError(RPCResponse.STATUS_VALIDATION_ERROR);
			for (ConstraintViolation<?> v : e.getConstraintViolations()) {
				response.addError(v.getPropertyPath().toString(), v.getMessage());
			}
			LOGGER.debug("", e);
			return response;
		} catch (DataIntegrityViolationException e) {
			JsDatasourceError response = new JsDatasourceError(RPCResponse.STATUS_VALIDATION_ERROR);
			response.addError("", e.getLocalizedMessage());
			LOGGER.debug("", e);
			return response;
		} catch (ValidationException e) {
			JsDatasourceError response = new JsDatasourceError(RPCResponse.STATUS_VALIDATION_ERROR);
			response.addError(e.getField(), e.getLocalizedMessage());
			LOGGER.debug("", e);
			return response;
		} catch (Exception e) {
			LOGGER.error("", e);
			return new JsDatasourceError(RPCResponse.STATUS_FAILURE);
		}
	}

	private <T extends HasIdField> JsDatasourceResponse<T> handleRequest(
			JsDatasourceRequest request,
			JsDatasourceTransactionRequest transaction,
			CustomRequestHandler customRequestHandler,
			EntityByDatasourceResolver entityResolver)
			throws ValidationException, IOException, SmartGwtException {
		SmartGwtRequestContext ctx = new SmartGwtRequestContext(entityResolver) {

			@Override
			protected <S extends HasIdField> S initWithId(Class<S> clazz, JsonNode data) throws IOException,
					SmartGwtException {
				return SmartGwtRequestProcessorImpl.this.initWithId(clazz, data);
			}
		};
		ctx.setRequest(request);
		ctx.setTransaction(transaction);
		ctx.setDao(dao);
		ctx.setMapper(mapper);
		JsDatasourceResponse<T> response = null;
		if (customRequestHandler != null) {
			response = customRequestHandler.<T>handleCustomRequest(ctx);
		}
		if (response == null) {
			response = ctx.<T>handleRequest();
		}
		return response;
	}

	private String getResponseString(Object response) throws IOException {
		StringWriter writer = new StringWriter();
		mapper.writeValue(writer, response);
		return writer.toString();
	}

	private <T> T readObject(JsonNode root, Class<T> clazz) throws IOException {
		JsonParser parser = root.traverse();
		parser.setCodec(mapper);
		return parser.readValueAs(clazz);
	}

	private <T extends HasIdField> T initWithId(Class<T> clazz, JsonNode data) throws IOException, SmartGwtException {
		T entity = readObject(data, clazz);
		return dao.initWithId(entity, clazz, data);
	}

}
