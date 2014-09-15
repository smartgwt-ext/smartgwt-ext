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

import com.fasterxml.jackson.databind.JsonNode;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Andreas Berger
 * @created 10.01.2013
 */
public interface SmartGwtRequestProcessor {

	/**
	 * Behandelt einen ServletRequest.
	 *
	 * @param req der HttpServletRequest
	 * @param resp der HttpServletResponse
	 * @param entityResolver der EntityByDatasourceResolver
	 * @param customRequestHandler zum Anpassen des RequestHandlings
	 * @throws IOException
	 */
	void handleServletRequest(
			HttpServletRequest req,
			HttpServletResponse resp,
			EntityByDatasourceResolver entityResolver,
			CustomRequestHandler customRequestHandler, ErrorHandler errorHandler) throws IOException;

	/**
	 * Behandelt ein Json-Request
	 *
	 * @param node der Json-Request
	 * @param customRequestHandler zum Anpassen des RequestHandlings
	 * @param entityResolver der EntityByDatasourceResolver
	 * @return die Response als String
	 *
	 * @throws IOException
	 */
	String handleNode(
			JsonNode node,
			CustomRequestHandler customRequestHandler,
			EntityByDatasourceResolver entityResolver, ErrorHandler errorHandler) throws IOException;
}
