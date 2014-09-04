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

package com.github.smartgwt_ext.frontend.server_binding;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.i18n.client.ConstantsWithLookup;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Andreas Berger
 * @created 25.10.12 - 10:24
 */
public class Helper {
	public static ToolStripButton addButton(ToolStrip toolStrip, String text, String icon, String id,
			boolean hideTitle, Integer position,
			ClickHandler handler) {
		ToolStripButton button = new ToolStripButton();
		button.setID(id);
		button.setAutoFit(true);
		if (icon != null) {
			button.setIcon(icon);
		}
		if (icon != null && hideTitle) {
			button.setPrompt(text);
		} else {
			button.setTitle(text);
		}
		button.addClickHandler(handler);
		if (position != null) {
			toolStrip.addMember(button, position);
		} else {
			toolStrip.addMember(button);
		}
		return button;
	}

	public static <T> Set<T> transformToSet(T... obj) {
		Set<T> result = new LinkedHashSet<T>();
		Collections.addAll(result, obj);
		return result;
	}

	/**
	 * Liefert den GWT OverlayType für ein bestimmten Record zurück.
	 *
	 * @param <T> der Typ des OverlayTypes
	 * @param record der Record
	 * @return den OverlayType
	 */
	@SuppressWarnings("unchecked")
	public static <T extends JavaScriptObject> T getOverlayType(Record record) {
		if (record == null) {
			return null;
		}
		return (T) record.getJsObj();
	}

	/**
	 * Liefert ein Array von GWT OverlayTypes für ein Record Array zurück.
	 *
	 * @param <T> der Typ des OverlayTypes
	 * @param records das Record-Array
	 * @return ein Array mit den OverlayTypes
	 */
	public static <T extends JavaScriptObject> List<T> getOverlayTypes(Record... records) {
		if (records == null) {
			return null;
		}
		List<T> result = new ArrayList<T>();
		for (Record record : records) {
			result.add(Helper.<T>getOverlayType(record));
		}
		return result;
	}

	public static Map<String, String> getEnumMap(ConstantsWithLookup names, Enum<?>... values) {
		Map<String, String> result = new LinkedHashMap<String, String>();
		for (Enum<?> value : values) {
			result.put(value.name(), names.getString(value.getDeclaringClass().getSimpleName() + "_" + value.name()));
		}
		return result;
	}
}
