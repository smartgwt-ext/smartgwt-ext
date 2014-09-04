/*
 * Copyright 2012-Copyright 2012-2014 the original author or authors.
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

package com.github.smartgwt_ext.server.introspection.facade;

import javax.lang.model.element.TypeElement;
import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Andreas Berger
 *         Date: 04.09.14 13:42
 */
public final class BeanInformationFactory {

    private static final Map<Object, SoftReference<BeanInformation<?>>> SOFT_REFERENCE_MAP = new ConcurrentHashMap<Object, SoftReference<BeanInformation<?>>>();

    private BeanInformationFactory() {
    }

    public static BeanInformationFromClass createBeanInformation(Class<?> clazz) {
        SoftReference<BeanInformation<?>> ref = SOFT_REFERENCE_MAP.get(clazz);
		BeanInformationFromClass bi;
        if (ref != null){
            //noinspection unchecked
            bi = (BeanInformationFromClass) ref.get();
            if (bi != null){
                return bi;
            }
        }
        bi = new BeanInformationFromClass(clazz);
        SOFT_REFERENCE_MAP.put(clazz, new SoftReference<BeanInformation<?>>(bi));
        return bi;
    }

    public static BeanInformationFromSource createBeanInformation(TypeElement type) {
        SoftReference<BeanInformation<?>> ref = SOFT_REFERENCE_MAP.get(type);
		BeanInformationFromSource bi;
        if (ref != null){
            //noinspection unchecked
            bi = (BeanInformationFromSource) ref.get();
            if (bi != null){
                return bi;
            }
        }
        bi = new BeanInformationFromSource(type);
        SOFT_REFERENCE_MAP.put(type, new SoftReference<BeanInformation<?>>(bi));
        return bi;
    }
}
