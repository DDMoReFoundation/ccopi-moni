/*******************************************************************************
 * Copyright (C) 2015 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.engine.common;

import crx.converter.spi.blocks.ObservationBlock;
import eu.ddmore.libpharmml.dom.commontypes.PharmMLRootType;
import eu.ddmore.libpharmml.dom.modeldefn.PopulationParameter;

/**
 * A class that flags a parameter has observation model scope only.<br/>
 * Simple a wrapper class so that a parameter declaration does not end up in the
 * global parameter array.
 */
public class ObservationParameter extends PharmMLRootType {
	public PopulationParameter param = null;
	public ObservationBlock parent = null;
	
	/**
	 * Constructor
	 * @param parent_ Parent Block
	 * @param param_ Observation model scoped parameter
	 */
	public ObservationParameter(ObservationBlock parent_, PopulationParameter param_) {
		if (parent_ == null || param_ == null) throw new NullPointerException("A parameter reference argument cannot be NULL.");
		
		parent = parent_;
		param = param_;
	}
	
	/**
	 * Variable name in a coding context.
	 * @return String
	 */
	public String getName() {
		String format = "%s_%s";	
		return String.format(format, parent.getName(), param.getSymbId());
	}
	
	/**
	 * Get the referenced population parameter.
	 * @return PopulationParameter
	 */
	public PopulationParameter getPopulationParameter() { return param; }
}
