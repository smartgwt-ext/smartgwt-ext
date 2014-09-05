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

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Andreas Berger
 */
public class BeanInformationFromSource extends BeanInformationBase<PropertyInformationFromSource> {

	private static final Logger LOGGER = LoggerFactory.getLogger(BeanInformationFromSource.class);

	private TypeElement type;

	/**
	 * @param type
	 */
	BeanInformationFromSource(TypeElement type) {
		this.type = type;
		parseElement(type);
	}

	private void parseElement(TypeElement type) {
		Map<String, ExecutableElement> getter = new LinkedHashMap<String, ExecutableElement>();
		Map<String, ExecutableElement> setter = new LinkedHashMap<String, ExecutableElement>();
		Map<String, VariableElement> fields = new LinkedHashMap<String, VariableElement>();

		for (Element elem : type.getEnclosedElements()) {
			switch (elem.getKind()) {
				case FIELD:
					if (elem.getModifiers().contains(Modifier.STATIC)) {
						continue;
					}
					fields.put(elem.getSimpleName().toString(), (VariableElement) elem);
					break;
				case METHOD:
					ExecutableElement method = (ExecutableElement) elem;
					if (!method.getModifiers().contains(Modifier.PUBLIC)) {
						continue;
					}
					if (method.getModifiers().contains(Modifier.ABSTRACT)) {
						continue;
					}
					if (method.getParameters().size() == 0 && !method.getReturnType().toString().equals("void")) {
						if (boolean.class.getName().equals(method.getReturnType().toString()) ||
								Boolean.class.getName().equals(method.getReturnType().toString())) {
							if (method.getSimpleName().toString().startsWith("get") ||
									method.getSimpleName().toString().startsWith("has")) {
								getter.put(getPropertyName(method.getSimpleName().toString(), 3), method);
							} else if (method.getSimpleName().toString().startsWith("is")) {
								getter.put(getPropertyName(method.getSimpleName().toString(), 2), method);
							}
						} else if (method.getSimpleName().toString().startsWith("get")) {
							getter.put(getPropertyName(method.getSimpleName().toString(), 3), method);
						}
					} else if (method.getParameters().size() == 1 && method.getReturnType().toString().equals("void")) {
						if (method.getSimpleName().toString().startsWith("set")) {
							setter.put(getPropertyName(method.getSimpleName().toString(), 3), method);
						}
					}
					break;
			}
		}

		for (Map.Entry<String, ExecutableElement> entry : getter.entrySet()) {
			PropertyInformationFromSource info = new PropertyInformationFromSource();
			info.setName(entry.getKey());
			info.addElementForAnnotationCheck(entry.getValue());
			info.setType(entry.getValue().getReturnType());
			info.setDeclaringElement((TypeElement) entry.getValue().getEnclosingElement());

			ExecutableElement setterMethod = setter.remove(entry.getKey());
			if (setterMethod != null) {
				if (!setterMethod.getParameters().get(0).asType().toString()
						.equals(entry.getValue().getReturnType().toString())) {
					LOGGER.warn("Types of setter does not match getter [{}, {}] {}::{}",
							setterMethod.getParameters().get(0).asType(),
							entry.getValue().getReturnType(),
							((TypeElement) entry.getValue().getEnclosingElement()).getQualifiedName(),
							entry.getKey());
				} else {
					info.addElementForAnnotationCheck(setterMethod);
				}
			}

			VariableElement field = fields.remove(entry.getKey());
			if (field != null) {
				if (!field.asType().toString().equals(entry.getValue().getReturnType().toString())) {
					LOGGER.warn("Types of field does not match getter [{}, {}] {}::{}",
							field.asType(),
							entry.getValue().getReturnType().toString(),
							((TypeElement) entry.getValue().getEnclosingElement()).getQualifiedName(),
							entry.getKey());
				} else {
					info.addElementForAnnotationCheck(field);
				}
			}

			addProperty(info);
		}

		for (Map.Entry<String, ExecutableElement> entry : setter.entrySet()) {
			PropertyInformationFromSource info = new PropertyInformationFromSource();
			info.setName(entry.getKey());
			info.addElementForAnnotationCheck(entry.getValue());
			info.setType(entry.getValue().getParameters().get(0).asType());
			info.setDeclaringElement((TypeElement) entry.getValue().getEnclosingElement());

			VariableElement field = fields.remove(entry.getKey());
			if (field != null) {
				if (!field.asType().toString().equals(entry.getValue().getParameters().get(0).asType().toString())) {
					LOGGER.warn("Types of field does not match setter [{}, {}] {}::{}",
							field.asType(),
							entry.getValue().getParameters().get(0).asType().toString(),
							((TypeElement) entry.getValue().getEnclosingElement()).getQualifiedName(),
							entry.getKey());
				} else {
					info.addElementForAnnotationCheck(field);
				}
			}

			addProperty(info);
		}

		for (Map.Entry<String, VariableElement> entry : fields.entrySet()) {
			PropertyInformationFromSource info = new PropertyInformationFromSource();
			info.setName(entry.getKey());
			info.addElementForAnnotationCheck(entry.getValue());
			info.setType(entry.getValue().asType());
			info.setDeclaringElement((TypeElement) entry.getValue().getEnclosingElement());
			info.setFieldModifier(entry.getValue().getModifiers());

			addProperty(info);
		}

	}

	@Override
	public String getSimpleName() {
		return type.getSimpleName().toString();
	}

	@Override
	public String getName() {
		return type.getQualifiedName().toString();
	}

	@Override
	public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
		return type.getAnnotation(annotationClass);
	}

	@Override
	public boolean isTypeOfClass(Class<?> clazz) {
		return PropertyInformationFromSource.isTypeOfClass(type.asType(), clazz);
	}

	@Override
	protected BeanInformation<PropertyInformationFromSource> initSuperBean() {
		if (!(type.getSuperclass() instanceof DeclaredType)) {
			return null;
		}
		TypeElement superClass = (TypeElement) ((DeclaredType) type.getSuperclass()).asElement();
		if (superClass.getQualifiedName().toString().equals(Object.class.getName())) {
			return null;
		}
		return BeanInformationFactory.createBeanInformation(superClass);
	}
}
