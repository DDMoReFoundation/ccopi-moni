/*******************************************************************************
 * Copyright (C) 2016 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.engine.common;

import static crx.converter.engine.PharmMLTypeChecker.isPopulationParameter;
import static crx.converter.engine.PharmMLTypeChecker.isSymbolReference;

import java.util.ArrayList;
import java.util.List;

import crx.converter.engine.Accessor;
import crx.converter.spi.ILexer;
import crx.converter.tree.BinaryTree;
import crx.converter.tree.Node;
import eu.ddmore.libpharmml.dom.commontypes.Matrix;
import eu.ddmore.libpharmml.dom.commontypes.PharmMLRootType;
import eu.ddmore.libpharmml.dom.commontypes.StandardAssignable;
import eu.ddmore.libpharmml.dom.commontypes.SymbolRef;
import eu.ddmore.libpharmml.dom.modeldefn.ParameterRandomVariable;
import eu.ddmore.libpharmml.dom.modeldefn.PopulationParameter;

/**
 * Correlation Reference.
 */
public class CorrelationRef {
	/**
	 * Converter instance.
	 */
	public ILexer c = null;
	
	/**
	 * Correlation coefficient.
	 */
	public StandardAssignable correlationCoefficient = null;
	
	/**
	 * List of parameters linked to the random block.
	 */
	public List<PopulationParameter> cov_or_corr_params = new ArrayList<PopulationParameter>();
	
	/**
	 * Covariance value
	 */
	public StandardAssignable covariance = null;
	
	/**
	 * Flag if a correlation involves a matrix. 
	 */
	public boolean is_matrix = false;
	
	/**
	 * Flag if correlation is a pairwise type.
	 */
	public boolean is_pairwise = false;
	
	/**
	 * Matrix associated with the correlation.
	 */
	public Matrix matrix = null;
	
	/**
	 * Pairwise random variable 1
	 */
	public ParameterRandomVariable rnd1 = null;
	
	/**
	 * Pairwise random variable 2
	 */
	public ParameterRandomVariable rnd2 = null;
	
	/**
	 * Constructor
	 */
	public CorrelationRef() {}
	
	/**
	 * A default constructor for a parsed correlation.
	 * @param c_ Converter
	 * @param rnd1_ Pairwise random variable 1
	 * @param rnd2_ Pairwise random variable 1
	 * @param correlationCoefficient_ Correlation coefficient
	 * @param covariance_ Covariance Matrix
	 */
	public CorrelationRef(ILexer c_, ParameterRandomVariable rnd1_, ParameterRandomVariable rnd2_, 
			StandardAssignable correlationCoefficient_, StandardAssignable covariance_) 
	{
		if (c_ != null) c = c_;
		if (rnd1_ != null) rnd1 = rnd1_;
		if (rnd2_ != null) rnd2 = rnd2_;
		if (correlationCoefficient_ != null) correlationCoefficient = correlationCoefficient_;
		if (covariance_  != covariance) covariance = covariance_;
		
		is_pairwise = true;
	}
	
	/**
	 * A default constructor for a parsed correlation.
	 * @param rnd1_ Pairwise random variable 1
	 * @param rnd2_ Pairwise random variable 1
	 * @param correlationCoefficient_ Correlation coefficient
	 * @param covariance_ Covariance Matrix
	 */
	public CorrelationRef(ParameterRandomVariable rnd1_, ParameterRandomVariable rnd2_, 
			StandardAssignable correlationCoefficient_, StandardAssignable covariance_) 
	{
		if (rnd1_ != null) rnd1 = rnd1_;
		if (rnd2_ != null) rnd2 = rnd2_;
		if (correlationCoefficient_ != null) correlationCoefficient = correlationCoefficient_;
		if (covariance_  != covariance) covariance = covariance_;
		
		is_pairwise = true;
	}
	
	/**
	 * Find if parameter defined in the block is referenced in a tree list.
	 * @param trees AST statement list
	 */
	public void findParameterReferences(List<BinaryTree> trees) {
		Accessor a = c.getAccessor();
		for (BinaryTree bt : trees) {
			if (bt == null) continue;
			for (Node node : bt.nodes) {
				if (node == null) continue;
				if (isSymbolReference(node.data)) {
					PharmMLRootType element = a.fetchElement((SymbolRef) node.data);
					if (isPopulationParameter(element)) cov_or_corr_params.add((PopulationParameter) element);
				}
			}
		}
	}
	
	/**
	 * Flag vairable linkage if correlation
	 * @return boolean
	 */
	public boolean isCorrelation() { return correlationCoefficient != null; }
	
	/**
	 * Flag if variable linkage is covariance
	 * @return boolean
	 */
	public boolean isCovariance() { return covariance != null; }
}
