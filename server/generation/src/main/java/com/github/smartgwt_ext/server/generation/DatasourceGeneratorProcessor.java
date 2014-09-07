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

import com.github.smartgwt_ext.server.core.SpecialEncodingControl;
import com.github.smartgwt_ext.server.core.annotations.GenerateUiInformation;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;
import org.apache.commons.lang.StringUtils;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import static com.github.smartgwt_ext.server.generation.DatasourceGeneratorProcessor.*;

/**
 * @author Andreas Berger
 */
@SupportedAnnotationTypes("com.github.smartgwt_ext.server.core.annotations.GenerateUiInformation")
@SupportedOptions(
		{PARAM_SERVLET_URL, PARAM_BUNDLES, PARAM_DATA_SOURCE_ENCODING, PARAM_BUNDLE_ENCODING, PARAM_TARGET_PACKAGE, I18N_KEY_RESOLVER_CLASS, DATA_SOURCE_HANDLER_CLASS})
@SupportedSourceVersion(SourceVersion.RELEASE_5)
public class DatasourceGeneratorProcessor extends AbstractProcessor {

	public static final String PARAM_SERVLET_URL = "servletURL";
	public static final String PARAM_BUNDLES = "bundles";
	public static final String PARAM_DATA_SOURCE_ENCODING = "dataSourceEncoding";
	public static final String PARAM_BUNDLE_ENCODING = "bundleEncoding";
	public static final String PARAM_TARGET_PACKAGE = "targetPackage";
	public static final String I18N_KEY_RESOLVER_CLASS = "i18nKeyResolverClass";
	public static final String DATA_SOURCE_HANDLER_CLASS = "dataSourceHandlerClass";

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (annotations.isEmpty()) {
			return false;
		}
		String dataSourceEncoding = processingEnv.getOptions().get(PARAM_DATA_SOURCE_ENCODING);
		if (StringUtils.isBlank(dataSourceEncoding)) {
			dataSourceEncoding = "UTF-8";
		}
		String bundleEncoding = processingEnv.getOptions().get(PARAM_BUNDLE_ENCODING);
		if (StringUtils.isBlank(bundleEncoding)) {
			bundleEncoding = "ISO-8859-11";
		}
		try {
			List<ResourceBundle> bundles = new ArrayList<ResourceBundle>();
			for (String bundle : processingEnv.getOptions().get(PARAM_BUNDLES).split(",")) {
				bundles.add(ResourceBundle.getBundle(bundle.trim(), new SpecialEncodingControl(bundleEncoding)));
			}
			String servletUrl = processingEnv.getOptions().get(PARAM_SERVLET_URL).trim();

			DatasourceGenerator generator = new DatasourceGenerator(bundles, servletUrl, getKeyResolver(),
					getDatasourceHandler());

			StringWriter stringWriter = new StringWriter();

			generator.process(processingEnv.getMessager(), stringWriter,
					roundEnv.getElementsAnnotatedWith(GenerateUiInformation.class));

			String js = (stringWriter).toString();
			JavaScriptCompressor compressor = new JavaScriptCompressor(new StringReader(js), new ErrorReporter() {

				@Override
				public void warning(String message, String sourceName, int line, String lineSource, int lineOffset) {
					if (line < 0) {
						System.err.println("\n[WARNING] " + message);
					} else {
						System.err.println("\n[WARNING] " + line + ':' + lineOffset + ':' + message);
					}
				}

				@Override
				public void error(String message, String sourceName,
						int line, String lineSource, int lineOffset) {
					if (line < 0) {
						System.err.println("\n[ERROR] " + message);
					} else {
						System.err.println("\n[ERROR] " + line + ':' + lineOffset + ':' + message);
					}
				}

				@Override
				public EvaluatorException runtimeError(String message, String sourceName,
						int line, String lineSource, int lineOffset) {
					error(message, sourceName, line, lineSource, lineOffset);
					return new EvaluatorException(message);
				}
			});

			String pkg = processingEnv.getOptions().get(PARAM_TARGET_PACKAGE);
			if (pkg == null) {
				pkg = "";
			}
			FileObject file = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, pkg,
					"ds_compressed.js");
			Writer writer = new OutputStreamWriter(file.openOutputStream(), dataSourceEncoding);
			compressor.compress(writer, 100000, true, false, false, false);
			writer.close();

			file = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, pkg, "ds.js");
			writer = new OutputStreamWriter(file.openOutputStream(), dataSourceEncoding);
			writer.write(js);
			writer.close();

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

	private DatasourceHandler getDatasourceHandler()
			throws ClassNotFoundException, IllegalAccessException, InstantiationException {
		String datasourceHandlerClass = processingEnv.getOptions().get(DATA_SOURCE_HANDLER_CLASS);
		DatasourceHandler datasourceHandler = null;
		if (StringUtils.isNotBlank(datasourceHandlerClass)) {
			datasourceHandler = (DatasourceHandler) Class.forName(datasourceHandlerClass.trim()).newInstance();
		}
		if (datasourceHandler == null) {
			datasourceHandler = new DatasourceHandler();
		}
		return datasourceHandler;
	}
}
