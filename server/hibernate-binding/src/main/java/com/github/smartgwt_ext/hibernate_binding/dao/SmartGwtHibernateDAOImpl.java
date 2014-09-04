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
package com.github.smartgwt_ext.hibernate_binding.dao;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.smartgwt_ext.server.core.HasIdField;
import com.github.smartgwt_ext.server.core.communication.SmartGwtException;
import com.github.smartgwt_ext.server.core.communication.model.JsCriterion;
import com.github.smartgwt_ext.server.core.communication.model.JsSortSpecifier;
import com.github.smartgwt_ext.server.core.dao.FetchResult;
import com.github.smartgwt_ext.server.core.dao.SmartGwtDAOImpl;
import com.github.smartgwt_ext.server.core.processing.EnhancedObjectMapper;
import com.github.smartgwt_ext.server.introspection.facade.BeanInformationFactory;
import com.github.smartgwt_ext.server.introspection.facade.BeanInformationFromClass;
import com.github.smartgwt_ext.server.introspection.facade.PropertyInformationFromClass;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EmbeddedId;
import javax.persistence.MapsId;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

/**
 * @author Andreas Berger
 * @created 09.01.2013
 */
@SuppressWarnings("UnusedDeclaration")
public abstract class SmartGwtHibernateDAOImpl extends SmartGwtDAOImpl {

	private final Map<Class<?>, PkInfo> pkInfo = new HashMap<Class<?>, PkInfo>();

	private static final Logger LOGGER = LoggerFactory.getLogger(SmartGwtHibernateDAOImpl.class);

	private boolean cachingEnabled = true;

	protected abstract Session getSession();

	/**
	 * @return the cachingEnabled
	 */
	public boolean isCachingEnabled() {
		return cachingEnabled;
	}

	/**
	 * @param cachingEnabled the cachingEnabled to set
	 */
	public void setCachingEnabled(boolean cachingEnabled) {
		this.cachingEnabled = cachingEnabled;
	}

	@Override
	public <T> FetchResult<T> find(final Class<T> entityClass, final JsCriterion criteria,
			final Integer startRow,
			final Integer endRow, final List<JsSortSpecifier> sortBy)
			throws SmartGwtException {
		Session session = getSession();
		final StringBuilder sql = new StringBuilder("from ").append(entityClass.getSimpleName());

		List<Object> params = new ArrayList<Object>();
		addCiterias(criteria, sql, params, 0, session, entityClass, null);

		if (sortBy != null && !sortBy.isEmpty()) {
			boolean first = true;
			for (JsSortSpecifier sort : sortBy) {
				if (first) {
					sql.append(" order by ");
					first = false;
				} else {
					sql.append(", ");
				}
				sql.append(sort.getField().replace('_', '.'));
				if (sort.getDirection() == JsSortSpecifier.Direction.DESC) {
					sql.append(" desc");
				}
			}
		}
		try {
			Query queryCount = session.createQuery("select count(*) " + sql.toString());
			Query query = session.createQuery(sql.toString());
			return getFetchResult(startRow, endRow, query, params, queryCount, params);
		} catch (RuntimeException e) {
			LOGGER.error(sql.toString(), e);
			throw e;
		}
	}

	public <U> FetchResult<U> getFetchResult(Integer startRow, Integer endRow,
			Query query, List<Object> params,
			Query queryCount, List<Object> paramsCount) {
		if (startRow != null && endRow != null) {
			query.setFirstResult(startRow);
			query.setMaxResults(endRow - startRow);
		}
		for (int i = 0; i < params.size(); i++) {
			Object param = params.get(i);
			query.setParameter(String.valueOf(i + 1), param);
		}
		for (int i = 0; i < paramsCount.size(); i++) {
			Object param = paramsCount.get(i);
			queryCount.setParameter(String.valueOf(i + 1), param);
		}
		queryCount.setCacheable(cachingEnabled);
		query.setCacheable(cachingEnabled);
		Long count = (Long) queryCount.uniqueResult();
		List<U> result = Collections.emptyList();
		if (count > 0) {
			//noinspection unchecked
			result = query.list();
		}
		return new FetchResult<U>((int) count.longValue(), result);
	}

