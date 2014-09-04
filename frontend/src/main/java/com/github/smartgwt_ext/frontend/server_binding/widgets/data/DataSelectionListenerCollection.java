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

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * @author Andreas Berger
 *         abg $
 * @created 11.01.2012 - 13:41:46
 */
public class DataSelectionListenerCollection<T extends JavaScriptObject> implements DataSelectionListener<T> {

	private Collection<DataSelectionListener<T>> listeners;

	public void addListener(DataSelectionListener<T> listener) {
		if (listeners == null) {
			listeners = new LinkedHashSet<DataSelectionListener<T>>();
		}
		listeners.add(listener);
	}

	public void removeListener(DataSelectionListener<T> listener) {
		if (listeners == null) {
			return;
		}
		listeners.remove(listener);
		if (listeners.isEmpty()) {
			listeners = null;
		}
	}

	@Override
	public void onSelection(T element, Record record) {
		if (listeners == null) {
			return;
		}
		for (DataSelectionListener<T> listener : listeners) {
			listener.onSelection(element, record);
		}
	}

	@Override
	public void onSelection(List<T> elements, Record[] records) {
		if (listeners == null) {
			return;
		}
		for (DataSelectionListener<T> listener : listeners) {
			listener.onSelection(elements, records);
		}
	}
}
