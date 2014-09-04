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

import com.github.smartgwt_ext.frontend.charting.configuration.appearance.Axis;
import com.github.smartgwt_ext.frontend.charting.configuration.appearance.Grid;
import com.github.smartgwt_ext.frontend.charting.configuration.features.Legend;
import com.google.gwt.core.client.JsArrayString;

/**
 * @author Andreas Berger
 * @created 21.02.2013
 */
public class Options extends BaseConfiguration<Options> {

	protected Options() {
	}

	public static native final Options create()/*-{
        return {};
    }-*/;

	/**
	 * The default colorscheme. When there are > 5 series, additional colors are generated..
	 * default ['#00A8F0', '#C0D800', '#CB4B4B', '#4DA74D', '#9440ED']
	 *
	 * @return
	 */
	public final native JsArrayString getColors()/*-{
        return this.colors;
    }-*/;

	/**
	 * The default colorscheme. When there are > 5 series, additional colors are generated..
	 * default ['#00A8F0', '#C0D800', '#CB4B4B', '#4DA74D', '#9440ED']
	 *
	 * @param colors
	 */
	public final native Options setColors(JsArrayString colors)/*-{
        this.colors = colors;
        return this;
    }-*/;

	/**
	 * Background color for excanvas clipping.
	 * default '#FFFFFF'
	 *
	 * @return
	 */
	public final native String getIeBackgroundColor()/*-{
        return this.ieBackgroundColor;
    }-*/;

	/**
	 * Background color for excanvas clipping.
	 * default '#FFFFFF'
	 *
	 * @param ieBackgroundColor
	 */
	public final native Options setIeBackgroundColor(String ieBackgroundColor)/*-{
        this.ieBackgroundColor = ieBackgroundColor;
        return this;
    }-*/;

	/**
	 * The graph's title.
	 * default null
	 *
	 * @return
	 */
	public final native String getTitle()/*-{
        return this.title;
    }-*/;

	/**
	 * The graph's title.
	 * default null
	 *
	 * @param title
	 */
	public final native Options setTitle(String title)/*-{
        this.title = title;
        return this;
    }-*/;

	/**
	 * The graph's subtitle.
	 * default null
	 *
	 * @return
	 */
	public final native String getSubtitle()/*-{
        return this.subtitle;
    }-*/;

	/**
	 * The graph's subtitle.
	 * default null
	 *
	 * @param subtitle
	 */
	public final native Options setSubtitle(String subtitle)/*-{
        this.subtitle = subtitle;
        return this;
    }-*/;

	/**
	 * size of the 'fake' shadow.
	 * default 4
	 *
	 * @return
	 */
	public final native int getShadowSize()/*-{
        return this.shadowSize;
    }-*/;

	/**
	 * size of the 'fake' shadow.
	 * default 4
	 *
	 * @param shadowSize
	 */
	public final native Options setShadowSize(int shadowSize)/*-{
        this.shadowSize = shadowSize;
        return this;
    }-*/;

	/**
	 * default series type.
	 * default null
	 *
	 * @return
	 */
	public final native String getDefaultType()/*-{
        return this.defaultType;
    }-*/;

	/**
	 * default series type.
	 * default null
	 *
	 * @param defaultType
	 */
	public final native Options setDefaultType(String defaultType)/*-{
        this.defaultType = defaultType;
        return this;
    }-*/;

	/**
	 * wether to draw the text using HTML or on the canvas.
	 * default true
	 *
	 * @return
	 */
	public final native boolean isHtmlText()/*-{
        return this.HtmlText;
    }-*/;

	/**
	 * wether to draw the text using HTML or on the canvas.
	 * default true
	 *
	 * @param HtmlText
	 */
	public final native Options setHtmlText(boolean HtmlText)/*-{
        this.HtmlText = HtmlText;
        return this;
    }-*/;

	/**
	 * default font color.
	 * default '#545454'
	 *
	 * @return
	 */
	public final native String getFontColor()/*-{
        return this.fontColor;
    }-*/;

	/**
	 * default font color.
	 * default '#545454'
	 *
	 * @param fontColor
	 */
	public final native Options setFontColor(String fontColor)/*-{
        this.fontColor = fontColor;
        return this;
    }-*/;

	/**
	 * canvas' text font size.
	 * default 7.5
	 *
	 * @return
	 */
	public final native float getFontSize()/*-{
        return this.fontSize;
    }-*/;

	/**
	 * canvas' text font size.
	 * default 7.5
	 *
	 * @param fontSize
	 */
	public final native Options setFontSize(float fontSize)/*-{
        this.fontSize = fontSize;
        return this;
    }-*/;

