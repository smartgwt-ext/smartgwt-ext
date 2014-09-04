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
 * @created 22.02.13 - 11:09
 */
public class Grid extends JavaScriptObject {
	protected Grid() {
	}

	public static native final Grid create()/*-{
        return {};
    }-*/;

	/**
	 * primary color used for outline and labels.
	 * default '#545454'
	 *
	 * @return
	 */
	public final native String getColor()/*-{
        return this.color;
    }-*/;

	/**
	 * primary color used for outline and labels.
	 * default '#545454'
	 *
	 * @param color
	 */
	public final native Grid setColor(String color)/*-{
        this.color = color;
        return this;
    }-*/;

	/**
	 * null for transparent, else color.
	 * default null
	 *
	 * @return
	 */
	public final native String getBackgroundColor()/*-{
        return this.backgroundColor;
    }-*/;

	/**
	 * null for transparent, else color.
	 * default null
	 *
	 * @param backgroundColor
	 */
	public final native Grid setBackgroundColor(String backgroundColor)/*-{
        this.backgroundColor = backgroundColor;
        return this;
    }-*/;

	/**
	 * background image. String or object with src, left and top.
	 * default null
	 *
	 * @return
	 */
	public final native String getBackgroundImage()/*-{
        return this.backgroundImage;
    }-*/;

	/**
	 * background image. String or object with src, left and top.
	 * default null
	 *
	 * @param backgroundImage
	 */
	public final native Grid setBackgroundImage(String backgroundImage)/*-{
        this.backgroundImage = backgroundImage;
        return this;
    }-*/;

	/**
	 * .
	 * default 0.4
	 *
	 * @return
	 */
	public final native float getWatermarkAlpha()/*-{
        return this.watermarkAlpha;
    }-*/;

	/**
	 * .
	 * default 0.4
	 *
	 * @param watermarkAlpha
	 */
	public final native Grid setWatermarkAlpha(float watermarkAlpha)/*-{
        this.watermarkAlpha = watermarkAlpha;
        return this;
    }-*/;

	/**
	 * color used for the ticks.
	 * default '#DDDDDD'
	 *
	 * @return
	 */
	public final native String getTickColor()/*-{
        return this.tickColor;
    }-*/;

	/**
	 * color used for the ticks.
	 * default '#DDDDDD'
	 *
	 * @param tickColor
	 */
	public final native Grid setTickColor(String tickColor)/*-{
        this.tickColor = tickColor;
        return this;
    }-*/;

	/**
	 * margin in pixels.
	 * default 3
	 *
	 * @return
	 */
	public final native int getLabelMargin()/*-{
        return this.labelMargin;
    }-*/;

	/**
	 * margin in pixels.
	 * default 3
	 *
	 * @param labelMargin
	 */
	public final native Grid setLabelMargin(int labelMargin)/*-{
        this.labelMargin = labelMargin;
        return this;
    }-*/;

	/**
	 * whether to show gridlines in vertical direction.
	 * default true
	 *
	 * @return
	 */
	public final native boolean isVerticalLines()/*-{
        return this.verticalLines;
    }-*/;

	/**
	 * whether to show gridlines in vertical direction.
	 * default true
	 *
	 * @param verticalLines
	 */
	public final native Grid setVerticalLines(boolean verticalLines)/*-{
        this.verticalLines = verticalLines;
        return this;
    }-*/;

	/**
	 * whether to show gridlines for minor ticks in vertical dir..
	 * default null
	 *
	 * @return
	 */
	public final native boolean isMinorVerticalLines()/*-{
        return this.minorVerticalLines;
    }-*/;

	/**
	 * whether to show gridlines for minor ticks in vertical dir..
	 * default null
	 *
	 * @param minorVerticalLines
	 */
	public final native Grid setMinorVerticalLines(boolean minorVerticalLines)/*-{
        this.minorVerticalLines = minorVerticalLines;
        return this;
    }-*/;

	/**
	 * whether to show gridlines in horizontal direction.
	 * default true
	 *
	 * @return
	 */
	public final native boolean isHorizontalLines()/*-{
        return this.horizontalLines;
    }-*/;

	/**
	 * whether to show gridlines in horizontal direction.
	 * default true
	 *
	 * @param horizontalLines
	 */
	public final native Grid setHorizontalLines(boolean horizontalLines)/*-{
        this.horizontalLines = horizontalLines;
        return this;
    }-*/;

	/**
	 * whether to show gridlines for minor ticks in horizontal dir..
	 * default null
	 *
	 * @return
	 */
	public final native boolean isMinorHorizontalLines()/*-{
        return this.minorHorizontalLines;
    }-*/;

	/**
	 * whether to show gridlines for minor ticks in horizontal dir..
	 * default null
	 *
	 * @param minorHorizontalLines
	 */
	public final native Grid setMinorHorizontalLines(boolean minorHorizontalLines)/*-{
        this.minorHorizontalLines = minorHorizontalLines;
        return this;
    }-*/;

	/**
	 * width of the grid outline/border in pixels.
	 * default 1
	 *
	 * @return
	 */
	public final native int getOutlineWidth()/*-{
        return this.outlineWidth;
    }-*/;

	/**
	 * width of the grid outline/border in pixels.
	 * default 1
	 *
	 * @param outlineWidth
	 */
	public final native Grid setOutlineWidth(int outlineWidth)/*-{
        this.outlineWidth = outlineWidth;
        return this;
    }-*/;

	/**
	 * walls of the outline to display.
	 * default 'nsew'
	 *
	 * @return
	 */
	public final native String getOutline()/*-{
        return this.outline;
    }-*/;

	/**
	 * walls of the outline to display.
	 * default 'nsew'
	 *
	 * @param outline
	 */
	public final native Grid setOutline(String outline)/*-{
        this.outline = outline;
        return this;
    }-*/;

	/**
	 * if set to true, the grid will be circular, must be used when radars are drawn.
	 * default false
	 *
	 * @return
	 */
	public final native boolean isCircular()/*-{
        return this.circular;
    }-*/;

	/**
	 * if set to true, the grid will be circular, must be used when radars are drawn.
	 * default false
	 *
	 * @param circular
	 */
	public final native Grid setCircular(boolean circular)/*-{
        this.circular = circular;
        return this;
    }-*/;

}
