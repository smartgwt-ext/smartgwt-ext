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
package com.github.smartgwt_ext.server.core.communication.model;

import com.github.smartgwt_ext.server.core.processing.EnhancedObjectMapper;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * @author Andreas Berger
 * @created 09.01.2013
 */
public class JsCriterion {
	public enum Operator {
		/** exactly equal to */
		equals("=", false, false),
		/** not equal to */
		notEqual("!=", false, false),
		/** exactly equal to, if case is disregarded */
		iEquals("=", true, false),
		/** not equal to, if case is disregarded */
		iNotEqual("!=", true, false),
		/** Greater than */
		greaterThan(">", false, false),
		/** Less than */
		lessThan("<", false, false),
		/** Greater than or equal to */
		greaterOrEqual(">=", false, false),
		/** Less than or equal to */
		lessOrEqual("<=", false, false),
		/** Contains as sub-string (match case) */
		contains("like", false, "%", "%", false),
		/** Starts with (match case) */
		startsWith("like", false, null, "%", false),
		/** Ends with (match case) */
		endsWith("like", false, "%", null, false),
		/** Contains as sub-string (case insensitive) */
		iContains("like", true, "%", "%", false),
		/** Starts with (case insensitive) */
		iStartsWith("like", true, null, "%", false),
		/** Ends with (case insensitive) */
		iEndsWith("like", true, "%", null, false),
		/** Does not contain as sub-string (match case) */
		notContains("not like", false, "%", "%", false),
		/** Does not start with (match case) */
		notStartsWith("not like", false, null, "%", false),
		/** Does not end with (match case) */
		notEndsWith("not like", false, "%", null, false),
		/** Does not contain as sub-string (case insensitive) */
		iNotContains("not like", true, "%", "%", false),
		/** Does not start with (case insensitive) */
		iNotStartsWith("not like", true, null, "%", false),
		/** Does not end with (case insensitive) */
		iNotEndsWith("not like", true, "%", null, false),

		/** matches another field (match case, specify fieldName as criterion.value) */
		equalsField("=", false, true),
		/** does not match another field (match case, specify fieldName as criterion.value) */
		notEqualField("!=", false, true),
		/** matches another field (case insensitive, specify fieldName as criterion.value) */
		iEqualsField("=", true, true),
		/** does not match another field (case insensitive, specify fieldName as criterion.value) */
		iNotEqualField("!=", true, true),
		/** Greater than another field (specify fieldName as criterion.value) */
		greaterThanField(">", false, true),
		/** Less than another field (specify fieldName as criterion.value) */
		lessThanField("<", false, true),
		/** Greater than or equal to another field (specify fieldName as criterion.value) */
		greaterOrEqualField(">=", false, true),
		/** Less than or equal to another field (specify fieldName as criterion.value) */
		lessOrEqualField("<=", false, true),
		/** Contains as sub-string (match case) another field value (specify fieldName as criterion.value) */
		containsField("like", false, "%", "%", true),
		/** Starts with (match case) another field value (specify fieldName as criterion.value) */
		startsWithField("like", false, null, "%", true),
		/** Ends with (match case) another field value (specify fieldName as criterion.value) */
		endsWithField("like", false, "%", null, true),
		/** Contains as sub-string (case insensitive) another field value (specify fieldName as criterion.value) */
		iContainsField("like", true, "%", "%", true),
		/** Starts with (case insensitive) another field value (specify fieldName as criterion.value) */
		iStartsWithField("like", true, null, "%", true),
		/** Ends with (case insensitive) another field value (specify fieldName as criterion.value) */
		iEndsWithField("like", true, "%", null, true),
		/** Does not contain as sub-string (match case) another field value (specify fieldName as criterion.value) */
		notContainsField("not like", false, "%", "%", true),
		/** Does not start with (match case) another field value (specify fieldName as criterion.value) */
		notStartsWithField("not like", false, null, "%", true),
		/** Does not end with (match case) another field value (specify fieldName as criterion.value) */
		notEndsWithField("not like", false, "%", null, true),
		/** Does not contain as sub-string (case insensitive) another field value (specify fieldName as criterion.value) */
		iNotContainsField("not like", true, "%", "%", true),
		/** Does not start with (case insensitive) another field value (specify fieldName as criterion.value) */
		iNotStartsWithField("not like", true, null, "%", true),
		/** Does not end with (case insensitive) another field value (specify fieldName as criterion.value) */
		iNotEndsWithField("not like", true, "%", null, true),

