/*******************************************************************************
 * Copyright (C) 2016 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.spi.blocks;

import java.util.List;
import java.util.Map;

import crx.converter.engine.common.InterventionSequenceRef;
import eu.ddmore.libpharmml.dom.commontypes.DerivativeVariable;
import eu.ddmore.libpharmml.dom.commontypes.PharmMLRootType;
import eu.ddmore.libpharmml.dom.trialdesign.ArmDefinition;
import eu.ddmore.libpharmml.dom.trialdesign.Observation;
import eu.ddmore.libpharmml.dom.trialdesign.TrialDesign;

/**
 * Extended TrialDesign block implementing the trial design features introduced
 * into PharmML >= 0.8.1.<br/>
 * Required for more interpreter-based languages like PFIM, R, MATLAB, Python that
 * do not use NONMEM data frames to encode the trial design data.
 */
public interface TrialDesignBlock2 extends TrialDesignBlock {
	/**
	 * Get the dose time associated with an ad
	 * @param admin_oid
	 * @return double
	 */
	public double getAdministrationStartTime(String admin_oid);
	
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
	 * Get a list of model elements that are targeted for dosing.
	 * @return Map<String, PharmMLRootType>
	 */
	public Map<String, PharmMLRootType> getDoseTargetMap();
	
	/**
	 * Get the dose targets declared in the trial design section of a model.
	 * @return List<PharmMLRootType>
	 */
	public List<PharmMLRootType> getDoseTargets();
	
	/**
	 * Get the intervention reference linked to the arm.
	 * @param arm Arm Definition
	 * @return InterventionSequenceRef
	 */
	public InterventionSequenceRef getInterventionSequenceRef(ArmDefinition arm);
	
	/**
	 * Get the model/source for the trial design block.
	 * @return TrialDesign
	 */
	public TrialDesign getModel();
	
	/**
	 * Get the observation linked to the Arm.
	 * @param arm Arm Instance
	 * @return Observation
	 */
	public Observation getObservation(ArmDefinition arm);
	
	/**
	 * Get the named observation element
	 * @param oid Observation Identifier
	 * @return Observation
	 */
	public Observation getObservation(String oid);
	
	/**
	 * Get list of declared observations.
	 * @return List<Observation>
	 */
	public List<Observation> getObservations();
	
	/**
	 * Get the start offset for an Arm
	 * @param arm Arm Instance
	 * @return double
	 */
	public double getObservationStart(ArmDefinition arm);
	
	/**
	 * Return a list of state variables that a linked to dosing events.
	 * @return java.util.List<eu.ddmore.libpharmml.dom.commontypes.DerivativeVariable>
	 */
	public List<DerivativeVariable> getStateVariablesWithDosing();
	
	/**
	 * Flag if a state variable is associated with a dosing event.
	 * @return boolean
	 */
	public boolean hasDosing();
	
	/**
	 * Flag if the trial design has occassions.
	 * @return boolean
	 */
	public boolean hasOccassions();
}
