/*******************************************************************************
 * Copyright (C) 2015 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.spi.blocks;

import java.util.List;

import crx.converter.engine.Part;
import eu.ddmore.libpharmml.dom.modeldefn.IndividualParameter;

/**
 * A block of individual parameters.
 *
 */
public interface IndividualParameterBlock extends Part {
	/**
	 * Add an individual parameters to the block
	 * @param ip Individual parameter
	 * @return boolean
	 */
	public boolean addIndividualParameter(IndividualParameter ip);
	
	/**
	 * The the list of individual parameters.
	 * @return java.util.List<IndividualParameter>
	 */
	public List<IndividualParameter> getIndividualParameters();
	
	/**
	 * Flag whether the individual parameter block is empty.
	 * @return boolean
	 */
	public boolean hasIndividualParameters();
}
