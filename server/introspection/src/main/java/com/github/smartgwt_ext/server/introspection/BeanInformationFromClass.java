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
package com.github.smartgwt_ext.server.introspection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Andreas Berger
 */
public class BeanInformationFromClass extends BeanInformationBase<PropertyInformationFromClass> {

	private static final Logger LOGGER = LoggerFactory.getLogger(BeanInformationFromClass.class);

	private Class<?> clazz;

	BeanInformationFromClass(Class<?> clazz) {
		this.clazz = clazz;
		parseClass();
	}

	private void parseClass() {
		if (clazz == Object.class) {
			return;
		}
		Map<String, Method> getter = new LinkedHashMap<String, Method>();
		Map<String, Method> setter = new LinkedHashMap<String, Method>();
		Map<String, Field> fields = new LinkedHashMap<String, Field>();
		for (Method method : clazz.getDeclaredMethods()) {
			if (!Modifier.isPublic(method.getModifiers())) {
				continue;
			}
			if (Modifier.isAbstract(method.getModifiers())) {
				continue;
			}
			if (method.getParameterTypes().length == 0 && void.class !=
					method.getReturnType()) {
				if (boolean.class == method.getReturnType() || Boolean.class ==
						method.getReturnType()) {
					if (method.getName().startsWith("get") ||
							method.getName().startsWith("has")) {
						getter.put(getPropertyName(method.getName(), 3), method);
					} else if (method.getName().startsWith("is")) {
						getter.put(getPropertyName(method.getName(), 2), method);
					}
				} else if (method.getName().startsWith("get")) {
					getter.put(getPropertyName(method.getName(), 3), method);
				}
			} else if (method.getParameterTypes().length == 1 && void.class ==
					method.getReturnType()) {
				if (method.getName().startsWith("set")) {
					setter.put(getPropertyName(method.getName(), 3), method);
				}
			}
		}

		for (Field field : clazz.getDeclaredFields()) {
			fields.put(field.getName(), field);
		}

		for (Map.Entry<String, Method> entry : getter.entrySet()) {

			PropertyInformationFromClass info = new PropertyInformationFromClass();
			info.setName(entry.getKey());
			info.setGetter(entry.getValue());
			info.setType(entry.getValue().getReturnType());

			Method setterMethod = setter.remove(entry.getKey());
			if (setterMethod != null) {
				if (!setterMethod.getParameterTypes()[0].isAssignableFrom(entry.getValue().getReturnType())) {
					LOGGER.warn("Types of setter does not match getter [{}, {}] {}::{}",
							setterMethod.getParameterTypes()[0],
							entry.getValue().getReturnType(),
							clazz.getName(),
							entry.getKey());
				} else {
					info.setSetter(setterMethod);
				}
			}

			Field field = fields.remove(entry.getKey());
			if (field != null) {
				if (!field.getType().isAssignableFrom(entry.getValue().getReturnType())) {
					LOGGER.warn("Types of field does not match getter [{}, {}] {}::{}",
							field.getType(),
							entry.getValue().getReturnType(),
							clazz.getName(),
							entry.getKey());
				} else {
					info.setField(field);
				}
			}
			addProperty(info);
		}

		for (Map.Entry<String, Method> entry : setter.entrySet()) {

			PropertyInformationFromClass info = new PropertyInformationFromClass();
			info.setName(entry.getKey());
			info.setSetter(entry.getValue());
			info.setType(entry.getValue().getParameterTypes()[0]);

			Field field = fields.remove(entry.getKey());
			if (field != null) {
				if (!field.getType().isAssignableFrom(entry.getValue().getParameterTypes()[0])) {
					LOGGER.warn("Types of field does not match setter [{}, {}] {}::{}",
							field.getType(),
							entry.getValue().getReturnType(),
							clazz.getName(),
							entry.getKey());
				} else {
					info.setField(field);
				}
			}

			addProperty(info);
		}

		for (Map.Entry<String, Field> entry : fields.entrySet()) {
			PropertyInformationFromClass info = new PropertyInformationFromClass();
			info.setName(entry.getKey());
			info.setField(entry.getValue());
			info.setType(entry.getValue().getType());
			addProperty(info);
		}
	}

	/**
	 * @return the simpleName
	 */
	@Override
	public String getSimpleName() {
		return clazz.getSimpleName();
	}

	@Override
	public String getName() {
		return clazz.getName();
	}

	@Override
	public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
		return clazz.getAnnotation(annotationClass);
	}

	@Override
	public boolean isTypeOfClass(Class<?> clazz) {
		return clazz.isAssignableFrom(this.clazz);
	}

	@Override
	protected BeanInformation<PropertyInformationFromClass> initSuperBean() {
		return clazz.getSuperclass() == Object.class ? null
				: BeanInformationFactory.createBeanInformation(clazz.getSuperclass());
	}
}