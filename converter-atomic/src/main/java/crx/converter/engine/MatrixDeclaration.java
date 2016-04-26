/*******************************************************************************
 * Copyright (C) 2015 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.engine;

import static crx.converter.engine.PharmMLTypeChecker.isColumnDefinition;
import static crx.converter.engine.PharmMLTypeChecker.isCommonParameter;
import static crx.converter.engine.PharmMLTypeChecker.isContinuousCovariate;
import static crx.converter.engine.PharmMLTypeChecker.isCovariate;
import static crx.converter.engine.PharmMLTypeChecker.isDerivative;
import static crx.converter.engine.PharmMLTypeChecker.isFunction;
import static crx.converter.engine.PharmMLTypeChecker.isFunctionParameter;
import static crx.converter.engine.PharmMLTypeChecker.isGeneralError;
import static crx.converter.engine.PharmMLTypeChecker.isIndependentVariable;
import static crx.converter.engine.PharmMLTypeChecker.isIndividualParameter;
import static crx.converter.engine.PharmMLTypeChecker.isLocalVariable;
import static crx.converter.engine.PharmMLTypeChecker.isObservationError;
import static crx.converter.engine.PharmMLTypeChecker.isPopulationParameter;
import static crx.converter.engine.PharmMLTypeChecker.isRandomVariable;
import static crx.converter.engine.PharmMLTypeChecker.isRootType;
import static crx.converter.engine.PharmMLTypeChecker.isSymbolReference;
import static crx.converter.engine.PharmMLTypeChecker.isVariabilityLevelDefinition;
import eu.ddmore.libpharmml.dom.IndependentVariable;
import eu.ddmore.libpharmml.dom.commontypes.DerivativeVariable;
import eu.ddmore.libpharmml.dom.commontypes.FunctionDefinition;
import eu.ddmore.libpharmml.dom.commontypes.FunctionParameter;
import eu.ddmore.libpharmml.dom.commontypes.Matrix;
import eu.ddmore.libpharmml.dom.commontypes.PharmMLRootType;
import eu.ddmore.libpharmml.dom.commontypes.Rhs;
import eu.ddmore.libpharmml.dom.commontypes.SymbolRef;
import eu.ddmore.libpharmml.dom.commontypes.VariableDefinition;
import eu.ddmore.libpharmml.dom.dataset.ColumnDefinition;
import eu.ddmore.libpharmml.dom.modeldefn.CommonParameter;
import eu.ddmore.libpharmml.dom.modeldefn.ContinuousCovariate;
import eu.ddmore.libpharmml.dom.modeldefn.CovariateDefinition;
import eu.ddmore.libpharmml.dom.modeldefn.CovariateTransformation;
import eu.ddmore.libpharmml.dom.modeldefn.GeneralObsError;
import eu.ddmore.libpharmml.dom.modeldefn.IndividualParameter;
import eu.ddmore.libpharmml.dom.modeldefn.ObservationError;
import eu.ddmore.libpharmml.dom.modeldefn.ParameterRandomVariable;
import eu.ddmore.libpharmml.dom.modeldefn.PopulationParameter;
import eu.ddmore.libpharmml.dom.modeldefn.TransformedCovariate;
import eu.ddmore.libpharmml.dom.modeldefn.VariabilityLevelDefinition;

/**
 * A variable reference assigned a matrix type.<br/>
 * Explicit type to permit matrix assignment to a given model element.
 */
public class MatrixDeclaration extends PharmMLRootType  {
	private static String getClassName(Object o) {
		if (o == null) return null;
		
		Class<?> c = o.getClass();
		String FQClassName = c.getName();
		int firstChar;
		firstChar = FQClassName.lastIndexOf ('.') + 1;
		if ( firstChar > 0 ) {
			FQClassName = FQClassName.substring ( firstChar );
		}
		return FQClassName;
	}
	
