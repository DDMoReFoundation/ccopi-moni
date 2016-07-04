/*******************************************************************************
 * Copyright (C) 2016 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.tree;

import static crx.converter.engine.PharmMLTypeChecker.*;
import static crx.converter.tree.Utils.getClassName;

import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import crx.converter.engine.Accessor;
import crx.converter.engine.BaseEngine;
import crx.converter.engine.CategoryRef_;
import crx.converter.engine.FixedEffectCategoryRef;
import crx.converter.engine.FixedParameter;
import crx.converter.engine.MatrixDeclaration;
import eu.ddmore.libpharmml.dom.IndependentVariable;
import eu.ddmore.libpharmml.dom.commontypes.AnnotationType;
import eu.ddmore.libpharmml.dom.commontypes.BooleanValue;
import eu.ddmore.libpharmml.dom.commontypes.Delay;
import eu.ddmore.libpharmml.dom.commontypes.DelayVariable;
import eu.ddmore.libpharmml.dom.commontypes.DerivativeVariable;
import eu.ddmore.libpharmml.dom.commontypes.FunctionDefinition;
import eu.ddmore.libpharmml.dom.commontypes.FunctionParameter;
import eu.ddmore.libpharmml.dom.commontypes.IdValue;
import eu.ddmore.libpharmml.dom.commontypes.InitialCondition;
import eu.ddmore.libpharmml.dom.commontypes.IntValue;
import eu.ddmore.libpharmml.dom.commontypes.Interpolation;
import eu.ddmore.libpharmml.dom.commontypes.Interval;
import eu.ddmore.libpharmml.dom.commontypes.LowUpLimit;
import eu.ddmore.libpharmml.dom.commontypes.Matrix;
import eu.ddmore.libpharmml.dom.commontypes.MatrixCell;
import eu.ddmore.libpharmml.dom.commontypes.MatrixCellValue;
import eu.ddmore.libpharmml.dom.commontypes.MatrixSelector;
import eu.ddmore.libpharmml.dom.commontypes.MatrixVectorIndex;
import eu.ddmore.libpharmml.dom.commontypes.PharmMLRootType;
import eu.ddmore.libpharmml.dom.commontypes.Product;
import eu.ddmore.libpharmml.dom.commontypes.RealValue;
import eu.ddmore.libpharmml.dom.commontypes.Rhs;
import eu.ddmore.libpharmml.dom.commontypes.Scalar;
import eu.ddmore.libpharmml.dom.commontypes.Sequence;
import eu.ddmore.libpharmml.dom.commontypes.StandardAssignable;
import eu.ddmore.libpharmml.dom.commontypes.StringValue;
import eu.ddmore.libpharmml.dom.commontypes.Sum;
import eu.ddmore.libpharmml.dom.commontypes.SymbolRef;
import eu.ddmore.libpharmml.dom.commontypes.VariableDefinition;
import eu.ddmore.libpharmml.dom.commontypes.Vector;
import eu.ddmore.libpharmml.dom.commontypes.VectorSegmentSelector;
import eu.ddmore.libpharmml.dom.commontypes.VectorSelector;
import eu.ddmore.libpharmml.dom.commontypes.VectorValue;
import eu.ddmore.libpharmml.dom.dataset.ColumnDefinition;
import eu.ddmore.libpharmml.dom.dataset.ColumnReference;
import eu.ddmore.libpharmml.dom.dataset.MapType;
import eu.ddmore.libpharmml.dom.maths.Binop;
import eu.ddmore.libpharmml.dom.maths.Condition;
import eu.ddmore.libpharmml.dom.maths.Constant;
import eu.ddmore.libpharmml.dom.maths.ExpressionValue;
import eu.ddmore.libpharmml.dom.maths.FunctionArgumentType;
import eu.ddmore.libpharmml.dom.maths.FunctionCallType;
import eu.ddmore.libpharmml.dom.maths.FunctionCallType.FunctionArgument;
import eu.ddmore.libpharmml.dom.maths.LogicBinOp;
import eu.ddmore.libpharmml.dom.maths.LogicUniOp;
import eu.ddmore.libpharmml.dom.maths.MatrixUniOp;
import eu.ddmore.libpharmml.dom.maths.Piece;
import eu.ddmore.libpharmml.dom.maths.Piecewise;
import eu.ddmore.libpharmml.dom.maths.Uniop;
import eu.ddmore.libpharmml.dom.modeldefn.CategoricalPMF;
import eu.ddmore.libpharmml.dom.modeldefn.CategoricalRelation;
import eu.ddmore.libpharmml.dom.modeldefn.Category;
import eu.ddmore.libpharmml.dom.modeldefn.CommonDiscreteState;
import eu.ddmore.libpharmml.dom.modeldefn.CommonDiscreteVariable;
import eu.ddmore.libpharmml.dom.modeldefn.CommonParameter;
import eu.ddmore.libpharmml.dom.modeldefn.ContinuousCovariate;
import eu.ddmore.libpharmml.dom.modeldefn.CountPMF;
import eu.ddmore.libpharmml.dom.modeldefn.CovariateDefinition;
import eu.ddmore.libpharmml.dom.modeldefn.CovariateRelation;
import eu.ddmore.libpharmml.dom.modeldefn.CovariateTransformation;
import eu.ddmore.libpharmml.dom.modeldefn.DSCategoricalCovariateType;
import eu.ddmore.libpharmml.dom.modeldefn.DSCategoryType;
import eu.ddmore.libpharmml.dom.modeldefn.DSCovariateDefinitionType;
import eu.ddmore.libpharmml.dom.modeldefn.Dependance;
import eu.ddmore.libpharmml.dom.modeldefn.DiscreteDataParameter;
import eu.ddmore.libpharmml.dom.modeldefn.Distribution;
import eu.ddmore.libpharmml.dom.modeldefn.FixedEffectRelation;
import eu.ddmore.libpharmml.dom.modeldefn.GeneralObsError;
import eu.ddmore.libpharmml.dom.modeldefn.IndividualParameter;
import eu.ddmore.libpharmml.dom.modeldefn.ObservationError;
import eu.ddmore.libpharmml.dom.modeldefn.Parameter;
import eu.ddmore.libpharmml.dom.modeldefn.ParameterRandomEffect;
import eu.ddmore.libpharmml.dom.modeldefn.ParameterRandomVariable;
import eu.ddmore.libpharmml.dom.modeldefn.PopulationParameter;
import eu.ddmore.libpharmml.dom.modeldefn.Probability;
import eu.ddmore.libpharmml.dom.modeldefn.ProbabilityAssignment;
import eu.ddmore.libpharmml.dom.modeldefn.StructuredModel;
import eu.ddmore.libpharmml.dom.modeldefn.StructuredModel.GeneralCovariate;
import eu.ddmore.libpharmml.dom.modeldefn.StructuredModel.LinearCovariate.PopulationValue;
import eu.ddmore.libpharmml.dom.modeldefn.TTEFunction;
import eu.ddmore.libpharmml.dom.modeldefn.TransformedCovariate;
import eu.ddmore.libpharmml.dom.modeldefn.TransitionRate;
import eu.ddmore.libpharmml.dom.modeldefn.UncertML;
import eu.ddmore.libpharmml.dom.modeldefn.VariabilityLevelDefinition;
import eu.ddmore.libpharmml.dom.modeldefn.pkmacro.AbsorptionMacro;
import eu.ddmore.libpharmml.dom.modeldefn.pkmacro.AbsorptionOralMacro;
import eu.ddmore.libpharmml.dom.modeldefn.pkmacro.CompartmentMacro;
import eu.ddmore.libpharmml.dom.modeldefn.pkmacro.DepotMacro;
import eu.ddmore.libpharmml.dom.modeldefn.pkmacro.EffectMacro;
import eu.ddmore.libpharmml.dom.modeldefn.pkmacro.EliminationMacro;
import eu.ddmore.libpharmml.dom.modeldefn.pkmacro.IVMacro;
import eu.ddmore.libpharmml.dom.modeldefn.pkmacro.MacroValue;
import eu.ddmore.libpharmml.dom.modeldefn.pkmacro.PeripheralMacro;
import eu.ddmore.libpharmml.dom.modeldefn.pkmacro.TransferMacro;
import eu.ddmore.libpharmml.dom.modellingsteps.InitialEstimate;
import eu.ddmore.libpharmml.dom.modellingsteps.OperationProperty;
import eu.ddmore.libpharmml.dom.modellingsteps.ParameterEstimate;
import eu.ddmore.libpharmml.dom.probonto.DistributionParameter;
import eu.ddmore.libpharmml.dom.probonto.ProbOnto;
import eu.ddmore.libpharmml.dom.trialdesign.DosingTimesPoints;
import eu.ddmore.libpharmml.dom.trialdesign.DosingVariable;
import eu.ddmore.libpharmml.dom.trialdesign.SingleDesignSpace;
import eu.ddmore.libpharmml.dom.trialdesign.StageDefinition;
import eu.ddmore.libpharmml.dom.uncertml.AbstractCategoricalMultivariateDistributionType;
import eu.ddmore.libpharmml.dom.uncertml.AbstractCategoricalUnivariateDistributionType;
import eu.ddmore.libpharmml.dom.uncertml.AbstractContinuousUnivariateDistributionType;
import eu.ddmore.libpharmml.dom.uncertml.AbstractDiscreteMultivariateDistributionType;
import eu.ddmore.libpharmml.dom.uncertml.AbstractDiscreteUnivariateDistributionType;
import eu.ddmore.libpharmml.dom.uncertml.ArrayVarRefType;
import eu.ddmore.libpharmml.dom.uncertml.BernoulliDistribution;
import eu.ddmore.libpharmml.dom.uncertml.BetaDistribution;
import eu.ddmore.libpharmml.dom.uncertml.BinomialDistribution;
import eu.ddmore.libpharmml.dom.uncertml.CategoricalDistribution;
import eu.ddmore.libpharmml.dom.uncertml.CategoricalMultivariateMixtureModelType;
import eu.ddmore.libpharmml.dom.uncertml.CategoricalProbabilityValueType;
import eu.ddmore.libpharmml.dom.uncertml.CategoricalUnivariateMixtureModel;
import eu.ddmore.libpharmml.dom.uncertml.CategoricalUnivariateMixtureModelType;
import eu.ddmore.libpharmml.dom.uncertml.CauchyDistribution;
import eu.ddmore.libpharmml.dom.uncertml.ChiSquareDistribution;
import eu.ddmore.libpharmml.dom.uncertml.ContinuousValueType;
import eu.ddmore.libpharmml.dom.uncertml.CovarianceMatrixType;
import eu.ddmore.libpharmml.dom.uncertml.DiscreteMultivariateMixtureModel;
import eu.ddmore.libpharmml.dom.uncertml.DiscreteMultivariateMixtureModelType;
import eu.ddmore.libpharmml.dom.uncertml.DiscreteUnivariateMixtureModel;
import eu.ddmore.libpharmml.dom.uncertml.DiscreteUnivariateMixtureModelType;
import eu.ddmore.libpharmml.dom.uncertml.ExponentialDistribution;
import eu.ddmore.libpharmml.dom.uncertml.FDistribution;
import eu.ddmore.libpharmml.dom.uncertml.GammaDistribution;
import eu.ddmore.libpharmml.dom.uncertml.GammaDistributionType;
import eu.ddmore.libpharmml.dom.uncertml.GeometricDistribution;
import eu.ddmore.libpharmml.dom.uncertml.HypergeometricDistribution;
import eu.ddmore.libpharmml.dom.uncertml.InverseGammaDistribution;
import eu.ddmore.libpharmml.dom.uncertml.InverseGammaDistributionType;
import eu.ddmore.libpharmml.dom.uncertml.LaplaceDistribution;
import eu.ddmore.libpharmml.dom.uncertml.LogNormalDistribution;
import eu.ddmore.libpharmml.dom.uncertml.LogisticDistribution;
import eu.ddmore.libpharmml.dom.uncertml.MultinomialDistribution;
import eu.ddmore.libpharmml.dom.uncertml.NaturalNumberValueType;
import eu.ddmore.libpharmml.dom.uncertml.NegativeBinomialDistribution;
import eu.ddmore.libpharmml.dom.uncertml.NormalDistribution;
import eu.ddmore.libpharmml.dom.uncertml.ParetoDistribution;
import eu.ddmore.libpharmml.dom.uncertml.PoissonDistribution;
import eu.ddmore.libpharmml.dom.uncertml.PositiveNaturalNumber;
import eu.ddmore.libpharmml.dom.uncertml.PositiveNaturalNumberValueType;
import eu.ddmore.libpharmml.dom.uncertml.PositiveRealNumber;
import eu.ddmore.libpharmml.dom.uncertml.PositiveRealValueType;
import eu.ddmore.libpharmml.dom.uncertml.ProbabilityValueType;
import eu.ddmore.libpharmml.dom.uncertml.RealArrayValueType;
import eu.ddmore.libpharmml.dom.uncertml.StudentTDistribution;
import eu.ddmore.libpharmml.dom.uncertml.UniformDistribution;
import eu.ddmore.libpharmml.dom.uncertml.VarRefType;
import eu.ddmore.libpharmml.dom.uncertml.WeibullDistribution;
import eu.ddmore.libpharmml.dom.uncertml.WishartDistribution;

/**
 * Base TreeMaker.
 * Focuses exclusively on building trees for core PharmML data types.
 */
