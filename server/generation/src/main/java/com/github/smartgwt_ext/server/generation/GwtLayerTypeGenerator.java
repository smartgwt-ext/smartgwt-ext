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
import com.github.smartgwt_ext.server.core.HasIdField;
import com.github.smartgwt_ext.server.core.annotations.FieldFeatures;
import com.github.smartgwt_ext.server.core.processing.EnhancedObjectMapper;
import com.github.smartgwt_ext.server.introspection.facade.BeanInformation;
import com.github.smartgwt_ext.server.introspection.facade.BeanInformationFactory;
import com.github.smartgwt_ext.server.introspection.facade.PropertyInformation;
import com.google.gwt.core.client.JavaScriptObject;
import com.smartgwt.client.util.JSOHelper;
import com.sun.codemodel.*;

import javax.annotation.Generated;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.persistence.MappedSuperclass;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

/**
 * Generiert GWT OverlayTypen anhand der Entities.
 *
 * @author Andreas Berger
 * @created 07.01.2013
 */
public class GwtLayerTypeGenerator {

	private static final int MODS = JMod.PUBLIC | JMod.FINAL;
	private String sourcePackage;
	private String destinationPackage;
	private JCodeModel cm;

	/**
	 * @param sourcePackage
	 * @param destinationPackage
	 */
	public GwtLayerTypeGenerator(String sourcePackage, String destinationPackage) {
		super();
		this.sourcePackage = sourcePackage;
		this.destinationPackage = destinationPackage;
		cm = new JCodeModel();
	}

	/**
	 * Generiert GWT OverlayTypen anhand von Klassen.
	 *
	 * @param writer der CodeWriter
	 * @param classes die Klassen
	 * @throws IOException
	 */
	public void process(CodeWriter writer, Class<?>... classes) throws IOException {
		for (Class<?> clazz : classes) {
			writeLayerType(BeanInformationFactory.createBeanInformation(clazz));
		}
		cm.build(writer);
	}

	/**
	 * Generiert GWT OverlayTypen anhand von Sourcen.
	 *
	 * @param writer der CodeWriter
	 * @param elements die Sourcen
	 * @throws IOException
	 */
	public void process(CodeWriter writer, Set<? extends Element> elements)
			throws IOException {
		for (Element element : elements) {
			if (!(element instanceof TypeElement)) {
				continue;
			}
			writeLayerType(BeanInformationFactory.createBeanInformation((TypeElement) element));
		}
		cm.build(writer);
	}

