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
 * @created 22.02.13 - 10:32
 */
public class Line extends JavaScriptObject {
	protected Line() {
	}

	public static native final Line create()/*-{
        return {};
    }-*/;

	/**
	 * setting to true will show lines, false will hide.
	 * default false
	 *
	 * @return
	 */
	public final native boolean isShow()/*-{
        return this.show;
    }-*/;

	/**
	 * setting to true will show lines, false will hide.
	 * default false
	 *
	 * @param show
	 */
	public final native Line setShow(boolean show)/*-{
        this.show = show;
        return this;
    }-*/;

	/**
	 * line width in pixels.
	 * default 2
	 *
	 * @return
	 */
	public final native int getLineWidth()/*-{
        return this.lineWidth;
    }-*/;

	/**
	 * line width in pixels.
	 * default 2
	 *
	 * @param lineWidth
	 */
	public final native Line setLineWidth(int lineWidth)/*-{
        this.lineWidth = lineWidth;
        return this;
    }-*/;

	/**
	 * true to fill the area from the line to the x axis, false for (transparent) no fill.
	 * default false
	 *
	 * @return
	 */
	public final native boolean isFill()/*-{
        return this.fill;
    }-*/;

	/**
	 * true to fill the area from the line to the x axis, false for (transparent) no fill.
	 * default false
	 *
	 * @param fill
	 */
	public final native Line setFill(boolean fill)/*-{
        this.fill = fill;
        return this;
    }-*/;

	/**
	 * draw a border around the fill.
	 * default false
	 *
	 * @return
	 */
	public final native boolean isFillBorder()/*-{
        return this.fillBorder;
    }-*/;

	/**
	 * draw a border around the fill.
	 * default false
	 *
	 * @param fillBorder
	 */
	public final native Line setFillBorder(boolean fillBorder)/*-{
        this.fillBorder = fillBorder;
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
	public final native Line setFillColor(FillColor fillColor)/*-{
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
	public final native Line setFillOpacity(float fillOpacity)/*-{
        this.fillOpacity = fillOpacity;
        return this;
    }-*/;

	/**
	 * draw steps.
	 * default false
	 *
	 * @return
	 */
	public final native boolean isSteps()/*-{
        return this.steps;
    }-*/;

	/**
	 * draw steps.
	 * default false
	 *
	 * @param steps
	 */
	public final native Line setSteps(boolean steps)/*-{
        this.steps = steps;
        return this;
    }-*/;

	/**
	 * setting to true will show stacked lines, false will show normal lines.
	 * default false
	 *
	 * @return
	 */
	public final native boolean isStacked()/*-{
        return this.stacked;
    }-*/;

	/**
	 * setting to true will show stacked lines, false will show normal lines.
	 * default false
	 *
	 * @param stacked
	 */
	public final native Line setStacked(boolean stacked)/*-{
        this.stacked = stacked;
        return this;
    }-*/;

}
