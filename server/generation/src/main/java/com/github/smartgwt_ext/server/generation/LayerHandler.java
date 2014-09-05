package com.github.smartgwt_ext.server.generation;

import com.github.smartgwt_ext.server.introspection.BeanInformation;

/**
 * Override to customize layer generation.
 *
 * @author Andreas Berger
 * @created 05.09.14 - 12:43
 */
public class LayerHandler {
	private String sourcePackage;
	private String destinationPackage;

	public LayerHandler(String sourcePackage, String destinationPackage) {
		this.sourcePackage = sourcePackage;
		this.destinationPackage = destinationPackage;
	}

	public String resolveDataSourceName(BeanInformation<?> beanInformation) {
		return beanInformation.getSimpleName();
	}

	/**
	 * @param beanInformation
	 * @return the full qualified name of the GWT-Overlay Type
	 */
	public String resolveLayerName(BeanInformation<?> beanInformation) {
		String result = beanInformation.getName().replace(sourcePackage, destinationPackage);
		int pos = result.lastIndexOf('.');
		return result.substring(0, pos) + ".Js" + result.substring(pos + 1);
	}

	/**
	 * @param type the type of the source property
	 * @return the type to use in the layer
	 */
	public String getLayerType(String type) {
		return type;
	}
}
