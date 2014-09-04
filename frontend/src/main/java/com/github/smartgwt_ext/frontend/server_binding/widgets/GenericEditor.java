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

import com.github.smartgwt_ext.frontend.server_binding.Helper;
import com.github.smartgwt_ext.frontend.server_binding.datasource.DSCallback;
import com.github.smartgwt_ext.frontend.server_binding.i18n.ServerBindingStrings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.events.SubmitValuesEvent;
import com.smartgwt.client.widgets.form.events.SubmitValuesHandler;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * Ein Generischer Editor, der mittels der Datasource aus dem übergebenen
 * ListGrid befüllt wird.
 *
 * @author Andreas Berger
 */
public class GenericEditor {

	public static final int ELEMENT_PADDING = 5;

	private DynamicForm form;
	private DataSource dataSource;
	private Window window;

	private ToolStrip buttons;
	private Canvas canvas;
	private String title;
	private SectionStackSection section;
	private SectionStack sectionStack;
	private boolean reuseWindow;

	private String id;

	private ToolStripButton saveButton;

	private ToolStripButton cancelButton;

	public GenericEditor(DataSource dataSource, String id) {
		this(dataSource, ServerBindingStrings.$.WINDOW_EDITOR_TITEL(), id);
	}

	/**
	 * Create a new content editor using the title
	 *
	 * @param title the title
	 */
	public GenericEditor(DataSource dataSource, String title, String id) {
		this.id = id;
		this.dataSource = dataSource;
		this.title = title;
		buttons = new ToolStrip();
		buttons.setWidth100();
		saveButton = addButton(ServerBindingStrings.$.BUTTON_SAVE(), GWT.getModuleBaseURL() + "/images/save.png",
				"_save", false, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onSave(true);
			}
		});
		cancelButton = addButton(ServerBindingStrings.$.BUTTON_CANCEL(), GWT.getModuleBaseURL() + "/images/cancel.png",
				"_cancel", false, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onCancel();
			}
		});
	}

	public ToolStripButton addButton(String text, String icon, String id_suffix,
			boolean hideTitle, ClickHandler handler) {
		return Helper.addButton(buttons, text, icon, id + id_suffix, hideTitle, null, handler);

	}

	protected DynamicForm createForm() {
		return new DynamicForm();
	}

	/** @return the form */
	public DynamicForm getForm() {
		if (form != null) {
			return form;
		}
		form = createForm();
		form.setID(id + "_form");

		form.setHeight100();
		form.setWidth100();
		form.setPadding(ELEMENT_PADDING);
		form.setOverflow(Overflow.AUTO);

		form.setTitleWidth(120);
		form.setCanSubmit(false);
		form.addSubmitValuesHandler(new SubmitValuesHandler() {
			@Override
			public void onSubmitValues(SubmitValuesEvent event) {
				onSave(true);
			}
		});
		form.setSaveOnEnter(true);

		form.setDataSource(dataSource);
		form.setUseAllDataSourceFields(true);
		form.setColWidths(120, "*");

		// allow customization before rendering
		customizeForm(form);

		// add all unspecified fields to the form
		if (form.getUseAllDataSourceFields() == null || form.getUseAllDataSourceFields()) {
			LinkedHashSet<String> names = new LinkedHashSet<String>();
			List<FormItem> fields = new ArrayList<FormItem>();
			for (FormItem field : form.getFields()) {
				names.add(field.getName());
				fields.add(field);
			}
			for (DataSourceField field : dataSource.getFields()) {
				if (!names.contains(field.getName())) {
					names.add(field.getName());
					fields.add(new FormItem(field.getName()));
				}
			}
			form.setFields(fields.toArray(new FormItem[fields.size()]));
		}
		for (FormItem field : form.getFields()) {
			field.setWidth("*");
		}
		return form;
	}

	public Canvas getSimplePanel() {
		if (canvas != null) {
			return canvas;
		}
		VLayout layout = new VLayout();
		layout.setID(id + "_panel");
		layout.setHeight100();
		layout.setWidth100();
		layout.addMember(getForm());
		layout.addMember(buttons);

		return canvas = customizeLayout(layout);
	}

	protected void onCancel() {
		closeWindow();
	}

	protected void onSave(final boolean closeWindow) {
		if (form.validate()) {
			if (form.getDataSource() == null) {
				if (closeWindow) {
					closeWindow();
				}
			} else {
				form.saveData(new DSCallback() {
					@Override
					protected void onSuccess(DSResponse dsResponse, Object rawData, DSRequest request) {
						Record record = dsResponse.getData()[0];
						form.editRecord(record);
						if (closeWindow) {
							closeWindow();
						}
					}
				});
			}
		}
	}

	protected void closeWindow() {
		if (window != null) {
			if (reuseWindow) {
				window.hide();
			} else {
				window.destroy();
			}
		}
	}

	public Window openWindow() {
		if (window != null && reuseWindow) {
			window.show();
			return window;
		}
		window = new Window();
		window.setID(id + "_window");
		window.setBodyColor("#ffffff");
		window.setWidth("80%");
		window.setHeight("80%");
		window.setMinWidth(300);
		window.setMinHeight(200);

		window.setIsModal(true);
		window.setShowModalMask(true);
		window.setModalMaskOpacity(50);

		window.setShowMinimizeButton(false);
		window.setRedrawOnResize(true);
		window.setCanDragResize(true);

		window.setAutoCenter(true);

		window.addItem(getSimplePanel());
		window.addCloseClickHandler(new CloseClickHandler() {
			@Override
			public void onCloseClick(CloseClickEvent event) {
				closeWindow();
			}
		});

		if (title != null) {
			window.setTitle(title);
		}

		customizeWindow(window);

		for (FormItem item : form.getFields()) {
			item.setWidth("*");
		}

		window.show();
		return window;
	}

	/** @see com.smartgwt.client.widgets.form.DynamicForm#editNewRecord() */
	public void editNewRecord() {
		prepareFormForEditing(getForm(), true, null);
		getForm().editNewRecord();
	}

	/**
	 * @param initialValues
	 * @see com.smartgwt.client.widgets.form.DynamicForm#editNewRecord(java.util.Map)
	 */
	public void editNewRecord(Map<String, Object> initialValues) {
		prepareFormForEditing(getForm(), true, null);
		getForm().editNewRecord(initialValues);
	}

	public void editNewRecord(Record record) {
		prepareFormForEditing(getForm(), true, record);
		editNewRecord(getForm().getJsObj(), record.getJsObj());
	}

	/**
	 * Prepare to edit a new record by clearing the current set of values (or
	 * replacing them with initialValues if specified). Subsequent calls to
	 * saveData() will use an add rather than an update operation.
	 *
	 * @param initialValuesJS initial set of values for the editor as a map of field names
	 * to their corresponding values
	 */
	private native void editNewRecord(JavaScriptObject form, JavaScriptObject initialValuesJS) /*-{
        return form.editNewRecord(initialValuesJS);
    }-*/;

	public void editSelectedData(ListGrid grid) {
		prepareFormForEditing(getForm(), false, grid.getSelectedRecord());
		getForm().editSelectedData(grid);
	}

	public void editRecord(Record record) {
		prepareFormForEditing(getForm(), false, record);
		getForm().editRecord(record);
	}

	/**
	 * Overwrite this method to customize the Form, before the window appears.
	 *
	 * @param form the form
	 * @param newRecord true, if the data to be edit is a new record, false otherwise
	 * @param data the data to use for editing
	 */
	protected void customizeFormBeforeEdit(DynamicForm form, boolean newRecord, Record data) {

	}

	private void prepareFormForEditing(DynamicForm form, boolean newRecord, Record data) {
		if (!newRecord && form.getDataSource() != null) {
			for (DataSourceField field : form.getDataSource().getFields()) {
				if (field.getPrimaryKey()) {
					try {
						FormItem formItem = form.getField(field.getName());
						if (formItem != null) {
							formItem.setCanEdit(false);
						}
					} catch (Exception e) {
						// TODO das muss so lange drin bleiben, bis:
						// http://forums.smartclient.com/showthread.php?t=25234
						// behoben ist
					}

				}
			}
		}
		customizeFormBeforeEdit(form, newRecord, data);
	}

	/**
	 * Override this method to do custom initialization of the Form (e.g.
	 * setAttribute custom field editors)
	 *
	 * @param form the Form
	 */
	protected void customizeForm(DynamicForm form) {

	}

	/**
	 * Override this method to do custom initialization of window (e.g.
	 * setWidth)
	 *
	 * @param window the window
	 */
	protected void customizeWindow(Window window) {
	}

	/**
	 * Override this method to do custom initialization of the layout (e.g. add
	 * other Items)
	 *
	 * @param layout the layout
	 * @return the customized layout
	 */
	protected Layout customizeLayout(VLayout layout) {
		return layout;
	}

	public void setTitle(String title) {
		this.title = title;
		if (window != null) {
			window.setTitle(title);
		}
		if (section != null) {
			section.setTitle(title);
		}
	}

	/**
	 * @return Liefert das SimplePanel innerhalb einer Section zurück, diese
	 *         Section kann dann in ein SectionStack eingebunden werden
	 */
	public SectionStackSection getSection() {
		if (section == null) {
			section = new SectionStackSection();
			section.setID(id + "_section");
			section.setExpanded(true);
			section.addItem(getSimplePanel());
			if (title != null) {
				section.setTitle(title);
			}
		}
		return section;
	}

	/**
	 * @param title der Titel der Section
	 * @return Liefert das SimplePanel innerhalb einer Section zurück, diese
	 *         Section kann dann in ein SectionStack eingebunden werden
	 */
	public SectionStackSection getSection(String title) {
		setTitle(title);
		return section;
	}

	/** @return Liefert den Editor mit einer Überschrift zurück. */
	public SectionStack getTitledPanel() {
		if (sectionStack == null) {
			sectionStack = new SectionStack();
			section.setID(id + "_titled_panel");
			sectionStack.setHeight100();
			sectionStack.setWidth100();
			section = getSection();
			section.setCanCollapse(false);
			sectionStack.addSection(section);
		}
		return sectionStack;
	}

	/**
	 * Liefert den Editor mit einer Überschrift zurück.
	 *
	 * @param title Die Überschrift
	 * @return ein SectionStack
	 */
	public SectionStack getTitledPanel(String title) {
		setTitle(title);
		return getTitledPanel();
	}

	public boolean isReuseWindow() {
		return reuseWindow;
	}

	public void setReuseWindow(boolean reuseWindow) {
		this.reuseWindow = reuseWindow;
	}

	/** @return the saveButton */
	public ToolStripButton getSaveButton() {
		return saveButton;
	}

	/** @return the cancelButton */
	public ToolStripButton getCancelButton() {
		return cancelButton;
	}

}
