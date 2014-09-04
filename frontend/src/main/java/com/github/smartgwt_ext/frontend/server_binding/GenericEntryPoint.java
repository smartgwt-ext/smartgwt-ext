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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.smartgwt.client.SmartGwtEntryPoint;
import com.smartgwt.client.data.DataSource;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Andreas Berger
 * @created 24.10.12 - 23:44
 */
public abstract class GenericEntryPoint extends SmartGwtEntryPoint {

	private static final Logger logger = Logger.getLogger(GenericEntryPoint.class.getName());

	@Override
	public final void onModuleLoad() {
		super.onModuleLoad();
		GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
			@Override
			public void onUncaughtException(Throwable e) {
				if (!GWT.isScript()) {
					Window.alert("Uncaught exception escaped : " + e.getClass().getName() + "\n" + e.getMessage() +
							"\nSee the Development console log for details." +
							"\nRegister a GWT.setUncaughtExceptionHandler(..) for custom uncaught exception handling."
					);
				} else {
					logger.log(Level.SEVERE, "", e);
				}
				GWT.log("Uncaught exception escaped", e);
			}
		});
		initApplication();
	}

	public abstract void initApplication();

	/**
	 * Changes the DataUrl for the DataSources with the specified IDs.
	 *
	 * @param url the URL to set
	 * @param dsIds the IDs of the DataSources
	 */
	protected void changeDataUrl(String url, String... dsIds) {
		for (String id : dsIds) {
			DataSource.get(id).setAttribute("dataURL", url, true);
		}
	}
}
