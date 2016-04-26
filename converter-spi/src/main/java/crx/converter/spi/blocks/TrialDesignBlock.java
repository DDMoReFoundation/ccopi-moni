/*******************************************************************************
 * Copyright (C) 2015 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.spi.blocks;

import java.util.List;

import crx.converter.engine.Part;
import eu.ddmore.libpharmml.dom.commontypes.DerivativeVariable;
import eu.ddmore.libpharmml.dom.trialdesign.TrialDesign;

/**
 * Wrapper class for the PharmML trial design block.
 */
public interface TrialDesignBlock extends Part {
	/**
	 * Get the model/source for the trial design block.
	 * @return TrialDesign
	 */
	public TrialDesign getModel();
	
	/**
	 * Return a list of state variables that a linked to dosing events.
	 * @return java.util.List<eu.ddmore.libpharmml.dom.commontypes.DerivativeVariable>
	 */
	public List<DerivativeVariable> getStateVariablesWithDosing();
}
