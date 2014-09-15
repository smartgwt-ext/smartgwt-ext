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

package com.github.smartgwt_ext.frontend.server_binding.layer;


import com.google.gwt.core.client.JavaScriptObject;
import com.smartgwt.client.data.Record;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Andreas Berger
 * @created 20.03.2007 - 21:51:49
 */
public class JsBase extends JavaScriptObject implements Serializable {

	protected JsBase() {
	}

	public static native String getAttribute(JavaScriptObject elem, String attr) /*-{
        var ret = elem.__base ? elem.__base[elem.__prefix + attr] : elem[attr];
        return (ret === undefined || ret == null) ? null : String(ret);
    }-*/;

	public static native void setAttribute(JavaScriptObject elem, String attr, String value) /*-{
        if (elem.__base) {
            elem.__base[elem.__prefix + attr] = value;
        } else {
            elem[attr] = value;
        }
    }-*/;

	public static native Integer getAttributeAsInt(JavaScriptObject elem, String attr) /*-{
        var ret = elem.__base ? elem.__base[elem.__prefix + attr] : elem[attr];
        return (ret === undefined || ret == null) ? null
                : @com.github.smartgwt_ext.frontend.server_binding.layer.JsBase::toInteger(I)(ret);
    }-*/;

	public static native Long getAttributeAsLong(JavaScriptObject elem, String attr) /*-{
        var ret = elem.__base ? elem.__base[elem.__prefix + attr] : elem[attr];
        return (ret === undefined || ret == null) ? null
                : @com.github.smartgwt_ext.frontend.server_binding.layer.JsBase::toLong(D)(ret);
    }-*/;


	public static native Double getAttributeAsDouble(JavaScriptObject elem, String attr) /*-{
        var ret = elem.__base ? elem.__base[elem.__prefix + attr] : elem[attr];
        if (ret === undefined || ret == null) {
            return null;
        } else {
            if (typeof ret == "object") {
                return ret;
            } else {
                return  @com.github.smartgwt_ext.frontend.server_binding.layer.JsBase::toDouble(D)(ret);
            }
        }
    }-*/;

	public static native BigDecimal getAttributeAsBigDecimal(JavaScriptObject elem, String attr) /*-{
        var ret = elem.__base ? elem.__base[elem.__prefix + attr] : elem[attr];
        if (ret === undefined || ret == null) {
            return null;
        } else {
            if (typeof ret == "object") {
                return ret;
            } else {
                return  @com.github.smartgwt_ext.frontend.server_binding.layer.JsBase::toBigDecimal(D)(ret);
            }
        }
    }-*/;

	public static native Float getAttributeAsFloat(JavaScriptObject elem, String attr) /*-{
        var ret = elem.__base ? elem.__base[elem.__prefix + attr] : elem[attr];
        return (ret === undefined || ret == null) ? null
                : @com.github.smartgwt_ext.frontend.server_binding.layer.JsBase::toFloat(F)(ret);
    }-*/;

	public static native boolean getAttributeAsBoolean(JavaScriptObject elem, String attr) /*-{
        var ret = elem.__base ? elem.__base[elem.__prefix + attr] : elem[attr];
        return (ret == null || ret === undefined) ? false : ret;
    }-*/;

	public static Character getAttributeAsCharacter(JavaScriptObject elem, String attr) {
		String val = getAttribute(elem, attr);
		if (val == null || val.length() == 0) {
			return null;
		}
		return val.charAt(0);
	}


	public static Integer toInteger(int value) {
		return value;
	}

	public static Long toLong(double value) {
		return (long) value;
	}

	public static Float toFloat(float value) {
		return value;
	}

	public static Double toDouble(double value) {
		return value;
	}

	public static BigDecimal toBigDecimal(double value) {
		return BigDecimal.valueOf(value);
	}

	public static native void setAttribute(JavaScriptObject elem, String attr, int value) /*-{
        if (elem.__base) {
            elem.__base[elem.__prefix + attr] = value;
        } else {
            elem[attr] = value;
        }
    }-*/;

