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

package com.github.smartgwt_ext.frontend.charting.configuration.graphs;

import com.github.smartgwt_ext.frontend.charting.configuration.appearance.FillColor;
import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author Andreas Berger
 * @created 22.02.13 - 10:12
 */
public class Bar extends JavaScriptObject {

	protected Bar() {
	}

	public static native final Bar create()/*-{
        return {};
    }-*/;

	/**
	 * setting to true will show bars, false will hide.
	 * default false
	 *
	 * @return
	 */
	public final native boolean isShow()/*-{
        return this.show;
    }-*/;

	/**
	 * setting to true will show bars, false will hide.
	 * default false
	 *
	 * @param show
	 */
	public final native Bar setShow(boolean show)/*-{
        this.show = show;
        return this;
    }-*/;

	/**
	 * in pixels.
	 * default 2
	 *
	 * @return
	 */
	public final native int getLineWidth()/*-{
        return this.lineWidth;
    }-*/;

	/**
	 * in pixels.
	 * default 2
	 *
	 * @param lineWidth
	 */
	public final native Bar setLineWidth(int lineWidth)/*-{
        this.lineWidth = lineWidth;
        return this;
    }-*/;

	/**
	 * in units of the x axis.
	 * default 1
	 *
	 * @return
	 */
	public final native float getBarWidth()/*-{
        return this.barWidth;
    }-*/;

	/**
	 * in units of the x axis.
	 * default 1
	 *
	 * @param barWidth
	 */
	public final native Bar setBarWidth(float barWidth)/*-{
        this.barWidth = barWidth;
        return this;
    }-*/;

	/**
	 * true to fill the area from the line to the x axis, false for (transparent) no fill.
	 * default true
	 *
	 * @return
	 */
	public final native boolean isFill()/*-{
        return this.fill;
    }-*/;

	/**
	 * true to fill the area from the line to the x axis, false for (transparent) no fill.
	 * default true
	 *
	 * @param fill
	 */
	public final native Bar setFill(boolean fill)/*-{
        this.fill = fill;
        return this;
    }-*/;

	/**
	 * fill color.
	 * default null
	 *
	 * @return
	 */
	public final native FillColor getFillColor()/*-{
        return this.fillColor;
    }-*/;

	/**
	 * fill color.
	 * default null
	 *
	 * @param fillColor
	 */
	public final native Bar setFillColor(FillColor fillColor)/*-{
        this.fillColor = fillColor;
        return this;
    }-*/;

	/**
	 * opacity of the fill color, set to 1 for a solid fill, 0 hides the fill.
	 * default 0.4
	 *
	 * @return
	 */
	public final native float getFillOpacity()/*-{
        return this.fillOpacity;
    }-*/;

	/**
	 * opacity of the fill color, set to 1 for a solid fill, 0 hides the fill.
	 * default 0.4
	 *
	 * @param fillOpacity
	 */
	public final native Bar setFillOpacity(float fillOpacity)/*-{
        this.fillOpacity = fillOpacity;
        return this;
    }-*/;

	/**
	 * horizontal bars (x and y inverted).
	 * default false
	 *
	 * @return
	 */
	public final native boolean isHorizontal()/*-{
        return this.horizontal;
    }-*/;

	/**
	 * horizontal bars (x and y inverted).
	 * default false
	 *
	 * @param horizontal
	 */
	public final native Bar setHorizontal(boolean horizontal)/*-{
        this.horizontal = horizontal;
        return this;
    }-*/;

	/**
	 * stacked bar charts.
	 * default false
	 *
	 * @return
	 */
	public final native boolean isStacked()/*-{
        return this.stacked;
    }-*/;

	/**
	 * stacked bar charts.
	 * default false
	 *
	 * @param stacked
	 */
	public final native Bar setStacked(boolean stacked)/*-{
        this.stacked = stacked;
        return this;
    }-*/;

	/**
	 * center the bars to their x axis value.
	 * default true
	 *
	 * @return
	 */
	public final native boolean isCentered()/*-{
        return this.centered;
    }-*/;

	/**
	 * center the bars to their x axis value.
	 * default true
	 *
	 * @param centered
	 */
	public final native Bar setCentered(boolean centered)/*-{
        this.centered = centered;
        return this;
    }-*/;

	/**
	 * top padding in percent.
	 * default 0.1
	 *
	 * @return
	 */
	public final native float getTopPadding()/*-{
        return this.topPadding;
    }-*/;

	/**
	 * top padding in percent.
	 * default 0.1
	 *
	 * @param topPadding
	 */
	public final native Bar setTopPadding(float topPadding)/*-{
        this.topPadding = topPadding;
        return this;
    }-*/;

	/**
	 * groups bars together which share x value, hit not supported..
	 * default false
	 *
	 * @return
	 */
	public final native boolean isGrouped()/*-{
        return this.grouped;
    }-*/;

	/**
	 * groups bars together which share x value, hit not supported..
	 * default false
	 *
	 * @param grouped
	 */
	public final native Bar setGrouped(boolean grouped)/*-{
        this.grouped = grouped;
        return this;
    }-*/;
}