	private void writeLayerType(BeanInformation<?> beanInformation) {
		try {
			JDefinedClass theClass = cm._class(getLayerTypeName(beanInformation.getName()));
			BeanInformation<?> superBean = beanInformation.getSuperBeanInformation();
			if (superBean == null || superBean.getAnnotation(MappedSuperclass.class) != null) {
				theClass._extends(JavaScriptObject.class);
			} else {
				theClass._extends(cm.ref(getLayerTypeName(beanInformation.getSuperBeanInformation().getName())));
			}
			theClass.annotate(Generated.class)
					.param("value", GwtLayerTypeGenerator.class.getName())
					.param("date", new Date().toString());
			theClass.javadoc().add(
					"<b>DO NOT EDIT</b>\n" +
							"Generated by {@link " + GwtLayerTypeGenerator.class.getName() + "}\n" +
							"from {@link " + beanInformation.getName() + "}");
			theClass.constructor(JMod.PROTECTED);

			Collection<? extends PropertyInformation<?>> props;
			if (beanInformation.getAnnotation(MappedSuperclass.class) == null) {
				props = beanInformation.getProperties();
			} else {
				props = beanInformation.getAllProperties();
			}
			theClass.field(JMod.PUBLIC | JMod.STATIC | JMod.FINAL, String.class, "DS").init(
					JExpr.lit(beanInformation.getSimpleName()));

			for (PropertyInformation<?> prop : props) {
				if (prop.getAnnotation(JsonIgnore.class) != null) {
					continue;
				}
				FieldFeatures features = prop.getAnnotation(FieldFeatures.class);
				if (features != null && features.ignore()) {
					continue;
				}

				theClass.field(JMod.PUBLIC | JMod.STATIC | JMod.FINAL, String.class,
						getFieldNameConstant(prop.getName())).init(JExpr.lit(prop.getName()));
				if (prop.getBeanInformation() != null && prop.getBeanInformation().isTypeOfClass(Enum.class)) {
					addEnumGetter(theClass, prop);
					addEnumSetter(theClass, prop);
				} else {

					String getterName = getGetterName(prop);
					String setterName = getSetterName(prop);
					String field = prop.getName();
					String type;
					BeanInformation<?> referenceType = prop.getBeanInformation();
					if (prop.isTypeOfClass(HasIdField.class)) {
						getterName += "Id";
						setterName += "Id";
						type = referenceType.getProperty("id").getTypeQualifiedName();
						PropertyInformation<?> displayFieldProperty = EnhancedObjectMapper
								.getDisplayField(referenceType);
						if (displayFieldProperty != null) {
							String converterMethodName = getConverterMethodName(displayFieldProperty
									.getTypeQualifiedName());
							if (converterMethodName != null) {
								addGetter(
										theClass,
										getGetterName(prop) + "DisplayField",
										prop.getName() + "_" + displayFieldProperty.getName(),
										cm.ref(displayFieldProperty.getTypeQualifiedName()),
										converterMethodName);
							}
						}
					} else {
						type = prop.getTypeQualifiedName();
					}
					String converterMethodName = getConverterMethodName(type);
					if (converterMethodName == null) {
						continue;
					}
					addGetter(theClass, getterName, field, cm.ref(type), converterMethodName);
					addSetter(theClass, setterName, field, cm.ref(type));
				}
			}
		} catch (JClassAlreadyExistsException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Generiert einen setter für ein Enum-Property.
	 *
	 * @param theClass die Klasse in der der setter generiert werden soll
	 * @param prop das Property für das der setter generiert werden soll
	 */
	private void addEnumSetter(JDefinedClass theClass, PropertyInformation<?> prop) {
		JMethod setter = addSetter(
				theClass,
				getSetterName(prop) + "String",
				prop.getName(),
				cm._ref(String.class));
		JMethod enumSetter = theClass.method(JMod.PUBLIC | JMod.FINAL, void.class, getSetterName(prop));
		JVar param = enumSetter.param(cm.ref(prop.getTypeQualifiedName()), prop.getName());
		JBlock block = enumSetter.body();

		JConditional ifBlock = block._if(param.eq(JExpr._null()));
		ifBlock._then().invoke(setter).arg(JExpr._null());
		ifBlock._else().invoke(setter).arg(JExpr.invoke(param, "name"));
	}

	/**
	 * Generiert einen setter für ein Property.
	 *
	 * @param theClass die Klasse in der der setter generiert werden soll
	 * @param name der Name des setters
	 * @param field das Feld auf das der setter zugreift
	 * @param type der Typ des setters
	 * @return die setter Methode
	 */
	private JMethod addSetter(
			JDefinedClass theClass,
			String name,
			String field,
			JType type) {
		JMethod setter = theClass.method(MODS, void.class, name);
		JVar param = setter.param(type, field);
		setter.body().staticInvoke(cm.ref(JSOHelper.class), "setAttribute")
				.arg(JExpr._this())
				.arg(JExpr.lit(field))
				.arg(param);
		return setter;
	}

	/**
	 * Generiert einen getter für ein Enum-Property.
	 *
	 * @param theClass die Klasse in der der getter generiert werden soll
	 * @param prop das Property für das der getter generiert werden soll
	 */
	private void addEnumGetter(JDefinedClass theClass, PropertyInformation<?> prop) {
		JMethod getter = addGetter(
				theClass,
				getGetterName(prop) + "String",
				prop.getName(),
				cm._ref(String.class),
				"getAttribute");
		JBlock block = theClass.method(JMod.PUBLIC | JMod.FINAL, cm.ref(prop.getTypeQualifiedName()),
				getGetterName(prop)).body();

		JVar localStringValue = block.decl(cm._ref(String.class), prop.getName(), JExpr.invoke(getter));
		block._return(JOp.cond(
				JOp.eq(localStringValue, JExpr._null()),
				JExpr._null(),
				cm.ref(prop.getTypeQualifiedName()).staticInvoke("valueOf").arg(localStringValue)
		));
	}

	/**
	 * Generiert einen getter für ein Property.
	 *
	 * @param theClass die Klasse in der der getter generiert werden soll
	 * @param name der Name des getters
	 * @param field das Feld auf das der getter zugreift
	 * @param type der Typ des getters
	 * @return die getter Methode
	 */
	private JMethod addGetter(
			JDefinedClass theClass,
			String name,
			String field,
			JType type,
			String converterMethodName) {
		JMethod getter = theClass.method(MODS, type, name);
		getter.body()._return(cm.ref(JSOHelper.class).staticInvoke(converterMethodName)
				.arg(JExpr._this())
				.arg(JExpr.lit(field)));

		return getter;
	}

	private String getConverterMethodName(String type) {
		if (type.equals(String.class.getName())) {
			return "getAttribute";
		} else if (type.equals(Integer.class.getName()) || type.equals("int")) {
			return "getAttributeAsInt";
		} else if (type.equals(Boolean.class.getName()) || type.equals("boolean")) {
			return "getAttributeAsBoolean";
		} else if (type.equals(Long.class.getName()) || type.equals("long")) {
			return "getAttributeAsLong";
		} else if (type.equals(Double.class.getName()) || type.equals("double")) {
			return "getAttributeAsDouble";
		} else if (type.equals(Float.class.getName()) || type.equals("float")) {
			return "getAttributeAsFloat";
		}
		return null;
	}

	private String getSetterName(PropertyInformation<?> prop) {
		return "set" + getFirstUpperCase(prop.getName());
	}

	private String getGetterName(PropertyInformation<?> prop) {
		String name = getFirstUpperCase(prop.getName());
		if (prop.isTypeOfClass(boolean.class)) {
			return "is" + name;
		}
		return "get" + name;
	}

	private static String getFirstUpperCase(String name) {
		return Character.toUpperCase(name.charAt(0)) + name.substring(1);
	}

	private String getLayerTypeName(String fqname) {
		String result = fqname.replace(sourcePackage, destinationPackage);
		int pos = result.lastIndexOf('.');
		return result.substring(0, pos) + ".Js" + result.substring(pos + 1);
	}

	public static String getFieldNameConstant(String fielName) {
		return "FIELD_" + fielName.replaceAll("([A-Z])", "_$1").toUpperCase();
	}

}
