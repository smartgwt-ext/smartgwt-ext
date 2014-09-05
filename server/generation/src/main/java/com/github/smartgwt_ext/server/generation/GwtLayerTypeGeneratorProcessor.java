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
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import static com.github.smartgwt_ext.server.generation.GwtLayerTypeGeneratorProcessor.*;

/**
 * @author Andreas Berger
 * @created 07.01.2013
 */
@SupportedAnnotationTypes("com.github.smartgwt_ext.server.core.annotations.GenerateUiInformation")
@SupportedOptions(
		{PARAM_SOURCE_PACKAGE, PARAM_DESTINATION_PACKAGE, PARAM_LAYER_HANDLER_CLASS, PARAM_LAYER_BASE_CLASS})
@SupportedSourceVersion(SourceVersion.RELEASE_5)
public class GwtLayerTypeGeneratorProcessor extends AbstractProcessor {

	public static final String PARAM_SOURCE_PACKAGE = "sourcePackage";
	public static final String PARAM_DESTINATION_PACKAGE = "destinationPackage";
	public static final String PARAM_LAYER_HANDLER_CLASS = "layerHandlerClass";
	public static final String PARAM_LAYER_BASE_CLASS = "layerBaseClass";

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (annotations.isEmpty()) {
			return false;
		}
		try {
			GwtLayerTypeGenerator generator = new GwtLayerTypeGenerator(getLayerNameResolver(), getLayerBaseClass());
			CodeWriter writer = new FilerCodeWriter(processingEnv.getFiler());
			generator.process(writer, roundEnv.getElementsAnnotatedWith(GenerateUiInformation.class));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return true;
	}

	private String getLayerBaseClass() {
		String layerBaseClass = processingEnv.getOptions().get(PARAM_LAYER_BASE_CLASS);
		if (StringUtils.isNotBlank(layerBaseClass)) {
			layerBaseClass = layerBaseClass.trim();
		} else {
			layerBaseClass = "com.github.smartgwt_ext.frontend.server_binding.layer.JsBase";
		}
		return layerBaseClass;
	}

	private LayerHandler getLayerNameResolver()
			throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException,
			ClassNotFoundException {
		String sourcePackage = processingEnv.getOptions().get(PARAM_SOURCE_PACKAGE);
		if (sourcePackage != null) {
			sourcePackage = sourcePackage.trim();
		}
		String destinationPackage = processingEnv.getOptions().get(PARAM_DESTINATION_PACKAGE);
		if (destinationPackage != null) {
			destinationPackage = destinationPackage.trim();
		}
		String datasourceNameResolverClass = processingEnv.getOptions().get(PARAM_LAYER_HANDLER_CLASS);
		LayerHandler layerHandler = null;
		if (StringUtils.isNotBlank(datasourceNameResolverClass)) {
			layerHandler = (LayerHandler) Class.forName(datasourceNameResolverClass.trim())
					.getConstructor(String.class, String.class)
					.newInstance(sourcePackage, destinationPackage);
		}
		if (layerHandler == null) {
			layerHandler = new LayerHandler(sourcePackage, destinationPackage);
		}
		return layerHandler;
	}
}
