package com.github.smartgwt_ext.server.generation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.smartgwt_ext.server.core.annotations.FieldFeatures;
import com.github.smartgwt_ext.server.core.annotations.UiIgnore;
import com.github.smartgwt_ext.server.introspection.PropertyInformation;

import javax.persistence.ElementCollection;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

/**
 * @author Andreas Berger (latest modification by $Author$)
 * @version $Id$
 * @created 05.09.14 - 15:31
 */
public class AnnotationHelper {

	public static boolean shouldIgnore(PropertyInformation<?> prop) {
		if (prop.getAnnotation(UiIgnore.class) != null) {
			return true;
		}
		JsonIgnore jsonIgnore = prop.getAnnotation(JsonIgnore.class);
		if (jsonIgnore != null && jsonIgnore.value() && prop.getAnnotation(JsonProperty.class) == null) {
			return true;
		}
		FieldFeatures features = prop.getAnnotation(FieldFeatures.class);

		if (features == null) {
			if (prop.getAnnotation(OneToMany.class) != null
					|| prop.getAnnotation(ManyToMany.class) != null
					|| prop.getAnnotation(ElementCollection.class) != null
					) {
				// ignore OneToMany and ManyToMany by default
				return true;
			}
		} else if (features.ignore()) {
			return true;
		}
		return false;
	}
}
