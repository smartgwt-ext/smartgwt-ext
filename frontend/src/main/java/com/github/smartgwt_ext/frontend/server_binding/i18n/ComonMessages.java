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
package com.github.smartgwt_ext.frontend.server_binding.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

/**
 * Messages.
 *
 * @author Andreas Berger
 */
public interface ComonMessages extends Messages {

	ComonMessages $ = GWT.create(ComonMessages.class);

	@DefaultMessage("{0}&nbsp;&#8209;&nbsp;{1}&nbsp;({2})")
	String GRID_SUMMARY(int start, int end, int total);

}
