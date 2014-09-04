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
package com.github.smartgwt_ext.frontend.server_binding.widgets;

import com.github.smartgwt_ext.frontend.server_binding.i18n.ServerBindingStrings;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.grid.ListGrid;

/**
 * @author Andreas Berger
 * @created 03.01.2013
 */
public class GenericGridEditor extends GenericEditor {

	private ListGrid grid;

	public GenericGridEditor(ListGrid grid, String id) {
		this(grid, ServerBindingStrings.$.WINDOW_EDITOR_TITEL(), id);
	}

	/**
	 * Create a new content editor using the title
	 *
	 * @param title the title
	 */
	public GenericGridEditor(ListGrid grid, String title, String id) {
		super(grid.getDataSource(), title, id);
		this.grid = grid;
	}

	public void editSelectedData() {
		editSelectedData(grid);
	}

	public Record getSelectedRecord() {
		return grid.getSelectedRecord();
	}
}
