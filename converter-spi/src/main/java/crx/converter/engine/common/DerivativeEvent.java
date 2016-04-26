/*******************************************************************************
 * Copyright (C) 2015 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.engine.common;

import static crx.converter.engine.PharmMLTypeChecker.isBinaryOperation;
import static crx.converter.engine.PharmMLTypeChecker.isColumnDefinition;
import static crx.converter.engine.PharmMLTypeChecker.isCommonParameter;
import static crx.converter.engine.PharmMLTypeChecker.isConstant;
import static crx.converter.engine.PharmMLTypeChecker.isContinuousCovariate;
import static crx.converter.engine.PharmMLTypeChecker.isCovariate;
import static crx.converter.engine.PharmMLTypeChecker.isDelay;
import static crx.converter.engine.PharmMLTypeChecker.isDerivative;
import static crx.converter.engine.PharmMLTypeChecker.isFunction;
import static crx.converter.engine.PharmMLTypeChecker.isFunctionCall;
import static crx.converter.engine.PharmMLTypeChecker.isFunctionParameter;
import static crx.converter.engine.PharmMLTypeChecker.isGeneralError;
import static crx.converter.engine.PharmMLTypeChecker.isIndependentVariable;
import static crx.converter.engine.PharmMLTypeChecker.isIndividualParameter;
import static crx.converter.engine.PharmMLTypeChecker.isInterpolation;
import static crx.converter.engine.PharmMLTypeChecker.isInterval;
import static crx.converter.engine.PharmMLTypeChecker.isJAXBElement;
import static crx.converter.engine.PharmMLTypeChecker.isLocalVariable;
import static crx.converter.engine.PharmMLTypeChecker.isMatrix;
import static crx.converter.engine.PharmMLTypeChecker.isMatrixSelector;
import static crx.converter.engine.PharmMLTypeChecker.isMatrixUnaryOperation;
import static crx.converter.engine.PharmMLTypeChecker.isObservationError;
import static crx.converter.engine.PharmMLTypeChecker.isPiecewise;
import static crx.converter.engine.PharmMLTypeChecker.isPopulationParameter;
import static crx.converter.engine.PharmMLTypeChecker.isProbability;
import static crx.converter.engine.PharmMLTypeChecker.isProduct;
import static crx.converter.engine.PharmMLTypeChecker.isRandomVariable;
import static crx.converter.engine.PharmMLTypeChecker.isRhs;
import static crx.converter.engine.PharmMLTypeChecker.isScalarInterface;
import static crx.converter.engine.PharmMLTypeChecker.isSequence;
import static crx.converter.engine.PharmMLTypeChecker.isSum;
import static crx.converter.engine.PharmMLTypeChecker.isSymbolReference;
import static crx.converter.engine.PharmMLTypeChecker.isUnaryOperation;
import static crx.converter.engine.PharmMLTypeChecker.isVariabilityLevelDefinition;
import static crx.converter.engine.PharmMLTypeChecker.isVector;
import static crx.converter.engine.PharmMLTypeChecker.isVectorSelector;
import static crx.converter.engine.Utils.getClassName;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import crx.converter.engine.Accessor;
import eu.ddmore.libpharmml.dom.IndependentVariable;
import eu.ddmore.libpharmml.dom.commontypes.Delay;
import eu.ddmore.libpharmml.dom.commontypes.DerivativeVariable;
import eu.ddmore.libpharmml.dom.commontypes.FalseBoolean;
import eu.ddmore.libpharmml.dom.commontypes.FunctionDefinition;
import eu.ddmore.libpharmml.dom.commontypes.FunctionParameter;
import eu.ddmore.libpharmml.dom.commontypes.IntValue;
import eu.ddmore.libpharmml.dom.commontypes.Interpolation;
import eu.ddmore.libpharmml.dom.commontypes.Interval;
import eu.ddmore.libpharmml.dom.commontypes.Matrix;
import eu.ddmore.libpharmml.dom.commontypes.MatrixSelector;
import eu.ddmore.libpharmml.dom.commontypes.PharmMLRootType;
import eu.ddmore.libpharmml.dom.commontypes.Product;
import eu.ddmore.libpharmml.dom.commontypes.RealValue;
import eu.ddmore.libpharmml.dom.commontypes.Rhs;
import eu.ddmore.libpharmml.dom.commontypes.Scalar;
import eu.ddmore.libpharmml.dom.commontypes.Sequence;
import eu.ddmore.libpharmml.dom.commontypes.StringValue;
import eu.ddmore.libpharmml.dom.commontypes.Sum;
import eu.ddmore.libpharmml.dom.commontypes.SymbolRef;
import eu.ddmore.libpharmml.dom.commontypes.TrueBoolean;
import eu.ddmore.libpharmml.dom.commontypes.VariableDefinition;
import eu.ddmore.libpharmml.dom.commontypes.Vector;
import eu.ddmore.libpharmml.dom.dataset.ColumnDefinition;
import eu.ddmore.libpharmml.dom.maths.Binop;
import eu.ddmore.libpharmml.dom.maths.Condition;
import eu.ddmore.libpharmml.dom.maths.Constant;
import eu.ddmore.libpharmml.dom.maths.FunctionCallType;
import eu.ddmore.libpharmml.dom.maths.MatrixUniOp;
import eu.ddmore.libpharmml.dom.maths.Piece;
import eu.ddmore.libpharmml.dom.maths.Piecewise;
import eu.ddmore.libpharmml.dom.maths.Uniop;
import eu.ddmore.libpharmml.dom.modeldefn.CommonParameter;
import eu.ddmore.libpharmml.dom.modeldefn.ContinuousCovariate;
import eu.ddmore.libpharmml.dom.modeldefn.CovariateDefinition;
import eu.ddmore.libpharmml.dom.modeldefn.CovariateTransformation;
import eu.ddmore.libpharmml.dom.modeldefn.GeneralObsError;
import eu.ddmore.libpharmml.dom.modeldefn.IndividualParameter;
import eu.ddmore.libpharmml.dom.modeldefn.ObservationError;
import eu.ddmore.libpharmml.dom.modeldefn.ParameterRandomVariable;
import eu.ddmore.libpharmml.dom.modeldefn.PopulationParameter;
import eu.ddmore.libpharmml.dom.modeldefn.Probability;
import eu.ddmore.libpharmml.dom.modeldefn.TransformedCovariate;
import eu.ddmore.libpharmml.dom.modeldefn.VariabilityLevelDefinition;

/**
 * Structural model event linked to state variable.
 * @see eu.ddmore.libpharmml.dom.commontypes.DerivativeVariable
 */
