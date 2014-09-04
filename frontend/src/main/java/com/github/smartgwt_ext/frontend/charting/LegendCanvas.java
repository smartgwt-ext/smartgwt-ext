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
package com.github.smartgwt_ext.frontend.charting;

import com.github.smartgwt_ext.frontend.charting.configuration.Series;
import com.google.gwt.core.client.JsArray;
import com.smartgwt.client.widgets.HTMLFlow;

/**
 * Eine Legende die separat gerendert werden kann;
 *
 * @author Andreas Berger
 * @created 21.02.2013
 */
public class LegendCanvas extends HTMLFlow {

	public LegendCanvas(JsArray<Series> data) {
		this();
		setData(data);
	}

	public LegendCanvas() {
		setWidth100();
		setMargin(5);
	}

	public void setData(JsArray<Series> data) {
		String html = "<div class=\"legendCanvas\">";
		for (int i = 0, size = data.length(); i < size; i++) {
			Series serie = data.get(i);
			html += getLabelHtml(serie.getColor(), serie.getLabel());
		}
		html += "<div style=\"clear:both;\"></div></div>";
		setContents(html);
	}


	private String getLabelHtml(String color, String text) {
		return "<div class=\"wrapper\">"
				+ "<div class=\"box\">"
				+ "<div style=\"background-color:" + color + ";\">&nbsp;</div>"
				+ "</div>"
				+ "<div class=\"text\">" + text + "</div>"
				+ "</div>";
	}
}
