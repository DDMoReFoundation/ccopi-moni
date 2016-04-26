/*******************************************************************************
 * Copyright (C) 2015 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.spi.blocks;

import java.util.List;

import crx.converter.engine.common.ObservationParameter;
import crx.converter.engine.common.SimulationOutput;
import eu.ddmore.libpharmml.dom.commontypes.PharmMLRootType;
import eu.ddmore.libpharmml.dom.commontypes.VariableDefinition;
import eu.ddmore.libpharmml.dom.modeldefn.CategoricalData;
import eu.ddmore.libpharmml.dom.modeldefn.CountData;
import eu.ddmore.libpharmml.dom.modeldefn.Dependance;
import eu.ddmore.libpharmml.dom.modeldefn.ObservationError;
import eu.ddmore.libpharmml.dom.modeldefn.ObservationModel;
import eu.ddmore.libpharmml.dom.modeldefn.ParameterRandomVariable;
import eu.ddmore.libpharmml.dom.modeldefn.PopulationParameter;
import eu.ddmore.libpharmml.dom.modeldefn.TimeToEventData;

/**
 * The observation block of a PharmML model.<br/>
 *
 */
public interface ObservationBlock extends BaseRandomVariableBlock {
	/**
	 * Check if the error model contains a specific model element.
	 * @param v Model Element
	 * @return boolean
	 */
	public boolean contains(PharmMLRootType v);
		
	/**
	 * Get the categorical data structure of a discrete model.
	 * @return eu.ddmore.libpharmml.dom.modeldefn.CategoricalData
	 */
	public CategoricalData getCategoricalData();
	
	/**
	 * Get the source count data structure associated with a discrete model.
	 * @return eu.ddmore.libpharmml.dom.modeldefn.CountData
	 */
	public CountData getCountData();
		
	/**
	 * Get the dependance bound to a discrete model.
	 * @return eu.ddmore.libpharmml.dom.modeldefn.Dependance
	 */
	public Dependance getDependance();
	
	/**
	 * Get a list of function names used in the error model.<br/>
	 * Cross-reference terms against a function library.
	 * @return boolean
	 */
	public List<String> getErrorFunctionNames();
	
	/**
	 * Get the source observation model for an enclosing observation block.
	 * @return eu.ddmore.libpharmml.dom.modeldefn.ObservationModel
	 */
	public ObservationModel getModel();
	
	/**
	 * Get the Observation Error object.
	 * @return eu.ddmore.libpharmml.dom.modeldefn.ObservationError
	 */
	public ObservationError getObservationError();
	
	/**
	 * Get the observation parameter entry referenced by the simple parameter. 
	 * @param p
	 * @return ObservationParameter
	 */
	public ObservationParameter getObservationParameter(PopulationParameter p);
	
	/**
	 * List of observation-model scoped parameters.
	 * @return java.util.List<ObservationParameter>
	 */
	public List<ObservationParameter> getObservationParameters();
	
	/**
	 * Get list of random variables declared in the error model.
	 * @return java.util.List<ParameterRandomVariable>
	 */
	public List<ParameterRandomVariable> getRandomVariables();
	
	/**
	 * Get the list of simulation outputs referenced by the error model.
	 * @return java.util.List<SimulationOutput>
	 */
	public List<SimulationOutput> getSimulationOutputs();
	
	/**
	 * Get the source TTE data structure associated with a discrete model.
	 * @return eu.ddmore.libpharmml.dom.modeldefn.TimeToEventData
	 */
	public TimeToEventData getTimeToEventData();
	
	/**
	 * Flag if a discrete model has categorical data bound to it.
	 * @return boolean
	 * @see eu.ddmore.libpharmml.dom.modeldefn.CategoricalData
	 * 
	 */
	public boolean hasCategoricalData();

	/**
	 * Report that a discrete model has a count data model.
	 * @return boolean
	 */
	public boolean hasCountData();
	
	/**
	 * Flag if the observation model has local scoped parameters.
	 * @return boolean
	 */
	public boolean hasParameters();
	
	/**
	 * Flag if the observation block has a TTE structure.
	 * @return boolean
	 * @see ObservationBlock#getTimeToEventData()
	 */
	public boolean hasTimeToEventData();
	
	/**
	 * Test if the error model should be applied to a structural block.
	 * @param sb A Structural Block
	 * @return boolean
	 */
	public boolean isApplicable(StructuralBlock sb);
	
	/**
	 * Flag if the error model is discrete.
	 * @return boolean
	 */
	public boolean isDiscrete();
	
	/**
	 * Test if a parameter has error model (observation) scope.
	 * @param p Simple Parameter
	 * @return boolean
	 */
	public boolean isObservationParameter(PopulationParameter p);
	
	/**
	 * Check whether a model element is simulation output associated with an error model.
	 * @param v Model element under consideration.
	 * @return boolean
	 */
	public boolean isSimulationOutput(PharmMLRootType v);

	/**
	 * Check whether the error model references a local variable in a calculation.
	 * @param v Local variable 
	 * @return boolean
	 */
	public boolean isUsing(VariableDefinition v);
}
