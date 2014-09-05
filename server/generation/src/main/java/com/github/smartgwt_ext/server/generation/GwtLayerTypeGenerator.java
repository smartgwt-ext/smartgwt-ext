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

import com.github.smartgwt_ext.server.core.annotations.DisplayField;
import com.github.smartgwt_ext.server.core.annotations.FieldFeatures;
import com.github.smartgwt_ext.server.core.annotations.GenerateUiInformation;
import com.github.smartgwt_ext.server.introspection.BeanInformation;
import com.github.smartgwt_ext.server.introspection.BeanInformationFactory;
import com.github.smartgwt_ext.server.introspection.PropertyInformation;
import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JOp;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Set;

/**
 * Generiert GWT OverlayTypen anhand der Entities.
 *
 * @author Andreas Berger
 * @created 07.01.2013
 */
public class GwtLayerTypeGenerator {

	private static final int MODS = JMod.PUBLIC | JMod.FINAL;
	private JCodeModel cm;
	private LayerHandler layerHandler;
	private String baseClassName;

	public GwtLayerTypeGenerator(LayerHandler layerHandler, String baseClassName) {
		this.layerHandler = layerHandler;
		this.baseClassName = baseClassName;
		cm = new JCodeModel();
	}

	/**
	 * Generiert GWT OverlayTypen anhand von Klassen.
	 *
	 * @param writer der CodeWriter
	 * @param classes die Klassen
	 * @throws IOException
	 */
	@SuppressWarnings("UnusedDeclaration")
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
			JDefinedClass theClass = cm._class(layerHandler.resolveLayerName(beanInformation));
			BeanInformation<?> superBean = beanInformation.getSuperBeanInformation();
			boolean getAll = false;
			if (superBean == null || superBean.getAnnotation(MappedSuperclass.class) != null) {
				theClass._extends(cm.ref(baseClassName));
				getAll = true;
			} else {
				theClass._extends(
						cm.ref(layerHandler.resolveLayerName(beanInformation.getSuperBeanInformation())));
			}
			GenerationHelper.addGeneratedInfo(theClass, GwtLayerTypeGenerator.class, beanInformation, true);
			theClass.constructor(JMod.PROTECTED);

			Collection<? extends PropertyInformation<?>> props;
			if (getAll) {
				props = beanInformation.getAllProperties();
			} else {
				props = beanInformation.getProperties();
			}
			String datasourceName = layerHandler.resolveDataSourceName(beanInformation);
			theClass.field(JMod.PUBLIC | JMod.STATIC | JMod.FINAL, String.class, "DS").init(JExpr.lit(datasourceName));

			for (PropertyInformation<?> prop : props) {
				if (!getAll && superBean.getProperty(prop.getName()) != null) {
					//don't override existing property
					continue;
				}
				FieldFeatures features = prop.getAnnotation(FieldFeatures.class);
				if (AnnotationHelper.shouldIgnore(prop)) {
					if (prop.getAnnotation(Id.class) != null) {
						theClass.field(JMod.PUBLIC | JMod.STATIC | JMod.FINAL, String.class,
								getFieldNameConstant(prop.getName())).init(JExpr.lit(prop.getName()));
					}
					continue;
				}

				theClass.field(JMod.PUBLIC | JMod.STATIC | JMod.FINAL, String.class,
						getFieldNameConstant(prop.getName())).init(JExpr.lit(prop.getName()));
				if (prop.getBeanInformation() != null && prop.getBeanInformation().isTypeOfClass(Enum.class)) {
					addEnumGetter(theClass, prop);
					if (features == null || !features.immutable()) {
						addEnumSetter(theClass, prop);
					}
					continue;
				}
				String getterName = getGetterName(prop);
				String setterName = getSetterName(prop);
				String field = prop.getName();
				String type = prop.getTypeQualifiedName();
				BeanInformation<?> referenceType = prop.getBeanInformation();
				if (referenceType != null) {
					PropertyInformation<?> displayFieldProperty = referenceType.getAnnotatedProperty(
							DisplayField.class);
					if (displayFieldProperty != null) {
						String converterMethodName = getConverterMethodName(displayFieldProperty
								.getTypeQualifiedName());
						if (converterMethodName != null) {
							addGetter(MODS,
									theClass,
									getterName + "DisplayField",
									field + "_" + displayFieldProperty.getName(),
									cm.ref(displayFieldProperty.getTypeQualifiedName()),
									converterMethodName);
						}
					}
					// TODO use @Id
					if (referenceType.getProperty("id") != null) {
						type = referenceType.getProperty("id").getTypeQualifiedName();
					}
					if (referenceType.getAnnotation(GenerateUiInformation.class) != null) {
						addDelegate(MODS, theClass, getterName + "Js", field,
								cm.ref(layerHandler.resolveLayerName(referenceType)));
					}
				}
				type = layerHandler.getLayerType(type);
				String converterMethodName = getConverterMethodName(type);
				if (converterMethodName == null) {
					continue;
				}
				addGetter(MODS, theClass, getterName, field, cm.ref(type), converterMethodName);
				if (features == null || !features.immutable()) {
					addSetter(MODS, theClass, setterName, field, cm.ref(type));
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
		JMethod setter = addSetter(JMod.PRIVATE,
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
			int mods,
			JDefinedClass theClass,
			String name,
			String field,
			JType type) {
		JMethod setter = theClass.method(mods, void.class, name);
		JVar param = setter.param(type, field);
		setter.body().invoke("setAttribute")
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
		JMethod getter = addGetter(JMod.PRIVATE,
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
			int mods,
			JDefinedClass theClass,
			String name,
			String field,
			JType type,
			String converterMethodName) {
		JMethod getter = theClass.method(mods, type, name);
		getter.body()._return(JExpr.invoke(converterMethodName)
				.arg(JExpr._this())
				.arg(JExpr.lit(field)));

		return getter;
	}


	private void addDelegate(
			int mods,
			JDefinedClass theClass,
			String name,
			String field,
			JType type) {
		JMethod getter = theClass.method(mods, type, name);
		getter.body()._return(JExpr.invoke("getDelegate")
				.arg(JExpr.lit(field)));
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
		} else if (type.equals(Character.class.getName()) || type.equals("char")) {
			return "getAttributeAsCharacter";
		} else if (type.equals(BigDecimal.class.getName())) {
			return "getAttributeAsBigDecimal";
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

	public static String getFieldNameConstant(String fielName) {
		return "FIELD_" + fielName.replaceAll("([A-Z])", "_$1").toUpperCase();
	}

}
