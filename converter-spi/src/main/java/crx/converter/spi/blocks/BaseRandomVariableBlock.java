/*******************************************************************************
 * Copyright (C) 2016 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.spi.blocks;

import java.util.List;

import crx.converter.engine.common.CorrelationRef;
import eu.ddmore.libpharmml.dom.modeldefn.ParameterRandomVariable;

/**
 * Base code block that contains random variables.
 */
public interface BaseRandomVariableBlock extends IndividualParameterBlock {
	/**
	 * Get a list of correlation references.
	 * @return java.util.List<CorrelationRef>
	 */
	public List<CorrelationRef> getCorrelations();
	
	/**
	 * A list of pairwise linked variables
	 * @return java.util.List<eu.ddmore.libpharmml.dom.modeldefn.ParameterRandomVariable>
	 */
	public List<ParameterRandomVariable> getLinkedRandomVariables();
}
