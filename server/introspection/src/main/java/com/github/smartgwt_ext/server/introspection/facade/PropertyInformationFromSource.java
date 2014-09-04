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

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * // TODO Comment me!
 *
 * @author Andreas Berger
 */
public class PropertyInformationFromSource extends PropertyInformationBase<PropertyInformationFromSource> {

	private TypeMirror type;
	private TypeElement declaringElement;
	private List<Element> elemsForAnnotations;
	private Set<Modifier> fieldModifier;
	private BeanInformationFromSource typeBean;

	public PropertyInformationFromSource() {
		elemsForAnnotations = new ArrayList<Element>();
	}

	@Override
	public String getTypeSimpleName() {
		if (type instanceof DeclaredType) {
			return ((DeclaredType) type).asElement().getSimpleName().toString();
		}
		return type.toString();
	}

	@Override
	public String getTypeQualifiedName() {
		if (type instanceof DeclaredType) {
			return ((TypeElement) ((DeclaredType) type).asElement()).getQualifiedName().toString();
		}
		return type.toString();
	}

	@Override
	public boolean isEnum() {
		if (type instanceof DeclaredType) {
			return ((DeclaredType) type).asElement().getKind() == ElementKind.ENUM;
		}
		return false;
	}

	@Override
	public List<String> getEnumNames() {
		if (!isEnum()) {
			throw new IllegalStateException("Cant get Enum-Constants of Non-Enum Type");
		}
		List<String> result = new ArrayList<String>();
		for (Element enumElement : ((DeclaredType) type).asElement().getEnclosedElements()) {
			if (enumElement.getKind() == ElementKind.ENUM_CONSTANT) {
				result.add(enumElement.getSimpleName().toString());
			}
		}
		return result;
	}

	@Override
	public boolean isTypeOfClass(Class<?> clazz) {
		return isTypeOfClass(type, clazz);
	}

	public static boolean isTypeOfClass(TypeMirror type, Class<?> clazz) {
		if (clazz.isPrimitive() && type.getKind().isPrimitive()) {
			switch (type.getKind()) {
				case BOOLEAN:
					return clazz == boolean.class;
				case BYTE:
					return clazz == byte.class;
				case SHORT:
					return clazz == short.class;
				case INT:
					return clazz == int.class;
				case LONG:
					return clazz == long.class;
				case CHAR:
					return clazz == char.class;
				case FLOAT:
					return clazz == float.class;
				case DOUBLE:
					return clazz == double.class;
				default:
					return false;
			}
		}
		return isTypeOfClass(type, clazz.getName());
	}

	private static boolean isTypeOfClass(TypeMirror type, String name) {
		if (!(type instanceof DeclaredType)) {
			return false;
		}
		TypeElement typeElem = ((TypeElement) ((DeclaredType) type).asElement());
		if (typeElem.getQualifiedName().toString().endsWith(name)) {
			return true;
		}
		for (TypeMirror interf : typeElem.getInterfaces()) {
			if (isTypeOfClass(interf, name)) {
				return true;
			}
		}
		return isTypeOfClass(typeElem.getSuperclass(), name);
	}

	@Override
	public BeanInformation<PropertyInformationFromSource> getBeanInformation() {
		if (typeBean == null) {
			if (!(type instanceof DeclaredType)) {
				return null;
			}
			typeBean = BeanInformationFactory.createBeanInformation(((TypeElement) ((DeclaredType) type).asElement()));
		}
		return typeBean;
	}

	@Override
	public String getDeclaringTypeSimpleName() {
		return declaringElement.getSimpleName().toString();
	}

	@Override
	public boolean isTransient() {
		return fieldModifier != null && fieldModifier.contains(Modifier.TRANSIENT);
	}

	/** @param declaringElement the declaringElement to set */
	public void setDeclaringElement(TypeElement declaringElement) {
		this.declaringElement = declaringElement;
	}

	/** @param value */
	public void addElementForAnnotationCheck(Element value) {
		elemsForAnnotations.add(value);
	}

	/** @param asType */
	public void setType(TypeMirror type) {
		this.type = type;
	}

	@Override
	public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
		for (Element e : elemsForAnnotations) {
			A a = e.getAnnotation(annotationClass);
			if (a != null) {
				return a;
			}
		}
		return null;
	}

	public void setFieldModifier(Set<Modifier> fieldModifier) {
		this.fieldModifier = fieldModifier;
	}
}