public class DerivativeEvent {
	private static void assign(Rhs rhs, Object o, Accessor a) {
		if (rhs == null || o == null) return;
		
		if (isConstant(o)) rhs.setConstant((Constant) o);
		else if (isLocalVariable(o)) rhs.setSymbRef(symbolRef((VariableDefinition) o, a));
		else if (isPopulationParameter(o)) rhs.setSymbRef(symbolRef((PopulationParameter) o, a));
		else if (isBinaryOperation(o)) rhs.setBinop((Binop) o); 
		else if (isDerivative(o)) rhs.setSymbRef(symbolRef((DerivativeVariable) o, a));
		else if (o instanceof String) rhs.setScalar(new StringValue((String) o));
		else if (o instanceof Boolean) {
			Boolean b = (Boolean) o;
			if (b.booleanValue()) rhs.setScalar(new TrueBoolean());
			else rhs.setScalar(new FalseBoolean());
		}
		else if (o instanceof Integer) rhs.setScalar(new IntValue((Integer) o));
		else if (o instanceof BigInteger) {
			BigInteger v = (BigInteger) o;
			rhs.setScalar(new IntValue(v.intValue()));
		}
		else if (o instanceof Double) rhs.setScalar(new RealValue((Double) o));
		else if (isUnaryOperation(o)) rhs.setUniop((Uniop) o);
		else if (isIndependentVariable(o)) rhs.setSymbRef(symbolRef((IndependentVariable) o, a));
		else if (isIndividualParameter(o)) rhs.setSymbRef(symbolRef((IndividualParameter) o, a));
		else if (isFunctionCall(o)) rhs.setFunctionCall((FunctionCallType) o);
		else if (isScalarInterface(o)) rhs.setScalar((Scalar) o);
		else if (isPiecewise(o)) rhs.setPiecewise((Piecewise) o);
		else if (isSymbolReference(o)) rhs.setSymbRef((SymbolRef) o);
		else if (isDelay(o)) rhs.setDelay((Delay) o);
		else if (isSum(o)) rhs.setSum((Sum) o);
		else if (isJAXBElement(o)) {
			JAXBElement<?> element = (JAXBElement<?>) o;
			assign(rhs, element.getValue(), a);
		} else if (isMatrix(o)) rhs.setMatrix((Matrix) o);
		else if (isProduct(o)) rhs.setProduct((Product) o);
		else if (isSequence(o)) rhs.setSequence((Sequence) o);
		else if (isVector(o)) rhs.setVector((Vector) o);
		else if (isInterval(o)) rhs.setInterval((Interval) o);
		else if (isInterpolation(o)) rhs.setInterpolation((Interpolation) o);
		else if (isProbability(o)) rhs.setProbability((Probability) o);
		else if (isMatrixUnaryOperation(o)) rhs.setMatrixUniop((MatrixUniOp) o);
		else if (isMatrixSelector(o)) rhs.setMatrixSelector((MatrixSelector) o);
		else if (isVectorSelector(o)) rhs.setMatrixSelector((MatrixSelector) o);
		else if (isRhs(o)) {
			Rhs assign = (Rhs) o;
			assign(rhs, assign.getContent(), a);
		}
		else
			throw new UnsupportedOperationException("Unsupported Expression Term (value='" + o + "')");
	}
	
