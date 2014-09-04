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
package com.github.smartgwt_ext.server.core.dao;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.smartgwt_ext.server.core.communication.SmartGwtException;
import com.github.smartgwt_ext.server.core.communication.model.JsCriterion;
import com.github.smartgwt_ext.server.core.communication.model.JsSortSpecifier;

import java.io.Serializable;
import java.util.List;

/**
 * Contract for using the SmartGwtDatasource
 *
 * @author Andreas Berger
 * @created 09.01.2013
 */
public interface SmartGwtDAO {

	/**
	 * Gets an Entity by its ID
	 *
	 * @param clazz the Class of the Entity
	 * @param id the Id
	 * @return the Entity
	 */
	<T> T getById(Class<T> clazz, Serializable id);

	/**
	 * Persist the given transient instance.
	 *
	 * @param entity entity the transient instance to persist
	 */
	void save(Object entity);

	/**
	 * Merge the state of the given entity into the current persistence context.
	 *
	 * @param transientObject
	 * @return the managed instance that the state was merged
	 */
	<T> T merge(T transientObject);

	/**
	 * Remove an object from persistent storage in the database
	 *
	 * @param entity to be removed
	 */
	void remove(Object entity);

	/**
	 * Queries for an Entity.
	 *
	 * @param <T> the Type of the Entity
	 * @param entityClass the Class of the Entity
	 * @param criteria the Criteria for the Query
	 * @param startRow the first row to return
	 * @param endRow the last row to return
	 * @param sortBy the sort order
	 * @return the Entities
	 */
	<T> FetchResult<T> find(Class<T> entityClass, JsCriterion criteria, Integer startRow,
			Integer endRow, List<JsSortSpecifier> sortBy) throws SmartGwtException;

	/**
	 * Initialize the Entity with its id. If there is nothing to do just return
	 * the <code>entity<code> given to this method.
	 *
	 * @param <T> the Type of the Entity
	 * @param entity the Entity initialized by Jackson
	 * @param clazz the Class of the Entity
	 * @param data the pure Js-Data
	 * @return the Entity initialized with its id
	 *
	 * @throws SmartGwtException if sth. went wrong
	 */
	<T> T initWithId(T entity, Class<T> clazz, JsonNode data) throws SmartGwtException;

}
