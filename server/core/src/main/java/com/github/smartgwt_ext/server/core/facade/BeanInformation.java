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
package com.github.smartgwt_ext.server.core.facade;

import java.lang.annotation.Annotation;
import java.util.Collection;

/** @author Andreas Berger */
public interface BeanInformation<T extends PropertyInformation<?>> {

	/** @return  */
	String getSimpleName();

	String getName();

	/** @return properties of this bean, no inherited Properties */
	Collection<T> getProperties();

	/** @return all properties, including inherited Properties */
	Collection<T> getAllProperties();

	BeanInformation<T> getSuperBeanInformation();

	/**
	 * @param string
	 * @return
	 */
	T getProperty(String name);

	<A extends Annotation> A getAnnotation(Class<A> annotationClass);

	boolean isTypeOfClass(Class<?> clazz);

}
