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

import com.github.smartgwt_ext.server.core.communication.SmartGwtException;
import com.github.smartgwt_ext.server.core.communication.ValidationException;
import com.github.smartgwt_ext.server.core.communication.model.JsDatasourceResponse;

import java.io.IOException;

/**
 * @author Andreas Berger
 * @created 10.01.2013
 */
public interface CustomRequestHandler {

	/**
	 * Override Point. Customization of Response.
	 *
	 * @param <T> the Type of the Result
	 * @param ctx the Context
	 * @return null to enable default Requets Handling
	 *
	 * @throws IOException
	 * @throws ValidationException
	 * @throws SmartGwtException
	 */
	<T> JsDatasourceResponse<T> handleCustomRequest(SmartGwtRequestContext ctx) throws IOException,
			ValidationException,
			SmartGwtException;
}
