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

import com.smartgwt.client.types.Side;
import com.smartgwt.client.types.TabBarControls;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.tab.events.TabSelectedEvent;
import com.smartgwt.client.widgets.tab.events.TabSelectedHandler;

/**
 * Tabs dieses TabSets werden erst initialisiert, wenn es ausgewählt wurde.
 *
 * @author Andreas Berger
 * @created 24.10.12 - 15:39
 */
public class LazyTabSet<T extends LazyTab> implements TabSelectedHandler {

	private T selectetdTab;
	private TabSet tabSet;

	public LazyTabSet() {
		tabSet = new TabSet();
		tabSet.setTabBarPosition(Side.TOP);
		tabSet.setWidth100();
		tabSet.setHeight100();
		tabSet.setTabBarControls(TabBarControls.TAB_SCROLLER, TabBarControls.TAB_PICKER);
		tabSet.addTabSelectedHandler(this);
	}

	public void addTab(T tab) {
		tabSet.addTab(tab);
	}

	@Override
	@SuppressWarnings({"unchecked"})
	public void onTabSelected(TabSelectedEvent event) {
		selectetdTab = (T) event.getTab();
		selectetdTab.getInitializedPane();
	}

	public T getSelectetdTab() {
		return selectetdTab;
	}

	public TabSet getCanvas() {
		return tabSet;
	}

	public Tab[] getTabs() {
		return tabSet.getTabs();
	}

	public void selectTab(T tab) {
		selectetdTab = tab;
		tabSet.selectTab(tab);
	}
}
