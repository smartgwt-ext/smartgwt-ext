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

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayNumber;

/**
 * @author Andreas Berger
 * @created 22.02.13 - 09:51
 */
public class Series extends BaseConfiguration<Series> {

	protected Series() {
	}

	public static native final Series create()/*-{
        return {};
    }-*/;

	/**
	 * Die Beschriftung des Datensatzes.
	 *
	 * @return
	 */
	public final native String getLabel()/*-{
        return this.label;
    }-*/;

	/**
	 * Die Beschriftung des Datensatzes.
	 *
	 * @param label
	 */
	public final native Series setLabel(String label)/*-{
        this.label = label;
        return this;
    }-*/;

	/**
	 * Die Farbe des Datensatzes.
	 *
	 * @return
	 */
	public final native String getColor()/*-{
        return this.color;
    }-*/;

	/**
	 * Die Farbe des Datensatzes.
	 *
	 * @param color
	 */
	public final native Series setColor(String color)/*-{
        this.color = color;
        return this;
    }-*/;

	/**
	 * Die Daten.
	 *
	 * @return
	 */
	public final native JsArray<JsArrayNumber> getData()/*-{
        return this.data;
    }-*/;

	/**
	 * Die Daten.
	 *
	 * @param data
	 */
	public final native Series setData(JsArray<JsArrayNumber> data)/*-{
        this.data = data;
        return this;
    }-*/;

	public final native Series addPoint(Double x, Double y)/*-{
        this.data = this.data || $wnd.Array.create();
        this.data.push([x == null ? null : x.@java.lang.Double::doubleValue()(),
            y == null ? null : y.@java.lang.Double::doubleValue()()]);
        return this;
    }-*/;

	/**
	 * Nummer der Y-Achse. default 1
	 *
	 * @return
	 */
	public final native int getYaxis()/*-{
        return this.yaxis;
    }-*/;

	/**
	 * Nummer der Y-Achse. default 1
	 *
	 * @param yaxis
	 */
	public final native Series setYaxis(int yaxis)/*-{
        this.yaxis = yaxis;
        return this;
    }-*/;

	/**
	 * Nummer der X-Achse (für vertikale Darstellung). default 1
	 *
	 * @return
	 */
	public final native int getXaxis()/*-{
        return this.xaxis;
    }-*/;

	/**
	 * Nummer der X-Achse (für vertikale Darstellung). default 1
	 *
	 * @param xaxis
	 */
	public final native Series setXaxis(int xaxis)/*-{
        this.xaxis = xaxis;
        return this;
    }-*/;
}
