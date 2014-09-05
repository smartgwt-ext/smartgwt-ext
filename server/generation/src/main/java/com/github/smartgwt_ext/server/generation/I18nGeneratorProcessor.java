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

import com.github.smartgwt_ext.server.core.annotations.GenerateUiInformation;
import com.sun.codemodel.CodeWriter;
import org.apache.commons.lang.StringUtils;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Set;

import static com.github.smartgwt_ext.server.generation.I18nGeneratorProcessor.I18N_KEY_RESOLVER_CLASS;
import static com.github.smartgwt_ext.server.generation.I18nGeneratorProcessor.TARGET_CLASS;

/**
 * @author Andreas Berger
 * @created 07.01.2013
 */
@SupportedAnnotationTypes("com.github.smartgwt_ext.server.core.annotations.GenerateUiInformation")
@SupportedOptions({TARGET_CLASS, I18N_KEY_RESOLVER_CLASS})
@SupportedSourceVersion(SourceVersion.RELEASE_5)
public class I18nGeneratorProcessor extends AbstractProcessor {

	public static final String TARGET_CLASS = "targetClass";
	public static final String I18N_KEY_RESOLVER_CLASS = "i18nKeyResolverClass";

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (annotations.isEmpty()) {
			return false;
		}
		try {
			I18nGenerator generator = new I18nGenerator(
					getKeyResolver(),
					processingEnv.getOptions().get(TARGET_CLASS).trim()
			);
			CodeWriter writer = new FilerCodeWriter(processingEnv.getFiler());
			generator.process(writer, roundEnv.getElementsAnnotatedWith(GenerateUiInformation.class));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return true;
	}

	private I18nKeyResolver getKeyResolver()
			throws ClassNotFoundException, IllegalAccessException, InstantiationException {
		String i18nKeyResolverClass = processingEnv.getOptions().get(I18N_KEY_RESOLVER_CLASS);
		I18nKeyResolver i18nKeyResolver = null;
		if (StringUtils.isNotBlank(i18nKeyResolverClass)) {
			i18nKeyResolver = (I18nKeyResolver) Class.forName(i18nKeyResolverClass.trim()).newInstance();
		}
		if (i18nKeyResolver == null) {
			i18nKeyResolver = new I18nKeyResolver();
		}
		return i18nKeyResolver;
	}
}
