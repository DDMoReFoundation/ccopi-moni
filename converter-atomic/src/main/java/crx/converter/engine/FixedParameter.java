/*******************************************************************************
 * Copyright (C) 2015 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.engine;

import eu.ddmore.libpharmml.dom.modellingsteps.ParameterEstimate;

/**
 * A fixed parameter in an estimation.
 */
public class FixedParameter {
	public ParameterEstimate pe = null;
	
	/**
	 * Constructor
	 * @param p_ Parameter Estimate source
	 */
	public FixedParameter(ParameterEstimate p_) {
		if (p_ == null) throw new NullPointerException();
		pe = p_;
	}
}
