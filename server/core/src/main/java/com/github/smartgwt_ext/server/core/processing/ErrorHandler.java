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

import com.github.smartgwt_ext.server.core.communication.model.JsDatasourceError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andreas Berger
 * @created 29.08.14 - 22:07
 */
public class ErrorHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(ErrorHandler.class);

	public JsDatasourceError handleException(Exception ex) throws Exception {
		LOGGER.error("", ex);
		return new JsDatasourceError(ex.getLocalizedMessage());
	}
}
