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
package com.github.smartgwt_ext.server.core.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Stuert die Eigenschaften die beim generieren und (De)Serialisieren eines
 * Feldes zu berücksichtigen sind.
 *
 * @author Andreas Berger
 */
@Documented
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface FieldFeatures {
	enum Password {
		NONE, ENCRYPTED, HASHED
	}

	/**
	 * Gibt an, ob das Feld im UI editierbar sein soll
	 *
	 * @return
	 */
	boolean immutable() default false;

	/**
	 * Gibt an, dass dieses Feld im UI nicht zur anzeige kommen soll.
	 *
	 * @return
	 */
	boolean hidden() default false;

	/**
	 * defines this field as an password field. The Data is not transferred to the client. A passowrd field will be used
	 * for the GUI. Defaults to NONE.
	 *
	 * @return if not NONE a password field will be generated
	 */
	Password password() default Password.NONE;

	/**
	 * gibt an, dass dieses Feld nicht an das UI übertragen wird
	 *
	 * @deprecated use {@link UiIgnore}
	 */
	@Deprecated boolean ignore() default false;

	boolean flatten() default false;

	/**
	 * @return if true, all fields will be transferred with the parent entity
	 */
	boolean transferAll() default false;

	/**
	 * @return if true, no display field will be transferred for the field
	 */
	boolean skipDisplayField() default false;

	/**
	 * @return if true, delegate field will be editable
	 */
	boolean delegateEditable() default false;
}
