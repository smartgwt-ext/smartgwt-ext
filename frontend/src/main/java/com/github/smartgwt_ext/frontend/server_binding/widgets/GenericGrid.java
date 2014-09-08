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
import com.github.smartgwt_ext.frontend.server_binding.i18n.ComonMessages;
import com.github.smartgwt_ext.frontend.server_binding.i18n.ServerBindingStrings;
import com.github.smartgwt_ext.frontend.server_binding.layer.JsBase;
import com.github.smartgwt_ext.frontend.server_binding.widgets.data.DataSelectionListener;
import com.github.smartgwt_ext.frontend.server_binding.widgets.data.DataSelectionListenerCollection;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.HandlerRegistration;
import com.smartgwt.client.core.Function;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.*;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.StretchImgButton;
import com.smartgwt.client.widgets.events.*;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.*;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

import java.util.*;

/**
 * AdminGrid contains the main properties of all admin tables
 *
 * @author Andreas Berger
 */
public class GenericGrid<T extends JavaScriptObject> extends ListGrid {

	private SectionStack sectionStack;
	private SectionStackSection section;

	private ToolStripButton reloadButton;
	private final String reloadText = ServerBindingStrings.$.BUTTON_REFRESH();
	private final String reloadIcon = "[SKIN]/actions/refresh.png";

	private ToolStripButton newButton;
	private final String newText = ServerBindingStrings.$.BUTTON_ADD();
	private final String newIcon = "[SKIN]/actions/add.png";

	private ToolStripButton editButton;
	private final String editText = ServerBindingStrings.$.BUTTON_EDIT();
	private final String editIcon = "[SKIN]/actions/edit.png";

	private ToolStripButton removeButton;
	private final String removeText = ServerBindingStrings.$.BUTTON_REMOVE();
	private final String removeIcon = "[SKIN]/actions/remove.png";

	private ToolStripButton saveButton;
	private final String saveText = ServerBindingStrings.$.BUTTON_SAVE();
	private final String saveIcon = GWT.getModuleBaseURL() + "/images/save.png";

	private ToolStripButton cancelButton;
	private final String cancelText = ServerBindingStrings.$.BUTTON_CANCEL();
	private final String cancelIcon = "[SKIN]/actions/undo.png";

	private String title;

	private LayoutSpacer spacer;

	private ToolStrip toolStrip;
	private Label summary;

	private boolean editInGrid;
	private boolean canSave;
	private boolean canAdd;
	private boolean canEdit;
	private boolean canRemove;
	private boolean queueRemoves;

	private boolean hasEditableFields;

	private Set<String> dropAccepted = null;

	private Record currentSelectedRecord;

	private DataSelectionListenerCollection<T> selectionListener;
	private HandlerRegistration recordClickHandlerRegistration;

	private ListGridRecord selectedRecord;

	public GenericGrid(String datasource, String id) {
		this(DataSource.get(datasource), id);
	}

	public GenericGrid(DataSource datasource, String id) {
		setID(id);
		setWidth100();
		setHeight100();
		setDataSource(datasource);
		canAdd = true;
		canEdit = true;
		canSave = true;
		canRemove = true;
		editInGrid = true;

		setAutoFitWidthApproach(AutoFitWidthApproach.BOTH);
		setAutoFitFieldWidths(true);

		setShowAllColumns(true);
		setAutoFetchData(true);
		super.setCanEdit(true);

		setRecordEnabledProperty("recordEnabled");

		setAutoSaveEdits(false);
		setListEndEditAction(RowEndEditAction.NEXT);
		setRowEndEditAction(RowEndEditAction.NEXT);
		setEditEvent(ListGridEditEvent.DOUBLECLICK);
		setDragType(getClass().getName());

		toolStrip = new ToolStrip();
		setGridComponents(
				ListGridComponent.FILTER_EDITOR,
				ListGridComponent.HEADER,
				ListGridComponent.BODY,
				ListGridComponent.SUMMARY_ROW,
				toolStrip);

		addScrolledHandler(new ScrolledHandler() {
			@Override
			public void onScrolled(ScrolledEvent event) {
				updateSummary();
			}
		});
		addDataArrivedHandler(new DataArrivedHandler() {
			@Override
			public void onDataArrived(DataArrivedEvent event) {
				updateSummary();
			}
		});
		addDrawHandler(new DrawHandler() {

			@Override
			public void onDraw(DrawEvent event) {
				updateSummary();
			}
		});
		addEditorExitHandler(new EditorExitHandler() {
			@Override
			public void onEditorExit(EditorExitEvent event) {
				updateEditButtons();
			}
		});
		addSelectionUpdatedHandler(new SelectionUpdatedHandler() {
			@Override
			public void onSelectionUpdated(SelectionUpdatedEvent event) {
				updateSelectionState(getSelectedRecords());
			}
		});
		addRecordDoubleClickHandler(new RecordDoubleClickHandler() {
			@Override
			public void onRecordDoubleClick(RecordDoubleClickEvent event) {
				GenericGrid.this.onRecordDoubleClick(event);
			}
		});

		Map<String, ListGridField> fields = getFieldsAsMap();
		for (ListGridField field : fields.values()) {
			if (field.getCanEdit() != null && field.getCanEdit()) {
				hasEditableFields = true;
				break;
			}
		}
		setFields(fields.values().toArray(new ListGridField[fields.size()]));

		initToolStrip();
	}

