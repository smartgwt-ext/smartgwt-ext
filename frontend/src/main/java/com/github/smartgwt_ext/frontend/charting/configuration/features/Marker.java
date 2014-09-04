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

package com.github.smartgwt_ext.frontend.charting.configuration.features;

import com.github.smartgwt_ext.frontend.charting.configuration.appearance.FillColor;
import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author Andreas Berger
 * @created 22.02.13 - 10:44
 */
public class Marker extends JavaScriptObject {
	protected Marker() {
	}

	public static native final Marker create()/*-{
        return {};
    }-*/;

	/**
	 * setting to true will show markers, false will hide.
	 * default false
	 *
	 * @return
	 */
	public final native boolean isShow()/*-{
        return this.show;
    }-*/;

	/**
	 * setting to true will show markers, false will hide.
	 * default false
	 *
	 * @param show
	 */
	public final native Marker setShow(boolean show)/*-{
        this.show = show;
        return this;
    }-*/;

	/**
	 * line width of the rectangle around the marker.
	 * default 1
	 *
	 * @return
	 */
	public final native int getLineWidth()/*-{
        return this.lineWidth;
    }-*/;

	/**
	 * line width of the rectangle around the marker.
	 * default 1
	 *
	 * @param lineWidth
	 */
	public final native Marker setLineWidth(int lineWidth)/*-{
        this.lineWidth = lineWidth;
        return this;
    }-*/;

	/**
	 * text color.
	 * default '#000000'
	 *
	 * @return
	 */
	public final native String getColor()/*-{
        return this.color;
    }-*/;

	/**
	 * text color.
	 * default '#000000'
	 *
	 * @param color
	 */
	public final native Marker setColor(String color)/*-{
        this.color = color;
        return this;
    }-*/;

	/**
	 * fill or not the marekers' rectangles.
	 * default false
	 *
	 * @return
	 */
	public final native boolean isFill()/*-{
        return this.fill;
    }-*/;

	/**
	 * fill or not the marekers' rectangles.
	 * default false
	 *
	 * @param fill
	 */
	public final native Marker setFill(boolean fill)/*-{
        this.fill = fill;
        return this;
    }-*/;

	/**
	 * fill color.
	 * default "#FFFFFF"
	 *
	 * @return
	 */
	public final native FillColor getFillColor()/*-{
        return this.fillColor;
    }-*/;

	/**
	 * fill color.
	 * default "#FFFFFF"
	 *
	 * @param fillColor
	 */
	public final native Marker setFillColor(FillColor fillColor)/*-{
        this.fillColor = fillColor;
        return this;
    }-*/;

	/**
	 * fill opacity.
	 * default 0.4
	 *
	 * @return
	 */
	public final native float getFillOpacity()/*-{
        return this.fillOpacity;
    }-*/;

	/**
	 * fill opacity.
	 * default 0.4
	 *
	 * @param fillOpacity
	 */
	public final native Marker setFillOpacity(float fillOpacity)/*-{
        this.fillOpacity = fillOpacity;
        return this;
    }-*/;

	/**
	 * draw the rectangle around the markers.
	 * default false
	 *
	 * @return
	 */
	public final native boolean isStroke()/*-{
        return this.stroke;
    }-*/;

	/**
	 * draw the rectangle around the markers.
	 * default false
	 *
	 * @param stroke
	 */
	public final native Marker setStroke(boolean stroke)/*-{
        this.stroke = stroke;
        return this;
    }-*/;

	/**
	 * the markers position (vertical align: b, m, t, horizontal align: l, c, r).
	 * default 'ct'
	 *
	 * @return
	 */
	public final native String getPosition()/*-{
        return this.position;
    }-*/;

	/**
	 * the markers position (vertical align: b, m, t, horizontal align: l, c, r).
	 * default 'ct'
	 *
	 * @param position
	 */
	public final native Marker setPosition(String position)/*-{
        this.position = position;
        return this;
    }-*/;

	/**
	 * the margin between the point and the text..
	 * default 0
	 *
	 * @return
	 */
	public final native int getVerticalMargin()/*-{
        return this.verticalMargin;
    }-*/;

	/**
	 * the margin between the point and the text..
	 * default 0
	 *
	 * @param verticalMargin
	 */
	public final native Marker setVerticalMargin(int verticalMargin)/*-{
        this.verticalMargin = verticalMargin;
        return this;
    }-*/;

//	/**
//	 * Formatter for lables.
//	 * default Flotr.defaultMarkerFormatter
//	 *
//	 * @return
//	 */
//	public final native todo getlabelFormatter()/*-{
//        return this.labelFormatter;
//    }-*/;
//
//	/**
//	 * Formatter for lables.
//	 * default Flotr.defaultMarkerFormatter
//	 *
//	 * @param labelFormatter
//	 */
//	public final native MarkerConfiguration setlabelFormatter(todo labelFormatter)/*-{
//        this.labelFormatter = labelFormatter;return this;
//    }-*/;

	/**
	 * Font size.
	 * default Flotr.defaultOptions.fontSize
	 *
	 * @return
	 */
	public final native float getFontSize()/*-{
        return this.fontSize;
    }-*/;

	/**
	 * Font size.
	 * default Flotr.defaultOptions.fontSize
	 *
	 * @param fontSize
	 */
	public final native Marker setFontSize(float fontSize)/*-{
        this.fontSize = fontSize;
        return this;
    }-*/;

	/**
	 * true if markers should be stacked.
	 * default false
	 *
	 * @return
	 */
	public final native boolean isStacked()/*-{
        return this.stacked;
    }-*/;

	/**
	 * true if markers should be stacked.
	 * default false
	 *
	 * @param stacked
	 */
	public final native Marker setStacked(boolean stacked)/*-{
        this.stacked = stacked;
        return this;
    }-*/;

	/**
	 * define stacking behavior, (b- bars like, a - area like) (see Issue 125 for details).
	 * default 'b'
	 *
	 * @return
	 */
	public final native String getStackingType()/*-{
        return this.stackingType;
    }-*/;

	/**
	 * define stacking behavior, (b- bars like, a - area like) (see Issue 125 for details).
	 * default 'b'
	 *
	 * @param stackingType
	 */
	public final native Marker setStackingType(String stackingType)/*-{
        this.stackingType = stackingType;
        return this;
    }-*/;

	/**
	 * true if markers should be horizontal (For now only in a case on horizontal stacked bars, stacks should be calculated horizontaly).
	 * default false
	 *
	 * @return
	 */
	public final native boolean isHorizontal()/*-{
        return this.horizontal;
    }-*/;

	/**
	 * true if markers should be horizontal (For now only in a case on horizontal stacked bars, stacks should be calculated horizontaly).
	 * default false
	 *
	 * @param horizontal
	 */
	public final native Marker setHorizontal(boolean horizontal)/*-{
        this.horizontal = horizontal;
        return this;
    }-*/;

}
