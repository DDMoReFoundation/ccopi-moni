/*******************************************************************************
 * Copyright (C) 2016 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.engine.common;

import eu.ddmore.libpharmml.dom.commontypes.PharmMLRootType;

/**
 * Class representing a continuous time series variable.
 */
public class Continuous {
	public PharmMLRootType element = null;
	public boolean in_model_function = false, is_rhs = true;
	
	public Continuous(PharmMLRootType element_) { element = element_; }
}
