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
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;
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

/** @author Andreas Berger */
@SupportedAnnotationTypes("com.github.smartgwt_ext.server.core.annotations.GenerateUiInformation")
@SupportedOptions({PARAM_SERVLET_URL, PARAM_BUNDLES, PARAM_ENCODING, PARAM_RESOUCR_PACKAGE})
@SupportedSourceVersion(SourceVersion.RELEASE_5)
public class DatasourceGeneratorProcessor extends AbstractProcessor {

	public static final String PARAM_SERVLET_URL = "servletURL";
	public static final String PARAM_BUNDLES = "bundles";
	public static final String PARAM_ENCODING = "encoding";
	public static final String PARAM_RESOUCR_PACKAGE = "package";

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (annotations.isEmpty()) {
			return false;
		}
		String encoding = processingEnv.getOptions().get(PARAM_ENCODING);
		if (encoding == null || encoding.isEmpty()) {
			encoding = "UTF-8";
		}
		try {
			List<ResourceBundle> bundles = new ArrayList<ResourceBundle>();
			for (String bundle : processingEnv.getOptions().get(PARAM_BUNDLES).split(",")) {
				bundles.add(ResourceBundle.getBundle(bundle.trim()));
			}
			String servletUrl = processingEnv.getOptions().get(PARAM_SERVLET_URL);

			DatasourceGenerator generator = new DatasourceGenerator(bundles, servletUrl);

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

			String pkg = processingEnv.getOptions().get(PARAM_RESOUCR_PACKAGE);
			if (pkg == null) {
				pkg = "";
			}
			FileObject file = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, pkg,
					"ds_compressed.js");
			Writer writer = new OutputStreamWriter(file.openOutputStream(), encoding);
			compressor.compress(writer, 100000, true, false, false, false);
			writer.close();

			file = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, pkg, "ds.js");
			writer = new OutputStreamWriter(file.openOutputStream(), encoding);
			writer.write(js);
			writer.close();

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return true;
	}
}
