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
 * @created 22.02.13 - 10:35
 */
public class Pie extends JavaScriptObject {
	protected Pie() {
	}

	public static native final Pie create()/*-{
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
	public final native Pie setShow(boolean show)/*-{
        this.show = show;
        return this;
    }-*/;

	/**
	 * in pixels.
	 * default 1
	 *
	 * @return
	 */
	public final native int getLineWidth()/*-{
        return this.lineWidth;
    }-*/;

	/**
	 * in pixels.
	 * default 1
	 *
	 * @param lineWidth
	 */
	public final native Pie setLineWidth(int lineWidth)/*-{
        this.lineWidth = lineWidth;
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
	public final native Pie setFill(boolean fill)/*-{
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
	public final native Pie setFillColor(FillColor fillColor)/*-{
        this.fillColor = fillColor;
        return this;
    }-*/;

	/**
	 * opacity of the fill color, set to 1 for a solid fill, 0 hides the fill.
	 * default 0.6
	 *
	 * @return
	 */
	public final native float getFillOpacity()/*-{
        return this.fillOpacity;
    }-*/;

	/**
	 * opacity of the fill color, set to 1 for a solid fill, 0 hides the fill.
	 * default 0.6
	 *
	 * @param fillOpacity
	 */
	public final native Pie setFillOpacity(float fillOpacity)/*-{
        this.fillOpacity = fillOpacity;
        return this;
    }-*/;

	/**
	 * the number of pixels the splices will be far from the center.
	 * default 6
	 *
	 * @return
	 */
	public final native int getExplode()/*-{
        return this.explode;
    }-*/;

	/**
	 * the number of pixels the splices will be far from the center.
	 * default 6
	 *
	 * @param explode
	 */
	public final native Pie setExplode(int explode)/*-{
        this.explode = explode;
        return this;
    }-*/;

	/**
	 * the size ratio of the pie relative to the plot .
	 * default 0.6
	 *
	 * @return
	 */
	public final native float getSizeRatio()/*-{
        return this.sizeRatio;
    }-*/;

	/**
	 * the size ratio of the pie relative to the plot .
	 * default 0.6
	 *
	 * @param sizeRatio
	 */
	public final native Pie setSizeRatio(float sizeRatio)/*-{
        this.sizeRatio = sizeRatio;
        return this;
    }-*/;

	/**
	 * the first slice start angle.
	 * default Math.PI/4
	 *
	 * @return
	 */
	public final native float getstartAngle()/*-{
        return this.startAngle;
    }-*/;

	/**
	 * the first slice start angle.
	 * default Math.PI/4
	 *
	 * @param startAngle
	 */
	public final native Pie setStartAngle(float startAngle)/*-{
        this.startAngle = startAngle;
        return this;
    }-*/;

	/**
	 * whether to draw the pie in 3 dimenstions or not (ineffective) .
	 * default false
	 *
	 * @return
	 */
	public final native boolean isPie3D()/*-{
        return this.pie3D;
    }-*/;

	/**
	 * whether to draw the pie in 3 dimenstions or not (ineffective) .
	 * default false
	 *
	 * @param pie3D
	 */
	public final native Pie setPie3D(boolean pie3D)/*-{
        this.pie3D = pie3D;
        return this;
    }-*/;

	/**
	 * The 3D Angle.
	 * default (Math.PI/2
	 *
	 * @return
	 */
	public final native float getPie3DviewAngle()/*-{
        return this.pie3DviewAngle;
    }-*/;

	/**
	 * The 3D Angle.
	 * default (Math.PI/2
	 *
	 * @param pie3DviewAngle
	 */
	public final native Pie setPie3DviewAngle(float pie3DviewAngle)/*-{
        this.pie3DviewAngle = pie3DviewAngle;
        return this;
    }-*/;

	/**
	 * The 3D splice Thickness.
	 * default 20
	 *
	 * @return
	 */
	public final native int getPie3DspliceThickness()/*-{
        return this.pie3DspliceThickness;
    }-*/;

	/**
	 * The 3D splice Thickness.
	 * default 20
	 *
	 * @param pie3DspliceThickness
	 */
	public final native Pie setPie3DspliceThickness(int pie3DspliceThickness)/*-{
        this.pie3DspliceThickness = pie3DspliceThickness;
        return this;
    }-*/;

	/**
	 * how close do you have to get to hit empty slice.
	 * default 0.1
	 *
	 * @return
	 */
	public final native float getEpsilon()/*-{
        return this.epsilon;
    }-*/;

	/**
	 * how close do you have to get to hit empty slice.
	 * default 0.1
	 *
	 * @param epsilon
	 */
	public final native Pie setEpsilon(float epsilon)/*-{
        this.epsilon = epsilon;
        return this;
    }-*/;

}
