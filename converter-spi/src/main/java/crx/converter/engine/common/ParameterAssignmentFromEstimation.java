/*******************************************************************************
 * Copyright (C) 2016 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.engine.common;

import eu.ddmore.libpharmml.dom.modellingsteps.ParameterEstimate;

/**
 * Class wrap a simple parameter assignment from an estimation.<br/>
 * Dual-problem assignment class.
 */
public class ParameterAssignmentFromEstimation {
	public ParameterEstimate p = null;
	
	public ParameterAssignmentFromEstimation(ParameterEstimate p_) {
		if (p_ == null) throw new NullPointerException();
		p = p_;
	}
}
