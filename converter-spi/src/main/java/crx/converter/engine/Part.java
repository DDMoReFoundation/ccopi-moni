/*******************************************************************************
 * Copyright (C) 2015 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.engine;

import java.util.List;

/**
 * A part represents a block of code to be created from elements in a PharmMl model.
 *
 */
public interface Part {
	/**
	 * Method to build the syntax trees bound to a part.
	 */
	public void buildTrees();
	
	/**
	 * Get the name of the code block
	 * @return java.lang.String
	 */
	public String getName();
	
	/**
	 * Get a list of symbols bound to a part.
	 * @return java.util.List<java.lang.String>
	 */
	abstract public List<String> getSymbolIds();
	
	/**
	 * Check if the Part has a bound model symbol.
	 * @param name Name of a variable
	 * @return boolean
	 */
	abstract public boolean hasSymbolId(String name);
}
