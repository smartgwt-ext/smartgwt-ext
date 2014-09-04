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

package com.github.smartgwt_ext.frontend.charting.configuration.appearance;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

/**
 * @author Andreas Berger
 * @created 22.02.13 - 12:06
 */
public class FillColor extends JavaScriptObject {
	protected FillColor() {
	}

	public static native final FillColor create()/*-{
        return {};
    }-*/;

	/**
	 * Start des Farbverlaufes. Mögliche Werte: top, left, bottom, right
	 *
	 * @return
	 */
	public final native String getStart()/*-{
        return this.start;
    }-*/;

	/**
	 * Start des Farbverlaufes. Mögliche Werte: top, left, bottom, right
	 *
	 * @param start
	 */
	public final native FillColor setStart(String start)/*-{
        this.start = start;
        return this;
    }-*/;

	/**
	 * Ende des Farbverlaufes. Mögliche Werte: top, left, bottom, right
	 *
	 * @return
	 */
	public final native String getEnd()/*-{
        return this.end;
    }-*/;

	/**
	 * Ende des Farbverlaufes. Mögliche Werte: top, left, bottom, right
	 *
	 * @param end
	 */
	public final native FillColor setEnd(String end)/*-{
        this.end = end;
        return this;
    }-*/;

	/**
	 * Die Farben für den Farbverlauf
	 *
	 * @return
	 */
	public final native JsArrayString getColors()/*-{
        return this.colors;
    }-*/;

	/**
	 * Die Farben für den Farbverlauf
	 *
	 * @param colors
	 */
	public final native FillColor setColors(JsArrayString colors)/*-{
        this.colors = colors;
        return this;
    }-*/;
}
