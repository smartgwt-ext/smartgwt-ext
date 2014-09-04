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

package com.github.smartgwt_ext.frontend.server_binding.widgets.data;

import com.google.gwt.core.client.JavaScriptObject;
import com.smartgwt.client.data.Record;

import java.util.List;

/**
 * @author Andreas Berger
 * @created 05.01.2012 - 22:33:56
 */
public interface DataSelectionListener<T extends JavaScriptObject> {

	/**
	 * wird aufgerufen, wenn ein Element selektiert wurde.
	 *
	 * @param element das Element
	 * @param record der Record
	 */
	void onSelection(T element, Record record);

	/**
	 * wird aufgerufen, wenn mehrere Elemente ausgewählt wurden.
	 *
	 * @param elements die Elemente
	 * @param records die Records
	 */
	void onSelection(List<T> elements, Record[] records);
}
