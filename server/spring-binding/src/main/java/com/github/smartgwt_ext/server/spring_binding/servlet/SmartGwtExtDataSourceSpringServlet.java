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
package com.github.smartgwt_ext.server.spring_binding.servlet;

import com.github.smartgwt_ext.server.core.communication.servlet.SmartGwtExtDataSourceServlet;
import com.github.smartgwt_ext.server.core.processing.SmartGwtRequestProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;

/**
 * @author Andreas Berger
 * @created 29.08.14 - 21:40
 */
public class SmartGwtExtDataSourceSpringServlet extends SmartGwtExtDataSourceServlet {
	@Autowired
	private SmartGwtRequestProcessor smartGwtRequestProcessor;

	@Override
	public SmartGwtRequestProcessor getSmartGwtRequestProcessor() {
		return smartGwtRequestProcessor;
	}

	@Override
	public void init() throws ServletException {
		super.init();
		final WebApplicationContext ctx =
				WebApplicationContextUtils.getWebApplicationContext(getServletContext());

		if (ctx == null) {
			throw new IllegalStateException("No Spring web application context found");
		}

		ctx.getAutowireCapableBeanFactory().autowireBeanProperties(this,
				AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE,
				true);
	}
}
