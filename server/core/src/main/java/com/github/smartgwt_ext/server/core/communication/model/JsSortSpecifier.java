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
package com.github.smartgwt_ext.server.core.communication.model;

/**
 * @author Andreas Berger
 * @created 09.01.2013
 */
public class JsSortSpecifier {
	public enum Direction {
		ASC, DESC
	}

	private String field;
	private Direction direction;

	public JsSortSpecifier(String field, Direction direction) {
		this.field = field;
		this.direction = direction;
	}

	public String getField() {
		return field;
	}

	public Direction getDirection() {
		return direction;
	}
}