	public static void setAttribute(JavaScriptObject elem, String attr, long value) {
		setAttribute(elem, attr, new Double(value).doubleValue());
	}

	public static void setAttribute(JavaScriptObject elem, String attr, Integer value) {
		if (value == null) {
			setNullAttribute(elem, attr);
		} else {
			setAttribute(elem, attr, value.intValue());
		}
	}

	public static void setAttribute(JavaScriptObject elem, String attr, Long value) {
		if (value == null) {
			setNullAttribute(elem, attr);
		} else {
			setAttribute(elem, attr, value.longValue());
		}
	}

	public static void setAttribute(JavaScriptObject elem, String attr, Double value) {
		if (value == null) {
			setNullAttribute(elem, attr);
		} else {
			setAttribute(elem, attr, value.doubleValue());
		}
	}

	public static void setAttribute(JavaScriptObject elem, String attr, Float value) {
		if (value == null) {
			setNullAttribute(elem, attr);
		} else {
			setAttribute(elem, attr, value.floatValue());
		}
	}

	public static void setAttribute(JavaScriptObject elem, String attr, Boolean value) {
		if (value == null) {
			setNullAttribute(elem, attr);
		} else {
			setAttribute(elem, attr, value.booleanValue());
		}
	}

	public static void setAttribute(JavaScriptObject elem, String attr, BigDecimal value) {
		if (value == null) {
			setNullAttribute(elem, attr);
		} else {
			setAttribute(elem, attr, value.doubleValue());
		}
	}

	public static native void setNullAttribute(JavaScriptObject elem, String attr) /*-{
        if (elem.__base) {
            elem.__base[elem.__prefix + attr] = value;
        } else {
            elem[attr] = value;
        }
    }-*/;

	public static native void setAttribute(JavaScriptObject elem, String attr, boolean value) /*-{
        if (elem.__base) {
            elem.__base[elem.__prefix + attr] = value;
        } else {
            elem[attr] = value;
        }
    }-*/;

	public static native void setAttribute(JavaScriptObject elem, String attr, float value) /*-{
        if (elem.__base) {
            elem.__base[elem.__prefix + attr] = value;
        } else {
            elem[attr] = value;
        }
    }-*/;


	public static native void setAttribute(JavaScriptObject elem, String attr, double value) /*-{
        if (elem.__base) {
            elem.__base[elem.__prefix + attr] = value;
        } else {
            elem[attr] = value;
        }
    }-*/;

	public static void setAttribute(JavaScriptObject elem, String attr, char value) {
		setAttribute(elem, attr, String.valueOf(value));
	}

	protected final native <T extends JavaScriptObject> T getDelegate(String prefix)/*-{
        return {__base: this.__base ? this.__base : this, __prefix: (this.__prefix ? this.__prefix : '') + prefix + '_'}
    }-*/;


	/**
	 * Liefert den GWT OverlayType für ein bestimmten Record zurück.
	 *
	 * @param <T> der Typ des OverlayTypes
	 * @param record der Record
	 * @return den OverlayType
	 */
	@SuppressWarnings("unchecked")
	public static <T extends JavaScriptObject> T transformRecord(Record record) {
		if (record == null) {
			return null;
		}
		return (T) record.getJsObj();
	}

	/**
	 * Liefert ein Array von GWT OverlayTypes für ein Record Array zurück.
	 *
	 * @param <T> der Typ des OverlayTypes
	 * @param records das Record-Array
	 * @return ein Array mit den OverlayTypes
	 */
	public static <T extends JavaScriptObject> List<T> transformRecords(Record... records) {
		if (records == null) {
			return null;
		}
		List<T> result = new ArrayList<T>();
		for (Record record : records) {
			result.add(JsBase.<T>transformRecord(record));
		}
		return result;
	}

	public static Record transformRecord(JavaScriptObject object) {
		return object == null ? null : Record.getOrCreateRef(object);
	}
}
