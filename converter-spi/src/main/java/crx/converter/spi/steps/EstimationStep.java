/*******************************************************************************
 * Copyright (C) 2016 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.spi.steps;

import java.util.List;

import crx.converter.engine.FixedParameter;
import eu.ddmore.libpharmml.dom.modeldefn.PopulationParameter;
import eu.ddmore.libpharmml.dom.modellingsteps.Estimation;
import eu.ddmore.libpharmml.dom.modellingsteps.EstimationOperation;
import eu.ddmore.libpharmml.dom.modellingsteps.ParameterEstimate;

/**
 * A class the wraps an estimation code block.
 *
 */
public interface EstimationStep extends BaseStep {
	/**
	 * Retrieves the fixed parameter record associated with a parameter estimation.
	 * @param p Parameter Variable name
	 * @return FixedParameter
	 */
	public FixedParameter getFixedParameter(PopulationParameter p);
	
	/**
	 * Get a list of fixed parameters.
	 * @return java.util.List<FixedParameter>
	 */
	public List<FixedParameter> getFixedParameters();
	
	/**
	 * Get the index value for a number in a parameter estimate vector.
	 * @param pe Parameter Estimate
	 * @return java.lang.Integer
	 */
	public Integer getIndividualParameterIndex(ParameterEstimate pe);
		
	/**
	 * List of operations associated with this estimation step.
	 * @return eu.ddmore.libpharmml.dom.modellingsteps.EstimationOperation[]
	 */
	public EstimationOperation [] getOperations();
	
	/**
	 * Get the parameter estimate objedt associated with a parameter object.
	 * @param p Parameter
	 * @return eu.ddmore.libpharmml.dom.modellingsteps.ParameterEstimate
	 */
	public ParameterEstimate getParameterEstimate(PopulationParameter p);
	
	/**
	 * Get the index number of a parameter in an estimation vector.
	 * @param pe Parameter Estimate
	 * @return java.lang.Integer
	 */
	public Integer getParameterIndex(ParameterEstimate pe);	
	/**
	 * Get the list of parameter estimates
	 * @return java.util.List<eu.ddmore.libpharmml.dom.modellingsteps.ParameterEstimate>
	 */
	public List<ParameterEstimate> getParametersToEstimate();
	
	/**
	 * Get the estimation step.
	 * @return eu.ddmore.libpharmml.dom.modellingsteps.Estimation
	 */
	public Estimation getStep();

	@Override
	public List<String> getSymbolIds();
	
	/**
	 * Flag if the estimation has fixed parameters.
	 * @return boolean
	 */
	public boolean hasFixedParameters();

	/**
	 * If the estimation has parameters to estimate.
	 * @return boolean
	 */
	public boolean hasParametersToEstimate();
	
	/**
	 * Check if the estimation has simple parameters to estimate.
	 * @return boolean
	 */
	public boolean hasSimpleParametersToEstimate();
	
	/**
	 * Flag if the estimation is constrained.
	 * @return boolean
	 */
	public boolean isConstrained();
	
	/**
	 * Check is a given parameter is a fixed parameter in an estimation.
	 * @param p
	 * @return boolean
	 */
	public boolean isFixedParameter(PopulationParameter p);
	
	/**
	 * Update the parameter estimation order to that expressed in the parameter model.
	 */
	public void update();
}
