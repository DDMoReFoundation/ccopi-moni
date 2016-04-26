/*******************************************************************************
 * Copyright (C) 2016 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.engine.common;

import eu.ddmore.libpharmml.dom.IndependentVariable;
import eu.ddmore.libpharmml.dom.maths.*;
import static crx.converter.engine.PharmMLTypeChecker.*;

import java.math.BigInteger;
import java.util.List;

import javax.xml.bind.JAXBElement;

import crx.converter.engine.Accessor;
import crx.converter.spi.ILexer;
import crx.converter.tree.BinaryTree;
import crx.converter.tree.Node;
import crx.converter.tree.TreeMaker;
import eu.ddmore.libpharmml.dom.commontypes.*;
import eu.ddmore.libpharmml.dom.dataset.ColumnDefinition;
import eu.ddmore.libpharmml.dom.maths.Condition;
import eu.ddmore.libpharmml.dom.maths.Piece;
import eu.ddmore.libpharmml.dom.maths.Piecewise;
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
 * Class representing a global parameter with a conditional.
 * Make assumption that the event is linked to either the
 * IDV or the state vector.
 * Currently only linked to a simple parameter.
 */
public class ParameterEvent {
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
	
	private ILexer c = null;
	private BinaryTree defaultTree = null;
	private boolean model_function_scope = false;
	private boolean modified_by_local_variable = false;
	private PopulationParameter p = null;
	private BinaryTree piecewiseTree = null;
	private boolean treesBuilt = false;
	
	/**
	 * Constructor
	 * @param p_ Parameter
	 * @param c_ Converter
	 */
	public ParameterEvent(PopulationParameter p_, ILexer c_) {
		if (p_ == null) throw new NullPointerException("Population parameter is NULL.");
		if (c_ == null) throw new NullPointerException("Converter reference is NULL.");
		
		p = p_;
		c = c_;
	}
	
	/**
	 * Build the AST for the parameter associated with the event.
	 */
	public void buildTrees() {
		if (treesBuilt) return;
		TreeMaker tm = c.getTreeMaker();
		Accessor a = c.getAccessor();
		
		Rhs rhs = p.getAssign();
		Piecewise pw = rhs.getPiecewise();
		List<Piece> pieces = pw.getListOfPiece();
		
		boolean hasDefaultAssignment = false;
		for (Piece piece : pieces) {
			if (piece == null) continue;
			Condition condition = piece.getCondition();
			if (condition == null) continue;
			if (condition.getOtherwise() != null) {
				p.setAssign(rhs(piece.getValue(), a));
				hasDefaultAssignment = true;
				break;
			}
		}
		
		if (!hasDefaultAssignment) p.setAssign(rhs((Double) 0.0, a));
		
		defaultTree = tm.newInstance(p);
		c.updateNestedTrees();
		
		piecewiseTree = tm.newInstance(rhs);
		c.updateNestedTrees();
		c.addStatement(this, piecewiseTree); // Just in case people look in the statement map.
		treesBuilt = true;
		
		checkEventScope();
	}
	
	private void checkEventScope() {
		if (piecewiseTree == null) return;

		TreeMaker tm = c.getTreeMaker();
		Accessor a = c.getAccessor();
		
		// A 'root' tree so only ever one node.
		Node node = piecewiseTree.nodes.get(0);
		Piecewise pw = (Piecewise) node.data;
		
		for (Piece piece : pw.getListOfPiece()) {
			if (model_function_scope) break;
			if (piece == null) continue;
			Condition cond = piece.getCondition();
			if (cond == null) continue;
			
			// Use a BT to crack open an expression and have a look-see inside.
			// See if conditional involves a common 'model' function type.
			BinaryTree bt = tm.newInstance(cond);
			for (Node node_ : bt.nodes) {
				if (node_ == null) continue;
				if (isSymbolReference(node_.data)){
					SymbolRef ref = (SymbolRef) node_.data;
					PharmMLRootType element = a.fetchElement(ref);
					if (isDerivative(element) || isLocalVariable(element) || isIndependentVariable(element)) {
						model_function_scope = true; 
					}
					if (isLocalVariable(element)) modified_by_local_variable = true;
				}
			}
		}
	}
	
	/**
	 * Get the default value associated with parameter declaration
	 * @return crx.converter.tree.BinaryTree
	 */
	public BinaryTree getDefaultTree() { return defaultTree; }
	
	/**
	 * Get the parameter associated with the event
	 * @return eu.ddmore.libpharmml.dom.modeldefn.PopulationParameter
	 */
	public PopulationParameter getParameter() {
		return p;
	}
	
	/**
	 * Get the piecewise tree bound to the model.
	 * @return crx.converter.tree.BinaryTree
	 */
	public BinaryTree getPiecewiseTree() { return piecewiseTree; }
	
	/**
	 * Flag that a parameter is modified by a value normally passed to a
	 * model function.
	 * @return boolean
	 */
	public boolean isModelFunctionScope() { return model_function_scope; }
	
	/**
	 * 
	 */
	public boolean isModifiedByLocalVariable() { return modified_by_local_variable; }
}
