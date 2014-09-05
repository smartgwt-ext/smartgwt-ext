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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.smartgwt_ext.server.core.HasIdField;
import com.github.smartgwt_ext.server.core.annotations.FieldFeatures;
import com.github.smartgwt_ext.server.core.processing.EnhancedObjectMapper;
import com.github.smartgwt_ext.server.generation.model.JsDatasource;
import com.github.smartgwt_ext.server.generation.model.JsDatasourceField;
import com.github.smartgwt_ext.server.generation.model.JsValidator;
import com.github.smartgwt_ext.server.introspection.BeanInformation;
import com.github.smartgwt_ext.server.introspection.BeanInformationFactory;
import com.github.smartgwt_ext.server.introspection.PropertyInformation;
import com.smartgwt.client.types.FieldType;
import com.smartgwt.client.types.ValidatorType;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.tools.Diagnostic.Kind;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Erzeugt die Javaskript Datei zur Verwedung mit diesem Framework
 *
 * @author Andreas Berger
 */
public class DatasourceGenerator {

	private List<ResourceBundle> bundles;
	private List<String> missingProperties;
	private ObjectMapper mapper;
	private String servletUrl;

	public DatasourceGenerator(List<ResourceBundle> bundles, String servletUrl) {
		super();
		this.bundles = bundles;
		this.servletUrl = servletUrl;
		missingProperties = new LinkedList<String>();
		mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		mapper.configure(SerializationFeature.CLOSE_CLOSEABLE, false);
		mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
		mapper.getFactory().disable(Feature.AUTO_CLOSE_TARGET);
	}

	public void process(Writer writer, Class<?>... classes) throws IOException {
		writeBaseClass(writer);
		for (Class<?> clazz : classes) {
			writeDatasource(writer, BeanInformationFactory.createBeanInformation(clazz));
		}

		if (!missingProperties.isEmpty()) {
			System.err.println("Missing Ressource Properties: ");
			for (String string : missingProperties) {
				System.err.println(string);
			}
		}

	}

	public void process(Messager messager, Writer writer, Set<? extends Element> elements)
			throws IOException {
		writeBaseClass(writer);
		for (Element element : elements) {
			if (!(element instanceof TypeElement)) {
				continue;
			}
			writeDatasource(writer, BeanInformationFactory.createBeanInformation((TypeElement) element));
		}

		if (!missingProperties.isEmpty()) {
			messager.printMessage(Kind.ERROR, "Missing Ressource Properties: ");
			for (String string : missingProperties) {
				messager.printMessage(Kind.ERROR, string);
			}
		}

	}

	private void writeDatasource(Writer writer, BeanInformation<?> beanInfo) throws IOException {
		JsDatasource ds = generateDatasource(beanInfo);
		writer.write("isc.JsonDataSource.create(");
		mapper.writeValue(writer, ds);
		writer.write(");\n");
	}

	private void writeBaseClass(Writer writer) throws IOException {
		writer.append("isc.defineClass(\"JsonDataSource\", \"RestDataSource\");\n")
				.append("isc.JsonDataSource.addProperties({\n")
				.append("    dataFormat:\"json\",\n")
				.append("    dataURL:\"").append(servletUrl).append("\",\n")
				.append("    jsonPrefix:null,\n")
				.append("    jsonSuffix:null,\n")
				.append("    operationBindings:[\n")
				.append("        {\n")
				.append("            operationType:\"fetch\",\n")
				.append("            dataProtocol:\"postMessage\"\n")
				.append("        },\n")
				.append("        {\n")
				.append("            operationType:\"add\",\n")
				.append("            dataProtocol:\"postMessage\"\n")
				.append("        },\n")
				.append("        {\n")
				.append("            operationType:\"update\",\n")
				.append("            dataProtocol:\"postMessage\"\n")
				.append("        },\n")
				.append("        {\n")
				.append("            operationType:\"remove\",\n")
				.append("            dataProtocol:\"postMessage\"\n")
				.append("        },\n")
				.append("        {\n")
				.append("            operationType:\"custom\",\n")
				.append("            dataProtocol:\"postMessage\"\n")
				.append("        }\n")
				.append("    ],\n"
						+ "    transformResponse : function (dsResponse, dsRequest, data) {\n"
						+ "        dsResponse = this.Super(\"transformResponse\", arguments);\n"
						+ "        dsResponse.relatedUpdates = data.response.relatedUpdates;\n"
						+ "        return dsResponse;\n"
						+ "    }\n")
				.append("});\n");
	}