	public boolean addCiterias(JsCriterion criterion, StringBuilder sql, List<Object> params, int level,
			Session session,
			Class<?> entityClass, String alias) throws SmartGwtException {
		if (criterion == null) {
			return false;
		}
		if (criterion.getCriteria() != null) {
			if (criterion.getCriteria().isEmpty()) {
				return false;
			}
			if (level == 0) {
				sql.append(" where (");
			} else {
				sql.append(" (");
			}
			boolean first = true;
			boolean not = criterion.getOperator() == JsCriterion.Operator.not;
			for (JsCriterion jsCriterion : criterion.getCriteria()) {
				if (first) {
					first = false;
					if (not) {
						sql.append(" not(");
					}
				} else {
					if (criterion.getOperator() == JsCriterion.Operator.not) {
						sql.append(" or");
					} else {
						sql.append(" ").append(criterion.getOperator());
					}
				}
				addCiterias(jsCriterion, sql, params, level + 1, session, entityClass, alias);
			}
			if (not) {
				sql.append(")");
			}
			sql.append(")");
			return true;
		}

		String fieldname = criterion.getFieldName();
		if (fieldname == null) {
			return false;
		}
		SessionFactory factory = session.getSessionFactory();
		Object value = criterion.getValue();
		Object value2 = criterion.getEnd();
		if (criterion.getOperator().isCompareToField()) {
			if (value == null) {
				throw new SmartGwtException("Feld muss definiert sein");
			}
		} else {
			if (value == null) {
				value = criterion.getStart();
			}
			if (value == null) {
				criterion.setOperator(JsCriterion.Operator.isNull);
			}
			if (fieldname.contains("_")) {
				fieldname = fieldname.replace('_', '.');
			} else if (value != null) {
				Class<?> clazz = getPropertyType(entityClass, 0, factory, fieldname).getReturnedClass();
				if (HasIdField.class.isAssignableFrom(clazz)) {
					// hier wird die ID des referenzierten Entities noch
					// angehanen

					ClassMetadata meta2 = factory.getClassMetadata(clazz);
					fieldname += "." + meta2.getIdentifierPropertyName();
				}
			}
			Class<?> expectedClass = null;
			if (value != null || value2 != null) {
				expectedClass = getPropertyType(entityClass, 0, factory, fieldname.split("\\.")).getReturnedClass();
			}
			if (expectedClass != null) {
				if (value != null && !expectedClass.isAssignableFrom(value.getClass())) {
					value = convertValue(expectedClass, value);
				}
				if (value2 != null && !expectedClass.isAssignableFrom(value2.getClass())) {
					value2 = convertValue(expectedClass, value2);
				}
			}
		}
		if (alias != null) {
			fieldname = alias + "." + fieldname;
		}
		if (level == 0) {
			sql.append(" where ");
		} else {
			sql.append(" ");
		}
		switch (criterion.getOperator()) {
			case notNull:
				sql.append(fieldname).append(" is not null");
				break;
			case isNull:
				sql.append(fieldname).append(" is null");
				break;
			case iBetweenInclusive:
			case betweenInclusive:
				addCiteria(sql, params, fieldname, value, JsCriterion.Operator.greaterOrEqual);
				sql.append(" and ");
				addCiteria(sql, params, fieldname, value2, JsCriterion.Operator.lessOrEqual);
				break;
			case between:
				addCiteria(sql, params, fieldname, value, JsCriterion.Operator.greaterThan);
				sql.append(" and ");
				addCiteria(sql, params, fieldname, value2, JsCriterion.Operator.lessThan);
				break;
			default:
				if (!criterion.getOperator().isDefaultHandling()) {
					throw new SmartGwtException("Operation wird nicht unterstützt: " + criterion.getOperator());
				}
				addCiteria(sql, params, fieldname, value, criterion.getOperator());
		}
		return true;
	}

	protected void addCiteria(StringBuilder sql, List<Object> params, String fieldname, Object value,
			JsCriterion.Operator operator) throws SmartGwtException {
		if (value == null) {
			throw new SmartGwtException("Wert darf nicht null sein");
		}
		if (operator.isCaseInsensitive()) {
			sql.append("lower(cast(").append(fieldname).append(" as string))");
		} else {
			sql.append(fieldname);
		}
		sql.append(" ").append(operator.getOperation()).append(" ");
		if (operator.isCompareToField()) {
			if (operator.isCaseInsensitive()) {
				sql.append("lower(cast(").append(value).append(" as string))");
			} else {
				sql.append(value);
			}
		} else {
			sql.append("?").append(params.size() + 1);
			Object param = null;
			if (operator.getPrefix() != null) {
				param = operator.getPrefix();
			}
			if (param != null) {
				param = operator.isCaseInsensitive() ? param + value.toString().toLowerCase() : (String) param + value;
			} else {
				param = operator.isCaseInsensitive() ? value.toString().toLowerCase() : value;
			}
			if (operator.getSuffix() != null) {
				param = param + operator.getSuffix();
			}
			params.add(param);
		}

	}