	private static Rhs rhs(Object o, Accessor a) {
		if (o == null) throw new NullPointerException("Expression Class NULL");
		Rhs rhs = new Rhs();
		assign(rhs, o, a);
		return rhs;
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
	
	private Accessor accessor = null;
	
	/**
	 * Assignment code linked to an event.
	 */
	public List<Object> assignments = new ArrayList<Object>();
	
	/**
	 * Condition associated with each assignment.
	 */
	public List<Condition> conditions = new ArrayList<Condition>();
	
	/**
	 * Derivative associated with the event.
	 */
	public DerivativeVariable dv = null;
	
	/**
	 * Constructor
	 * @param element Model Element
	 * @param a Element Accessor
	 */
	public DerivativeEvent(DerivativeVariable element, Accessor a) {
		if (element == null || a == null) throw new NullPointerException("Required event argument null.");
		dv = element;
		accessor = a;
		process();
	}
	
	private void process() {
		Rhs eq = dv.getAssign();
		Piecewise pw = eq.getPiecewise();
		List<Piece> pieces = pw.getListOfPiece();
		if (pieces.size() == 0) throw new IllegalStateException("Conditional piecewise block has no nested statements.");
		
		// Find the 'otherwise' block.
		Piece otherwise_piece = null;
		for (Piece piece : pieces) {
			if (piece == null) continue;
			if (piece.getCondition().getOtherwise() != null) {
				otherwise_piece = piece;
				break;
			}
		}
		
		if (otherwise_piece == null) throw new IllegalStateException("The conditional block has no default assignment statment.");
		
		Object value = otherwise_piece.getValue();
		Rhs rhs = rhs(value, accessor);
		dv.setAssign(rhs);
		
		// Add the other logical clauses and assignment blocks from the remaining pieces.
		for (Piece piece : pieces) {
			if (piece == null) continue;
			else if (piece.getCondition().getOtherwise() != null) continue;
			
			conditions.add(piece.getCondition());
			assignments.add(piece.getValue());
		}
	}
}
