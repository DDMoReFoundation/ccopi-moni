/*******************************************************************************
 * Copyright (C) 2015 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.engine;

import eu.ddmore.libpharmml.dom.commontypes.SymbolRef;

/**
 * A category reference in a fixed effect statement.
 */
public class FixedEffectCategoryRef {
	public String catId = null;
	public SymbolRef cov = null; 
	public SymbolRef coeff = null;
	
	/**
	 * Constructor
	 * @param cov_ Covariate Nae
	 * @param catId_ Category Identifier
	 */
	public FixedEffectCategoryRef(SymbolRef cov_, SymbolRef coeff_, String catId_) {
		if (cov_ == null) throw new NullPointerException("The covariate cannot be NULL");
		if (coeff_ == null) throw new NullPointerException("The coefficient cannot be NULL");
		if (catId_ == null) throw new NullPointerException("The category identifier cannot be NULL");
		
		cov = cov_;
		catId = catId_;
		coeff = coeff_;
	}
}