	private static SymbolRef symbolRef(PharmMLRootType o, Accessor a) {
		String symbId = null;
		
		boolean addScope = false;
		
		if (isSymbolReference(o)) {
			return (SymbolRef) o;
		} else if (isCommonParameter(o)) { 
			symbId = ((CommonParameter) o).getSymbId();
			addScope = true;
		} else if (isLocalVariable(o)) {
			symbId = ((VariableDefinition) o).getSymbId();
			addScope = true;
		} else if (isDerivative(o)) { 
			symbId = ((DerivativeVariable) o).getSymbId();
			addScope = true;
		} else if (isIndividualParameter(o)) { 
			symbId = ((IndividualParameter) o).getSymbId();
			addScope = true;
		} else if (isRandomVariable(o)) {
			symbId = ((ParameterRandomVariable) o).getSymbId();
			addScope = true;
		} else if (isIndependentVariable(o)) {
			symbId = ((IndependentVariable) o).getSymbId();
		} else if (isCovariate(o)) {
			symbId = ((CovariateDefinition) o).getSymbId();
			addScope = true;
		} else if (isFunctionParameter(o)) {
			symbId = ((FunctionParameter) o).getSymbId();
		} else if (isFunction(o)) {
			symbId = ((FunctionDefinition) o).getSymbId();
		} else if (isObservationError(o)) {
			symbId = ((ObservationError) o).getSymbId();
			addScope = true;
		} else if (isColumnDefinition(o)) {
			symbId = ((ColumnDefinition) o).getColumnId();
			addScope = false;	
		} else if (isContinuousCovariate(o)) {
			ContinuousCovariate ccov = (ContinuousCovariate) o;
			
			// INFO: Assuming a unary application for this release. 
			for (CovariateTransformation trans : ccov.getListOfTransformation()) {
				if (trans == null) continue;
				
				TransformedCovariate tc = trans.getTransformedCovariate();
				if (tc == null) continue;
				
				symbId = tc.getSymbId();
				addScope = true;
				break;
			}
		} else if (isVariabilityLevelDefinition(o)) {
			VariabilityLevelDefinition level = (VariabilityLevelDefinition) o;
			symbId = level.getSymbId();
			addScope = true;
		}
		else if (isGeneralError(o)) {
			GeneralObsError goe = (GeneralObsError) o;
			symbId = goe.getSymbId();
			addScope = true;
		}
		else 
			throw new UnsupportedOperationException("Unsupported Symbol reference (src='" + o + "')");
		
		if (symbId == null) throw new NullPointerException("SymbId is NULL.");
		
		SymbolRef ref = new SymbolRef();
		ref.setSymbIdRef(symbId);
		
		if (addScope) {
			String blkId = a.getBlockId(o);
			if (blkId == null) {
				throw new NullPointerException("BlkId is not known (symbId='" + symbId + "', class='" + getClassName(o) + "')");
			}
			
			ref.setBlkIdRef(blkId);
		}
		
		return ref;
	}
	
	/**
	 * Matrix
	 */
	public Matrix M = null;
	
	/**
	 * PharmML model element containing the matrix assignment.
	 */
	public PharmMLRootType element = null;
	
	/**
	 * A symbol reference to the PharmML model.
	 */
	public SymbolRef ref = null;
	
	/**
	 * Constructor
	 * @param v Model Element
	 * @param a Element Accessor
	 */
	public MatrixDeclaration(PharmMLRootType v, Accessor a) {
		if (v == null || a == null) new NullPointerException("Matrix parameter arguments are NULL");
		M = readMatrix(v);
		if (M == null) new NullPointerException("Parameter matrix is NULL");
		ref = symbolRef(v, a);
		if (ref == null) throw new NullPointerException("Symbol Reference of the mstrix variable is NULL.");
		element = v;
	}
	
	/**
	 * Get the Block for the referenced model element.
	 * @return java.lang.String
	 */
	public String getBlkIdRef() {
		if (ref != null) return ref.getBlkIdRef();
		return null;
	}
	
	/**
	 * Get the symbol reference for the model element.
	 * @return java.lang.String
	 */
	public String getSymbIdRef() {
		if (ref != null) return ref.getSymbIdRef();
		return null;
	}
	
	/**
	 * Test if referenced element is a local variable.
	 * @return boolean
	 * @see eu.ddmore.libpharmml.dom.commontypes.VariableDefinition
	 */
	public boolean isLocalVariable_() { return isLocalVariable(element); }
	
	/**
	 * Test if referenced element is a parameter.
	 * @return boolean
	 * @see eu.ddmore.libpharmml.dom.modeldefn.SimpleParameter
	 */
	public boolean isParameter_() { return isPopulationParameter(element); }
	
	private Matrix readMatrix(Object src) {
		if (src == null) return null;
		if (!isRootType(src)) return null;
		
		if (isDerivative(src)) {
			DerivativeVariable dv = (DerivativeVariable) src;
			Rhs rhs = dv.getAssign();
			if (rhs != null) return rhs.getMatrix();
		} else if (isLocalVariable(src)) {
			VariableDefinition v = (VariableDefinition) src;
			Rhs rhs = v.getAssign();
			if (rhs != null) return rhs.getMatrix();
		} else if (isPopulationParameter(src)) {
			PopulationParameter p = (PopulationParameter) src;
			Rhs rhs = p.getAssign();
			if (rhs != null) return rhs.getMatrix();
		}
		
		return null;
	}
}