	private JsDatasource generateDatasource(BeanInformation<?> beanInfo) {
		JsDatasource ds = new JsDatasource();
		ds.setId(beanInfo.getSimpleName());

		List<JsDatasourceField> fields = new ArrayList<JsDatasourceField>();

		for (PropertyInformation<?> prop : beanInfo.getAllProperties()) {
			if (prop.getAnnotation(JsonIgnore.class) != null) {
				continue;
			}
			FieldFeatures features = prop.getAnnotation(FieldFeatures.class);
			if (features != null && features.ignore()) {
				continue;
			}
			JsDatasourceField field = new JsDatasourceField();

			field.setName(prop.getName());
			if (prop.getAnnotation(Id.class) != null) {
				field.setPrimaryKey(true);
			}

			field.setType(getType(prop));
			field.setTitle(getText(prop.getDeclaringTypeSimpleName(), prop.getName(), "", true));
			field.setPrompt(getText(prop.getDeclaringTypeSimpleName(), prop.getName(), "_prompt", false));
			field.setValueMap(getValueMap(prop));

			addValidation(field, prop);

			if (prop.getAnnotation(ManyToOne.class) != null) {
				BeanInformation<?> referenceType = prop.getBeanInformation();
				if (prop.isTypeOfClass(HasIdField.class)) {
					field.setForeignKey(prop.getTypeSimpleName() + ".id");
					field.setType(getType(referenceType.getProperty("id")));
				}
				PropertyInformation<?> displayFieldProperty = EnhancedObjectMapper.getDisplayField(referenceType);
				if (displayFieldProperty != null) {

					JsDatasourceField displayField = new JsDatasourceField();
					displayField.setName(prop.getName() + "_" + displayFieldProperty.getName());
					displayField.setType(getType(displayFieldProperty));
					displayField.setHidden(true);

					fields.add(displayField);

					field.setDisplayField(displayField.getName());
					field.setEditorType("SelectItem");
					Map<String, Object> props = new LinkedHashMap<String, Object>();
					props.put("displayField", displayFieldProperty.getName());
					props.put("allowEmptyValue", field.getRequired() == null || !field.getRequired());
					field.setEditorProperties(props);
				}
			}
			if (features != null) {
				if (features.immutable()) {
					field.setCanEdit(false);
					field.setCanSave(false);
				}
				if (features.hidden()) {
					field.setHidden(true);
				}
			}
			fields.add(field);
		}

		ds.setFields(fields);
		return ds;
	}

