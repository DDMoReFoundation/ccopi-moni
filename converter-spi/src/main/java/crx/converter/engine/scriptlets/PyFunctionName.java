/*******************************************************************************
 * Copyright (C) 2016 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.engine.scriptlets;

import crx.converter.engine.BaseEngine;


/**
 * Name of a Python utility function definition that can be included in a scriptlet.
 */
public enum PyFunctionName {
	LINSPACE("linspace"),
	MAX(BaseEngine.MAX.toLowerCase()),
	MIN(BaseEngine.MIN.toLowerCase()),
	SORT("sorted");
	
	/**
	 * Create enumeration value from a string.
	 * @param v
	 * @return FunctionDefinitionRef
	 */
	public static PyFunctionName fromValue(String v) {
        for (PyFunctionName c: PyFunctionName.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
	
	private final String value;
	
	private PyFunctionName(String v) { value = v; }
	
    @Override
    public String toString() { return value; }
}