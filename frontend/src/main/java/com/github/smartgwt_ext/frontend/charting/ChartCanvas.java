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
package com.github.smartgwt_ext.frontend.charting;

import com.github.smartgwt_ext.frontend.charting.configuration.Options;
import com.github.smartgwt_ext.frontend.charting.configuration.Series;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.smartgwt.client.core.Function;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.util.JSOHelper;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.events.DrawEvent;
import com.smartgwt.client.widgets.events.DrawHandler;
import com.smartgwt.client.widgets.events.ResizedEvent;
import com.smartgwt.client.widgets.events.ResizedHandler;

/**
 * Eine Smartgwt-Integration von flotr2 (http://www.humblesoftware.com/flotr2/).
 *
 * @author Andreas Berger
 * @created 21.02.2013
 */
public class ChartCanvas extends HTMLPane {

	private String wrapperId;
	private JsArray<Series> data;
	private Options options;

	public ChartCanvas() {
		wrapperId = SC.generateID();
		data = JSOHelper.createJavaScriptArray().cast();
		options = Options.create();
		setWidth100();
		setHeight100();
		setContents("<div id=\"" + wrapperId + "\" style=\"width:100%;height:100%;\"></div>");
		setOverflow(Overflow.HIDDEN);
		addResizedHandler(new ResizedHandler() {

			@Override
			public void onResized(ResizedEvent event) {
				redrawChart();

			}
		});
		doOnRender(new Function() {

			@Override
			public void execute() {
				redrawChart();
			}
		});
		addDrawHandler(new DrawHandler() {

			@Override
			public void onDraw(DrawEvent event) {
				redrawChart();
			}
		});
	}

	public void setData(JsArray<Series> data) {
		this.data = data;
	}

	/**
	 * Die Daten
	 *
	 * @param series
	 */
	public void addData(Series series) {
		data.push(series);
	}

	/**
	 * Die Daten
	 *
	 * @return
	 */
	public JsArray<Series> getData() {
		return data;
	}

	/**
	 * Die Optionen des Charts.
	 *
	 * @return
	 */
	public Options getOptions() {
		return options;
	}

	public Options createOptions() {
		return options = Options.create();
	}

	/**
	 * Die Optionen des Charts.
	 *
	 * @param options
	 */
	public void setOptions(Options options) {
		this.options = options;
	}

	/** Zeichnet den Chart erneut */
	public void redrawChart() {
		Element elem = DOM.getElementById(wrapperId);
		if (elem != null) {
			if (SC.isIE()) {
				elem.setInnerHTML("");
			}
			drawChart(elem, data, options);
		}
	}

	private native void drawChart(Element ctnr, JsArray<Series> data, Options options)/*-{
        $wnd.Flotr.draw(ctnr, data, options);
    }-*/;

}