	/**
	 * This method must return the fields displayed in the grid
	 *
	 * @return fields (columns)
	 */
	protected Map<String, ListGridField> getFieldsAsMap() {
		DataSource ds = getDataSource();
		Map<String, ListGridField> result = new LinkedHashMap<String, ListGridField>();
		for (DataSourceField dsField : ds.getFields()) {
			if (dsField.getHidden()) {
				continue;
			}
			ListGridField field = new ListGridField(dsField.getName());
			result.put(dsField.getName(), field);
		}
		return result;
	}

	protected void setFieldsVisible(Collection<ListGridField> fields, String... defaultOrder) {
		Set<String> visibleFields = Helper.transformToSet(defaultOrder);
		for (ListGridField field : fields) {
			field.setHidden(!visibleFields.contains(field.getName()));
		}
	}

	protected Map<String, ListGridField> sortFields(Map<String, ListGridField> fields, String... defaultOrder) {
		fields = new LinkedHashMap<String, ListGridField>(fields);
		Map<String, ListGridField> result = new LinkedHashMap<String, ListGridField>();
		for (String name : defaultOrder) {
			ListGridField listGridField = fields.remove(name);
			if (listGridField == null) {
				continue;
			}
			result.put(name, listGridField);
		}
		result.putAll(fields);
		return result;
	}

