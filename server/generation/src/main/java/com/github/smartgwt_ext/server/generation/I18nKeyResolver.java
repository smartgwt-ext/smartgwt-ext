package com.github.smartgwt_ext.server.generation;

import com.github.smartgwt_ext.server.introspection.BeanInformation;
import com.github.smartgwt_ext.server.introspection.PropertyInformation;

/**
 * @author Andreas Berger (latest modification by $Author$)
 * @version $Id$
 * @created 05.09.14 - 15:56
 */
public class I18nKeyResolver {

	public String getKey(BeanInformation<?> bean, PropertyInformation<?> property) {
		return bean.getSimpleName() + "_" + property.getName();
	}
}
