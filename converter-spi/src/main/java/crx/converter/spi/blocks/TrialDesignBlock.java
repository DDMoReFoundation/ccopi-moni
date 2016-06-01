/*******************************************************************************
 * Copyright (C) 2015 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.spi.blocks;

import java.util.List;

import crx.converter.engine.Part;
import eu.ddmore.libpharmml.dom.commontypes.DerivativeVariable;
import eu.ddmore.libpharmml.dom.commontypes.PharmMLRootType;
import eu.ddmore.libpharmml.dom.trialdesign.ArmDefinition;
import eu.ddmore.libpharmml.dom.trialdesign.TrialDesign;

/**
 * Wrapper class for the PharmML trial design block.
 */
public interface TrialDesignBlock extends Part {
	/**
	 * Number of arms in the trial design.
	 * @return int
	 */
	public int getArmCount();
	
	/**
	 * Get a list of the processed arms.
	 * @return List<ArmDefinition>
	 */
	public List<ArmDefinition> getArms();
	
	/**
	 * Get the size of a named Arm
	 * @param oid Object Identifier
	 * @return int
	 */
	public int getArmSize(String oid);
	
	/**
	 * Get the dose statement (a string) linked to a named administration.
	 * @param administration Administration
	 * @return String
	 */
	public String getDoseStatement(String administration);
	
	/**
	 * Get the dose target element for a named administration.
	 * @param administration_oid
	 * @return PharmMLRootType
	 */
	public PharmMLRootType getDoseTarget(String administration_oid);
	
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
	
	/**
	 * Flag if the trial design has occassions.
	 * @return boolean
	 */
	public boolean hasOccassions();
}
