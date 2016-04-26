/*******************************************************************************
 * Copyright (C) 2015 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.engine.common;

import eu.ddmore.libpharmml.dom.commontypes.PharmMLElement;
import eu.ddmore.libpharmml.dom.modeldefn.CovariateDefinition;

/**
 * Optimal design class to associate a proportions array/vector to a covariate.
 */
public class CategoryProportions {
	public CovariateDefinition cov = null;
	public PharmMLElement proportions = null;
	
	public CategoryProportions(CovariateDefinition cov_, PharmMLElement proportions_) {
		if (cov_ == null) throw new NullPointerException("Covariate is NULL");
		if (proportions_ == null) throw new NullPointerException("Proportions is NULL");
		
		cov = cov_;
		proportions = proportions_;
	}

	/**
	 * Get the covariate.
	 * @return CovariateDefinition
	 */
	public CovariateDefinition getCovariate() { return cov; }

	/**
	 * Get the proportions variable.
	 * @return PharmMLElement
	 */
	public PharmMLElement getProportions() { return proportions; }
}
