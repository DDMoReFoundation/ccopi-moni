/*******************************************************************************
 * Copyright (C) 2015 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.engine.common;

import java.util.ArrayList;
import java.util.List;

import eu.ddmore.libpharmml.dom.modeldefn.CommonParameter;
import eu.ddmore.libpharmml.dom.modeldefn.CovariateDefinition;

/**
 * Optimal design type, which associates a parameter type with a model covariate.
 */
public class CovariateParameterRef {
	private CovariateDefinition cov = null; 
	private List<CommonParameter> ps = new ArrayList<CommonParameter>();

	/**
	 * Constructor
	 * @param cov_ Covariate
	 */
	public CovariateParameterRef(CovariateDefinition cov_) {
		if (cov_ == null) throw new NullPointerException("Covariate is NULL");
		cov = cov_;
	}

	/**
	 * Add parameter to the reference list.
	 * @param p
	 * @return boolean
	 */
	public boolean addParameter(CommonParameter p) {
		if (p == null) return false;
		
		if (!ps.contains(p)) {
			ps.add(p);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Get the covariate term.
	 * @return CovariateDefinition
	 */
	public CovariateDefinition getCovariate() { return cov; }
	
	/**
	 * Get the parameter term.
	 * @return CommonParameter
	 */
	public List<CommonParameter> getParameters() { return ps; }
}
