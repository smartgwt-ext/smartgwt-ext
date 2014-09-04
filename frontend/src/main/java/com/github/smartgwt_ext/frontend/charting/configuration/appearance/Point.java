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

/**
 * @author Andreas Berger
 * @created 22.02.13 - 10:55
 */
public class Point extends JavaScriptObject {
	protected Point() {
	}

	public static native final Point create()/*-{
        return {};
    }-*/;

	/**
	 * setting to true will show points, false will hide.
	 * default false
	 *
	 * @return
	 */
	public final native boolean isShow()/*-{
        return this.show;
    }-*/;

	/**
	 * setting to true will show points, false will hide.
	 * default false
	 *
	 * @param show
	 */
	public final native Point setShow(boolean show)/*-{
        this.show = show;
        return this;
    }-*/;

	/**
	 * point radius (pixels).
	 * default 3
	 *
	 * @return
	 */
	public final native int getRadius()/*-{
        return this.radius;
    }-*/;

	/**
	 * point radius (pixels).
	 * default 3
	 *
	 * @param radius
	 */
	public final native Point setRadius(int radius)/*-{
        this.radius = radius;
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
	public final native Point setLineWidth(int lineWidth)/*-{
        this.lineWidth = lineWidth;
        return this;
    }-*/;

	/**
	 * true to fill the points with a color, false for (transparent) no fill.
	 * default true
	 *
	 * @return
	 */
	public final native boolean isfill()/*-{
        return this.fill;
    }-*/;

	/**
	 * true to fill the points with a color, false for (transparent) no fill.
	 * default true
	 *
	 * @param fill
	 */
	public final native Point setFill(boolean fill)/*-{
        this.fill = fill;
        return this;
    }-*/;

	/**
	 * fill color.  Null to use series color..
	 * default '#FFFFFF'
	 *
	 * @return
	 */
	public final native FillColor getFillColor()/*-{
        return this.fillColor;
    }-*/;

	/**
	 * fill color.  Null to use series color..
	 * default '#FFFFFF'
	 *
	 * @param fillColor
	 */
	public final native Point setFillColor(FillColor fillColor)/*-{
        this.fillColor = fillColor;
        return this;
    }-*/;

	/**
	 * opacity of color inside the points.
	 * default 1
	 *
	 * @return
	 */
	public final native float getFillOpacity()/*-{
        return this.fillOpacity;
    }-*/;

	/**
	 * opacity of color inside the points.
	 * default 1
	 *
	 * @param fillOpacity
	 */
	public final native Point setFillOpacity(float fillOpacity)/*-{
        this.fillOpacity = fillOpacity;
        return this;
    }-*/;

	/**
	 * override for points hit radius.
	 * default null
	 *
	 * @return
	 */
	public final native int getHitRadius()/*-{
        return this.hitRadius;
    }-*/;

	/**
	 * override for points hit radius.
	 * default null
	 *
	 * @param hitRadius
	 */
	public final native Point setHitRadius(int hitRadius)/*-{
        this.hitRadius = hitRadius;
        return this;
    }-*/;

}
