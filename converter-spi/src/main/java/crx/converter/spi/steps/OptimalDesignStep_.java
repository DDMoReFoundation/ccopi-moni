/*******************************************************************************
 * Copyright (C) 2016 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.spi.steps;

import java.util.List;

import crx.converter.engine.FixedParameter;
import eu.ddmore.libpharmml.dom.modeldefn.IndividualParameter;
import eu.ddmore.libpharmml.dom.modeldefn.PopulationParameter;
import eu.ddmore.libpharmml.dom.modellingsteps.OptimalDesignOperation;
import eu.ddmore.libpharmml.dom.modellingsteps.OptimalDesignStep;
import eu.ddmore.libpharmml.dom.modellingsteps.ParameterEstimate;

/**
 * Interface of an optimal design step referenced by a converter.
 */
public interface OptimalDesignStep_ extends BaseStep  {
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
	 * Get the operations array bound to the OD step.
	 * @return
	 */
	public OptimalDesignOperation[] getOperations();
	
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
	 * Get the source PharmML step for the optimal design.
	 * @return OptimalDesignStep
	 */
	public OptimalDesignStep getStep();

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

	@Override
	public boolean hasSymbolId(String name);

	/**
	 * Flag if the parameter estimation is constrained.
	 * @return boolean
	 */
	public boolean isConstrained();

	/**
	 * Flag if an evaluation task.
	 * @return boolean
	 */
	public boolean isEvaluation();

	/**
	 * Flag if an Individual parameter is fixed or not.
	 * @return boolean
	 */
	public boolean isFixed(IndividualParameter ip);
	
	/**
	 * Flag if an optimisation task.
	 * @return boolean
	 */
	public boolean isOptimisation();
}
