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

package com.github.smartgwt_ext.server.core.processing;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.smartgwt_ext.server.core.DelegateFieldInformation;
import com.github.smartgwt_ext.server.core.HasIdField;
import com.github.smartgwt_ext.server.core.annotations.FieldFeatures;
import com.github.smartgwt_ext.server.core.processing.entities.Delegate;
import com.github.smartgwt_ext.server.core.processing.entities.Product;
import com.github.smartgwt_ext.server.core.processing.entities.ReduceToDisplayField;
import com.github.smartgwt_ext.server.introspection.BeanInformation;
import com.github.smartgwt_ext.server.introspection.PropertyInformation;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;

/**
 * @author Andreas Berger
 * @created 13.09.14 - 23:16
 */
public class TestObjectMapper {

	@Test
	public void testReduceToDisplayField() throws IOException {
		DelegateFieldInformation delegates = new DelegateFieldInformation();
		delegates.addDelegateField(Product.class, "delegate", "name");
		delegates.addDelegateField(Product.class, "delegate", "delegate2", "name");
		EnhancedObjectMapper enhancedObjectMapper = new EnhancedObjectMapper(delegates) {
			@Override
			protected <T extends HasIdField> T getEntityById(Serializable id, Class<T> entityClass) {
				return (T) new Delegate((Integer) id, null);
			}

			@Override
			protected PasswordHandler getPasswordHandler(BeanInformation<?> bean, PropertyInformation<?> prop,
					FieldFeatures.Password password) {
				return null;
			}
		};

		Delegate delegate = new Delegate(2, new ReduceToDisplayField("bar"));
		delegate.setDelegate2(new Delegate(3, new ReduceToDisplayField("42")));
		Product product = new Product(1, new ReduceToDisplayField("foo"), delegate);

		StringWriter out = new StringWriter();
		enhancedObjectMapper.getMapper().configure(SerializationFeature.INDENT_OUTPUT, false);
		enhancedObjectMapper.getMapper().writeValue(out, product);

		product = enhancedObjectMapper.getMapper().readValue(out.toString(), Product.class);
		Assert.assertEquals(out.toString(),
				"{\"id\":1,\"name\":\"foo\",\"delegate\":2,\"delegate_name\":\"bar\",\"delegate_delegate2_name\":\"42\"}");
	}
}
