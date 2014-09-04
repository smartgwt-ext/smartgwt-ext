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
import com.sun.codemodel.JPackage;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;

import static com.github.smartgwt_ext.server.generation.GwtLayerTypeGeneratorProcessor.PARAM_DESTINATION_PACKAGE;
import static com.github.smartgwt_ext.server.generation.GwtLayerTypeGeneratorProcessor.PARAM_SOURCE_PACKAGE;

/**
 * @author Andreas Berger
 * @created 07.01.2013
 */
@SupportedAnnotationTypes("com.github.smartgwt_ext.server.core.annotations.GenerateUiInformation")
@SupportedOptions({PARAM_SOURCE_PACKAGE, PARAM_DESTINATION_PACKAGE})
@SupportedSourceVersion(SourceVersion.RELEASE_5)
public class GwtLayerTypeGeneratorProcessor extends AbstractProcessor {

	public static final String PARAM_SOURCE_PACKAGE = "sourcePackage";
	public static final String PARAM_DESTINATION_PACKAGE = "destinationPackage";

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (annotations.isEmpty()) {
			return false;
		}
		try {
			String sourcePackage = processingEnv.getOptions().get(PARAM_SOURCE_PACKAGE).trim();
			String destinationPackage = processingEnv.getOptions().get(PARAM_DESTINATION_PACKAGE).trim();

			GwtLayerTypeGenerator generator = new GwtLayerTypeGenerator(sourcePackage, destinationPackage);

			final Filer filer = processingEnv.getFiler();
			CodeWriter writer = new CodeWriter() {

				@Override
				public OutputStream openBinary(JPackage pkg, String fileName) throws IOException {
					return filer.createSourceFile(pkg.name() + "." + fileName.replace(".java", "")).openOutputStream();
				}

				@Override
				public void close() throws IOException {
				}
			};
			generator.process(writer, roundEnv.getElementsAnnotatedWith(GenerateUiInformation.class));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return true;
	}
}