public class BaseTreeMaker extends BaseEngine implements TreeMaker {
	private static void assign(Rhs rhs, Object o, Accessor a) {
		if (rhs == null || o == null) return;
		
		if (isConstant(o)) rhs.setConstant((Constant) o);
		else if (isLocalVariable(o)) rhs.setSymbRef(symbolRef((VariableDefinition) o, a));
		else if (isPopulationParameter(o)) rhs.setSymbRef(symbolRef((PopulationParameter) o, a));
		else if (isBinaryOperation(o)) rhs.setBinop((Binop) o); 
		else if (isDerivative(o)) rhs.setSymbRef(symbolRef((DerivativeVariable) o, a));
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
		else if (isVectorSelector(o)) rhs.setVectorSelector((VectorSelector) o);
		else if (isRhs(o)) {
			Rhs assign = (Rhs) o;
			assign(rhs, assign.getContent(), a);
		}
		else
			throw new UnsupportedOperationException("Unsupported Expression Term (value='" + o + "')");
	}
	
	private static Rhs rhs(Double value) {
		Rhs rhs = new Rhs();
		rhs.setScalar(new RealValue(value.doubleValue()));
		return rhs;
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
				throw new NullPointerException("BlkId is not known (symbId='" + symbId + "', class='" + Utils.getClassName(o) + "')");
			}
			
