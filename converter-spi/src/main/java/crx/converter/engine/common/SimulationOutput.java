/*******************************************************************************
 * Copyright (C) 2016 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.engine.common;

import eu.ddmore.libpharmml.dom.commontypes.PharmMLRootType;

/**
 * Simulation output element.
 */
public class SimulationOutput {
	public PharmMLRootType v = null;
	
	public SimulationOutput(PharmMLRootType v_) {
		if (v_ == null) throw new NullPointerException("Error model variable reference is NULL.");
		v = v_;
	}
}
