/*******************************************************************************
 * Copyright (C) 2015 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.engine.scriptlets;

import java.util.List;

import org.python.core.PyList;

import crx.converter.spi.ILexer;

/**
 * Basic reference fields required by a scriptlet to generate a minor lump of python code.
 */
public abstract class BaseScriptlet {
	/**
	 * Format for a python call (built-in function) and assignment
	 */
	protected static final String function_call_format = "%s = %s(%s)\n";
	
	/**
	 * Scriptlet output variable for a maximum value from a list.
	 */
	public static final String tmax = "tmax"; 
	
	/**
	 * Scriptlet output variable for a minimum value from a list.
	 */
	public static final String tmin = "tmin";
	
	/**
	 * Scriptlet output variable for time range/list.
	 */
	public static final String tspan = "tspan";
	
	/**
	 * Converter handle to the main target language.
	 */
	protected ILexer c = null; 
	
	/**
	 * Scripting host
	 */
	protected Host host = null;
	
	/**
	 * Constructor
	 * @param c_ Converter Handle
	 * @param host_ Sctipting to host to process complex settings
	 */
	public BaseScriptlet(ILexer c_, Host host_) {
		if (c_ == null) throw new NullPointerException("The converter instance cannot be NULL");
		if (host_ == null) throw new NullPointerException("The converter/lexer instance cannot be NULL");
		
		c = c_;
		host = host_;
	}
	
	/**
	 * Copy the tokens of a python list into a Java list.
	 * If all went well, this method returns TRUE.
	 * @param src Source Python List
	 * @param dst Destination Java List
	 * @return boolean
	 */
	protected boolean copy(PyList src, List<String> dst) {
		if (src == null || dst == null) return false;
		
		if (src.size() == 0) return false;
		
		dst.clear();
		int ncopied = 0;
		for (Object o : src) {
			if (o == null) continue;
			dst.add(o.toString());
			ncopied++;
		}
		
		return ncopied > 0;
	}
	
	/**
	 * Get the 'name' of the source object bound to the scriptlet.
	 * @return String
	 */
	public abstract String getName();
}
