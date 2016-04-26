/*******************************************************************************
 * Copyright (C) 2016 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.engine.common;

import eu.ddmore.libpharmml.dom.modeldefn.IndividualParameter;

/**
 * Class wrapping an individual parameter assignment within a trial design replicate loop.<br/>
 * Class differentiates an random variable assignment block from a reference in an equation.
 */
public class IndividualParameterAssignment {
	/**
	 * Parameter
	 */
	public IndividualParameter parameter = null;
	
	/**
	 * Constructor
	 * @param parameter_ Parameter
	 */
	public IndividualParameterAssignment(IndividualParameter parameter_) {
		parameter = parameter_;
	}
}
