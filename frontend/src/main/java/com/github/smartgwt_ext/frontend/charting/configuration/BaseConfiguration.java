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

package com.github.smartgwt_ext.frontend.charting.configuration;

import com.github.smartgwt_ext.frontend.charting.configuration.features.Marker;
import com.github.smartgwt_ext.frontend.charting.configuration.features.Mouse;
import com.github.smartgwt_ext.frontend.charting.configuration.graphs.Bar;
import com.github.smartgwt_ext.frontend.charting.configuration.graphs.Line;
import com.github.smartgwt_ext.frontend.charting.configuration.graphs.Pie;
import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author Andreas Berger
 * @created 22.02.13 - 11:57
 */
public class BaseConfiguration<T extends BaseConfiguration<T>> extends JavaScriptObject {
	protected BaseConfiguration() {
	}

	/**
	 * Optionen zum Mouse-Verhalten.
	 *
	 * @return
	 */
	public final native Mouse getMouseConfiguration()/*-{
        return this.mouse;
    }-*/;

	public final Mouse createMouseConfiguration() {
		Mouse obj = Mouse.create();
		setMouseConfiguration(obj);
		return obj;
	}

	/**
	 * Optionen zum Mouse-Verhalten.
	 *
	 * @param mouse
	 */
	public final native T setMouseConfiguration(Mouse mouse)/*-{
        this.mouse = mouse;
        return this;
    }-*/;

	/**
	 * Optionen für die Balken-Darstellung.
	 *
	 * @return
	 */
	public final native Bar getBarConfiguration()/*-{
        return this.bars;
    }-*/;

	public final Bar createBarConfiguration() {
		Bar obj = Bar.create();
		setBarConfiguration(obj);
		return obj;
	}

	/**
	 * Optionen für die Balken-Darstellung.
	 *
	 * @param bars
	 */
	public final native T setBarConfiguration(Bar bars)/*-{
        this.bars = bars;
        return this;
    }-*/;

	/**
	 * Optionen für die Linien-Darstellung
	 *
	 * @return
	 */
	public final native Line getLineConfiguration()/*-{
        return this.lines;
    }-*/;

	public final Line createLineConfiguration() {
		Line obj = Line.create();
		setLineConfiguration(obj);
		return obj;
	}

	/**
	 * Optionen für die Linien-Darstellung
	 *
	 * @param lines
	 */
	public final native T setLineConfiguration(Line lines)/*-{
        this.lines = lines;
        return this;
    }-*/;

	/**
	 * optionen für die Kuchen-Darstellung
	 *
	 * @return
	 */
	public final native Pie getPieConfiguration()/*-{
        return this.pie;
    }-*/;

	public final Pie createPieConfiguration() {
		Pie obj = Pie.create();
		setPieConfiguration(obj);
		return obj;
	}

	/**
	 * optionen für die Kuchen-Darstellung
	 *
	 * @param pieOptions
	 */
	public final native T setPieConfiguration(Pie pie)/*-{
        this.pie = pie;
        return this;
    }-*/;

	/**
	 * Markierungen der Datenpunkte
	 *
	 * @return
	 */
	public final native Marker getMarkerConfiguration()/*-{
        return this.markers;
    }-*/;

	public final Marker createMarkerConfiguration() {
		Marker obj = Marker.create();
		setMarkerConfiguration(obj);
		return obj;
	}

	/**
	 * Markierungen der Datenpunkte
	 *
	 * @param markers
	 */
	public final native T setMarkerConfiguration(Marker markers)/*-{
        this.markers = markers;
        return this;
    }-*/;
}
