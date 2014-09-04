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
package com.github.smartgwt_ext.server.core.communication.servlet;

import com.github.smartgwt_ext.server.core.HasIdField;
import com.github.smartgwt_ext.server.core.processing.CustomRequestHandler;
import com.github.smartgwt_ext.server.core.processing.EntityByDatasourceResolver;
import com.github.smartgwt_ext.server.core.processing.ErrorHandler;
import com.github.smartgwt_ext.server.core.processing.SmartGwtRequestProcessor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Extends GenericDataSourceServlet and override method getClass.
 *
 * @author Andreas Berger
 */
public abstract class SmartGwtExtDataSourceServlet extends HttpServlet implements EntityByDatasourceResolver {

	private static final long serialVersionUID = 1L;


	private final Map<String, Class<?>> ENTITY_MAP = new HashMap<String, Class<?>>();

	protected void register(Class<? extends HasIdField> clazz) {
		register(clazz.getSimpleName(), clazz);
	}

	protected void register(String name, Class<? extends HasIdField> clazz) {
		ENTITY_MAP.put(name, clazz);
	}

	public abstract SmartGwtRequestProcessor getSmartGwtRequestProcessor();

	public ErrorHandler getErrorHandler() {
		return new ErrorHandler();
	}

	@Override
	protected final void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected final void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		handleRequest(req, resp, this, null, getErrorHandler());
	}

	/**
	 * Override Point. Customization of Handling.
	 *
	 * @param req the Request
	 * @param resp the Response
	 * @param entityByDatasourceResolver the EntityByDatasourceResolver
	 * @param customRequestHandler a CustomRequestHandler
	 * @throws IOException
	 */
	protected void handleRequest(
			HttpServletRequest req,
			HttpServletResponse resp,
			EntityByDatasourceResolver entityByDatasourceResolver,
			CustomRequestHandler customRequestHandler, ErrorHandler errorHandler) throws IOException {
		getSmartGwtRequestProcessor()
				.handleServletRequest(req, resp, entityByDatasourceResolver, customRequestHandler, errorHandler);
	}

	/**
	 * Override getClass in the child servlet and return the according entity
	 *
	 * @param dataSource
	 * @return Entity child class for the datasource
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> Class<T> getEntity(String dataSource) {
		Class<?> result = ENTITY_MAP.get(dataSource);
		if (result != null) {
			return (Class<T>) result;
		}
		throw new IllegalArgumentException("DataSource " + dataSource + " is not supported");
	}

}