	private Object convertValue(Class expectedClass, Object value) {
		if (Number.class.isAssignableFrom(expectedClass)) {
			if (value instanceof Number) {
				if (Long.class.isAssignableFrom(expectedClass)) {
					return ((Number) value).longValue();
				}
				if (BigDecimal.class.isAssignableFrom(expectedClass)) {
					return BigDecimal.valueOf(((Number) value).doubleValue());
				}
			}
			if (value instanceof String) {
				if (Long.class.isAssignableFrom(expectedClass)) {
					return Long.valueOf((String) value);
				}
				if (Integer.class.isAssignableFrom(expectedClass)) {
					return Integer.valueOf((String) value);
				}
				if (BigDecimal.class.isAssignableFrom(expectedClass)) {
					return new BigDecimal((String) value);
				}
			}
		}
		if (Date.class.isAssignableFrom(expectedClass)) {
			if (value instanceof String) {
				try {
					return EnhancedObjectMapper.getSmartGWTDateFormat().parse((String) value);
				} catch (ParseException e) {
					// ignore
				}
			}
		}
		if (expectedClass.isEnum() && value instanceof String) {
			return Enum.valueOf(expectedClass, (String) value);
		}
		if (value instanceof Collection<?>) {
			if (Set.class.isAssignableFrom(expectedClass)) {
				//noinspection unchecked
				return new HashSet((Collection<?>) value);
			}
		}
		throw new RuntimeException(expectedClass + " is not assignable from " + value.getClass());
	}

	private Type getPropertyType(Class<?> clazz, int pos, SessionFactory factory, String... path) {
		ClassMetadata meta = factory.getClassMetadata(clazz);
		Type type = meta.getPropertyType(path[pos]);
		if (pos == path.length - 1) {
			return type;
		}
		return getPropertyType(type.getReturnedClass(), pos + 1, factory, path);
	}

	@Override
	public <T> T initWithId(T entity, Class<T> clazz, JsonNode data) throws SmartGwtException {
		PkInfo info = getPkInformaion(clazz);
		if (info == null) {
			return entity;
		}
		try {
			Serializable pk = (Serializable) info.pkField.getType().newInstance();
			for (Map.Entry<String, PropertyInformationFromClass> entry : info.values.entrySet()) {
				Method setter = entry.getValue().getSetter();
				Class<?> type = entry.getValue().getType();
				JsonNode idNode = data.get(entry.getKey());
				if (idNode == null || idNode.isNull()) {
					continue;
				}
				if (String.class.isAssignableFrom(type)) {
					setter.invoke(pk, idNode.asText());
				} else if (Long.class.isAssignableFrom(type)) {
					setter.invoke(pk, idNode.asLong());
				} else {
					throw new SmartGwtException("Unsupported Type " + type.getName());
				}
			}
			if (info.pkField.getSetter() != null) {
				info.pkField.getSetter().invoke(entity, pk);
			} else {
				info.pkField.getField().setAccessible(true);
				info.pkField.getField().set(entity, pk);
			}
			return entity;
		} catch (Exception e) {
			throw new SmartGwtException("can't create PK for " + clazz.getSimpleName(), e);
		}
	}

	private PkInfo getPkInformaion(Class<?> clazz) {
		PkInfo info = pkInfo.get(clazz);
		if (info != null) {
			return info;
		}
		if (pkInfo.containsKey(clazz)) {
			return null;
		}
		synchronized (pkInfo) {
			info = pkInfo.get(clazz);
			if (info != null) {
				return info;
			}
			if (pkInfo.containsKey(clazz)) {
				return null;
			}
			BeanInformationFromClass bi = BeanInformationFactory.createBeanInformation(clazz);

			PropertyInformationFromClass pk = null;
			Map<String, String> mapsIdFields = new HashMap<String, String>();
			for (PropertyInformationFromClass prop : bi.getAllProperties()) {
				if (prop.getAnnotation(EmbeddedId.class) != null) {
					pk = prop;
				}
				MapsId mapsId = prop.getAnnotation(MapsId.class);
				if (mapsId != null) {
					mapsIdFields.put(mapsId.value(), prop.getName());
				}
			}

			if (pk == null) {
				pkInfo.put(clazz, null);
				return null;
			}
			Map<String, PropertyInformationFromClass> idProps = new HashMap<String, PropertyInformationFromClass>();
			BeanInformationFromClass pkBeanInfo = BeanInformationFactory.createBeanInformation(pk.getType());
			for (PropertyInformationFromClass pkProp : pkBeanInfo.getAllProperties()) {
				String classFieldName = mapsIdFields.get(pkProp.getName());
				if (classFieldName != null) {
					idProps.put(classFieldName, pkProp);
				} else {
					idProps.put(pkProp.getName(), pkProp);
				}
			}
			info = new PkInfo(pk, idProps);
			pkInfo.put(clazz, info);
			return info;
		}
	}

	private static class PkInfo {
		PropertyInformationFromClass pkField;
		Map<String, PropertyInformationFromClass> values;

		private PkInfo(PropertyInformationFromClass pkField,
				Map<String, PropertyInformationFromClass> values) {
			this.pkField = pkField;
			this.values = values;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getById(Class<T> clazz, Serializable id) {
		return (T) getSession().get(clazz, id);
	}

	@Override
	public void save(Object entity) {
		getSession().save(entity);
	}

	@Override
	public void remove(Object entity) {
		getSession().delete(entity);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T merge(T transientObject) {
		return (T) getSession().merge(transientObject);
	}
}
