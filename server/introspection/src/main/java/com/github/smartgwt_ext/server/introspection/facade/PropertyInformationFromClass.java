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
package com.github.smartgwt_ext.server.introspection.facade;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Enthält die Information von einem Property.
 *
 * @author Andreas Berger
 */
public class PropertyInformationFromClass extends PropertyInformationBase<PropertyInformationFromClass> {

	private Class<?> type;

	private Class<?> declaringType;

	private Map<Class<? extends Annotation>, Annotation> annotations;

	private Method getter;
	private Method setter;
	private Field field;

	private BeanInformationFromClass typeBean;

	public PropertyInformationFromClass() {
	}

	public PropertyInformationFromClass(Class<?> type) {
		this.type = type;
	}

	/** @param field the field to set */
	public void setField(Field field) {
		this.field = field;
		addAnnotations(field.getAnnotations());
		declaringType = field.getDeclaringClass();
	}

	/** @param getter the getter to set */
	public void setGetter(Method getter) {
		this.getter = getter;
		addAnnotations(getter.getAnnotations());
		declaringType = getter.getDeclaringClass();
	}

	/** @param setter the setter to set */
	public void setSetter(Method setter) {
		this.setter = setter;
		addAnnotations(setter.getAnnotations());
		declaringType = setter.getDeclaringClass();
	}

	/** @return the type */
	public Class<?> getType() {
		return type;
	}

	/** @param type the type to set */
	public void setType(Class<?> type) {
		this.type = type;
	}

	@Override
	public String getTypeSimpleName() {
		return type.getSimpleName();
	}

	@Override
	public String getTypeQualifiedName() {
		return type.getName();
	}

	@Override
	public boolean isEnum() {
		return type.isEnum();
	}

	@Override
	public List<String> getEnumNames() {
		List<String> result = new ArrayList<String>();
		for (Enum<?> e : (Enum[]) type.getEnumConstants()) {
			result.add(e.name());
		}
		return result;
	}

	@Override
	public boolean isTypeOfClass(Class<?> clazz) {
		return clazz.isAssignableFrom(type);
	}

	@Override
	public BeanInformation<PropertyInformationFromClass> getBeanInformation() {
		if (typeBean == null) {
			typeBean = BeanInformationFactory.createBeanInformation(type);
		}
		return typeBean;
	}

	@Override
	public String getDeclaringTypeSimpleName() {
		return declaringType.getSimpleName();
	}

	@Override
	public boolean isTransient() {
		return Modifier.isTransient(field.getModifiers());
	}

	/** @param annotations */
	protected void addAnnotations(Annotation[] annotations) {
		for (Annotation annotation : annotations) {
			addAnnotation(annotation);
		}
	}

	protected void addAnnotation(Annotation annotation) {
		if (annotations == null) {
			annotations = new HashMap<Class<? extends Annotation>, Annotation>();
		}
		annotations.put(annotation.annotationType(), annotation);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
		if (annotations == null) {
			return null;
		}
		return (A) annotations.get(annotationClass);
	}

	public Method getGetter() {
		return getter;
	}

	public Method getSetter() {
		return setter;
	}

	public Field getField() {
		return field;
	}

}