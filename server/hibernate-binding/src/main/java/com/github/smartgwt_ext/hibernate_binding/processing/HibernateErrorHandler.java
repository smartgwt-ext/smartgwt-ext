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
package com.github.smartgwt_ext.hibernate_binding.processing;

import com.github.smartgwt_ext.server.core.communication.model.JsDatasourceError;
import com.github.smartgwt_ext.server.core.processing.ErrorHandler;
import org.hibernate.HibernateException;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Andreas Berger
 * @created 22.01.2013
 */
public class HibernateErrorHandler extends ErrorHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(HibernateErrorHandler.class);

	@Override
	public JsDatasourceError handleException(Exception ex) throws Exception {
		try {
			throw ex;
		} catch (ConstraintViolationException e) {
			return new JsDatasourceError(e.getLocalizedMessage());
//		} catch (org.springframework.dao.DataIntegrityViolationException e) {
			// TODO
//			String CONSTRAINT_VIOLATION = "Der Datensatz wird in anderen Datensätzen verwendet und kann daher nicht verändert oder gelöscht werden.";
//			return new JsDatasourceError(CONSTRAINT_VIOLATION);
		} catch (NonUniqueObjectException e) {
			return new JsDatasourceError(e.getLocalizedMessage());
		} catch (HibernateException e) {
			LOGGER.error("", e);
			return new JsDatasourceError(e.getLocalizedMessage());
		}
	}

}
