/*******************************************************************************
 * Copyright (C) 2016 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.spi.blocks;

import java.util.List;
import java.util.Map;

import crx.converter.engine.common.ElementaryDesign;
import crx.converter.engine.common.InterventionSequenceRef;
import crx.converter.engine.common.Protocol;
import eu.ddmore.libpharmml.dom.commontypes.DerivativeVariable;
import eu.ddmore.libpharmml.dom.commontypes.OidRef;
import eu.ddmore.libpharmml.dom.commontypes.PharmMLRootType;
import eu.ddmore.libpharmml.dom.trialdesign.Administration;
import eu.ddmore.libpharmml.dom.trialdesign.ArmDefinition;
import eu.ddmore.libpharmml.dom.trialdesign.Observation;
import eu.ddmore.libpharmml.dom.trialdesign.SingleDesignSpace;
import eu.ddmore.libpharmml.dom.trialdesign.TrialDesign;

/**
 * Extended TrialDesign block implementing the trial design features introduced
 * into PharmML >= 0.8.1.<br/>
 * Required for more interpreter-based languages like PFIM, R, MATLAB, Python that
 * do not use NONMEM data frames to encode the trial design data.<br/>
 * This interface design is based on the DDMoRe use cases (Dated=03/07/2016).
 */
public interface TrialDesignBlock2 extends TrialDesignBlock {
	/**
	 * Get a named Administration
	 * @param oid Object Identifier for the administration
	 * @return Administration
	 */
	public Administration getAdministration(String oid);
	
	/**
	 * Get the administration map.
	 * @return Map<String, Administration>
	 */
	public Map<String, Administration> getAdministrationMap();
	
	/**
	 * List of Administrations
	 * @return List<Administration>
	 */
	public List<Administration> getAdministrations();
	
	/**
	 * Get the dose time associated with an ad
	 * @param admin_oid
	 * @return double
	 */
	public double getAdministrationStartTime(String admin_oid);
	
	/**
	 * Get a named study arm.
	 * @param arm_oid Arm Identifier
	 * @return ArmDefinition
	 */
	public ArmDefinition getArm(String arm_oid);
	
	/**
	 * Number of arms in the trial design.
	 * @return int
	 */
	public int getArmCount();
	
	/**
	 * Get the arms linked to an observation/sampling window.
	 * @param ob
	 * @return List<ArmDefinition>
	 */
	public List<ArmDefinition> getArmMembership(Observation ob);
	
	/**
	 * Get a map of observations referenced by study arms.
	 * @return Map<Observation, List<ArmDefinition>>
	 */
	public Map<Observation, List<ArmDefinition>> getArmMembershipMap();
	
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
	 * Get the map linking a design space to an observation window.
	 * @return Map<SingleDesignSpace, Observation>
	 */
	public Map<SingleDesignSpace, Observation> getDesignSpaceObservationMap();
	
	/**
	 * Get the design spaces declared in a trial design PharmML element.
	 * @return List<SingleDesignSpace>
	 */
	public List<SingleDesignSpace> getDesignSpaces();
	
	/**
	 * Get the design space linked to a sampling protocol.
	 * @param protocol Protocol
	 * @return Map<ElementaryDesign, List<SingleDesignSpace>>
	 */
	public Map<ElementaryDesign, List<SingleDesignSpace>> getDesignSpaces(Protocol protocol);
	
	/**
	 * Get a design space with the sampling count limits for an optimal design converter.
	 * This is the NumberOfTimes property in a PharmML model.
	 * @return SingleDesignSpace
	 */
	public SingleDesignSpace getDesignSpaceWithSamplingCountLimits();
	
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
	 * @param ref Observation Reference
	 * @return Observation
	 */
	public Observation getObservation(OidRef ref);
	
	/**
	 * Get the observation linked to a design space.
	 * @param space Design Space
	 * @return Observation
	 */
	public Observation getObservation(SingleDesignSpace space);
	
	/**
	 * Get the named observation element
	 * @param oid Observation Identifier
	 * @return Observation
	 */
	public Observation getObservation(String oid);
	
	/**
	 * Get the relative index of an observation/sampling window in a trial design.
	 * @param ob_ref Observation
	 * @return int
	 */
	public int getObservationIndex(OidRef ob_ref);
	
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
	 * Get the protocol associating an observation model to the observation/sampling windows.
	 * @return List<Protocol>
	 */
	public List<Protocol> getProtocols();
	
	/**
	 * Return a list of state variables that a linked to dosing events.
	 * @return java.util.List<eu.ddmore.libpharmml.dom.commontypes.DerivativeVariable>
	 */
	public List<DerivativeVariable> getStateVariablesWithDosing();
	
	/**
	 * Get a named Administration
	 * @param oid Object Identifier for the administration
	 * @return boolean
	 */
	public boolean hasAdministration(String oid);
	
	/**
	 * Check if a design space with the sampling count limits for an optimal design converter.
	 * @return boolean
	 */
	public boolean hasDesignSpaceWithSamplingCountLimits();
	
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
