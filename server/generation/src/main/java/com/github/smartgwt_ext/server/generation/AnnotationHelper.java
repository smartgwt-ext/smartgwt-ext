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

package com.github.smartgwt_ext.server.generation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.smartgwt_ext.server.core.annotations.FieldFeatures;
import com.github.smartgwt_ext.server.core.annotations.UiIgnore;
import com.github.smartgwt_ext.server.introspection.PropertyInformation;

import javax.persistence.ElementCollection;
import javax.persistence.EmbeddedId;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

/**
 * @author Andreas Berger
 * @created 05.09.14 - 15:31
 */
public class AnnotationHelper {

	public static boolean shouldIgnore(PropertyInformation<?> prop) {
		if (prop.getAnnotation(UiIgnore.class) != null) {
			return true;
		}
		JsonIgnore jsonIgnore = prop.getAnnotation(JsonIgnore.class);
		if (jsonIgnore != null && jsonIgnore.value() && prop.getAnnotation(JsonProperty.class) == null) {
			return true;
		}
		FieldFeatures features = prop.getAnnotation(FieldFeatures.class);

		if (features == null) {
			if (prop.getAnnotation(OneToMany.class) != null
					|| prop.getAnnotation(ManyToMany.class) != null
					|| prop.getAnnotation(ElementCollection.class) != null
					|| prop.getAnnotation(EmbeddedId.class) != null
					) {
				// ignore OneToMany and ManyToMany by default
				return true;
			}
		} else if (features.ignore()) {
			return true;
		}
		return false;
	}
}