	private void initToolStrip() {
		toolStrip.setWidth100();

		reloadButton = addButton(reloadText, reloadIcon, getID() + "_reload", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onReload();
			}
		});
		newButton = addButton(newText, newIcon, getID() + "_add", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onAdd();
			}
		});
		saveButton = addButton(saveText, saveIcon, getID() + "_save", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onSave();
			}
		});
		cancelButton = addButton(cancelText, cancelIcon, getID() + "_cancel", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onCancel();
			}
		});
		editButton = addButton(editText, editIcon, getID() + "_edit", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onEdit();
			}
		});
		removeButton = addButton(removeText, removeIcon, getID() + "_remove", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onRemove();
			}
		});

		disableButton(removeButton, true);
		disableButton(editButton, true);

		updateEditState();

		toolStrip.addSeparator();
		spacer = new LayoutSpacer();
		spacer.setWidth("*");
		toolStrip.addMember(spacer);

		summary = new Label();
		summary.setID(getID() + "_summary");
		summary.setAutoFit(true);
		summary.setPadding(5);
		toolStrip.addMember(summary);
	}

	protected void onAdd() {
		editNewRecord();
	}

	private void onRecordDoubleClick(RecordDoubleClickEvent event) {
		if (getSelectionAppearance() == SelectionAppearance.CHECKBOX) {
			selectedRecord = getRecord(event.getRecordNum());
		}
		if (!editInGrid && canEdit) {
			onEdit();
		}
	}

	protected void onEdit() {
		openEditorWindow().editSelectedData();
	}

	@Override
	public ListGridRecord getSelectedRecord() {
		if (getSelectionAppearance() == SelectionAppearance.CHECKBOX) {
			return selectedRecord;
		}
		return super.getSelectedRecord();
	}

	protected ListGridRecord initNewRecord() {
		return new ListGridRecord();
	}

	public void editNewRecord() {
		editNewRecord(initNewRecord());
	}

	public void editNewRecord(Record data) {
		deselectAllRecords();
		if (editInGrid) {
			startEditingNew(data);
		} else {
			openEditorWindow().editNewRecord(data);
		}
	}

	/**
	 * Durch Überschreiben dieser Methode kann definiert werden, ob immer
	 * derselbe editor zurück geliefert wird oder immer ein neuer (default)
	 *
	 * @return Der Editor für den explorer
	 */
	public GenericGridEditor openEditorWindow() {
		GenericGridEditor editor = createEditor(this);
		editor.openWindow();
		return editor;
	}

	/**
	 * Override this method to use your custom editor
	 *
	 * @param grid the grid the editor is used on
	 * @return the editor
	 */
	protected GenericGridEditor createEditor(ListGrid grid) {
		return new GenericGridEditor(grid, getID() + "_editor");
	}

	protected void onSave() {
		saveAllEdits(new Function() {
			@Override
			public void execute() {
				updateEditButtons();
			}
		});
		updateEditButtons();
	}

	protected void onCancel() {
		discardAllEdits();
		updateEditButtons();
	}

	protected void onRemove() {
		if (!anySelected()) {
			return;
		}
		if (queueRemoves) {
			markSelectionRemoved();
			return;
		}
		SC.confirm(getDeleteConfirmationText(getSelectedRecords()), new BooleanCallback() {
			@Override
			public void execute(Boolean confirmed) {
				if (confirmed != null && confirmed) {
					removeSelectedData();
				}
			}
		});
	}

	protected void onReload() {
		invalidateCache();
		refreshFields();
	}

	protected String getDeleteConfirmationText(ListGridRecord[] deleteRecords) {
		return ServerBindingStrings.$.DELETE_CONFIRMATION();
	}

	public ToolStripButton addButton(String text, String icon, String id, ClickHandler handler) {
		return addButton(text, icon, id, true, handler);
	}

	public ToolStripButton addButton(
			String text,
			String icon,
			String id,
			boolean hideTitle,
			ClickHandler handler) {
		Integer position = null;
		if (spacer != null) {
			position = toolStrip.getMemberNumber(spacer);
		}
		return Helper.addButton(toolStrip, text, icon, id, hideTitle, position, handler);
	}

	protected void updateSummary() {
		if (!isDrawn()) {
			// wir können die Anzahl der angezeigten Zeilen nur dann ermitteln,
			// wenn das Grid schon gezeichnet wurde
			return;
		}
		Integer[] range = getVisibleRows();
		int start = range[0] + 1;
		int end = range[1] + 1;
		int total = getTotalRows();
		summary.setContents(ComonMessages.$.GRID_SUMMARY(start, end, total));
	}

	/**
	 * durch Überschreiben dieser Methode kann abhängig von der Auswahl
	 * innerhalb des Grid das Verhalten von elementen (z.B. Buttons) geändert
	 * werden
	 *
	 * @param selection die ausgewählten Records
	 */
	protected void updateSelectionState(ListGridRecord[] selection) {
		updateSelectionState(selection.length);
	}

	/**
	 * durch Überschreiben dieser Methode kann abhängig von der Auswahl
	 * innerhalb des Grid das Verhalten von elementen (z.B. Buttons) geändert
	 * werden
	 *
	 * @param selectedRows die Anzahl der ausgewählten Einträge
	 */
	protected void updateSelectionState(int selectedRows) {
		disableButton(editButton, selectedRows != 1);
		disableButton(removeButton, (!canRemove || selectedRows == 0));
	}

	private void updateEditState() {
		setGenerateDoubleClickOnEnter(canEdit && !editInGrid);
		super.setCanEdit(canEdit && editInGrid);

		hideButton(newButton, !canAdd);
		hideButton(editButton, !canEdit || editInGrid);
		hideButton(removeButton, !canRemove);
		updateEditButtons();
	}

	private void updateEditButtons() {
		if (!canEdit) {
			hideButton(cancelButton, true);
			hideButton(saveButton, true);
			return;
		}
		if (isCreated()) {
			updateSelectionState(getSelectedRecords());
		}
		if (isCreated() && editInGrid && (getAutoSaveEdits() == null || getAutoSaveEdits() == false)) {
			boolean hasChanges = hasChanges();
			hideButton(cancelButton, !hasChanges);
			hideButton(saveButton, !canSave || !hasChanges);
		} else {
			hideButton(cancelButton, true);
			hideButton(saveButton, true);
		}
	}

	protected void hideButton(StretchImgButton button, boolean hidden) {
		if (button == null) {
			return;
		}
		button.setVisible(!hidden);
	}

	protected void disableButton(StretchImgButton button, boolean disable) {
		if (button == null) {
			return;
		}
		button.setDisabled(disable);
	}

	/**
	 * @return Liefert das SimplePanel innerhalb einer Section zurück, diese
	 *         Section kann dann in ein SectionStack eingebunden werden
	 */
	public SectionStackSection getSection() {
		if (section == null) {
			section = new SectionStackSection();
			section.setID(getID() + "_section");
			section.setExpanded(true);
			section.addItem(this);
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
		return getSection();
	}

	/**
	 * @return Liefert den Explorer mit einer Überschrift zurück.
	 */
	public SectionStack getTitledPanel() {
		if (sectionStack == null) {
			sectionStack = new SectionStack();
			sectionStack.setID(getID() + "_titled_panel");
			sectionStack.setHeight100();
			sectionStack.setWidth100();
			section = getSection();
			section.setCanCollapse(false);
			sectionStack.addSection(section);
		}
		return sectionStack;
	}

	/**
	 * Liefert den Explorer mit einer Überschrift zurück.
	 *
	 * @param title Die Überschrift
	 * @return ein SectionStack
	 */
	public SectionStack getTitledPanel(String title) {
		setTitle(title);
		return getTitledPanel();
	}

	@Override
	public void setCanEdit(Boolean canEdit) {
		this.canEdit = canEdit;
		updateEditState();
	}

	public void setCanRemove(boolean canRemove) {
		this.canRemove = canRemove;
		updateEditState();
	}

	public void setCanAdd(boolean canAdd) {
		this.canAdd = canAdd;
		updateEditState();
	}

	public void setCanSave(boolean canSave) {
		this.canSave = canSave;
		updateEditState();
	}

	/**
	 * Wenn true, dann werden Löschungen nicht sofort ausgeführt sondern erst
	 * beim nächsten Speichern.
	 *
	 * @param queueRemoves
	 */
	public void setQueueRemoves(boolean queueRemoves) {
		this.queueRemoves = queueRemoves;
	}

	public void setEditInGrid(boolean editInGrid) {
		this.editInGrid = editInGrid;
		updateEditState();
	}

	public ToolStripButton getReloadButton() {
		return reloadButton;
	}

	public ToolStripButton getNewButton() {
		return newButton;
	}

	public ToolStripButton getEditButton() {
		return editButton;
	}

	public ToolStripButton getRemoveButton() {
		return removeButton;
	}

	public ToolStripButton getSaveButton() {
		return saveButton;
	}

	public ToolStripButton getCancelButton() {
		return cancelButton;
	}

	public void addDropAcceptFromExplorer(GenericGrid<?> explorer) {
		if (dropAccepted == null) {
			dropAccepted = new HashSet<String>();
		}
		dropAccepted.add(explorer.getClass().getName());
		setDropTypes(dropAccepted.toArray(new String[dropAccepted.size()]));
		setCanAcceptDroppedRecords(true);
	}

	public void addDataSelectionListener(DataSelectionListener<T> listener) {
		if (selectionListener == null) {
			selectionListener = new DataSelectionListenerCollection<T>();
			initSelectionListener();
		}
		selectionListener.addListener(listener);
	}

	private void initSelectionListener() {
		if (recordClickHandlerRegistration != null || selectionListener == null) {
			return;
		}
		recordClickHandlerRegistration = addRecordClickHandler(new RecordClickHandler() {

			@Override
			public void onRecordClick(RecordClickEvent event) {
				onSelection(event.getRecord());
			}
		});
	}

	public HandlerRegistration getRecordClickHandlerRegistration() {
		return recordClickHandlerRegistration;
	}

	public void onSelection(Record[] records) {
		if (selectionListener == null) {
			return;
		}
		selectionListener.onSelection(getElements(records), records);
	}

	public void onSelection(Record record) {
		if (selectionListener == null) {
			return;
		}
		if (record == currentSelectedRecord) {
			return;
		}
		currentSelectedRecord = record;
		selectionListener.onSelection(getElement(record), record);
	}

	public boolean hasEditableFields() {
		return hasEditableFields;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
		if (section != null) {
			section.setTitle(title);
		}
	}

	public T getElement(Record r) {
		return JsBase.<T>transformRecord(getSelectedRecord());
	}

	public List<T> getElements(Record... records) {
		return JsBase.<T>transformRecords(records);
	}

	public T getSelectedElement() {
		return getElement(getSelectedRecord());
	}
}