			ref.setBlkIdRef(blkId);
		}
		
		return ref;
	}
	
	private Accessor a = null;
	private double default_parameter_value = 0.0;
	private boolean flush_nested_tree_refs = true;
	/**
	 * Vector containing nested tree references.
	 */
	protected List<NestedTreeRef> nested_trees = new ArrayList<NestedTreeRef>();
	
	private boolean permit_parameter_without_assignment = false;
	private boolean permitDeclarationOnlyVariables = false;
	/**
	 * Create a root node for a binary tree.
	 * @param o Object
	 * @param contextMsg Context Message
	 * @return Node
	 */
	protected Node createRootNode(Object o, String contextMsg) {
		if (o == null) {
			String format = "A required PharmML element does not exist in the model (context='%s').";
			new NullPointerException(String.format(format, contextMsg));
		}
		
		Node root_node = new Node(o);
		root_node.root = true;
		
		return root_node;
	}
	
	/**
	 * Append a root node to a binary tree.
	 * @param o Object
	 * @param contextMsg Context Message
	 * @param bt BinaryTree
	 */
	protected void createRootNode(Object o, String contextMsg, BinaryTree bt) {
		if (bt == null) {
			String format = "A binary tree being assigned a root Node was NULL (context='%s').";
			new NullPointerException(String.format(format, contextMsg));
		}
		
		bt.add(createRootNode(o, contextMsg));
	}
	
	/**
	 * Create a binary tree with a single root node.
	 * @param o Object
	 * @param contextMsg Context Message
	 * @return BinaryTree
	 */
	protected BinaryTree createRootTree(Object o, String contextMsg) {
		BinaryTree bt = new BinaryTree();
		bt.add(createRootNode(o, contextMsg));
		return bt;
	}

	/**
	 * Create a binary tree of an absorption macro.
	 * @param am Macro
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(AbsorptionMacro am) { return createRootTree(am, "Absorption Macro"); }

	/**
	 * Create a binary tree of a oral macro.
	 * @param ref Macro
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(AbsorptionOralMacro ref) {  return createRootTree(ref, "Absorption Oral Macro"); }
	
	/**
	 * Create a binary tree of a categorical multivariate distribution.
	 * @param dist Distrubution
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(AbstractCategoricalMultivariateDistributionType dist) {
		if (dist == null) return null;
		
		BinaryTree bt = createRootTree(dist, "Categorical multivariate Distribution");
		
		if (isCategoricalDistribution(dist)) doCategoricalDistribution((CategoricalDistribution) dist);
		else if (isCategoricalMultivariateMixtureModel(dist)) doCategoricalMultivariateMixtureModel((CategoricalMultivariateMixtureModelType) dist);
		else throw new UnsupportedOperationException("Unrecognised distribution class.");
		
		return bt;
	}
	
	/**
	 * Create a binary tree of an univariate categorical distribution.
	 * @param dist Distribution
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(AbstractCategoricalUnivariateDistributionType dist) {
		BinaryTree bt = createRootTree(dist, "Categorical Univariate Distribution");
		
		if (isBernoulliDistribution(dist)) doBernoulliDistribution((BernoulliDistribution) dist);	
		else if (isCategoricalUnivariateMixtureModel(dist)) doCategoricalUnivariateMixtureModel((CategoricalUnivariateMixtureModel) dist);
		
		return bt;
	}
	
	
	
	/**
	 * Create a binary tree of an univariate distribution.
	 * @param dist Distribution
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(AbstractContinuousUnivariateDistributionType dist) {
		if (isNormalDistribution(dist)) doNormalDistribution((NormalDistribution) dist);	
		else if (isGammaDistribution(dist)) doGammaDistribution((GammaDistribution) dist);
		else if (isBetaDistribution(dist)) doBetaDistribution((BetaDistribution) dist);
		else if (isCauchyDistribution(dist)) doCauchyDistribution((CauchyDistribution) dist);
		else if (isChiSquareDistribution(dist)) doChiSquareDistribution((ChiSquareDistribution) dist);
		else if (isExponentialDistribution(dist)) doExponentialDistribution((ExponentialDistribution) dist);
		else if (isFDistribution(dist)) doFDistribution((FDistribution) dist);
		else if (isStudentTDistribution(dist)) doStudentTDistribution((StudentTDistribution) dist);
		else if (isInverseGammaDistribution(dist)) doInverseGammaDistribution((InverseGammaDistribution) dist);
		else if (isLaplaceDistribution(dist)) doLaplaceDistribution((LaplaceDistribution) dist);
		else if (isLogisticDistribution(dist)) doLogisticDistribution((LogisticDistribution) dist);
		else if (isLogNormalDistribution(dist)) doLogNormalDistribution((LogNormalDistribution) dist);
		else if (isWeibullDistribution(dist)) doWeibullDistribution((WeibullDistribution) dist);
		else if (isParetoDistribution(dist)) doParetoDistribution((ParetoDistribution) dist);
		else if (isUniformDistribution(dist)) doUniformDistribution((UniformDistribution) dist);
		else
			throw new UnsupportedOperationException("Univariate distribution (" + dist + ") not supported at the moment.");
		
		BinaryTree bt = createRootTree(dist, getClassName(dist));
		nested_trees.add(new NestedTreeRef(dist, bt));
		
		return bt;
	}

	/**
	 * Create a binary tree of a discrete multivariate distribution.
	 * @param dist Distribution
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(AbstractDiscreteMultivariateDistributionType dist) {
		if (dist == null) return null;
		
		BinaryTree bt = createRootTree(dist, "Discrete Multivariate Distribution");
			
		if (isDiscreteMultivariateMixtureModel(dist)) doDiscreteMultivariateMixtureModel((DiscreteMultivariateMixtureModel) dist); 
		else if (isMultinomialDistribution(dist)) doMultinomialDistribution((MultinomialDistribution) dist);
		else if (isWishartDistribution(dist)) doWishartDistribution((WishartDistribution) dist);
		else throw new UnsupportedOperationException("Unrecognised Discrete Multivariate Distribution function.");
		
		return bt;
	}
	
	/**
	 * Create a binary tree of a discrete univariate distribution.
	 * @param dist Distribution
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(AbstractDiscreteUnivariateDistributionType dist) {
		if (isBinomialDistribution(dist)) doBinomialDistribution((BinomialDistribution) dist);
		else if (isDiscreteUnivariateMixtureModel(dist)) doDiscreteUnivariateMixtureModel((DiscreteUnivariateMixtureModel) dist);
		else if (isGeometricDistribution(dist)) doGeometricDistribution((GeometricDistribution) dist);
		else if (isHypergeometricDistribution(dist)) doHypergeometricDistribution((HypergeometricDistribution) dist);
		else if (isNegativeBinomialDistribution(dist)) doNegativeBinomialDistribution((NegativeBinomialDistribution) dist);
		else if (isPoissonDistribution(dist)) doPoissonDistribution((PoissonDistribution) dist);
		else
			throw new UnsupportedOperationException("Univariate distribution (" + dist + ") not supported at the moment.");
		
		BinaryTree bt = createRootTree(dist, getClassName(dist));
		nested_trees.add(new NestedTreeRef(dist, bt));
		
		return bt;
	}
	
	/**
	 * Create a binary tree of an array variable reference.
	 * @param vref Varaible reference
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(ArrayVarRefType vref) {
		if (vref == null) return null;
		
		SymbolRef ref = new SymbolRef();
		ref.setSymbIdRef(vref.getVarId());
		
		return createTree(ref);
	}
	
	/**
	 * Create a binary tree of a Big Integer.
	 * @param v Big Integer value
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(BigInteger v) { return createRootTree(v, "Integer Value"); }
	
	/**
	 * Create a binary tree of a binary operation.
	 * @param b_op Binary Operation
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(Binop b_op) { return createTree(rhs(b_op, a)); }
	
	/**
	 * Create a tree of a boolean value.
	 * @param v Boolean Value
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(Boolean v) { return createRootTree(v, "Boolean"); }
	
	/**
	 * Create a tree of a PharmML boolean value.
	 * @param v Boolean Value
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(BooleanValue v) { return createRootTree(v, "Boolean Value"); }
	
	/**
	 * Create a binary tree of a categorical probability mass function.
	 * @param pmf Mass Function
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(CategoricalPMF pmf) { return createTree(pmf.getDistribution()); }
	
	/**
	 * Create a binary tree of a covariate category type.
	 * @param cat Category
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(Category cat) { return createRootTree(new StringValue(cat.getCatId()), "Category"); }
	
	/**
	 * Create a binary tree of a covariate category reference.
	 * @param ref Category Reference
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(CategoryRef_ ref) { return createRootTree(ref, "Category Reference"); }
	
	/**
	 * Create a binary tree of a column reference in a data set reference.
	 * @param ref Column Reference
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(ColumnReference ref) { return createRootTree(ref, "Column Reference"); }
	
	/**
	 * Create a binary tree of a discrete state variable.
	 * @param v Discrete Variable
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(CommonDiscreteState v) {
		BinaryTree bt = createRootTree(v, "Common Discrete Variable");
		
		LogicBinOp l = v.getLogicBinop();
		if (l != null) nested_trees.add(new NestedTreeRef(l, createTree(l)));
		
		LogicUniOp u = v.getLogicUniop();
		if (u != null) nested_trees.add(new NestedTreeRef(u, createTree(u)));
		
		Integer order = v.getMarkovOrder();
		if (order != null) nested_trees.add(new NestedTreeRef(order, createTree(order)));
		
		return bt;
	}
	
	/**
	 * Create a binary tree of a discrete variable.
	 * @param v Discrete Variable
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(CommonDiscreteVariable v) {
		return createRootTree(v, "Common Discrete Variable");
	}
	
	/**
	 * Create a binary tree of a compartment macro.
	 * @param ref Macro
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(CompartmentMacro ref) { return createRootTree(ref, "Compartment Reference"); }
	
	/**
	 * Create a binary tree of a conditional statement.
	 * @param c Condition
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(Condition c) {
		Node.resetCounter();
		if (c == null) throw new NullPointerException("The SequenceType is NULL.");
		
		BinaryTree bt = new BinaryTree();
		doCondition(bt, c);
		
		return bt;
	}
	
	/**
	 * Create a binary tree of a equation constant.
	 * @param c Constant
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(Constant c) {
		if (c == null) throw new NullPointerException("ConstantType is null.");
			
		return createRootTree(c, "Constant");
	}
	
	/**
	 * Create a binary tree of a continuous covariate.
	 * @param ccov Covariate
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(ContinuousCovariate ccov) {
		BinaryTree bt = createRootTree(ccov, "Continuous Covariate");
		
		Distribution dist = ccov.getDistribution();
		if (dist != null) nested_trees.add(new NestedTreeRef(dist, createTree(dist)));
		
		Rhs assign = ccov.getAssign();
		if (assign != null) nested_trees.add(new NestedTreeRef(assign, createTree(assign)));
		
		return bt;
	}
	
	/**
	 * Create a binary tree of a UncertML continuous value.
	 * @param v Value
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(ContinuousValueType v) {
		BinaryTree bt = null;
		
		if (v.getRVal() != null) bt = createTree(v.getRVal());
		else if (v.getVar() != null) bt = createTree(v.getVar());
		
		return bt;
	}
	
	/**
	 * Create a binary tree of a count distribution mass function
	 * @param pmf Mass Function
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(CountPMF pmf) {
		BinaryTree bt = null;
		
		bt = createRootTree(pmf, "Count PMF");
			
		Rhs assign = pmf.getAssign();
		if (assign != null) nested_trees.add(new NestedTreeRef(assign, createTree(assign)));
			
		CommonDiscreteState current_state = pmf.getCurrentState();
		if (current_state != null) nested_trees.add(new NestedTreeRef(current_state, createTree(current_state)));
			
		List<CommonDiscreteState> conditions = pmf.getListOfCondition();
		if (conditions != null) 
			for (CommonDiscreteState condition : conditions) 
				if (condition != null) nested_trees.add(new NestedTreeRef(condition, createTree(condition)));
			
		List<CommonDiscreteState> previous_states = pmf.getListOfPreviousState();
		for (CommonDiscreteState previous_state : previous_states) 
			if (previous_state != null) nested_trees.add(new NestedTreeRef(previous_state, createTree(previous_state)));
			
		LogicBinOp l = pmf.getLogicBinop();
		if (l != null) nested_trees.add(new NestedTreeRef(l, createTree(l)));
			
		LogicUniOp u = pmf.getLogicUniop();
		if (u != null) nested_trees.add(new NestedTreeRef(u, createTree(u)));
		
		Distribution dist = pmf.getDistribution();
		if (dist != null) nested_trees.add(new NestedTreeRef(dist, createTree(dist)));
		
		return bt;
	}
	
	/**
	 * BinaryTree of a covariance matrix type.
	 * @param cov Matrix Type
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(CovarianceMatrixType cov) {
		if (cov == null) return null;
		
		BinaryTree bt = createRootTree(cov, "Covariance Matrix");
		
		BigInteger dim = cov.getDimension();
		if (dim != null) nested_trees.add(new NestedTreeRef(dim, createTree(dim)));
		
		RealArrayValueType value = cov.getValue();
		if (value != null) {
			ArrayVarRefType array_var = value.getArrayVar();
			if (array_var != null) nested_trees.add(new NestedTreeRef(dim, createTree(array_var)));
		
			List<ContinuousValueType> values = value.getVal();
			if (values != null) {
				for (ContinuousValueType value_ : values) {
					if (value_ != null) nested_trees.add(new NestedTreeRef(value_, createTree(value_)));
				}
			}
		}
		
		return bt;
	}
	
	/**
	 * Create a binary tree of a covariate.
	 * @param cov Covariate
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(CovariateDefinition cov) { return createRootTree(cov, "Covariate Definition"); }
	
	/**
	 * Binary tree of a covariate transform.
	 * @param transformation Transformation Definition
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(CovariateTransformation transformation) {
		if (transformation == null) throw new NullPointerException("Transform equation is NULL."); 
		return createTree(transformation.getAssign());
	}
	
	/**
	 * Create a binary tree of a delay variable term.
	 * @param value Delay Variable
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(DelayVariable value) {
		BinaryTree bt = null;
		
		if (value.getScalar() != null) bt = newInstance(value.getScalar());
		else if (value.getSymbRef() != null) bt = newInstance(value.getSymbRef());
		
		return bt;
	}
	
	/**
	 * Create a binary tree of a dependance term.
	 * @param d Dependance term
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(Dependance d) { return createRootTree(d, "Discrete Model Dependence"); }
	
	/**
	 * Create a binary tree of a depot macro.
	 * @param ref Depot Macro
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(DepotMacro ref) { return createRootTree(ref, "Depot Macro"); }
	
	/**
	 * Create a binary tree of a derivative.
	 * @param d Derivative
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(DerivativeVariable d) {
		if (d == null) throw new NullPointerException("A XML variable-type is null");
		
		if (d.getDescription() == null) {
			AnnotationType text = new AnnotationType();
			text.setValue(d.getSymbId());
			d.setDescription(text);
		}
		
		Rhs assignment = d.getAssign();
		String symbolId = d.getSymbId();
		if (symbolId == null) symbolId = "Unknown_state_variable";
		if (assignment == null)
			throw new IllegalStateException("The required assignment statement (symbolID=" + symbolId + ") is null.");

		return createTree(assignment);
	}
	
	/**
	 * Create a binary tree of a discrete data parameter.
	 * @param v Discrete DAta Parameter
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(DiscreteDataParameter v) {
		if (v == null) throw new NullPointerException("A DiscreteDataParameter is null");
		return createTree(v.getAssign());
	}
	
	/**
	 * Create a tree from a random distribution.
	 * @param d Distribution
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(Distribution d) {
		if (d.getUncertML() != null) return createTree(d.getUncertML());
		else if (d.getProbOnto() != null) return createTree(d.getProbOnto());
		else throw new NullPointerException("A distribution block was not assigned a probability distribution.");
	}
	
	/**
	 * Create a binary tree of a distribution parameter.
	 * @param p Distribution Parameter
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(DistributionParameter p) {
		Rhs assign = p.getAssign();
		if (assign == null) throw new NullPointerException("Distribution parameter assignment statement is NULL.");
		return createTree(assign);
	}
	
	/**
	 * Create a tree for a dosing time-points.
	 * @param tps Dosing Time Points
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(DosingTimesPoints tps) {
		if (tps.getAssign() != null) return createTree(tps.getAssign());
		else if (tps.getSymbRef() != null) return createTree(tps.getSymbRef());
		else throw new NullPointerException("Dosing time points not assigned a value.");
	}
	
	/**
	 * Create a tree for a dosing variable
	 * @param v Dosing Variable
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(DosingVariable v) { return createTree(v.getAssign()); }
	
	/**
	 * Create a binary tree of a Java Double value.
	 * @param value Value
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(Double value) {
		Rhs assignment = new Rhs();
		RealValue rv = new RealValue(value);
		assignment.setScalar(rv); 
		return createTree(assignment);
	}
	
	/**
	 * Create a binary tree of a design space categorical covariate. 
	 * @param category Design Space Covariate
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(DSCategoricalCovariateType cat_cov) { 
		if (cat_cov.getListOfCategory() != null) {
			for (DSCategoryType category : cat_cov.getListOfCategory()) {
				if (category == null) continue;
				nested_trees.add(new NestedTreeRef(category, createTree(category)));
			}
		}
		
		return createRootTree(cat_cov, "DesignSpaceCategoricalCovariate"); 
	}
	
	/**
	 * Create a binary tree of a design space categorical covariate. 
	 * @param category Design Space Covariate
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(DSCategoryType category) {
		if (category.getProbability() != null) nested_trees.add(new NestedTreeRef(category.getProbability(), createTree(category.getProbability())));		
		return createRootTree(category, "DesignSpaceCategory"); 
	}
	
	/**
	 * Create a binary tree of a design space covariate. 
	 * @param type Design Space Covariate
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(DSCovariateDefinitionType cov) { 
		if (cov.getCategorical() != null) nested_trees.add(new NestedTreeRef(cov.getCategorical(), createTree(cov.getCategorical())));
		if (cov.getContinuous() != null) nested_trees.add(new NestedTreeRef(cov.getContinuous(), createTree(cov.getContinuous())));

		return createRootTree(cov, "DesignSpaceCovariate"); 
	}
	
	/**
	 * Create a binary tree of an effect macro.
	 * @param ref Macro
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(EffectMacro ref) { return createRootTree(ref, "Effect Macro"); }
	
	/**
	 * Create a binary tree of an elimination macro.
	 * @param ref Macro
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(EliminationMacro ref) { return createRootTree(ref, "Elimination Macro"); }
	
	/**
	 * Create a binary tree of an UncertML Double.
	 * @param d UncertML Double
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(eu.ddmore.libpharmml.dom.uncertml.Double d) {
		BinaryTree bt = null;
		
		if (d.getVar() != null) bt = createTree(d.getVar());
		else if (d.getRVal() != null) bt = createTree(d.getRVal());
		
		if (bt == null) throw new NullPointerException("uncertml.Double was not assigned a value.");
			
		return bt;
	}
	
	/**
	 * Create a binary tree of a mixed effect category reference.
	 * @param ref Reference
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(FixedEffectCategoryRef ref) { return createRootTree(ref, "Fixed Effect Category Reference"); }
	
	private BinaryTree createTree(FixedParameter fp) {
		if (fp == null) throw new NullPointerException("A fixed parameter associated with an estimation cannot be NULL");
		
		BinaryTree bt = createRootTree(fp, "Fixed Parameter");
		createTree(fp.pe); // Sub-trees. 
		
		return bt;
	}
	
	/**
	 * Create a binary tree for a function call argument.
	 * @param arg Function call argument
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(FunctionArgumentType arg) {
		if (arg.getAssign() != null) return createTree(arg.getAssign());
		else if (arg.getScalar() != null) return createTree(arg.getScalar());
		else if (arg.getSymbRef() != null) return createTree(arg.getSymbRef());
		else throw new NullPointerException("A function argument was not assigned a value.");
	}
	
	/**
	 * Create a binary tree of a function call.
	 * @param v Function call
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(FunctionCallType v) {
		for (FunctionArgument arg : v.getListOfFunctionArgument()) {
			if (arg == null) continue;
			nested_trees.add(new NestedTreeRef(arg, createTree(arg)));
		}
		
		return createRootTree(v, "Function Call"); 
	}
	
	/**
	 * Create a binary tree of a function definition.
	 * @param f Function
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(FunctionDefinition f) {
		BinaryTree bt = null;
		if (f != null) {
			if (f.getDefinition() != null) bt = createTree(f.getDefinition()); 
		}
		
		return bt;
	}
	
	/**
	 * Create a binary tree of a general covariate.
	 * @param o General Covariate
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(GeneralCovariate o) { return createTree(o.getAssign()); }
	
	/**
	 * Create a binary tree of an identifier value.
	 * @param v ID value
	 * @return BinaryTRee
	 */
	protected BinaryTree createTree(IdValue v) { return createRootTree(v, "ID value"); }
	
	/**
	 * Create a binary tree of an individual parameter.
	 * @param ip Individual parameter
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(IndividualParameter ip) {
		BinaryTree bt = new BinaryTree();
		
		Node root_node = new Node(ip);
		root_node.root = true;
		bt.add(root_node);
		
		if (ip.getDistribution() != null) 
			nested_trees.add(new NestedTreeRef(ip.getDistribution(), createTree(ip.getDistribution())));
		
		StructuredModel model = ip.getStructuredModel();
		Rhs assign = ip.getAssign();
		
		if (model != null) doStructuredModel(model);
		else if (assign != null) nested_trees.add(new NestedTreeRef(assign, createTree(assign)));
		
		return bt;
	}
	
	/**
	 * Create a binary tree of an initial condition.
	 * @param ic Initial Condition
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(InitialCondition ic) {
		if (ic == null) throw new NullPointerException("A XML variable-type is null");
		Rhs assignment = ic.getInitialValue().getAssign();
		
		if (assignment == null)
			throw new IllegalStateException("The required initial condition assignment statement is null.");

		return createTree(assignment);
	}
	
	/**
	 * Create a binary tree of a Java integer.
	 * @param v Integer
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(Integer v) { return createTree(new IntValue(v)); }
	
	/**
	 * Create a binary tree of an PharmML integer value.
	 * @param value Value
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(IntValue value) { return createRootTree(value, "Integer Value"); }
	
	/**
	 * Create a binary tree of an IV macro.
	 * @param ref Macro
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(IVMacro ref) { return createRootTree(ref, "IV Macro"); }
	
	/**
	 * Create a binary tree of enclosed JAXB element.
	 * @param tag JAXB Tag
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(JAXBElement<?> tag) {
		Object value = tag.getValue();
		return newInstance(value);
	}
	
	/**
	 * Create a binary tree of a binary logical operator.
	 * @param lbop Binary Logical operator
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(LogicBinOp lbop) {
		Condition cond = new Condition();
		cond.setLogicBinop(lbop);
		return createTree(cond);
	}
	
	/**
	 * Create a binary tree of logical unary operator.
	 * @param luop Logical Unary Operator
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(LogicUniOp luop) {
		Condition cond = new Condition();
		cond.setLogicUniop(luop);
		return createTree(cond);
	}
	
	/**
	 * Create a binary tree of an estimate lower limit.
	 * @param limit Lower Limit
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(LowUpLimit limit) {
		if (limit == null) throw new NullPointerException("Scalar is null");
		
		BinaryTree bt = null;
		
		if (limit.getAssign() != null) bt = createTree(limit.getAssign());
		else if (limit.getInt() != null) bt = createTree(limit.getInt());
		else if (limit.getSymbRef() != null) bt = createTree(limit.getSymbRef());
		else throw new NullPointerException("Limit has no assignment block.");
		
		return bt;
	}
	
	/**
	 * Create a binary tree of a Macro parameter value.
	 * @param v Value
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(MacroValue v) {
		if (v.getAssign() != null) return createTree(v.getAssign());
		else if (v.getSymbRef() != null) return createTree(v.getSymbRef());
		else
			throw new NullPointerException("A Macro Value as left unassigned.");
	}
	
	/**
	 * Create trees for a categorical map type.
	 * @param m
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(MapType m) {
		BinaryTree bt = createRootTree(m, "Mapping Declaration");
		
		Integer adm = m.getAdmNumber();
		if (adm != null) nested_trees.add(new NestedTreeRef(adm, createTree(adm)));
		
		String dataSymbol = m.getDataSymbol();
		if (dataSymbol != null) nested_trees.add(new NestedTreeRef(dataSymbol, createTree(dataSymbol)));
		
		String modelSymbol = m.getModelSymbol();
		if (modelSymbol != null) nested_trees.add(new NestedTreeRef(modelSymbol, createTree(modelSymbol)));
		
		return bt;
	}
	
	/**
	 * Create a binary tree of a matrix.
	 * @param M Matrix
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(Matrix M) {
		Rhs rhs = new Rhs();
		rhs.setMatrix(M);
		return createTree(rhs); 
	}
	
	/**
	 * Create a binary tree of a matrix cell value.
	 * @param cell Cell Value
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(MatrixCell cell) {
		MatrixCellValue value = cell.getValue();
		if (value == null) throw new NullPointerException("Matrix cell value is NULL.");
		
		return newInstance(value); 
	}
	
	/**
	 * Create a binary tree of a matrix declaration.
	 * @param v Matrix Declaration
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(MatrixDeclaration v) {
		BinaryTree bt = new BinaryTree();
		if (!doMatrix(bt, v.M)) throw new IllegalStateException("Tree creation for matrix variable reference failed.");
		return bt;
	}
	
	/**
	 * Create a tree of a Matrix/Vector Index.
	 * @param idx Matrix/Vector Index
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(MatrixVectorIndex idx) { 
		if (idx.getAssign() != null) nested_trees.add(new NestedTreeRef(idx.getAssign(), createTree(idx.getAssign())));
		if (idx.getIntValue() != null) nested_trees.add(new NestedTreeRef(idx.getIntValue(), createTree(idx.getIntValue())));
		if (idx.getSymbolRef() != null) nested_trees.add(new NestedTreeRef(idx.getSymbolRef(), createTree(idx.getSymbolRef())));
		
		return createRootTree(idx, "MatrixVectorIndex"); 
	}
	
	/**
	 * Create a binary tree of a Natural Number.
	 * @param v Natural Number
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(NaturalNumberValueType v) {
		BinaryTree bt = null;
		
		if (v.getNVal() != null) bt = createTree(v.getNVal());
		else if (v.getVar() != null) bt = createTree(v.getVar());
		
		return bt;
	}
	
	/**
	 * Create a binary tree of an observation error model.
	 * @param error Error Model
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(ObservationError error) {
		return createRootTree(error, "Observation Error");
	}
	
	/**
	 * Create a binary tree of an operation property.
	 * @param op Operation Property
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(OperationProperty op) {
		return createTree(op.getAssign());
	}
	
	/**
	 * Create a binary tree of a numeric parameter
	 * @param p Parameter
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(Parameter p) { return createTree(p.getAssign()); }
	
	/**
	 * Create a binary tree of a parameter estimate.
	 * @param v Parameter Estimate
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(ParameterEstimate v) {
		if (v == null) throw new NullPointerException("The parameter estimate cannot be NULL");
		
		BinaryTree bt = createRootTree(v, "Parameter Estimate");
		
		InitialEstimate ie = v.getInitialEstimate();
		Rhs lower = v.getLowerBound();
		Rhs upper = v.getUpperBound();
		SymbolRef ref = v.getSymbRef();
		
		if (ie != null) nested_trees.add(new NestedTreeRef(ie, createTree(ie)));
		if (lower != null) nested_trees.add(new NestedTreeRef(lower, createTree(lower)));
		if (upper != null) nested_trees.add(new NestedTreeRef(upper, createTree(upper)));
		if (ref != null) nested_trees.add(new NestedTreeRef(ref, createTree(ref)));
		
		return bt;
	}
	
	/**
	 * Create a binary tree of a random variable.
	 * @param rv Random variable
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(ParameterRandomVariable rv) { return createTree(rv.getDistribution()); }
	
	/**
	 * Create a binary tree of an peripheral macro.
	 * @param ref Macro
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(PeripheralMacro ref) { return createRootTree(ref, "Peripheral Macro"); }
	
	/**
	 * Create a binary tree of an conditional piecewise section.
	 * @param p Piecewise section
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(Piece p) {
		if (p == null) throw new NullPointerException("The conditional piece cannot be NULL");
		
		BinaryTree bt = createRootTree(p, "Piece of a Piecewise Conditional");
		
		boolean assigned = false;
		List<NestedTreeRef> local_trees = new ArrayList<NestedTreeRef>();
		if (p.getCondition() == null) {
			BinaryTree inner_bt = createTree(p.getCondition());
			local_trees.add(new NestedTreeRef(p.getCondition(), inner_bt));
		} 
		
		// Assignment block
		ExpressionValue expr = p.getValue();
		if (isBinaryOperation(expr)) {
			BinaryTree inner_bt = new BinaryTree();
			assigned = doExpression(inner_bt, rhs(expr, a));
			local_trees.add(new NestedTreeRef(expr, inner_bt));
		} else if (isConstant(expr)) {
			BinaryTree inner_bt = createTree((Constant) expr);
			local_trees.add(new NestedTreeRef(expr, inner_bt));
			assigned = true;
		} else if (isFunctionCall(expr)) {
			BinaryTree inner_bt = createTree((FunctionCallType) expr);
			local_trees.add(new NestedTreeRef(expr, inner_bt));
			assigned = true;
		} else if (isMatrixSelector(expr)) {
			throw new UnsupportedOperationException("MatrixSelector not supported yet.");
		} else if (isProbability(expr)) {
			BinaryTree inner_bt = createTree((Probability) expr);
			local_trees.add(new NestedTreeRef(expr, inner_bt));
			assigned = true;
		} else if (isProduct(expr)) {
			BinaryTree inner_bt = new BinaryTree();
			assigned = doExpression(inner_bt, rhs(expr, a));
			local_trees.add(new NestedTreeRef(expr, inner_bt));
		} else if (isJAXBElement(expr)) {
			BinaryTree inner_bt = new BinaryTree();
			assigned = doExpression(inner_bt, rhs(expr, a));
			local_trees.add(new NestedTreeRef(expr, inner_bt));
		} else if (isSum(expr)) {
			BinaryTree inner_bt = new BinaryTree();
			assigned = doExpression(inner_bt, rhs(expr, a));
			local_trees.add(new NestedTreeRef(expr, inner_bt));
		} else if (isSymbolReference(expr)) {
			BinaryTree inner_bt = new BinaryTree();
			assigned = doSymbolReference(inner_bt, (SymbolRef) expr);
			local_trees.add(new NestedTreeRef(expr, inner_bt));
		} else if (isUnaryOperation(expr)) {
			BinaryTree inner_bt = new BinaryTree();
			assigned = doExpression(inner_bt, rhs(expr, a));
			local_trees.add(new NestedTreeRef(expr, inner_bt));
		} else if (isScalarInterface(expr)) {
			BinaryTree inner_bt = new BinaryTree();
			assigned = doExpression(inner_bt, rhs(expr, a));
			local_trees.add(new NestedTreeRef(expr, inner_bt));
		} 
		
		if (!assigned) throw new IllegalStateException("Piece assignment block not defined.");
		nested_trees.addAll(local_trees);
		
		return bt;
	}
	
	/**
	 * Create all the nested trees required to process a piecewise block.
	 * @param pw Piecewise Definition
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(Piecewise pw) {
		List<NestedTreeRef> local_trees = new ArrayList<NestedTreeRef>();
		
		boolean temp = flush_nested_tree_refs;
		flush_nested_tree_refs = false;
		for (Piece piece : pw.getListOfPiece()) {
			if (piece == null) continue;
			local_trees.add(new NestedTreeRef(piece, createTree(piece)));
			local_trees.addAll(nested_trees);
		}
		flush_nested_tree_refs = temp;
		
		return createRootTree(pw, "Piecewise Block");
	}
	
	/**
	 * Create a binary tree of a simple/population parameter.
	 * @param p Parameter
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(PopulationParameter p) {
		if (p == null) throw new NullPointerException("A simple parameter is NULL");
		
		// An annotation to make script easier to read.
		if (p.getDescription() == null) {
			AnnotationType text = new AnnotationType();
			text.setValue(p.getSymbId());
			p.setDescription(text);
		}
		
		Rhs assignment = p.getAssign();
		String symbolId = p.getSymbId();
		if (symbolId == null) symbolId = "Unknown_parameter";
		if (assignment == null) {
			if (permit_parameter_without_assignment) assignment = rhs(default_parameter_value);
			else
				throw new IllegalStateException("The required assignment statement (symbolID=" + symbolId + ") is null.");
		}

		return createTree(assignment);
	}
	
	/**
	 * Create a binary tree of a population value in a structured error model.
	 * @param pv
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(PopulationValue pv) { return createTree(pv.getAssign()); }
	
	/**
	 * Create a binary tree of a population value in a positive natural number.
	 * @param v
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(PositiveNaturalNumber v) {
		BinaryTree bt = null;
		
		if (v.getPnVal() != null) bt = createTree(v.getPnVal());
		else if (v.getVar() != null) bt = createTree(v.getVar());
		
		return bt;
	}
	
	/**
	 * Create a binary tree of a positive real value
	 * @param d Value
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(PositiveRealValueType d) {
		BinaryTree bt = null;
		
		if (d.getVar() != null) bt = createTree(d.getVar());
		else if (d.getPrVal() != null) bt = createTree(d.getPrVal());
		
		if (bt == null) throw new NullPointerException("PositiveRealValueType was not assigned a value.");
			
		return bt;
	}
	
	/**
	 * Create a binary tree of a probability definition.
	 * @param p Probability
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(Probability p) {
		BinaryTree bt = createRootTree(p, "Probability");
		
		CommonDiscreteState current_state = p.getCurrentState();
		if (current_state != null) nested_trees.add(new NestedTreeRef(current_state, createTree(current_state)));
		
		List<CommonDiscreteState> conditions = p.getListOfCondition();
		if (conditions != null) 
			for (CommonDiscreteState condition : conditions) 
				if (condition != null) nested_trees.add(new NestedTreeRef(condition, createTree(condition)));
		
		List<CommonDiscreteState> previous_states = p.getListOfPreviousState();
		for (CommonDiscreteState previous_state : previous_states) 
			if (previous_state != null) nested_trees.add(new NestedTreeRef(previous_state, createTree(previous_state)));
		
		LogicBinOp l = p.getLogicBinop();
		if (l != null) nested_trees.add(new NestedTreeRef(l, createTree(l)));
		
		LogicUniOp u = p.getLogicUniop();
		if (u != null) nested_trees.add(new NestedTreeRef(u, createTree(u)));
		
		return bt;
	}

	/**
	 * Create a binary tree of a probability assignment.
	 * @param pa Assignment
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(ProbabilityAssignment pa) {
		BinaryTree bt = createRootTree(pa, "Transition Rate");
		
		Rhs rhs = pa.getAssign();
		if (rhs != null) nested_trees.add(new NestedTreeRef(rhs, createTree(rhs)));
		
		List<Probability> probs = pa.getListOfProbability();
		if (probs != null) 
			for (Probability prob : probs) 
				if (prob != null) nested_trees.add(new NestedTreeRef(prob, createTree(prob)));
		
		List<TransitionRate> trs = pa.getListOfTransitionRate();
		if (trs != null)
			for (TransitionRate tr : trs) 
				if (tr != null) nested_trees.add(new NestedTreeRef(tr, createTree(tr)));
		
		LogicBinOp lbop = pa.getLogicBinop();
		if (lbop != null) nested_trees.add(new NestedTreeRef(lbop, createTree(lbop)));
		
		return bt;
	}
	
	/**
	 * Create a binary tree of a probability value.
	 * @param v Prob Value
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(ProbabilityValueType v) {
		BinaryTree bt = null;
		
		if (v.getPVal() != null) bt = createTree(v.getPVal());
		else if (v.getVar() != null) bt = createTree(v.getVar());
		
		return bt;
	}
	
	private BinaryTree createTree(ProbOnto probOnto) {
		List<DistributionParameter> params = probOnto.getListOfParameter();
		if (params != null) {
			for (DistributionParameter param : params) {
				if (param == null) continue;
				if (param.getAssign() != null) {
					NestedTreeRef ref = new NestedTreeRef(param, createTree(param.getAssign()));
					nested_trees.add(ref);
				}
			}
		}
		
		return createRootTree(probOnto, getClassName(probOnto));
	}
	
	/**
	 * Create a binary tree of a real value.
	 * @param r Value
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(RealValue r) {
		Double v = r.getValue();
		return createTree(v);
	}
	
	/**
	 * Create a binary tree of an RHS maths expression.
	 * @param rhs Maths Expression
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(Rhs rhs) {
		BinaryTree bt = new BinaryTree();
		boolean assignmentElementPresent = doRhs(bt, rhs);
		
		if (!assignmentElementPresent)
			throw new IllegalStateException("Local variable assignment statement is not present.");

		return bt;
	}
	
	/**
	 * Create a binary tree of a PharmML Scalar.
	 * @param scalar Scalar Value
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(Scalar scalar) {
		if (scalar == null) throw new NullPointerException("Scalar is null");
		return createRootTree(scalar, "Scalar Value");
	}
	
	/**
	 * Create a binary tree of a sequence.
	 * @param v Sequence
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(Sequence v) {
		if (v == null) throw new NullPointerException("The SequenceType is NULL.");
		else if (nested_trees == null) throw new NullPointerException("The nested tree array is NULL.");
		
		BinaryTree bt = new BinaryTree();
		doSequence(bt, v);
		
		return bt;
	}
	
	/**
	 * Create a binary tree of a trial design space.
	 * @param space Design Space
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(SingleDesignSpace space) { 
		if (space.getArmSize() != null) nested_trees.add(new NestedTreeRef(space.getArmSize(), createTree(space.getArmSize())));
		if (space.getAssign() != null) nested_trees.add(new NestedTreeRef(space.getAssign(), createTree(space.getAssign())));
		if (space.getCovariateRef() != null) nested_trees.add(new NestedTreeRef(space.getCovariateRef(), createTree(space.getCovariateRef())));
		if (space.getDoseAmount() != null) nested_trees.add(new NestedTreeRef(space.getDoseAmount(), createTree(space.getDoseAmount())));
		if (space.getDosingTimes() != null) nested_trees.add(new NestedTreeRef(space.getDosingTimes(), createTree(space.getDosingTimes())));
		if (space.getDuration() != null) nested_trees.add(new NestedTreeRef(space.getDuration(), createTree(space.getDuration())));
		if (space.getNumberArms() != null) nested_trees.add(new NestedTreeRef(space.getNumberArms(), createTree(space.getNumberArms())));
		if (space.getNumberSamples() != null) nested_trees.add(new NestedTreeRef(space.getNumberSamples(), createTree(space.getNumberSamples())));
		if (space.getNumberTimes() != null) nested_trees.add(new NestedTreeRef(space.getNumberTimes(), createTree(space.getNumberTimes())));
		
		// Added code as MDL 8.0 generated PharmML may leave this element empty.
		if (space.getObservationTimes() != null) {
			if (space.getObservationTimes().getAssign() != null)
				nested_trees.add(new NestedTreeRef(space.getObservationTimes(), createTree(space.getObservationTimes())));
			else 
				nested_trees.add(new NestedTreeRef(space.getObservationTimes(), createRootTree(space.getObservationTimes(), "EmptyObservationTimes")));
		}
		
		if (space.getStageDefinition() != null) nested_trees.add(new NestedTreeRef(space.getStageDefinition(), createTree(space.getStageDefinition())));
		
		return createRootTree(space, "DesignSpace"); 
	}
	
	/**
	 * Create a binary tree for a stage referenced in a design space. 
	 * @param stage Design Space Covariate
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(StageDefinition stage) {
		if (stage.getLogicBinop() != null) nested_trees.add(new NestedTreeRef(stage.getLogicBinop(), createTree(stage.getLogicBinop())));		
		return createRootTree(stage, "Stage"); 
	}
	
	/**
	 * Create a binary tree of a standard expression assignment.
	 * @param sa Expression Assignment
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(StandardAssignable sa) {
		Rhs rhs = sa.getAssign();
		if (rhs == null) throw new NullPointerException("Assignment statement is NULL.");
		return createTree(rhs);
	}
	
	/**
	 * Create a binary tree of a string value.
	 * @param v String Value
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(String v) { return createTree(new StringValue(v)); }
	
	/**
	 * Create a binary tree of a string value
	 * @param v String Value
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(StringValue v) { return createRootTree(v, "String Value"); }

	/**
	 * Create a binary tree of a symbol reference.
	 * @param ref Symbol Reference
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(SymbolRef ref) {
		if (ref == null) throw new NullPointerException("Symbol Reference is NULL.");
		BinaryTree bt = new BinaryTree();
		doSymbolReference(bt, ref);
		return bt;
	}
	
	/**
	 * Create a binary tree of a transfer macro.
	 * @param ref Transfer Macro
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(TransferMacro ref) { return createRootTree(ref, "Transfer Macro"); }
	
	/**
	 * Create a binary tree of a transition rate.
	 * @param tr Transition Rate
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(TransitionRate tr) {
		BinaryTree bt = createRootTree(tr, "Transition Rate");
		
		CommonDiscreteState current_state = tr.getCurrentState();
		if (current_state != null) nested_trees.add(new NestedTreeRef(current_state, createTree(current_state)));
		 
		List<CommonDiscreteState> conditions = tr.getListOfCondition();
		if (conditions != null) {
			for (CommonDiscreteState condition : conditions) 
				if (condition != null) nested_trees.add(new NestedTreeRef(condition, createTree(condition)));
		}
		
		List<CommonDiscreteState> previous_states = tr.getListOfPreviousState();
		if (previous_states != null) {
			for (CommonDiscreteState previous_state : previous_states) 
				if (previous_state != null) nested_trees.add(new NestedTreeRef(previous_state, createTree(previous_state)));
		}
		
		return bt;
	}
	
	/**
	 * Create a binary tree of a time-to-event (TTE) function.
	 * @param ttef TTE Function
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(TTEFunction ttef) { return createTree(ttef.getAssign()); }
	
	private BinaryTree createTree(UncertML uncertML) {
		if (uncertML.getAbstractCategoricalMultivariateDistribution() != null) {
			return null;
		} else if (uncertML.getAbstractCategoricalUnivariateDistribution() != null) {
			return null;
		} else if (uncertML.getAbstractContinuousMultivariateDistribution() != null) {
			return null;
		} else if (uncertML.getAbstractContinuousUnivariateDistribution() != null) {
			JAXBElement<? extends AbstractContinuousUnivariateDistributionType> tag = uncertML.getAbstractContinuousUnivariateDistribution();
			return createTree(tag.getValue());
		} else if (uncertML.getAbstractDiscreteMultivariateDistribution() != null) {
			return null;
		} else if (uncertML.getAbstractDiscreteUnivariateDistribution() != null) {
			JAXBElement<? extends AbstractDiscreteUnivariateDistributionType> tag = uncertML.getAbstractDiscreteUnivariateDistribution();
			return createTree(tag.getValue());
		} else 
			throw new NullPointerException("UncertML has no defintiion terms."); 
	}
	
	/**
	 * Create a binary tree of an unary operation.
	 * @param u_op Unary Operation
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(Uniop u_op) { return createTree(rhs(u_op, a)); }
	
	/**
	 * Create a binary tree of a variable definition.
	 * @param v Variable
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(VariableDefinition v) {
		if (v == null) throw new NullPointerException("Variable Definition is NULL");
		
		Rhs assignment = v.getAssign();
		String symbolId = v.getSymbId();
		if (symbolId == null) symbolId = "Unknown_local_variable";
		
		// Modify the local variable tree generation if working with a declarative language only.
		if (assignment == null) {
			if (permitDeclarationOnlyVariables) return createTree(new Integer(0)); // Assign element as zero.
			else throw new IllegalStateException("The required assignment statement (symbolID=" + symbolId+ ") is null.");
		}
		
		return createTree(assignment);
	}
	
	/**
	 * Create a binary tree of an UncertML variable reference.
	 * @param v
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(VarRefType v) { return createRootTree(v, "UncertML Variable Reference"); }
	
	/**
	 * Create a binary tree of a vector
	 * @param v Vector
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(Vector v) {
		if (v == null) throw new NullPointerException("The SequenceType is NULL.");
		else if (nested_trees == null) throw new NullPointerException("The nested tree array is NULL.");
		
		BinaryTree bt = new BinaryTree();
		doVector(bt, v);
		
		return bt;
	}
	
	/**
	 * Create trees linked to a vector segment selection
	 * @param vss Vector Segment
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(VectorSegmentSelector vss) {
		if (vss.getSegmentLength() != null) nested_trees.add(new NestedTreeRef(vss.getSegmentLength(), createTree(vss.getSegmentLength())));
		if (vss.getStartIndex() != null) nested_trees.add(new NestedTreeRef(vss.getStartIndex(), createTree(vss.getStartIndex())));
		
		return createRootTree(vss, "VectorSegmentSelector");
	}
	
	/**
	 * Create a tree of a Vector Selector;
	 * @param vs Vector Selector
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(VectorSelector vs) { 
		List<PharmMLRootType> elements =  vs.getCellOrSegment();
		if (elements != null) {
			for (PharmMLRootType element : elements) {
				if (isMatrixVectorIndex(element)) 
					nested_trees.add(new NestedTreeRef((MatrixVectorIndex) element, createTree((MatrixVectorIndex) element)));
				else if (isVectorSegmentSelector(element)) 
					nested_trees.add(new NestedTreeRef((VectorSegmentSelector) element, createTree((VectorSegmentSelector) element)));
			}
		}
		
		if (vs.getHead() != null) nested_trees.add(new NestedTreeRef((MatrixVectorIndex) vs.getHead(), createTree((MatrixVectorIndex) vs.getHead())));
		if (vs.getSymbRef() != null) nested_trees.add(new NestedTreeRef(vs.getSymbRef(), createTree(vs.getSymbRef())));
		if (vs.getTail() != null) nested_trees.add(new NestedTreeRef((MatrixVectorIndex) vs.getTail(), createTree((MatrixVectorIndex) vs.getTail())));
		
		return createRootTree(vs, "VectorSelector"); 
	}
	
	private void doBernoulliDistribution(BernoulliDistribution bd) {
		List<CategoricalProbabilityValueType> probs = bd.getCategoryProb();
		
		if (probs == null) return;
		
		for (CategoricalProbabilityValueType prob : probs) {
			if (prob == null) continue;
			ProbabilityValueType value = prob.getProbability();
			if (value != null) nested_trees.add(new NestedTreeRef(prob, createTree(value)));
		}
	}
	
	private void doBetaDistribution(BetaDistribution b) {
		if (b == null) return;
		
		PositiveRealValueType alpha = b.getAlpha();
		if (alpha != null) nested_trees.add(new NestedTreeRef(alpha, createTree(alpha)));
		
		PositiveRealValueType beta = b.getBeta();
		if (beta != null) nested_trees.add(new NestedTreeRef(beta, createTree(beta)));
	}
	
	private void doBinomialDistribution(BinomialDistribution bd) {
		if (bd == null) return;
		
		NaturalNumberValueType nTrials = bd.getNumberOfTrials();
		if (nTrials != null) nested_trees.add(new NestedTreeRef(nTrials, createTree(nTrials)));
		
		ProbabilityValueType probOfSuccess = bd.getProbabilityOfSuccess();
		if (probOfSuccess != null) nested_trees.add(new NestedTreeRef(probOfSuccess, createTree(probOfSuccess)));
	}
	
	private void doCategoricalDistribution(CategoricalDistribution cd) {
		List<CategoricalProbabilityValueType> probs = cd.getCategoryProb();
		if (probs == null) return;
		
		for (CategoricalProbabilityValueType prob : probs) {
			if (prob == null) continue;
			ProbabilityValueType value = prob.getProbability();
			if (value != null) nested_trees.add(new NestedTreeRef(prob, createTree(value)));
		}
	}
	
	private void doCategoricalMultivariateMixtureModel(CategoricalMultivariateMixtureModelType mm) {
		if (mm == null) return;
		
		List<CategoricalMultivariateMixtureModelType.Component> components = mm.getComponent();
		if (components == null) return;
		
		for (CategoricalMultivariateMixtureModelType.Component component : components) {
			if (component == null) continue;
			
			JAXBElement<? extends AbstractCategoricalMultivariateDistributionType> tag = component.getAbstractCategoricalMultivariateDistribution();
			if (tag == null) continue;
			
			AbstractCategoricalMultivariateDistributionType dist = tag.getValue();
			if (dist != null) nested_trees.add(new NestedTreeRef(dist, createTree(dist)));
		}
	}
	
	private void doCategoricalUnivariateMixtureModel(CategoricalUnivariateMixtureModel mm) {
		if (mm == null) return;
		
		List<CategoricalUnivariateMixtureModelType.Component> components = mm.getComponent();
		if (components == null) return;
		
		for (CategoricalUnivariateMixtureModelType.Component component : components) {
			if (component == null) continue;
			
			JAXBElement<? extends AbstractCategoricalUnivariateDistributionType> tag = component.getAbstractCategoricalUnivariateDistribution();
			if (tag == null) continue;
			
			AbstractCategoricalUnivariateDistributionType dist = tag.getValue();
			if (dist != null) nested_trees.add(new NestedTreeRef(dist, createTree(dist)));
		}
	}
	
	private void doCauchyDistribution(CauchyDistribution c) {
		if (c == null) return;
		
		PositiveRealValueType scale = c.getScale();
		if (scale != null) nested_trees.add(new NestedTreeRef(scale, createTree(scale)));
		
		ContinuousValueType location = c.getLocation();
		if (location != null) nested_trees.add(new NestedTreeRef(location, createTree(location)));
	}
	
	private void doChiSquareDistribution(ChiSquareDistribution c) {
		if (c == null) return;
		
		PositiveNaturalNumberValueType N = c.getDegreesOfFreedom();
		if (N != null) nested_trees.add(new NestedTreeRef(N, createTree(N)));
	}
	
	/**
	 * Crack open a PharmMl logical/conditional expression and place in a binary tree.
	 * @param bt Binary Tree
	 * @param cond Condition
	 * @return boolean
	 */
	private boolean doCondition(BinaryTree bt, Condition cond) {
		if (bt != null && cond != null) {
			ArrayDeque<Node> stack = new ArrayDeque<Node>();
			
			boolean otherwisePiece = false, embeddedBoolean = false;
			Object root_data = null;
			if (cond.getBoolean() != null) {
				root_data = cond.getBoolean().getValue();
				embeddedBoolean = true;
			}
			else if (cond.getLogicBinop() != null) root_data = cond.getLogicBinop();
			else if (cond.getLogicUniop() != null) root_data = cond.getLogicUniop();
			else if (cond.getOtherwise() != null) {
				root_data = cond.getOtherwise();
				otherwisePiece = true;
			}
			
			if (root_data == null) throw new IllegalStateException("The Condition contains no root data.");
			
			Node root_node = new Node(root_data);
			root_node.root = true;
			bt.add(root_node);
			
			if (otherwisePiece || embeddedBoolean) return true; // No point processing further if TRUE.

			stack.push(root_node);
			int step_count = 0;
			while (stack.size() > 0 && step_count < MAX_STEP_COUNT) {
				Node current_node = stack.pop();
				if (isBinaryOperation(current_node.data)) {
					Binop b_op = (Binop) current_node.data;
					String op = convertBinoperator(b_op);
					if (!isSupportedBinaryOp(op)) throw new UnsupportedOperationException("The binary operator (" + op + ") is not supported at the moment.");

					Object left = b_op.getOperand1();
					Object right = b_op.getOperand2();
					int nElements = 0;
					if (left != null) nElements++;
					if (right != null) nElements++;
					if (nElements != 2) throw new IllegalStateException("Binary operator does not have 2 elements.");

					current_node.left = new Node(left, current_node);
					if (isBinaryOperation(current_node.left.data) || isUnaryOperation(current_node.left.data))
						stack.push(current_node.left);
					bt.add(current_node.left);

					current_node.right = new Node(right, current_node);
					if (isBinaryOperation(current_node.right.data) || isUnaryOperation(current_node.right.data)) stack.push(current_node.right);
					bt.add(current_node.right);
				} else if (isUnaryOperation(current_node.data)) {
					Uniop u_op = (Uniop) current_node.data;
					String op = convertUnioperator(u_op.getOperator());
					if (!isSupportedUnaryOp(op)) throw new UnsupportedOperationException("The unary operator (" + op + ") is not supported at the moment.");

					ExpressionValue content = u_op.getValue();
					if (content == null) throw new IllegalStateException("Unary operation content cannot be NULL");

					current_node.left = new Node(content, current_node);
					if (isBinaryOperation(current_node.left.data) || isUnaryOperation(current_node.left.data))
						stack.push(current_node.left);
					bt.add(current_node.left);
				} else if (isLogicalBinaryOperation(current_node.data)) {
					LogicBinOp l_b_op = (LogicBinOp) current_node.data;

					if (!isSupportedLogicalBinaryOp(l_b_op.getOp()))
						throw new UnsupportedOperationException("The binary logical operator (" + l_b_op.getOp() + ") is not supported at the moment.");

					List<JAXBElement<?>> content = l_b_op.getContent();
					if (content == null) throw new IllegalStateException("Binary operation content cannot be NULL");
					else if (content.size() != 2) throw new IllegalStateException("Binary operator does not have only 2 elements.");

					current_node.left = new Node(content.get(0).getValue(), current_node);
					if (isBinaryOperation(current_node.left.data) || isUnaryOperation(current_node.left.data)  ||
						isLogicalBinaryOperation(current_node.left.data)  || isLogicalUnaryOperation(current_node.left.data))
						stack.push(current_node.left);
					bt.add(current_node.left);

					current_node.right = new Node(content.get(1).getValue(), current_node);
					if (isBinaryOperation(current_node.right.data) || isUnaryOperation(current_node.right.data)  ||
							isLogicalBinaryOperation(current_node.right.data)  || isLogicalUnaryOperation(current_node.right.data))
						stack.push(current_node.right);
					bt.add(current_node.right);
				} else if (isLogicalUnaryOperation(current_node.data)) {
					LogicUniOp l_u_op = (LogicUniOp) current_node.data;

					if (!isSupportedLogicalUnaryOp(l_u_op.getOp()))
						throw new UnsupportedOperationException("The unary operator (" + l_u_op.getOp() + ") is not supported at the moment.");

					Object content = null;
					if (l_u_op.getBinop() != null) content = l_u_op.getBinop();
					else if (l_u_op.getConstant() != null) content = l_u_op.getConstant();
					else if (l_u_op.getFunctionCall() != null) content = l_u_op.getFunctionCall();
					else if (l_u_op.getLogicBinop() != null) content = l_u_op.getLogicBinop();
					else if (l_u_op.getLogicUniop() != null) content = l_u_op.getLogicUniop();
					else if (l_u_op.getScalar() != null) {
						Object v = l_u_op.getScalar().getValue();
						if (v == null) throw new NullPointerException("JAXB element is null");
						content = v;
					} else if (l_u_op.getSymbRef() != null) content = l_u_op.getSymbRef();
					else if (l_u_op.getUniop() != null) content = l_u_op.getUniop();

					if (content == null)
						throw new IllegalStateException("Unary operation content cannot be NULL");

					current_node.left = new Node(content, current_node);
					if (isBinaryOperation(current_node.left.data) || isUnaryOperation(current_node.left.data) ||
						isLogicalBinaryOperation(current_node.left.data) || isLogicalUnaryOperation(current_node.left.data))
						stack.push(current_node.left);
					bt.add(current_node.left);
				}
				
				step_count++;
				if (step_count >= MAX_STEP_COUNT) throw new IllegalStateException();
			}
			return true;
		}
		
		return false;
	}
	
	private void doDiscreteMultivariateMixtureModel(DiscreteMultivariateMixtureModel mm) {
		List<DiscreteMultivariateMixtureModelType.Component> components = mm.getComponent();
		if (components == null) return;
		
		for (DiscreteMultivariateMixtureModelType.Component component : components) {
			if (component == null) continue;
			
			JAXBElement<? extends AbstractDiscreteMultivariateDistributionType> tag = component.getAbstractDiscreteMultivariateDistribution();
			if (tag == null) continue;
			
			AbstractDiscreteMultivariateDistributionType dist = tag.getValue();
			if (dist != null) nested_trees.add(new NestedTreeRef(dist, createTree(dist)));
		}
	}
	
	private void doDiscreteUnivariateMixtureModel(DiscreteUnivariateMixtureModel mm) {
		if (mm == null) return;
		
		List<DiscreteUnivariateMixtureModelType.Component> cs = mm.getComponent();
		for (DiscreteUnivariateMixtureModelType.Component c : cs) {
			if (c == null) continue;

			JAXBElement<? extends AbstractDiscreteUnivariateDistributionType> tag = c.getAbstractDiscreteUnivariateDistribution();
			if (tag == null) continue;
			AbstractDiscreteUnivariateDistributionType dist = tag.getValue();
			if (dist != null) nested_trees.add(new NestedTreeRef(dist, createTree(dist)));
		}
	}
	
	private void doExponentialDistribution(ExponentialDistribution e) {
		if (e == null) return;
		
		PositiveRealValueType rate = e.getRate();
		if (rate != null) nested_trees.add(new NestedTreeRef(rate, createTree(rate)));
	}
	
	/**
	 * Crack open a PharmMl expression and place in a binary tree.
	 * @param bt Binary Tree
	 * @param rhs Assignment statement
	 * @return boolean
	 */
	private boolean doExpression(BinaryTree bt, Rhs rhs) {
		if (bt != null && rhs != null) {
			ArrayDeque<Node> stack = new ArrayDeque<Node>();
			Node root_node = new Node(rhs.getContent());
			root_node.root = true;
			bt.add(root_node);

			stack.push(root_node);
			int step_count = 0;
			while (stack.size() > 0 && step_count < MAX_STEP_COUNT) {
				Node current_node = stack.pop();
				if (isBinaryOperation(current_node.data)) {
					Binop b_op = (Binop) current_node.data;
					
					String op = convertBinoperator(b_op);
					if (!isSupportedBinaryOp(op)) throw new UnsupportedOperationException("The binary operator (" + op + ") is not supported at the moment.");
					
					Object left = b_op.getOperand1();
					Object right = b_op.getOperand2();
					int nElements = 0;
					if (left != null) nElements++;
					if (right != null) nElements++;
					if (nElements != 2) throw new IllegalStateException("Binary operator does not have 2 elements.");

					current_node.left = new Node(left, current_node);
					if (isBinaryOperation(current_node.left.data) || isUnaryOperation(current_node.left.data))
						stack.push(current_node.left);
					bt.add(current_node.left);

					current_node.right = new Node(right, current_node);
					if (isBinaryOperation(current_node.right.data) || isUnaryOperation(current_node.right.data))
						stack.push(current_node.right);
					bt.add(current_node.right);
				} else if (isUnaryOperation(current_node.data)) {
					Uniop u_op = (Uniop) current_node.data;
					String op = convertUnioperator(u_op.getOperator());
					if (!isSupportedUnaryOp(op)) throw new UnsupportedOperationException("The unary operator (" + op + ") is not supported at the moment.");
					
					ExpressionValue content = u_op.getValue();
					if (content == null) throw new IllegalStateException("Unary operation content cannot be NULL");

					current_node.left = new Node(content, current_node);
					if (isBinaryOperation(current_node.left.data) || isUnaryOperation(current_node.left.data))
						stack.push(current_node.left);
					bt.add(current_node.left);
				}  
				step_count++;
				if (step_count >= MAX_STEP_COUNT) throw new IllegalStateException();
			}
					
			return true;
		}
		
		return false;
	}
	
	private void doFDistribution(FDistribution f) {
		if (f == null) return;
		
		NaturalNumberValueType d = f.getDenominator();
		if (d != null) nested_trees.add(new NestedTreeRef(d, createTree(d)));
		
		NaturalNumberValueType n = f.getNumerator();
		if (n != null) nested_trees.add(new NestedTreeRef(n, createTree(n)));
	}
	
	private void doGammaDistribution(GammaDistributionType g) {
		if (g == null) return;
		
		PositiveRealValueType scale = g.getScale();
		if (scale != null) nested_trees.add(new NestedTreeRef(scale, createTree(scale)));
		
		PositiveRealValueType shape = g.getShape();
		if (scale != null) nested_trees.add(new NestedTreeRef(shape, createTree(shape)));
	}
	
	private void doGeometricDistribution(GeometricDistribution gd) {
		if (gd == null) return;
		
		ProbabilityValueType prob = gd.getProbability();
		if (prob != null) nested_trees.add(new NestedTreeRef(prob, createTree(prob)));
	}
	
	private void doHypergeometricDistribution(HypergeometricDistribution hd) {
		if (hd == null) return;
		
		NaturalNumberValueType nSuccesses = hd.getNumberOfSuccesses();
		if (nSuccesses != null) nested_trees.add(new NestedTreeRef(nSuccesses, createTree(nSuccesses)));
		
		NaturalNumberValueType nTrials = hd.getNumberOfTrials();
		if (nTrials != null) nested_trees.add(new NestedTreeRef(nTrials, createTree(nTrials)));
		
		NaturalNumberValueType populationSize = hd.getPopulationSize();
		if (populationSize != null) nested_trees.add(new NestedTreeRef(populationSize, createTree(populationSize)));
	}
	
	private void doInverseGammaDistribution(InverseGammaDistributionType g) {
		if (g == null) return;
		
		PositiveRealValueType scale = g.getScale();
		if (scale != null) nested_trees.add(new NestedTreeRef(scale, createTree(scale)));
		
		PositiveRealValueType shape = g.getShape();
		if (scale != null) nested_trees.add(new NestedTreeRef(shape, createTree(shape)));
	}
	
	private void doLaplaceDistribution(LaplaceDistribution c) {
		if (c == null) return;
		
		PositiveRealValueType scale = c.getScale();
		if (scale != null) nested_trees.add(new NestedTreeRef(scale, createTree(scale)));
		
		ContinuousValueType location = c.getLocation();
		if (location != null) nested_trees.add(new NestedTreeRef(location, createTree(location)));
	}
	
	private void doLogisticDistribution(LogisticDistribution c) {
		if (c == null) return;
		
		PositiveRealValueType scale = c.getScale();
		if (scale != null) nested_trees.add(new NestedTreeRef(scale, createTree(scale)));
		
		ContinuousValueType location = c.getLocation();
		if (location != null) nested_trees.add(new NestedTreeRef(location, createTree(location)));
	}
	
	private void doLogNormalDistribution(LogNormalDistribution ln) {
		PositiveRealValueType shape = ln.getShape();
		if (shape != null) nested_trees.add(new NestedTreeRef(shape, createTree(shape)));
		
		ContinuousValueType log_scale = ln.getLogScale();
		if (log_scale != null) nested_trees.add(new NestedTreeRef(log_scale, createTree(log_scale)));
	}
	
	private boolean doMatrix(BinaryTree bt, Matrix M) {
		if (M != null) {
			createRootNode(M, "Matrix", bt);
			return true;
		}
		
		return false;
	}
	
	private void doMultinomialDistribution(MultinomialDistribution md) {
		NaturalNumberValueType nTrials = md.getNumberOfTrials();
		if (nTrials != null) nested_trees.add(new NestedTreeRef(nTrials, createTree(nTrials)));
		
		ProbabilityValueType probabilities = md.getProbabilities();
		if (probabilities != null) nested_trees.add(new NestedTreeRef(probabilities, createTree(probabilities)));
	}
	
	private void doNegativeBinomialDistribution(NegativeBinomialDistribution nbd) {
		if (nbd == null) return;
		
		NaturalNumberValueType numberOfFailures = nbd.getNumberOfFailures();
		if (numberOfFailures != null) nested_trees.add(new NestedTreeRef(numberOfFailures, createTree(numberOfFailures)));
		
		ProbabilityValueType prob = nbd.getProbability();
		if (prob != null) nested_trees.add(new NestedTreeRef(prob, createTree(prob)));
	}
	
	private void doNormalDistribution(NormalDistribution n) {
		if (n == null) return;
		
		ContinuousValueType mean = n.getMean();
		if (mean != null) nested_trees.add(new NestedTreeRef(mean, createTree(mean)));
		
		PositiveRealValueType stddev = n.getStddev();
		if (stddev != null) nested_trees.add(new NestedTreeRef(stddev, createTree(stddev)));	
		
		PositiveRealValueType variance = n.getVariance();
		if (variance != null) nested_trees.add(new NestedTreeRef(variance, createTree(variance)));
	}
	
	private void doParetoDistribution(ParetoDistribution c) {
		if (c == null) return;
		
		PositiveRealValueType scale = c.getScale();
		if (scale != null) nested_trees.add(new NestedTreeRef(scale, createTree(scale)));
		
		PositiveRealValueType shape = c.getShape();
		if (shape != null) nested_trees.add(new NestedTreeRef(shape, createTree(shape)));
	}
	
	private void doPoissonDistribution(PoissonDistribution pd) {
		if (pd == null) return;
		
		PositiveRealValueType rate = pd.getRate();
		if (rate != null) nested_trees.add(new NestedTreeRef(rate, createTree(rate)));
	}
	
	private boolean doRhs(BinaryTree bt, Rhs rhs) {
		Object content = rhs.getContent();
		if (isScalarInterface(content)) return doScalar(bt, (Scalar) content);
		else if (isSequence(content)) return doSequence(bt, (Sequence) content);
		else if (isSymbolReference(content)) return doSymbolReference(bt, (SymbolRef) content); 
		else if (isVector(content)) return doVector(bt, (Vector) content);
		else if (isMatrix(content)) return doMatrix(bt, (Matrix) content);
		else if (isPiecewise(content)) {
			BinaryTree other_bt = createTree((Piecewise) content);
			
			bt.element = other_bt.element;
			bt.nodes = other_bt.nodes;
			
			return true;
		}
		else 
			return doExpression(bt, rhs);
	}
	
	private boolean doScalar(BinaryTree bt, VectorValue scalar) {
		if (scalar == null) throw new NullPointerException("Vector element is null");
		if (bt == null) throw new NullPointerException("Element Binary Tree is NULL");
		
		createRootNode(scalar, "Scalar", bt);
		
		return true;
	}
	
	private boolean doSequence(BinaryTree bt, Sequence v) {
		if (bt != null && v != null) {
			createRootNode(v, "Sequence", bt);
			
			// Parse a field at a time and added BT to the nested trees list.
			Rhs [] rhss = new Rhs[4];
			int i = 0;
			rhss[i++] = v.getBegin();
			rhss[i++] = v.getEnd();
			rhss[i++] = v.getRepetitions();
			rhss[i++] = v.getStepSize();
					
			for (Rhs rhs : rhss) {
				if (rhs != null) {
					BinaryTree nested_tree = createTree(rhs);
					NestedTreeRef ref = new NestedTreeRef(rhs, nested_tree);
					nested_trees.add(ref);
				}
			}
			
			return true;
		}
	
		return false;
	}
	
	private void doStructuredModel(StructuredModel m) {	
		StructuredModel.GeneralCovariate gcov = m.getGeneralCovariate();
		StructuredModel.LinearCovariate lcov = m.getLinearCovariate();
		List<ParameterRandomEffect> random_effects = m.getListOfRandomEffects();

		if (gcov != null) nested_trees.add(new NestedTreeRef(gcov, createTree(gcov.getAssign())));
		if (lcov != null) {
			PopulationValue pop_param = lcov.getPopulationValue();
			if (pop_param != null) nested_trees.add(new NestedTreeRef(pop_param, createTree(pop_param)));

			List<CovariateRelation> covariates = lcov.getListOfCovariate();
			if (covariates != null) {
				for (CovariateRelation covariate : covariates) {
					for (FixedEffectRelation fixed_effect : covariate.getListOfFixedEffect()) {
						if (fixed_effect == null) continue;

						if (fixed_effect.getCategory() != null) {
							CategoricalRelation cr = fixed_effect.getCategory();
							nested_trees.add(new NestedTreeRef(fixed_effect, createTree(new FixedEffectCategoryRef(covariate.getSymbRef(), fixed_effect.getSymbRef(), cr.getCatId())))); 
						}
						else if (fixed_effect.getSymbRef() != null) nested_trees.add(new NestedTreeRef(fixed_effect, createTree(fixed_effect.getSymbRef())));
					}
				}
			}	
		}

		if (random_effects != null) {
			for (ParameterRandomEffect random_effect : random_effects) {
				if (random_effect.getSymbRef() != null) {
					List<SymbolRef> refs = random_effect.getSymbRef();
					for (SymbolRef ref : refs) {
						if (ref == null) continue;
						nested_trees.add(new NestedTreeRef(random_effect, createTree(ref)));
					}	
				}
			}
		}
	}
	
	private void doStudentTDistribution(StudentTDistribution s) {
		if (s == null) return;
		
		if (s.getLocation() != null || s.getScale() != null) 
			throw new IllegalStateException("Only a single parameter t-student is supported by this converter.");
		
		PositiveNaturalNumberValueType df = s.getDegreesOfFreedom();
		if (df != null) nested_trees.add(new NestedTreeRef(df, createTree(df)));
	}
	
	private boolean doSymbolReference(BinaryTree bt, SymbolRef ref) {
		if (bt != null && ref != null) {
			createRootNode(ref, "Symbol Reference", bt);
			return true;
		}
		
		return false;
	}
	
	private void doUniformDistribution(UniformDistribution n) {
		if (n == null) return;
		
		ContinuousValueType min = n.getMinimum();
		if (min != null) nested_trees.add(new NestedTreeRef(min, createTree(min)));
		
		ContinuousValueType max = n.getMaximum();
		if (max != null) nested_trees.add(new NestedTreeRef(max, createTree(max)));	
	}
	
	private boolean doVector(BinaryTree bt, Vector v) {
		if (bt != null && v != null) {
			createRootNode(v, "Vector", bt);
			
			List<VectorValue> elements = v.getVectorElements().getListOfElements();
			for (VectorValue value : elements) {
				if (value == null) continue;
				
				if (isSequence(value)) {
					BinaryTree nested_tree  = createTree((Sequence) value);
					nested_trees.add(new NestedTreeRef(value, nested_tree));
				} else if (isScalarInterface(value)) {
					BinaryTree nested_tree = new BinaryTree();
					doScalar(nested_tree, value);
					nested_trees.add(new NestedTreeRef(value, nested_tree));
				} else if (isRhs(value)) { 
					BinaryTree nested_tree = new BinaryTree();
					doRhs(nested_tree, (Rhs) value);
					nested_trees.add(new NestedTreeRef(value, nested_tree));
				} else if (isSymbolReference(value)) {
					BinaryTree nested_tree = new BinaryTree();
					doSymbolReference(nested_tree, (SymbolRef) value);
					nested_trees.add(new NestedTreeRef(value, nested_tree));
				} else
					throw new UnsupportedOperationException("Unrecognised VectorValue type (object='" + value +  "')");
			}
			
			return true;
		}
		
		return false;
	}
	
	private void doWeibullDistribution(WeibullDistribution g) {
		if (g == null) return;
		
		PositiveRealValueType scale = g.getScale();
		if (scale != null) nested_trees.add(new NestedTreeRef(scale, createTree(scale)));
		
		PositiveRealValueType shape = g.getShape();
		if (scale != null) nested_trees.add(new NestedTreeRef(shape, createTree(shape)));
	}
	
	private void doWishartDistribution(WishartDistribution wd) {
		PositiveRealNumber dof = wd.getDegreesOfFreedom();
		if (dof.getPrVal() != null) nested_trees.add(new NestedTreeRef(dof, createTree(dof.getPrVal())));
		else if (dof.getVar() != null) nested_trees.add(new NestedTreeRef(dof, createTree(dof.getVar())));
		
		CovarianceMatrixType scale_matrix = wd.getScaleMatrix();
		if (scale_matrix != null) nested_trees.add(new NestedTreeRef(dof, createTree(scale_matrix)));
	}
	
	/**
	 * Clears the nested tree references with each newInstance() call unless otherwise instructed.
	 */
	protected void flushNestedTreeReferences() { if (flush_nested_tree_refs) nested_trees.clear(); }
	
	@Override
	public List<NestedTreeRef> getNestedTrees() { return nested_trees; }
	
	@Override
	public boolean isPermitDeclarationOnlyVariables() { return permitDeclarationOnlyVariables; }
	
	@Override
	public boolean isPermitParameterWithoutAssignment() { return permit_parameter_without_assignment; }
	
	@Override
	public BinaryTree newInstance(Object o) {
		BinaryTree bt = null;
		flushNestedTreeReferences();
		
		if (isJAXBElement(o)) bt = createTree((JAXBElement<?>) o);
		else if (isUnivariateDistribution(o)) bt = createTree((AbstractContinuousUnivariateDistributionType) o);
		else if (isBigInteger(o)) bt = createTree((BigInteger) o);
		else if (isCondition(o)) bt = createTree((Condition) o);
		else if (isConstant(o)) bt = createTree((Constant) o);
		else if (isContinuousCovariate(o)) bt = createTree((ContinuousCovariate) o);
		else if (isContinuousValue(o)) bt = createTree((ContinuousValueType) o);
		else if (isCovariate(o)) bt = createTree((CovariateDefinition) o);
		else if (isCovariateTransform(o)) bt = createTree((CovariateTransformation) o);
		else if (isDerivative(o)) bt = createTree((DerivativeVariable) o);
		else if (isDouble(o)) bt = createTree((Double) o);
		else if (isDouble_(o)) bt = createTree((eu.ddmore.libpharmml.dom.uncertml.Double) o);
		else if (isFunction(o)) bt = createTree((FunctionDefinition) o);
		else if (isIndividualParameter(o)) bt = createTree((IndividualParameter) o);
		else if (isInitialCondition(o)) bt = createTree((InitialCondition) o);
		else if (isInitialEstimate(o)) bt = createTree((InitialEstimate) o);
		else if (isNaturalNumberValue(o)) bt = createTree((NaturalNumberValueType) o); 
		else if (isObservationError(o)) bt = createTree((ObservationError) o); 
		else if (isParameterEstimate(o)) bt = createTree((ParameterEstimate) o); 
		else if (isRandomVariable(o)) bt = createTree((ParameterRandomVariable) o);
		else if (isPositiveNaturalNumber(o)) bt = createTree((PositiveNaturalNumber) o);
		else if (isRhs(o)) bt = createTree((Rhs) o);
		else if (isStandardAssignable(o)) bt = createTree((StandardAssignable) o);
		else if (isSequence(o)) bt = createTree((Sequence) o);
		else if (isPopulationParameter(o)) bt = createTree((PopulationParameter) o);
		else if (isSymbolReference(o)) bt = createTree((SymbolRef) o);
		else if (isLocalVariable(o)) bt = createTree((VariableDefinition) o);
		else if (isVariableReference(o)) bt = createTree((VarRefType) o);
		else if (isVector(o)) bt = createTree((Vector) o);
		else if (isReal(o)) bt = createTree((RealValue) o);
		else if (isScalar(o)) bt = createTree((Scalar) o);
		else if (isColumnReference(o)) bt = createTree((ColumnReference) o);
		else if (isPiece(o)) bt = createTree((Piece) o);
		else if (isFunctionCall(o)) bt = createTree((FunctionCallType) o);
		else if (isDelayVariable(o)) bt = createTree((DelayVariable) o);
		else if (isFalse(o) || isTrue(o)) bt = createTree((BooleanValue) o);
		else if (isId(o)) bt = createTree((IdValue) o);
		else if (isInt(o)) bt = createTree((IntValue) o);
		else if (isString(o)) bt = createTree((StringValue) o);
		else if (isLimit(o)) bt = createTree((LowUpLimit) o);
		else if (isMatrixCell(o)) bt = createTree((MatrixCell) o);
		else if (isOperationProperty(o)) bt = createTree((OperationProperty) o);
		else if (isAbsorptionOralMacro(o)) bt = createTree((AbsorptionOralMacro) o);
		else if (isCompartmentMacro(o)) bt = createTree((CompartmentMacro) o);
		else if (isDepotMacro(o)) bt = createTree((DepotMacro) o);
		else if (isEliminationMacro(o)) bt = createTree((EliminationMacro) o);
		else if (isEffectMacro(o)) bt = createTree((EffectMacro) o);
		else if (isIVMacro(o)) bt = createTree((IVMacro) o);
		else if (isPeripheralMacro(o)) bt = createTree((PeripheralMacro) o);
		else if (isTransferMacro(o)) bt = createTree((TransferMacro) o);
		else if (isCommonDiscreteVariable(o)) bt = createTree((CommonDiscreteVariable) o);
		else if (isDiscreteDataParameter(o)) bt = createTree((DiscreteDataParameter) o);
		else if (isDiscreteUnivariateDistribution(o)) bt = createTree((AbstractDiscreteUnivariateDistributionType) o);
		else if (isCountPMF(o)) bt = createTree((CountPMF) o);
		else if (isLogicalBinaryOperation(o)) bt = createTree((LogicBinOp) o);
		else if (isLogicalUnaryOperation(o)) bt = createTree((LogicUniOp) o);
		else if (isCommonDiscreteState(o)) bt = createTree((CommonDiscreteState) o);
		else if (isProbability(o)) bt = createTree((Probability) o);
		else if (isDependance(o)) bt = createTree((Dependance) o);
		else if (isCategoricalPMF(o)) bt = createTree((CategoricalPMF) o);
		else if (isCategoricalUnivariateDistribution(o)) bt = createTree((AbstractCategoricalUnivariateDistributionType) o);
		else if (isCategoricalMultivariateDistribution(o)) bt = createTree((AbstractCategoricalMultivariateDistributionType) o);
		else if (isDiscreteMultivariateDistribution(o)) bt = createTree((AbstractDiscreteMultivariateDistributionType) o);
		else if (isCovarianceMatrix(o)) bt = createTree((CovarianceMatrixType) o);
		else if (isTransitionRate(o)) bt = createTree((TransitionRate) o);
		else if (isProbabilityAssignment(o)) bt = createTree((ProbabilityAssignment) o);
		else if (isTTEFunction(o)) bt = createTree((TTEFunction) o);
		else if (isMacroValue(o)) bt = createTree((MacroValue) o);
		else if (isAbsorptionMacro(o)) bt = createTree((AbsorptionMacro) o);
		else if (isDistributionParameter(o)) bt = createTree((DistributionParameter) o);
		else if (isBinaryOperation(o)) bt = createTree((Binop) o);
		else if (isUnaryOperation(o)) bt = createTree((Uniop) o);
		else if (isMatrix(o)) bt = createTree((Matrix) o);
		else if (o instanceof Boolean) bt = createTree((Boolean) o);
		else if (isCategory(o)) bt = createTree((Category) o);
		else if (isPopulationValue(o)) bt = createTree((PopulationValue) o);
		else if (o instanceof Boolean) bt = createTree((Boolean) o);
		else if (o instanceof String) bt = createTree((String) o);
		else if (o instanceof Integer) bt = createTree((Integer) o);
		else if (isGeneralCovariate(o)) bt = createTree((GeneralCovariate) o);
		else if (isFunctionArgument(o)) bt = createTree((FunctionArgumentType) o);
		else if (isPiecewise(o)) bt = createTree((Piecewise) o);
		else if (isParameter(o)) bt = createTree((Parameter) o);
		else if (isDosingTimesPoints(o)) bt = createTree((DosingTimesPoints) o);
		else if (isMapType(o)) bt = createTree((MapType) o);
		else if (isDistribution(o)) bt = createTree((Distribution) o);
		else if (o instanceof FixedParameter) bt = createTree((FixedParameter) o);
		else if (isDosingVariable(o)) bt = createTree((DosingVariable) o);
		else if (isVectorSelector(o)) bt = createTree((VectorSelector) o);
		else if (isMatrixVectorIndex(o)) bt = createTree((MatrixVectorIndex) o);
		else if (isVectorSegmentSelector(o)) bt = createTree((VectorSegmentSelector) o);
		else if (isDesignSpace(o)) bt = createTree((SingleDesignSpace) o);
		else if (isDesignSpaceCovariate(o)) bt = createTree((DSCovariateDefinitionType) o); 
		else if (isDesignSpaceCategoricalCovariate(o)) bt = createTree((DSCategoricalCovariateType) o);
		else if (isDesignSpaceCategory(o)) bt = createTree((DSCategoryType) o);
		else if (isStage(o)) bt = createTree((StageDefinition) o);
		else {
			String msg = "Tree maker not supported (src='" + o + "'";
			if (o != null) msg = "Tree maker not supported (src='" + getClassName(o) + "')";
			throw new UnsupportedOperationException(msg);
		}
		
		return bt;
	}
	
	@Override
	public void setAccessor(Accessor a_) { a = a_; }

	@Override
	public void setDefaultParameterValue(double value) { default_parameter_value = value; }
	
	@Override
	public void setFlushNestedTreeReferences(boolean decision) { flush_nested_tree_refs = decision;}
	
	@Override
	public void setPermitDeclarationOnlyVariables(boolean decision) { permitDeclarationOnlyVariables = decision; }
	
	@Override
	public void setPermitParameterWithoutAssignment(boolean decision) { permit_parameter_without_assignment = decision; }
}
