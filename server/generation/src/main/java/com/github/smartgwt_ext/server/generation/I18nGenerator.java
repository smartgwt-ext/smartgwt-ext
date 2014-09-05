/*
 * Copyright (c) 2006 - 2012, evucom. All Rights Reserved.
 */

package com.github.smartgwt_ext.server.generation;

import com.github.smartgwt_ext.server.introspection.BeanInformation;
import com.github.smartgwt_ext.server.introspection.BeanInformationFactory;
import com.github.smartgwt_ext.server.introspection.PropertyInformation;
import com.google.gwt.core.client.GWT;
import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Andreas Berger (latest modification by $Author$)
 * @version $Id$
 * @created 12.09.12 - 20:27
 */
public class I18nGenerator {

	private TreeSet<String> labels;
	private I18nKeyResolver i18nKeyResolver;
	private String targetClass;

	public I18nGenerator(I18nKeyResolver i18nKeyResolver, String targetClass) {
		this.i18nKeyResolver = i18nKeyResolver;
		this.targetClass = targetClass;
		labels = new TreeSet<String>();
	}

	public void process(CodeWriter writer, Class<?>... classes) throws IOException, JClassAlreadyExistsException {
		for (Class<?> clazz : classes) {
			scanForLabels(BeanInformationFactory.createBeanInformation(clazz));
		}
		generateTranslationClass(writer);
	}

	public void process(CodeWriter writer, Set<? extends Element> elements)
			throws IOException, JClassAlreadyExistsException {
		for (Element element : elements) {
			if (!(element instanceof TypeElement)) {
				continue;
			}
			scanForLabels(BeanInformationFactory.createBeanInformation((TypeElement) element));
		}
		generateTranslationClass(writer);
	}

	private void scanForLabels(BeanInformation<?> beanInformation) {
		for (PropertyInformation<?> prop : beanInformation.getProperties()) {
			if (AnnotationHelper.shouldIgnore(prop)) {
				continue;
			}
			labels.add(i18nKeyResolver.getKey(beanInformation, prop));
		}
	}

	private void generateTranslationClass(CodeWriter writer) throws JClassAlreadyExistsException, IOException {
		JCodeModel model = new JCodeModel();

		int i = targetClass.lastIndexOf('.');
		String pkg = "";
		String className = "";
		if (i > 0) {
			pkg = targetClass.substring(0, i);
			className = targetClass.substring(i + 1);
		} else {
			className = targetClass;
		}
		JPackage jPackage = model._package(pkg);

		JDefinedClass generatedClass = jPackage._interface(JMod.PUBLIC, className);
		GenerationHelper.addGeneratedInfo(generatedClass, I18nGenerator.class, null, false);
		generatedClass._implements(com.google.gwt.i18n.client.Constants.class);

		JInvocation staticAccessor = model.ref(GWT.class).staticInvoke("create").arg(generatedClass.dotclass());
		generatedClass.field(JMod.STATIC, generatedClass, "$", staticAccessor);

		for (String label : labels) {
			generatedClass.method(0, String.class, label);
		}

		model.build(writer);
	}
}
