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
 * @created 22.02.13 - 11:03
 */
public class Mouse extends JavaScriptObject {
	protected Mouse() {
	}

	public static native final Mouse create()/*-{
        return {};
    }-*/;

	/**
	 * true to track the mouse, no tracking otherwise.
	 * default false
	 *
	 * @return
	 */
	public final native boolean isTrack()/*-{
        return this.track;
    }-*/;

	/**
	 * true to track the mouse, no tracking otherwise.
	 * default false
	 *
	 * @param track
	 */
	public final native Mouse setTrack(boolean track)/*-{
        this.track = track;
        return this;
    }-*/;

	/**
	 * .
	 * default false
	 *
	 * @return
	 */
	public final native boolean isTrackAll()/*-{
        return this.trackAll;
    }-*/;

	/**
	 * .
	 * default false
	 *
	 * @param trackAll
	 */
	public final native Mouse setTrackAll(boolean trackAll)/*-{
        this.trackAll = trackAll;
        return this;
    }-*/;

	/**
	 * position of the value box (default south-east).
	 * default 'se'
	 *
	 * @return
	 */
	public final native String getPosition()/*-{
        return this.position;
    }-*/;

	/**
	 * position of the value box (default south-east).
	 * default 'se'
	 *
	 * @param position
	 */
	public final native Mouse setPosition(String position)/*-{
        this.position = position;
        return this;
    }-*/;

	/**
	 * next to the mouse cursor.
	 * default false
	 *
	 * @return
	 */
	public final native boolean isRelative()/*-{
        return this.relative;
    }-*/;

	/**
	 * next to the mouse cursor.
	 * default false
	 *
	 * @param relative
	 */
	public final native Mouse setRelative(boolean relative)/*-{
        this.relative = relative;
        return this;
    }-*/;

//	/**
//	 * formats the values in the value box.
//	 * default Flotr.defaultTrackFormatter
//	 *
//	 * @return
//	 */
//	public final native todo gettrackFormatter()/*-{
//        return this.trackFormatter;
//    }-*/;
//
//	/**
//	 * formats the values in the value box.
//	 * default Flotr.defaultTrackFormatter
//	 *
//	 * @param trackFormatter
//	 */
//	public final native MouseConfiguration  settrackFormatter(todo trackFormatter)/*-{
//        this.trackFormatter = trackFormatter;
//    }-*/;

	/**
	 * margin in pixels of the valuebox.
	 * default 5
	 *
	 * @return
	 */
	public final native int getMargin()/*-{
        return this.margin;
    }-*/;

	/**
	 * margin in pixels of the valuebox.
	 * default 5
	 *
	 * @param margin
	 */
	public final native Mouse setMargin(int margin)/*-{
        this.margin = margin;
        return this;
    }-*/;

	/**
	 * line color of points that are drawn when mouse comes near a value of a series.
	 * default '#FF3F19'
	 *
	 * @return
	 */
	public final native String getLineColor()/*-{
        return this.lineColor;
    }-*/;

	/**
	 * line color of points that are drawn when mouse comes near a value of a series.
	 * default '#FF3F19'
	 *
	 * @param lineColor
	 */
	public final native Mouse setLineColor(String lineColor)/*-{
        this.lineColor = lineColor;
        return this;
    }-*/;

	/**
	 * decimals for the track values.
	 * default 1
	 *
	 * @return
	 */
	public final native int getTrackDecimals()/*-{
        return this.trackDecimals;
    }-*/;

	/**
	 * decimals for the track values.
	 * default 1
	 *
	 * @param trackDecimals
	 */
	public final native Mouse setTrackDecimals(int trackDecimals)/*-{
        this.trackDecimals = trackDecimals;
        return this;
    }-*/;

	/**
	 * the lower this number, the more precise you have to aim to show a value.
	 * default 2
	 *
	 * @return
	 */
	public final native int getSensibility()/*-{
        return this.sensibility;
    }-*/;

	/**
	 * the lower this number, the more precise you have to aim to show a value.
	 * default 2
	 *
	 * @param sensibility
	 */
	public final native Mouse setSensibility(int sensibility)/*-{
        this.sensibility = sensibility;
        return this;
    }-*/;

	/**
	 * whether or not to track the mouse in the y axis.
	 * default true
	 *
	 * @return
	 */
	public final native boolean isTrackY()/*-{
        return this.trackY;
    }-*/;

	/**
	 * whether or not to track the mouse in the y axis.
	 * default true
	 *
	 * @param trackY
	 */
	public final native Mouse setTrackY(boolean trackY)/*-{
        this.trackY = trackY;
        return this;
    }-*/;

	/**
	 * radius of the track point.
	 * default 3
	 *
	 * @return
	 */
	public final native int getRadius()/*-{
        return this.radius;
    }-*/;

	/**
	 * radius of the track point.
	 * default 3
	 *
	 * @param radius
	 */
	public final native Mouse setRadius(int radius)/*-{
        this.radius = radius;
        return this;
    }-*/;

	/**
	 * color to fill our select bar with only applies to bar and similar graphs (only bars for now).
	 * default null
	 *
	 * @return
	 */
	public final native FillColor getFillColor()/*-{
        return this.fillColor;
    }-*/;

	/**
	 * color to fill our select bar with only applies to bar and similar graphs (only bars for now).
	 * default null
	 *
	 * @param fillColor
	 */
	public final native Mouse setFillColor(FillColor fillColor)/*-{
        this.fillColor = fillColor;
        return this;
    }-*/;

	/**
	 * opacity of the fill color, set to 1 for a solid fill, 0 hides the fill .
	 * default 0.4
	 *
	 * @return
	 */
	public final native float getFillOpacity()/*-{
        return this.fillOpacity;
    }-*/;

	/**
	 * opacity of the fill color, set to 1 for a solid fill, 0 hides the fill .
	 * default 0.4
	 *
	 * @param fillOpacity
	 */
	public final native Mouse setFillOpacity(float fillOpacity)/*-{
        this.fillOpacity = fillOpacity;
        return this;
    }-*/;

}
