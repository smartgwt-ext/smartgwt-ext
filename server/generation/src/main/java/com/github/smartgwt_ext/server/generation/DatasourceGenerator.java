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
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.smartgwt_ext.server.core.annotations.DisplayField;
import com.github.smartgwt_ext.server.core.annotations.FieldFeatures;
import com.github.smartgwt_ext.server.core.annotations.GenerateUiInformation;
import com.github.smartgwt_ext.server.core.annotations.UiIgnore;
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
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.tools.Diagnostic.Kind;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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
	private Map<String, Collection<FieldInfo>> delegatedFields = new HashMap<String, Collection<FieldInfo>>();
	private I18nKeyResolver i18nKeyResolver;
	private DatasourceHandler datasourceHandler;

	public DatasourceGenerator(List<ResourceBundle> bundles,
			String servletUrl,
			I18nKeyResolver i18nKeyResolver,
			DatasourceHandler datasourceHandler
	) {
		super();
		this.bundles = bundles;
		this.servletUrl = servletUrl;
		this.i18nKeyResolver = i18nKeyResolver;
		this.datasourceHandler = datasourceHandler;
		missingProperties = new LinkedList<String>();
		mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		mapper.configure(SerializationFeature.CLOSE_CLOSEABLE, false);
		mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
		mapper.getFactory().disable(Feature.AUTO_CLOSE_TARGET);

		for (Map.Entry<Class<?>, Collection<String[]>> entry : this.datasourceHandler.getDelegateFields().entrySet()) {
			for (String[] path : entry.getValue()) {
				addDelegateField(BeanInformationFactory.createBeanInformation(entry.getKey()), path);
			}
		}
	}

	private void addDelegateField(BeanInformation<?> beanInformation, String... path) {

		StringBuilder pathString = new StringBuilder();
		for (String s : path) {
			if (pathString.length() > 0) {
				pathString.append('/');
			}
			pathString.append(s);
		}
		PropertyInformation<?> prop = null;
		boolean editable = false;
		BeanInformation<?> currentFieldType = beanInformation;
		for (String name : path) {
			prop = currentFieldType.getProperty(name);
			if (prop == null) {
				throw new IllegalArgumentException("Field not found: " + pathString);
			}
			if (prop.getAnnotation(FieldFeatures.class) != null
					&& prop.getAnnotation(FieldFeatures.class).delegateEditable()) {
				editable = true;
			}
			currentFieldType = prop.getBeanInformation();
		}
		assert prop != null;

		FieldInfo fi = new FieldInfo(pathString.toString(), false);
		fi.title = getText(prop, "", true);
		fi.prompt = getText(prop, "_prompt", false);
		fi.prop = prop;
		fi.editable = editable;
		addDelegateField(beanInformation, fi);
	}

	private void addDelegateField(BeanInformation<?> beanInformation, FieldInfo fieldInfo) {
		Collection<FieldInfo> infos = delegatedFields.get(beanInformation.getName());
		if (infos == null) {
			infos = new LinkedHashSet<FieldInfo>();
			delegatedFields.put(beanInformation.getName(), infos);
		}
		infos.add(fieldInfo);
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
		if (beanInfo.getAnnotation(GenerateUiInformation.class).reduceToDisplayField()) {
			return;
		}
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
		ds.setId(datasourceHandler.resolveDataSourceName(beanInfo));

		LinkedHashSet<FieldInfo> delegateFields = new LinkedHashSet<FieldInfo>();

		addFieldsOfClass(null, ds, delegateFields, beanInfo);

		BeanInformation<? extends PropertyInformation<?>> superBeanInformation = beanInfo.getSuperBeanInformation();
		if (superBeanInformation != null) {
			if (superBeanInformation.getAnnotation(MappedSuperclass.class) != null) {
				addFieldsOfSuperclass(null, ds, delegateFields, superBeanInformation);
			} else if (superBeanInformation.getAnnotation(GenerateUiInformation.class) != null) {
				ds.setInheritsFrom(datasourceHandler.resolveDataSourceName(superBeanInformation));
			}
		}
		for (FieldInfo delegateField : delegateFields) {
			addDelegateField(delegateField, ds);
		}
		return ds;
	}

	private void addDelegateField(FieldInfo delegateField, JsDatasource ds) {
		JsDatasourceField dsField = new JsDatasourceField();
		dsField.setName(delegateField.path.replace('/', '_'));
		dsField.setCanEdit(delegateField.editable);
		dsField.setCanSave(delegateField.editable);
		dsField.setHidden(delegateField.hidden);
		dsField.setValueMap(getValueMap(delegateField.prop));
		dsField.setType(getType(delegateField.prop));

		GenerateUiInformation uiInformation = delegateField.prop.getBeanInformation().getAnnotation(
				GenerateUiInformation.class);
		if (uiInformation != null && uiInformation.reduceToDisplayField()) {
			PropertyInformation<?> displayField = delegateField.prop.getBeanInformation().getAnnotatedProperty(
					DisplayField.class);

			dsField.setAccessPath(delegateField.path + "/" + displayField.getName());
		} else {
			if (delegateField.path.contains("/")) {
				dsField.setAccessPath(delegateField.path);
			}
		}
		dsField.setTitle(delegateField.title);
		dsField.setPrompt(delegateField.prompt);

		ds.addField(dsField);

	}

	private void addFieldsOfClass(String[] prefix, JsDatasource definition, Set<FieldInfo> delegateFields,
			BeanInformation<?> beanInfo) {

		Collection<FieldInfo> def = delegatedFields.get(beanInfo.getName());
		if (def != null) {
			delegateFields.addAll(def);
		}

		String xpathPrefix = "";
		String namePrefix = "";
		if (prefix != null) {
			for (String s : prefix) {
				xpathPrefix += "/" + s;
				namePrefix += s + "_";

			}
		}
		Map<String, PropertyInformation> idFields = new LinkedHashMap<String, PropertyInformation>();
		for (PropertyInformation<?> field : beanInfo.getProperties()) {
			JsonIgnore ignore = field.getAnnotation(JsonIgnore.class);
			if (ignore != null && ignore.value() && field.getAnnotation(JsonProperty.class) == null) {
				continue;
			}
			if (field.getAnnotation(EmbeddedId.class) != null) {
				for (PropertyInformation idField : field.getBeanInformation().getProperties()) {
					if (idField.isTransient()) {
						continue;
					}
					idFields.put(idField.getName(), idField);
				}
				removeMapsId(beanInfo, idFields);
			}
		}

		for (PropertyInformation<?> prop : beanInfo.getProperties()) {
			FieldFeatures features = prop.getAnnotation(FieldFeatures.class);
			boolean isId = prop.getAnnotation(Id.class) != null || prop.getAnnotation(MapsId.class) != null;

			if (AnnotationHelper.shouldIgnore(prop) && (!isId || (prefix != null))) {
				continue;
			}
			if (idFields.containsKey(prop.getName())) {
				// das ID-feld wird unten separat behandelt
				continue;
			}

			JsDatasourceField field = new JsDatasourceField();

			field.setName(namePrefix + prop.getName());
			if (isId && prefix == null) {
				field.setPrimaryKey(true);
			}
			if (prefix != null) {
				field.setAccessPath(xpathPrefix + "/" + prop.getName());
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
			if (prop.getAnnotation(UiIgnore.class) != null) {
				field.setHidden(true);
			}

			field.setType(getType(prop));
			field.setTitle(getText(prop, "", true));
			field.setPrompt(getText(prop, "_prompt", false));
			field.setValueMap(getValueMap(prop));

			addValidation(field, prop);

			if (prop.getAnnotation(Lob.class) != null) {
				field.setEditorType("PopUpTextAreaItem");
			}
			if (prop.isTypeOfClass(Boolean.class)) {
				field.setAllowEmptyValue(true);
			}
			datasourceHandler.handleCustomExtensions(prop, field);

			OneToOne oneToOne = prop.getAnnotation(OneToOne.class);
			OneToMany oneToMany = prop.getAnnotation(OneToMany.class);
			Embedded embedded = prop.getAnnotation(Embedded.class);
			ElementCollection elementCollection = prop.getAnnotation(ElementCollection.class);

			if (prop.getAnnotation(ManyToOne.class) != null) {
				if (prefix != null) {
					// foreign keys gehen nur eine Ebene
					continue;
				}
				addDisplayField(delegateFields, xpathPrefix, prop, features, field);
				if (features != null) {
					if (features.transferAll()) {
						field.setType(datasourceHandler.resolveDataSourceName(prop.getBeanInformation()));
					}
					if (features.flatten()) {
						addFieldsOfSuperclass(addPrefix(prefix, prop), definition, delegateFields,
								prop.getBeanInformation());
					}
				}
				field.setCanEdit(true);
				field.setForeignKey(getForeignKey(prop.getBeanInformation()));

			} else if (oneToOne != null || embedded != null) {
				addDisplayField(delegateFields, xpathPrefix, prop, features, field);
				if ((features != null && features.flatten()) || embedded != null) {
					GenerateUiInformation uiInformation = prop.getBeanInformation()
							.getAnnotation(GenerateUiInformation.class);
					if (uiInformation != null && uiInformation.reduceToDisplayField()) {
						PropertyInformation<?> displayField = prop.getBeanInformation().getAnnotatedProperty(
								DisplayField.class);
						field.setAccessPath(xpathPrefix + "/" + prop.getName() + "/" + displayField.getName());
					} else {
						addFieldsOfSuperclass(addPrefix(prefix, prop), definition, delegateFields,
								prop.getBeanInformation());
						continue;
					}
				}
				if (oneToOne != null) {
					field.setCanEdit(true);
					field.setForeignKey(getForeignKey(prop.getBeanInformation()));
				}
				if (features != null && features.transferAll()) {
					field.setType(datasourceHandler.resolveDataSourceName(prop.getBeanInformation()));
				}
			} else if (oneToMany != null) {
				field.setHidden(true);
				field.setMultiple(true);
				// TODO hibernate
//				ParameterizedType stringListType = (ParameterizedType) prop.getGenericType();
//				Class refType = (Class) stringListType.getActualTypeArguments()[0];
//
//				field.setForeignKey(getForeignKey(refType));
//				if (features.transferAll()) {
//					field.setType(getDatasourceId(refType));
//				}
			} else if (elementCollection != null) {
//				field.setRequired(!manyToOne.optional());
				field.setType(FieldType.ANY.getValue());
				field.setCanEdit(true);
				field.setMultiple(true);
			} else if (prop.getBeanInformation() != null
					&& prop.getBeanInformation().getAnnotation(GenerateUiInformation.class) != null) {
				continue;
			}

			definition.addField(field);
		}

		for (PropertyInformation<?> field : idFields.values()) {
			JsDatasourceField idField = new JsDatasourceField();
			idField.setType(getType(field));
			idField.setTitle(getText(beanInfo, field.getName(), "", true));
			idField.setPrompt(getText(beanInfo, field.getName(), "_prompt", false));
			idField.setRequired(true);
			idField.setValueMap(getValueMap(field));
			idField.setName(field.getName());
			idField.setPrimaryKey(true);
			definition.addField(idField);
		}
	}

	private void addDisplayField(Set<FieldInfo> delegateFields, String xpathPrefix,
			PropertyInformation<?> field, FieldFeatures fieldFeatures, JsDatasourceField dsField) {
		if (fieldFeatures != null && fieldFeatures.skipDisplayField()) {
			return;
		}
		PropertyInformation<?> displayField = field.getBeanInformation().getAnnotatedProperty(DisplayField.class);
		if (displayField == null) {
			return;
		}
		GenerateUiInformation ui = field.getBeanInformation().getAnnotation(GenerateUiInformation.class);
		if (ui != null && ui.reduceToDisplayField()) {
			return;
		}
		FieldInfo fi = new FieldInfo(xpathPrefix + field.getName() + "/" + displayField.getName(), true);
		fi.prop = displayField;
		fi.title = getText(field, "", true);
		fi.prompt = getText(field, "prompt", false);
		delegateFields.add(fi);
		dsField.setDisplayField(dsField.getName() + "_" + displayField.getName());
	}

	private void addValidation(JsDatasourceField dsField, PropertyInformation<?> field) {
		ManyToOne manyToOne = field.getAnnotation(ManyToOne.class);
		OneToOne oneToOne = field.getAnnotation(OneToOne.class);

		PropertyInformation<?> field2 = field;
		BeanInformation<?> bi = field.getBeanInformation();
		if (bi != null) {
			GenerateUiInformation ui = bi.getAnnotation(GenerateUiInformation.class);
			if (ui != null && ui.reduceToDisplayField()) {
				field2 = bi.getAnnotatedProperty(DisplayField.class);
			}
		}
		// TODO
//		interface javax.persistence.Enumerated
//		interface org.hibernate.validator.constraints.Email

		Column column = field.getAnnotation(Column.class);
		if (column == null) {
			AttributeOverride override = field.getAnnotation(AttributeOverride.class);
			if (override != null && override.name().equals(field2.getName())) {
				column = override.column();
			}
		}
		if ((manyToOne != null && !manyToOne.optional())
				|| (oneToOne != null && !oneToOne.optional())
				|| (column != null && !column.nullable())
				|| field.getAnnotation(NotNull.class) != null
				|| (field.getAnnotation(Id.class) != null && field.getAnnotation(GeneratedValue.class) == null)) {
			// TODO
//			dsField.addValidator(new JsValidator(ValidatorType.REQUIRED));
			dsField.setRequired(true);
		}
		if (field2.isTypeOfClass(String.class)) {
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
//					dsField.setLength(maxLength);
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
				values.put(name, getText(prop.getBeanInformation(), name, "", true));
			}
			return values;
		}
		return null;
	}

	private void removeMapsId(BeanInformation<?> beanInfo, Map<String, PropertyInformation> idFields) {
		if (beanInfo == null) {
			return;
		}
		for (PropertyInformation<?> field : beanInfo.getProperties()) {
			if (field.getAnnotation(MapsId.class) != null) {
				idFields.remove(field.getAnnotation(MapsId.class).value());
			}
		}
		removeMapsId(beanInfo.getSuperBeanInformation(), idFields);
	}

	private String[] addPrefix(String[] prefix, PropertyInformation field) {
		String[] p;
		if (prefix == null) {
			p = new String[]{field.getName()};
		} else {
			p = new String[prefix.length + 1];
			System.arraycopy(prefix, 0, p, 0, prefix.length);
			p[prefix.length] = field.getName();
		}
		return p;
	}

	private void addFieldsOfSuperclass(String[] prefix, JsDatasource ds,
			Set<FieldInfo> delegateFields, BeanInformation<?> bean) {
		addFieldsOfClass(prefix, ds, delegateFields, bean);
		BeanInformation<? extends PropertyInformation> superBean = bean.getSuperBeanInformation();
		if (superBean != null && superBean.getAnnotation(MappedSuperclass.class) != null) {
			addFieldsOfSuperclass(prefix, ds, delegateFields, superBean);
		}
	}

	private String getForeignKey(BeanInformation<?> bean) {
		return datasourceHandler.resolveDataSourceName(bean) + "." + getIdName(bean);
	}

	private String getIdName(BeanInformation<?> bean) {
		PropertyInformation<?> field = getIdField(bean);
		return field == null ? null : field.getName();
	}

	private PropertyInformation<?> getIdField(BeanInformation<?> bean) {
		if (bean == null) {
			return null;
		}
		for (PropertyInformation<?> prop : bean.getProperties()) {
			Id id = prop.getAnnotation(Id.class);
			if (id != null) {
				return prop;
			}
		}
		return getIdField(bean.getSuperBeanInformation());
	}

	private String getType(PropertyInformation<?> prop) {
		GenerateUiInformation uiInformation = null;
		BeanInformation<? extends PropertyInformation<?>> bean = prop.getBeanInformation();
		if (bean != null) {
			uiInformation = bean.getAnnotation(GenerateUiInformation.class);
		}
		if (uiInformation != null && uiInformation.reduceToDisplayField()) {
			prop = bean.getAnnotatedProperty(DisplayField.class);
		}
		FieldFeatures features = prop.getAnnotation(FieldFeatures.class);
		if (features != null && features.password() != FieldFeatures.Password.NONE) {
			return FieldType.PASSWORD.getValue();
		}
		if (prop.isTypeOfClass(String.class)) {

			return FieldType.TEXT.getValue();

		} else if (prop.isTypeOfClass(Long.class) || prop.isTypeOfClass(long.class)
				|| prop.isTypeOfClass(Integer.class) || prop.isTypeOfClass(int.class)) {

			return FieldType.LOCALEINT.getValue();

		} else if (prop.isTypeOfClass(Date.class)) {

			return FieldType.DATETIME.getValue();

		} else if (prop.isTypeOfClass(Boolean.class) || prop.isTypeOfClass(boolean.class)) {

			return FieldType.BOOLEAN.getValue();

		} else if (prop.isTypeOfClass(Float.class) || prop.isTypeOfClass(float.class)
				|| prop.isTypeOfClass(Double.class) || prop.isTypeOfClass(double.class)
				|| prop.isTypeOfClass(BigDecimal.class)) {

			return FieldType.LOCALEFLOAT.getValue();

		} else if (prop.isEnum()) {

			return FieldType.ENUM.getValue();

		}
		return null;
	}

	private String getText(PropertyInformation<?> property, String suffix, boolean errorWhenMissing) {
		return getText(property.getDeclaringTypeBeanInformation(), property.getName(), suffix, errorWhenMissing);
	}

	private String getText(BeanInformation bean, String propertyName, String suffix, boolean errorWhenMissing) {
		String id = i18nKeyResolver.getKey(bean, propertyName) + suffix;

		for (ResourceBundle bundle : bundles) {
			try {
				return bundle.getString(id);
			} catch (MissingResourceException e) {
				// ignore
			}
		}
		if (errorWhenMissing) {
			missingProperties.add(bean.getName() + ": " + id);
		}
		return null;

	}

	private static class FieldInfo {
		private String path;
		private boolean hidden;
		private boolean editable;
		private PropertyInformation<?> prop;
		private String title;
		private String prompt;

		public FieldInfo(String path, boolean hidden) {
			this.path = path;
			this.hidden = hidden;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}

			FieldInfo fieldInfo = (FieldInfo) o;

			if (path != null ? !path.equals(fieldInfo.path) : fieldInfo.path != null) {
				return false;
			}

			return true;
		}

		@Override
		public int hashCode() {
			return path != null ? path.hashCode() : 0;
		}
	}
}