	private void addValidation(JsDatasourceField dsField, PropertyInformation<?> field) {
		ManyToOne manyToOne = field.getAnnotation(ManyToOne.class);
		OneToOne oneToOne = field.getAnnotation(OneToOne.class);

		Column column = field.getAnnotation(Column.class);
		if (column == null) {
			AttributeOverride override = field.getAnnotation(AttributeOverride.class);
			if (override != null) {
				column = override.column();
			}
		}
		if ((manyToOne != null && !manyToOne.optional())
				|| (oneToOne != null && !oneToOne.optional())
				|| (column != null && !column.nullable())
				|| field.getAnnotation(NotNull.class) != null
				|| (field.getAnnotation(Id.class) != null && field.getAnnotation(GeneratedValue.class) == null)) {
			dsField.setRequired(true);
		}

		if (field.isTypeOfClass(String.class)) {
			Integer minLength = null;
			Integer maxLength = null;
			if (field.getAnnotation(NotEmpty.class) != null) {
				minLength = 1;
			}
			if (column != null && field.getAnnotation(Lob.class) == null) {
				maxLength = column.length();
			}
			Size size = field.getAnnotation(Size.class);
			Length length = field.getAnnotation(Length.class);
			if (size != null) {
				if (minLength == null) {
					minLength = size.min();
				} else {
					minLength = Math.max(minLength, size.min());
				}
				if (maxLength == null) {
					maxLength = size.max();
				} else {
					maxLength = Math.min(maxLength, size.max());
				}
			} else if (length != null) {
				if (minLength == null) {
					minLength = length.min();
				} else {
					minLength = Math.max(minLength, length.min());
				}
				if (maxLength == null) {
					maxLength = length.max();
				} else {
					maxLength = Math.min(maxLength, length.max());
				}
			}
			if (minLength != null || maxLength != null) {
				JsValidator validator = new JsValidator(ValidatorType.LENGTHRANGE);
				if (minLength != null) {
					validator.setMin(minLength);
				}
				if (maxLength != null) {
					validator.setMax(maxLength);
					dsField.setLength(maxLength);
				}
				dsField.addValidator(validator);
			}
		}

		Pattern pattern = field.getAnnotation(Pattern.class);
		if (pattern != null) {
			JsValidator validator = new JsValidator(ValidatorType.REGEXP);
			validator.setExpression(pattern.regexp());
			dsField.addValidator(validator);
		}

		Range range = field.getAnnotation(Range.class);
		if (range != null) {
			JsValidator validator;
			if (field.isTypeOfClass(Integer.class) || field.isTypeOfClass(int.class) ||
					field.isTypeOfClass(Long.class) || field.isTypeOfClass(long.class)) {
				validator = new JsValidator(ValidatorType.INTEGERRANGE);
			} else {
				validator = new JsValidator(ValidatorType.FLOATRANGE);
			}
			if (range.max() < Long.MAX_VALUE) {
				validator.setMax((int) range.max());
			}
			validator.setMin((int) range.min());
			dsField.addValidator(validator);
		}
	}

	private Map<String, Object> getValueMap(PropertyInformation<?> prop) {
		if (prop.isEnum()) {
			Map<String, Object> values = new LinkedHashMap<String, Object>();
			for (String name : prop.getEnumNames()) {
				values.put(name, getText(prop.getTypeSimpleName(), name, "", true));
			}
			return values;
		}
		return null;
	}

	private String getType(PropertyInformation<?> inf) {
		FieldFeatures features = inf.getAnnotation(FieldFeatures.class);
		if (features != null && features.password()) {
			return FieldType.PASSWORD.getValue();
		}
		if (inf.isTypeOfClass(String.class)) {
			return FieldType.TEXT.getValue();

		} else if (inf.isTypeOfClass(Long.class) || inf.isTypeOfClass(long.class)
				|| inf.isTypeOfClass(Integer.class) || inf.isTypeOfClass(int.class)) {

			return FieldType.LOCALEINT.getValue();

		} else if (inf.isTypeOfClass(Date.class)) {

			return FieldType.DATETIME.getValue();

		} else if (inf.isTypeOfClass(Boolean.class) || inf.isTypeOfClass(boolean.class)) {

			return FieldType.BOOLEAN.getValue();

		} else if (inf.isTypeOfClass(Float.class) || inf.isTypeOfClass(float.class)
				|| inf.isTypeOfClass(Double.class) || inf.isTypeOfClass(double.class)
				|| inf.isTypeOfClass(BigDecimal.class)) {

			return FieldType.LOCALEFLOAT.getValue();

		} else if (inf.isEnum()) {

			return FieldType.ENUM.getValue();

		}
		return null;
	}

	private String getText(String simpleName, String field, String suffix,
			boolean errorWhenMissing) {
		String id = simpleName + '_' + field + suffix;
		for (ResourceBundle bundle : bundles) {
			try {
				return bundle.getString(id);
			} catch (MissingResourceException e) {
				// ignore
			}
		}
		if (errorWhenMissing) {
			missingProperties.add(id);
		}
		return null;

	}
}
