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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;

/**
 * @author Andreas Berger
 * @created 22.02.13 - 16:32
 */
public class Legend extends JavaScriptObject {
	protected Legend() {
	}

	public static native final Legend create()/*-{
        return {};
    }-*/;

	/**
	 * setting to true will show the legend, hide otherwise.
	 * default true
	 *
	 * @return
	 */
	public final native boolean isShow()/*-{
        return this.show;
    }-*/;

	/**
	 * setting to true will show the legend, hide otherwise.
	 * default true
	 *
	 * @param show
	 */
	public final native Legend setShow(boolean show)/*-{
        this.show = show;
        return this;
    }-*/;

	/**
	 * number of colums in legend table // @todo: doesn't work for HtmlText = false.
	 * default 1
	 *
	 * @return
	 */
	public final native int getNoColumns()/*-{
        return this.noColumns;
    }-*/;

	/**
	 * number of colums in legend table // @todo: doesn't work for HtmlText = false.
	 * default 1
	 *
	 * @param noColumns
	 */
	public final native Legend setNoColumns(int noColumns)/*-{
        this.noColumns = noColumns;
        return this;
    }-*/;

//	/**
//	 * fn: string -> string.
//	 * default function(v){return v;}
//	 *
//	 * @return
//	 */
//	public final native todo getlabelFormatter()/*-{
//        return this.labelFormatter;
//    }-*/;
//
//	/**
//	 * fn: string -> string.
//	 * defaultfunction(v){return v;}
//	 *
//	 * @param labelFormatter
//	 */
//	public final native Legend setlabelFormatter(todo labelFormatter)/*-{
//        this.labelFormatter = labelFormatter;
//        return this;
//    }-*/;

	/**
	 * border color for the little label boxes.
	 * default '#CCCCCC'
	 *
	 * @return
	 */
	public final native String getLabelBoxBorderColor()/*-{
        return this.labelBoxBorderColor;
    }-*/;

	/**
	 * border color for the little label boxes.
	 * default '#CCCCCC'
	 *
	 * @param labelBoxBorderColor
	 */
	public final native Legend setLabelBoxBorderColor(String labelBoxBorderColor)/*-{
        this.labelBoxBorderColor = labelBoxBorderColor;
        return this;
    }-*/;

	/**
	 * .
	 * default 14
	 *
	 * @return
	 */
	public final native int getLabelBoxWidth()/*-{
        return this.labelBoxWidth;
    }-*/;

	/**
	 * .
	 * default 14
	 *
	 * @param labelBoxWidth
	 */
	public final native Legend setLabelBoxWidth(int labelBoxWidth)/*-{
        this.labelBoxWidth = labelBoxWidth;
        return this;
    }-*/;

	/**
	 * .
	 * default 10
	 *
	 * @return
	 */
	public final native int getLabelBoxHeight()/*-{
        return this.labelBoxHeight;
    }-*/;

	/**
	 * .
	 * default 10
	 *
	 * @param labelBoxHeight
	 */
	public final native Legend setLabelBoxHeight(int labelBoxHeight)/*-{
        this.labelBoxHeight = labelBoxHeight;
        return this;
    }-*/;

	/**
	 * .
	 * default 5
	 *
	 * @return
	 */
	public final native int getLabelBoxMargin()/*-{
        return this.labelBoxMargin;
    }-*/;

	/**
	 * .
	 * default 5
	 *
	 * @param labelBoxMargin
	 */
	public final native Legend setLabelBoxMargin(int labelBoxMargin)/*-{
        this.labelBoxMargin = labelBoxMargin;
        return this;
    }-*/;

	/**
	 * container (as jQuery object) to put legend in, null means default on top of graph.
	 * default null
	 *
	 * @return
	 */
	public final native Element getContainer()/*-{
        return this.container;
    }-*/;

	/**
	 * container (as jQuery object) to put legend in, null means default on top of graph.
	 * default null
	 *
	 * @param container
	 */
	public final native Legend setContainer(Element container)/*-{
        this.container = container;
        return this;
    }-*/;

	/**
	 * position of default legend container within plot.
	 * default 'nw'
	 *
	 * @return
	 */
	public final native String getPosition()/*-{
        return this.position;
    }-*/;

	/**
	 * position of default legend container within plot.
	 * default 'nw'
	 *
	 * @param position
	 */
	public final native Legend setPosition(String position)/*-{
        this.position = position;
        return this;
    }-*/;

	/**
	 * distance from grid edge to default legend container within plot.
	 * default 5
	 *
	 * @return
	 */
	public final native int getMargin()/*-{
        return this.margin;
    }-*/;

	/**
	 * distance from grid edge to default legend container within plot.
	 * default 5
	 *
	 * @param margin
	 */
	public final native Legend setMargin(int margin)/*-{
        this.margin = margin;
        return this;
    }-*/;

	/**
	 * Legend background color..
	 * default '#F0F0F0'
	 *
	 * @return
	 */
	public final native String getBackgroundColor()/*-{
        return this.backgroundColor;
    }-*/;

	/**
	 * Legend background color..
	 * default '#F0F0F0'
	 *
	 * @param backgroundColor
	 */
	public final native Legend setBackgroundColor(String backgroundColor)/*-{
        this.backgroundColor = backgroundColor;
        return this;
    }-*/;

	/**
	 * set to 0 to avoid background, set to 1 for a solid background.
	 * default 0.85
	 *
	 * @return
	 */
	public final native float getBackgroundOpacity()/*-{
        return this.backgroundOpacity;
    }-*/;

	/**
	 * set to 0 to avoid background, set to 1 for a solid background.
	 * default 0.85
	 *
	 * @param backgroundOpacity
	 */
	public final native Legend setBackgroundOpacity(float backgroundOpacity)/*-{
        this.backgroundOpacity = backgroundOpacity;
        return this;
    }-*/;

}
