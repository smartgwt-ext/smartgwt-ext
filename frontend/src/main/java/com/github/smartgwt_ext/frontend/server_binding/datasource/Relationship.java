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

package com.github.smartgwt_ext.frontend.server_binding.datasource;

import com.google.gwt.core.client.JavaScriptObject;
import com.smartgwt.client.data.DataSource;

/**
 * @author Andreas Berger
 * @created 19.09.12 - 20:56
 */
public class Relationship extends JavaScriptObject {

	protected Relationship() {
	}

	// childrenProperty

	public final native boolean isFolderProperty() /*-{
        return this.isFolderProperty;
    }-*/;

	public final native String getParentIdField() /*-{
        return this.parentIdField;
    }-*/;

	public final native String getIdField() /*-{
        return this.idField;
    }-*/;

	public final native String getRootValue() /*-{
        return this.rootValue;
    }-*/;

	public final native DataSource getChildDS() /*-{
        return @com.smartgwt.client.data.DataSource::getOrCreateRef(Lcom/google/gwt/core/client/JavaScriptObject;)(this.childDS);
    }-*/;

	public final native DataSource getParentDS() /*-{
        return @com.smartgwt.client.data.DataSource::getOrCreateRef(Lcom/google/gwt/core/client/JavaScriptObject;)(this.parentDS);
    }-*/;

	public static native Relationship create(DataSource ds1, DataSource ds2) /*-{
        var datasource1 = ds1.@com.smartgwt.client.data.DataSource::getOrCreateJsObj()();
        var datasource2 = ds2.@com.smartgwt.client.data.DataSource::getOrCreateJsObj()();
        return datasource1.getTreeRelationship(datasource2);
    }-*/;

}
