/*******************************************************************************
 * Copyright (C) 2015 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.engine.scriptlets;

/**
 * Factory class to instantiate the scripting host handle.<br/>
 * The scripting syntax is python.
 */
public interface Factory {
	/**
	 * Instantiate a scriptlet host.
	 * @return Host
	 */
	public Host newInstance();
}