	/**
	 * resolution of the graph, to have printer-friendly graphs !.
	 * default 1
	 *
	 * @return
	 */
	public final native int getResolution()/*-{
        return this.resolution;
    }-*/;

	/**
	 * resolution of the graph, to have printer-friendly graphs !.
	 * default 1
	 *
	 * @param resolution
	 */
	public final native Options setResolution(int resolution)/*-{
        this.resolution = resolution;
        return this;
    }-*/;

	/**
	 * whether to preprocess data for floats (ie. if input is string).
	 * default true
	 *
	 * @return
	 */
	public final native boolean isParseFloat()/*-{
        return this.parseFloat;
    }-*/;

	/**
	 * whether to preprocess data for floats (ie. if input is string).
	 * default true
	 *
	 * @param parseFloat
	 */
	public final native Options setParseFloat(boolean parseFloat)/*-{
        this.parseFloat = parseFloat;
        return this;
    }-*/;

	/**
	 * preventDefault by default for mobile events.  Turn off to enable scroll..
	 * default true
	 *
	 * @return
	 */
	public final native boolean isPreventDefault()/*-{
        return this.preventDefault;
    }-*/;

	/**
	 * preventDefault by default for mobile events.  Turn off to enable scroll..
	 * default true
	 *
	 * @param preventDefault
	 */
	public final native Options setPreventDefault(boolean preventDefault)/*-{
        this.preventDefault = preventDefault;
        return this;
    }-*/;

	/**
	 * Die untere X-Achse.
	 *
	 * @return
	 */
	public final native Axis getXaxis()/*-{
        return this.xaxis;
    }-*/;

	public final Axis createXaxis() {
		Axis obj = Axis.create();
		setXaxis(obj);
		return obj;
	}

	/**
	 * Die untere X-Achse.
	 *
	 * @param xaxis
	 */
	public final native Options setXaxis(Axis xaxis)/*-{
        this.xaxis = xaxis;
        return this;
    }-*/;

	/**
	 * Die Obere X-Achse (bei vertikaler Darstellung)
	 *
	 * @return
	 */
	public final native Axis getX2axis()/*-{
        return this.x2axis;
    }-*/;

	public final Axis createX2axis() {
		Axis obj = Axis.create();
		setX2axis(obj);
		return obj;
	}

	/**
	 * Die Obere X-Achse (bei vertikaler Darstellung)
	 *
	 * @param x2axis
	 */
	public final native Options setX2axis(Axis x2axis)/*-{
        this.x2axis = x2axis;
        return this;
    }-*/;

	/**
	 * Die Linke Y-Achse.
	 *
	 * @return
	 */
	public final native Axis getYaxis()/*-{
        return this.yaxis;
    }-*/;

	public final Axis createYaxis() {
		Axis obj = Axis.create();
		setYaxis(obj);
		return obj;
	}

	/**
	 * Die Linke Y-Achse.
	 *
	 * @param yaxis
	 */
	public final native Options setYaxis(Axis yaxis)/*-{
        this.yaxis = yaxis;
        return this;
    }-*/;

	/**
	 * Die rechte Y-Achse.
	 *
	 * @return
	 */
	public final native Axis getY2axis()/*-{
        return this.y2axis;
    }-*/;

	public final Axis createY2axis() {
		Axis obj = Axis.create();
		setY2axis(obj);
		return obj;
	}

	/**
	 * Die rechte Y-Achse.
	 *
	 * @param y2axis
	 */
	public final native Options setY2axis(Axis y2axis)/*-{
        this.y2axis = y2axis;
        return this;
    }-*/;

	/**
	 * Optinen für das Gitternetz.
	 *
	 * @return
	 */
	public final native Grid getGridConfiguration()/*-{
        return this.grid;
    }-*/;

	public final Grid createGridConfiguration() {
		Grid obj = Grid.create();
		setGridConfiguration(obj);
		return obj;
	}

	/**
	 * Optinen für das Gitternetz.
	 *
	 * @param grid
	 */
	public final native Options setGridConfiguration(Grid grid)/*-{
        this.grid = grid;
        return this;
    }-*/;

	/**
	 * Die Einstellung für die Legende.
	 *
	 * @return
	 */
	public final native Legend getLegendConfiguration()/*-{
        return this.legend;
    }-*/;

	public final Legend createLegendConfiguration() {
		Legend obj = Legend.create();
		setLegendConfiguration(obj);
		return obj;
	}

	/**
	 * Die Einstellung für die Legende.
	 *
	 * @param legend
	 */
	public final native void setLegendConfiguration(Legend legend)/*-{
        this.legend = legend;
    }-*/;
}
