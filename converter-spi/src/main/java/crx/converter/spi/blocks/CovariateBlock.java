/*******************************************************************************
 * Copyright (C) 2016 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.spi.blocks;

import java.util.Collection;
import java.util.List;

import crx.converter.engine.Part;
import crx.converter.engine.common.CovariateParameterRef;
import eu.ddmore.libpharmml.dom.modeldefn.ContinuousCovariate;
import eu.ddmore.libpharmml.dom.modeldefn.CovariateDefinition;
import eu.ddmore.libpharmml.dom.modeldefn.CovariateModel;
import eu.ddmore.libpharmml.dom.modeldefn.PopulationParameter;

/**
 * List of covariates read from the PharmML model.<br/>
 */
public interface CovariateBlock extends Part {
	/**
	 * Associate a parameter with a covariate definition.
	 * @param covName Covariate Name
	 * @param parameterName Parameter Name
	 * @return boolean Success if named elements exist in the PharmML model.
	 */
	public boolean addParameterToCovariate(String covName, String parameterName);
	
	/**
	 * Return a list of categorical covariate variable names
	 * @return java.util.List<String>
	 */
	public List<String> getCategoricalCovariateNames();
	
	/**
	 * Get a category list (if any) for the 'named' covariate term in a model.
	 * @param cov Covariate
	 * @return List<String>
	 */
	public List<String> getCategories(CovariateDefinition cov);
	
	/**
	 * Get the continuous covariates from the model.
	 * @return java.util.List<eu.ddmore.libpharmml.dom.modeldefn.ContinuousCovariate>
	 */
	public List<ContinuousCovariate> getContinuousCovariates();
	
	/**
	 * Get a list of covariate to parameter references.
	 * @return Collection<CovariateParameterRef>
	 */
	public Collection<CovariateParameterRef> getCovariateParameterRefs();

	/**
	 * Get a list of covariates in the model.
	 * @return java.util.List<eu.ddmore.libpharmml.dom.modeldefn.CovariateDefinition>
	 */
	public List<CovariateDefinition> getCovariates();
	
	/**
	 * Get the source model.
	 * @return eu.ddmore.libpharmml.dom.modeldefn.CovariateModel
	 */
	public CovariateModel getModel();
		
	/**
	 * Get a list of parameters declared in the covariate block.
	 * @return java.util.List<eu.ddmore.libpharmml.dom.modeldefn.PopulationParameter>
	 */
	public List<PopulationParameter> getParameters();

	/**
	 * Flag that the covariate model is a categorical.
	 * @return boolean
	 */
	public boolean isCategorical();

	/**
	 * Flag that the 'named' covariate is categorical in nature.
	 * @return boolean
	 */
	public boolean isCategorical(String covariateName);
}
