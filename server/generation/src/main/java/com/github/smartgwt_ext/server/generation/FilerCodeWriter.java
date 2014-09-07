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

import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JPackage;

import javax.annotation.processing.Filer;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Andreas Berger
 * @created 05.09.14 - 15:39
 */
public class FilerCodeWriter extends CodeWriter {
	private static final String JAVA_SOURCE_SUFFIX = ".java";
	private final Filer filer;
	private final List<OutputStream> closeables = new ArrayList<OutputStream>();

	public FilerCodeWriter(Filer filer) {
		this.filer = filer;
	}

	@Override
	public OutputStream openBinary(JPackage pkg, String fileName) throws IOException {
		if (!fileName.endsWith(JAVA_SOURCE_SUFFIX)) {
			throw new IllegalStateException("Unexpected file name passed to code writer: " + fileName);
		}
		String className = fileName.substring(0, fileName.length() - JAVA_SOURCE_SUFFIX.length());
		if (className.endsWith("$Hidden")) {
			return new NullOutputStream();
		} else {
			JavaFileObject fileObject = filer.createSourceFile(pkg.name() + "." + className);
			OutputStream stream = fileObject.openOutputStream();
			closeables.add(stream);
			return stream;
		}
	}

	@Override
	public void close() throws IOException {
		Exception exception = null;
		for (OutputStream stream : closeables) {
			try {
				stream.close();
			} catch (IOException ex) {
				if (exception != null) {
					ex.addSuppressed(exception);
				}
				exception = ex;
			}
		}
		if (exception != null) {
			if (exception instanceof IOException) {
				throw (IOException) exception;
			} else if (exception instanceof RuntimeException) {
				throw (RuntimeException) exception;
			} else {
				throw new IllegalStateException("Unexpected exception", exception);
			}
		}
	}

	private static class NullOutputStream extends OutputStream {
		@Override
		public void write(int b) throws IOException {
		}
	}
}