		/** Regular expression match */
		regexp,
		/** Regular expression match (case insensitive) */
		iregexp,
		/** value is null */
		isNull,
		/** value is non-null. Note empty string ("") is non-null */
		notNull,
		/** value is in a set of values. Specify criterion.value as an Array */
		inSet,
		/** value is not in a set of values. Specify criterion.value as an Array */
		notInSet,
		/** shortcut for "greaterThan" + "lessThan" + "and". Specify criterion.start and criterion.end */
		between,
		/** shortcut for "greaterOrEqual" + "lessOrEqual" + "and". Specify criterion.start and criterion.end */
		betweenInclusive,
		/** shortcut for "greaterOrEqual" + "and" + "lessOrEqual" (case insensitive) */
		iBetweenInclusive,


		/** all subcriteria (criterion.criteria) are true */
		and,
		/** all subcriteria (criterion.criteria) are false */
		not,
		/** at least one subcriteria (criterion.criteria) is true */
		or;

		private String prefix;
		private String suffix;
		private String operation;
		private boolean caseInsensitive;
		private boolean compareToField;
		private boolean defaultHandling;


		Operator(String operation, boolean caseInsensitive, String prefix, String suffix, boolean compareToField) {
			this(operation, caseInsensitive, compareToField);
			this.prefix = prefix;
			this.suffix = suffix;
		}


		Operator(String operation, boolean caseInsensitive, boolean compareToField) {
			this.operation = operation;
			this.caseInsensitive = caseInsensitive;
			this.compareToField = compareToField;
			defaultHandling = true;
		}


		Operator() {
		}

		public String getPrefix() {
			return prefix;
		}

		public String getSuffix() {
			return suffix;
		}

		public String getOperation() {
			return operation;
		}

		public boolean isCaseInsensitive() {
			return caseInsensitive;
		}

		public boolean isDefaultHandling() {
			return defaultHandling;
		}

		public boolean isCompareToField() {
			return compareToField;
		}
	}

	private Operator operator = Operator.and;

	private String fieldName;

	private Object value;

	private Object start;

	private Object end;

	private List<JsCriterion> criteria;

	public JsCriterion() {
	}

	public JsCriterion(String fieldName, Operator operator, Object value) {
		this.fieldName = fieldName;
		this.operator = operator;
		this.value = value;
	}

	public JsCriterion(Operator operator) {
		setOperator(operator);
	}

	public void setCriteria(List<JsCriterion> criteria) {
		this.criteria = criteria;
	}

	public void addCriteria(JsCriterion value) {
		if (criteria == null) {
			this.criteria = new ArrayList<JsCriterion>();
		}
		this.criteria.add(value);
	}

	public List<JsCriterion> getCriteria() {
		return criteria;
	}

	public Date getFieldValueAsDate(String id) {
		String value = getFieldValue(id);
		if (value == null) {
			return null;
		}
		try {
			return EnhancedObjectMapper.getSmartGWTDateFormat().parse(value);
		} catch (ParseException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T getFieldValue(String id) {
		if (criteria == null) {
			if (id.equals(getFieldName())) {
				return (T) getValue();
			}
			return null;
		}
		for (JsCriterion jsCriterion : criteria) {
			if (id.equals(jsCriterion.getFieldName())) {
				return (T) jsCriterion.getValue();
			}
		}
		return null;
	}

	public JsCriterion removeCriterion(String id) {
		if (criteria == null) {
			return null;
		}
		for (Iterator<JsCriterion> iterator = criteria.iterator(); iterator.hasNext(); ) {
			JsCriterion jsCriterion = iterator.next();
			if (id.equals(jsCriterion.getFieldName())) {
				iterator.remove();
				return jsCriterion;
			}
		}
		return null;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Object getStart() {
		return start;
	}

	public void setStart(Object start) {
		this.start = start;
	}

	public Object getEnd() {
		return end;
	}

	public void setEnd(Object end) {
		this.end = end;
	}
}
