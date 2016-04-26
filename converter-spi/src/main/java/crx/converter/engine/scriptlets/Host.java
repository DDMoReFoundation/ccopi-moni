/*******************************************************************************
 * Copyright (C) 2016 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.engine.scriptlets;

import org.python.core.PyObject;

import crx.converter.tree.TreeMaker;

/**
 * A very very very dumb scripting host for the Common Converter.<br/>
 * Use python syntax for macros.<br/>
 * Input scriptlets should define an output variable called 'results'.
 */
public interface Host {
	/**
	 * Execute a scriptlet and return the result element.
	 * @param stmt Python containing the scripting code
	 * @return PyObject Contents of the scriptlet result object.
	 */
	public PyObject execute(String stmt);
	
	/**
	 * Get a function implementation to be included in a scriptlet based on a reference.
	 * @param ref Function Reference
	 * @return String Python Code
	 */
	public String getFunctionImpl(PyFunctionName ref);
	
	/**
	 * Get the named result of post a scriptlet execution
	 * @param variable_name Variable Name
	 * @return PyObject Named Python Execution Result
	 */
	public PyObject getResult(String variable_name);
	
	/**
	 * Get the tree maker bound to the scripting host.
	 * @return TreeMaker
	 */
	public TreeMaker getTreeMaker();
	
	/**
	 * Convert a binary tree of a PharmML expression into Python code.
	 * @param context Assignment context
	 * @param element Model Element
	 * @return String Python Source Code
	 */
	public String parse(Object context, Object element);
}
