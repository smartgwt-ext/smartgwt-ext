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
import com.github.smartgwt_ext.frontend.server_binding.datasource.Relationship;
import com.github.smartgwt_ext.frontend.server_binding.i18n.ServerBindingStrings;
import com.github.smartgwt_ext.frontend.server_binding.layer.JsBase;
import com.github.smartgwt_ext.frontend.server_binding.widgets.data.DataSelectionListenerAdapter;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.rpc.RPCManager;
import com.smartgwt.client.types.DragDataAction;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.RecordDropEvent;
import com.smartgwt.client.widgets.grid.events.RecordDropHandler;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

import java.util.ArrayList;
import java.util.List;

/**
 * @param <S> der Typ des selektierten Elements
 * @param <A> der Typ der Zuweisung
 * @param <D> der Typ des dragged Elements
 * @author Andreas Berger
 * @created 25.10.12 - 14:48
 */
public class GenericAssignmentGrid<S extends JavaScriptObject, A extends JavaScriptObject, D extends JavaScriptObject>
		extends GenericGrid<A> implements RecordDropHandler {

	private Record currentSelectedRecord;
	private List<ToolStripButton> assignButtons;
	private GenericGrid<S> selectionGrid;
	private GenericGrid<D> dragOutGrid;
	private String assignButtonText = ServerBindingStrings.$.BUTTON_ASSIGN();
	private String assignButtonIcon = GWT.getModuleBaseURL() + "/images/page_go.png";
	private String selectionField;
	private String dragField;
	private String selectionIdField;
	private DataSource selectionDatasource;

	public GenericAssignmentGrid(String datasourceId, String owningDatasourceId, String id) {
		super(datasourceId, id);
		selectionDatasource = DataSource.get(owningDatasourceId);
		Relationship relationship = Relationship.create(getDataSource(), selectionDatasource);
		selectionField = relationship.getParentIdField();
		selectionIdField = relationship.getIdField();
		init();
	}

	public GenericAssignmentGrid(String datasourceId, String owningDatasourceId, String selectionField,
			String selectionIdField, String id) {
		super(datasourceId, id);
		this.selectionField = selectionField;
		this.selectionIdField = selectionIdField;
		selectionDatasource = DataSource.get(owningDatasourceId);
		init();
	}

	protected void init() {
		setCanAdd(false);

		setEmptyMessage(ServerBindingStrings.$.EMPTY_ASSIGNMENT_TABLE());

		setCanAcceptDroppedRecords(true);
		addRecordDropHandler(this);
		setAutoFetchData(false);

		setDisabled(true);
		hideButton(getReloadButton(), true);
	}

	@Override
	public void editNewRecord(Record data) {
		if (getCurrentSelectedRecord() == null) {
			return;
		}
		super.editNewRecord(data);
	}

	public final void selectElement(Record selectedRecord) {
		if (assignButtons != null) {
			for (ToolStripButton assignButton : assignButtons) {
				disableButton(assignButton, selectedRecord == null);
			}
		}
		disableButton(getNewButton(), selectedRecord == null);
		if (selectedRecord == currentSelectedRecord) {
			return;
		}
		boolean isQueuing = RPCManager.startQueue();
		try {
			currentSelectedRecord = selectedRecord;
			onUpdateSelection(JsBase.<S>transformRecord(selectedRecord), selectedRecord);
			if (selectedRecord == null) {
				setDisabled(true);
				hideButton(getReloadButton(), true);
			} else {
				fetchData(createFilterCriteria(
						selectedRecord,
						selectionField,
						selectionIdField));
				setDisabled(false);
				hideButton(getReloadButton(), false);
			}
		} finally {
			if (!isQueuing) {
				RPCManager.sendQueue();
			}
		}
	}

	protected Criteria createFilterCriteria(
			Record selectedRecord,
			String selectionField,
			String idField
	) {
		Criteria crit = new Criteria();
		copy(selectedRecord.getJsObj(), idField, crit.getJsObj(), selectionField);
		return crit;
	}

	private native void copy(JavaScriptObject source, String sourceField, JavaScriptObject target,
			String targetField)/*-{
        target[targetField] = source[sourceField];
    }-*/;

	@Override
	protected void onReload() {
		if (currentSelectedRecord == null) {
			return;
		}
		super.onReload();
	}

	/**
	 * diese Methode kann ueschrieben werden um z.B. den Titel zu aktuallisieren
	 *
	 * @param selectedElement das selektierte Element
	 * @param selectedRecord das selektierte Record
	 */
	protected void onUpdateSelection(S selectedElement, Record selectedRecord) {
	}

	public void setAssignButtonTitle(String title) {
		assignButtonText = title;
		if (assignButtons == null) {
			return;
		}
		for (ToolStripButton assignButton : assignButtons) {
			assignButton.setTitle(title);
		}
	}

	public void setAssignButtonIcon(String icon) {
		assignButtonIcon = icon;
		if (assignButtons == null) {
			return;
		}
		for (ToolStripButton assignButton : assignButtons) {
			assignButton.setIcon(icon);
		}
	}

	/**
	 * Bereitet den Explorer vor, aus dem die Elemente in diese
	 * AssignmentTabelle gezogen werden kÃ¶nnen.
	 *
	 * @param grid ein GenericDtoExplorer
	 * @param filterElement2 wenn != null, dann wird auf dem DragSourceGrid ein Listender
	 * initialisiert, der im SelectionSourceGrid die zugewiesenen
	 * Element selectiert. filterElement2 definiert hierbei den Key
	 * fÃ¼r die DB-Abfrage.
	 */
	public void initDragOutSource(final GenericGrid<D> grid) {
		dragOutGrid = grid;
		initAdditionalDragOutSource(grid);
	}

	/**
	 * ErmÃ¶glicht es ein zweiten Explorer fÃ¼r das DragOut zu initialisieren.
	 * Hier ist vor allem die Methode
	 * {@link #getDragOutDto(de.evucom.basegwt.client.dto.JsBase)} von relevanz
	 * da mit dieser Methode Ã¼ber ein DTO navigiert werden kann um das richtige
	 * DTO zurÃ¼ck zu liefern. Dem Explorer wird auch der Assign-Button
	 * zugewiesen.
	 *
	 * @param dragOutGrid der zu initialisierende Explorer
	 */
	public void initAdditionalDragOutSource(final GenericGrid<?> dragOutGrid) {
		dragOutGrid.setCanDragRecordsOut(true);
		dragOutGrid.setDragDataAction(DragDataAction.COPY);
		addDropAcceptFromExplorer(dragOutGrid);
		if (assignButtons == null) {
			assignButtons = new ArrayList<ToolStripButton>();
		}
		ToolStripButton assignButton = dragOutGrid.addButton(assignButtonText,
				assignButtonIcon, getID() + "_assign_from_" + dragOutGrid.getID(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				assignSelectedElements(dragOutGrid);
			}
		});
		assignButton.setTitle(assignButtonText);
		disableButton(assignButton, currentSelectedRecord == null);
		assignButtons.add(assignButton);
	}

	/**
	 * Bereitet den Explorer vor, aus dem ein Element als Auswahl in dieser
	 * AssignmentTabelle gesetzt werden kann.
	 *
	 * @param grid ein GenericDtoExplorer
	 */
	public void initSelectionSource(GenericGrid<S> grid) {
		selectionGrid = grid;
		grid.addDataSelectionListener(new DataSelectionListenerAdapter<S>() {
			@Override
			public void onSelection(S element, Record record) {
				selectElement(record);
			}
		});
	}

	@Override
	public void onRecordDrop(RecordDropEvent event) {
		if (event.getSourceWidget() == this) {
			return;
		}
		event.cancel();
		assignSelectedElements((ListGrid) event.getSourceWidget());
	}

	protected void assignSelectedElements(ListGrid listGrid) {
		if (currentSelectedRecord == null) {
			return;
		}
		Relationship relationship2 = Relationship.create(getDataSource(), listGrid.getDataSource());
		for (ListGridRecord record : listGrid.getSelectedRecords()) {
			ListGridRecord targetRecord = new ListGridRecord();
			initNewRecord(relationship2, record, targetRecord);
			if (hasEditableFields()) {
				startEditingNew(targetRecord);
			} else {
				addData(targetRecord);
			}
		}
	}

	@Override
	protected ListGridRecord initNewRecord() {
		ListGridRecord record = super.initNewRecord();
		if (currentSelectedRecord == null) {
			return record;
		}
		initNewRecord(null, null, record);
		return record;
	}

	private void initNewRecord(
			Relationship droppedRelationship,
			ListGridRecord droppedRecord,
			ListGridRecord targetRecord
	) {
		String droppedField = null;
		DataSource droppedDatasoure = null;
		if (droppedRelationship != null && droppedRecord != null) {
			droppedField = dragField == null ? droppedRelationship.getParentIdField() : dragField;
			droppedDatasoure = droppedRelationship.getParentDS();
			String droppedIdField = droppedRelationship.getIdField();
			if (droppedField != null && droppedIdField != null) {
				targetRecord.setAttribute(droppedField, droppedRecord.getAttributeAsObject(droppedIdField));
			}
		}
		copy(currentSelectedRecord.getJsObj(), selectionIdField, targetRecord.getJsObj(), selectionField);

		initNewRecord(targetRecord,
				currentSelectedRecord, selectionField, selectionDatasource,
				droppedRecord, droppedField, droppedDatasoure);
	}

	protected void initNewRecord(
			Record targetRecord,

			Record selectedRecord,
			String selectionField,
			DataSource selectionDatasource,

			Record droppedRecord,
			String droppedField,
			DataSource droppedDatasoure

	) {
		for (DataSourceField dsField : getDataSource().getFields()) {
			String valueXPath = dsField.getValueXPath();
			if (valueXPath == null) {
				continue;
			}
			if (valueXPath.startsWith("/")) {
				valueXPath = valueXPath.substring(1);
			}
			if (droppedField != null && droppedRecord != null && droppedDatasoure != null) {
				if (addDelegateValue(droppedRecord, targetRecord, dsField.getName(), valueXPath, droppedField,
						droppedDatasoure.getFields())) {
					continue;
				}
			}
			if (addDelegateValue(selectedRecord, targetRecord, dsField.getName(), valueXPath, selectionField,
					selectionDatasource.getFields())) {
				continue;
			}
		}
	}

	private static boolean addDelegateValue(
			Record sourceRecord,
			Record targetRecord,
			String fieldName,
			String valueXPath,
			String parentId,
			DataSourceField... sourceDataSourceFields
	) {
		String prefix = parentId + "/";
		if (!valueXPath.startsWith(prefix)) {
			return false;
		}
		String name = valueXPath.substring(prefix.length());

		if (!name.contains("/")) {
			targetRecord.setAttribute(fieldName, sourceRecord.getAttributeAsObject(name));
			return true;
		}

		for (DataSourceField field : sourceDataSourceFields) {
			String xpath = field.getValueXPath();
			if (xpath == null) {
				continue;
			}
			if (xpath.startsWith("/")) {
				xpath = xpath.substring(1);
			}
			if (name.equals(xpath)) {
				targetRecord.setAttribute(fieldName, sourceRecord.getAttributeAsObject(field.getName()));
				return true;
			}
		}
		return false;
	}

	public GenericGrid<S> getSelectionGrid() {
		return selectionGrid;
	}

	public GenericGrid<D> getDragOutGrid() {
		return dragOutGrid;
	}

	public Record getCurrentSelectedRecord() {
		return currentSelectedRecord;
	}

	public S getCurrentSelectedElement() {
		return JsBase.<S>transformRecord(currentSelectedRecord);
	}

	public void setDragField(String dragField) {
		this.dragField = dragField;
	}

	public void setSelectionField(String selectionField) {
		this.selectionField = selectionField;
	}
}
