/*******************************************************************************
 * Copyright (C) 2015 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.spi.steps;

import java.util.List;
import java.util.Map;

import crx.converter.engine.common.Assignment;
import eu.ddmore.libpharmml.dom.commontypes.StandardAssignable;
import eu.ddmore.libpharmml.dom.commontypes.SymbolRef;
import eu.ddmore.libpharmml.dom.modellingsteps.Simulation;
import eu.ddmore.libpharmml.dom.modellingsteps.SimulationOperation;
import eu.ddmore.libpharmml.dom.trialdesign.Observation;

/**
 * Class wrapping a simulation step.
 */
public interface SimulationStep extends BaseStep {
	/**
	 * This is the condensed list of continuous 'simulation' outputs as read from observations
	 * contained within th trial design block.
	 * @return List<SymbolRef>
	 * @see eu.ddmore.libpharmml.dom.trialdesign.Observation#getContinuous()
	 */
	public List<SymbolRef> getContinuousList();
	
	/**
	 * Return the source PharmML, i.e. model for a simulation step.
	 * @return eu.ddmore.libpharmml.dom.modellingsteps.Simulation
	 */
	public Simulation getModel();
	
	/**
	 * Get the list of simulation operations/options bound to the simulation step.
	 * @return eu.ddmore.libpharmml.dom.modellingsteps.SimulationOperation []
	 */
	public SimulationOperation [] getOperations();
		
	/**
	 * Map of the TSPAN/time points object bound to the current observation model.
	 * @return java.util.HashMap<eu.ddmore.libpharmml.dom.modellingsteps.Observations, eu.ddmore.libpharmml.dom.modellingsteps.Timepoints>
	 */
	public Map<Observation, StandardAssignable> getTimePoints();
	
	/**
	 * Get a list of variable assignments that are specific to the simulation step.<br/>
	 * An assignment is a parsed version of the PharmML class.
	 * @return List<Assignment>
	 * @see eu.ddmore.libpharmml.dom.commontypes.VariableAssignment
	 */
	public List<Assignment> getVariableAssignments();
	
	/**
	 * Flag is the simulation step has continuous output variables declared.
	 * @return boolean
	 */
	public boolean hasContinuous();
	
	@Override
	public boolean hasSymbolId(String name);
	
	/**
	 * Flag if at dosing, the value replaces or is added to value in a compartment.
	 * @return boolean
	 */
	public boolean isDoseReplaceCurrentValue();
}
