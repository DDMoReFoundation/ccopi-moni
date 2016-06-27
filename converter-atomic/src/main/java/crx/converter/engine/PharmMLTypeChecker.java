/*******************************************************************************
 * Copyright (C) 2015 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.engine;

import javax.xml.bind.JAXBElement;

import eu.ddmore.libpharmml.dom.Identifiable;
import eu.ddmore.libpharmml.dom.IndependentVariable;
import eu.ddmore.libpharmml.dom.commontypes.BooleanValue;
import eu.ddmore.libpharmml.dom.commontypes.CommonVariableDefinition;
import eu.ddmore.libpharmml.dom.commontypes.Delay;
import eu.ddmore.libpharmml.dom.commontypes.DelayVariable;
import eu.ddmore.libpharmml.dom.commontypes.DerivativeVariable;
import eu.ddmore.libpharmml.dom.commontypes.FalseBoolean;
import eu.ddmore.libpharmml.dom.commontypes.FunctionDefinition;
import eu.ddmore.libpharmml.dom.commontypes.FunctionParameter;
import eu.ddmore.libpharmml.dom.commontypes.IdValue;
import eu.ddmore.libpharmml.dom.commontypes.InitialCondition;
import eu.ddmore.libpharmml.dom.commontypes.IntValue;
import eu.ddmore.libpharmml.dom.commontypes.Interpolation;
import eu.ddmore.libpharmml.dom.commontypes.Interval;
import eu.ddmore.libpharmml.dom.commontypes.LevelReference;
import eu.ddmore.libpharmml.dom.commontypes.LinkFunction;
import eu.ddmore.libpharmml.dom.commontypes.LowUpLimit;
import eu.ddmore.libpharmml.dom.commontypes.Matrix;
import eu.ddmore.libpharmml.dom.commontypes.MatrixCell;
import eu.ddmore.libpharmml.dom.commontypes.MatrixCellSelector;
import eu.ddmore.libpharmml.dom.commontypes.MatrixSelector;
import eu.ddmore.libpharmml.dom.commontypes.MatrixVectorIndex;
import eu.ddmore.libpharmml.dom.commontypes.PharmMLElement;
import eu.ddmore.libpharmml.dom.commontypes.PharmMLRootType;
import eu.ddmore.libpharmml.dom.commontypes.Product;
import eu.ddmore.libpharmml.dom.commontypes.RealValue;
import eu.ddmore.libpharmml.dom.commontypes.Rhs;
import eu.ddmore.libpharmml.dom.commontypes.Scalar;
import eu.ddmore.libpharmml.dom.commontypes.Sequence;
import eu.ddmore.libpharmml.dom.commontypes.StandardAssignable;
import eu.ddmore.libpharmml.dom.commontypes.StringValue;
import eu.ddmore.libpharmml.dom.commontypes.Sum;
import eu.ddmore.libpharmml.dom.commontypes.Symbol;
import eu.ddmore.libpharmml.dom.commontypes.SymbolRef;
import eu.ddmore.libpharmml.dom.commontypes.TrueBoolean;
import eu.ddmore.libpharmml.dom.commontypes.VariableDefinition;
import eu.ddmore.libpharmml.dom.commontypes.Vector;
import eu.ddmore.libpharmml.dom.commontypes.VectorCell;
import eu.ddmore.libpharmml.dom.commontypes.VectorSegment;
import eu.ddmore.libpharmml.dom.commontypes.VectorSegmentSelector;
import eu.ddmore.libpharmml.dom.commontypes.VectorSelector;
import eu.ddmore.libpharmml.dom.dataset.ColumnDefinition;
import eu.ddmore.libpharmml.dom.dataset.ColumnMapping;
import eu.ddmore.libpharmml.dom.dataset.ColumnReference;
import eu.ddmore.libpharmml.dom.dataset.DataSetTable;
import eu.ddmore.libpharmml.dom.dataset.MapType;
import eu.ddmore.libpharmml.dom.dataset.TargetMapping;
import eu.ddmore.libpharmml.dom.maths.Binop;
import eu.ddmore.libpharmml.dom.maths.Condition;
import eu.ddmore.libpharmml.dom.maths.Constant;
import eu.ddmore.libpharmml.dom.maths.FunctionArgumentType;
import eu.ddmore.libpharmml.dom.maths.FunctionCallType;
import eu.ddmore.libpharmml.dom.maths.LogicBinOp;
import eu.ddmore.libpharmml.dom.maths.LogicUniOp;
import eu.ddmore.libpharmml.dom.maths.MatrixUniOp;
import eu.ddmore.libpharmml.dom.maths.Piece;
import eu.ddmore.libpharmml.dom.maths.Piecewise;
import eu.ddmore.libpharmml.dom.maths.Uniop;
import eu.ddmore.libpharmml.dom.modeldefn.CategoricalPMF;
import eu.ddmore.libpharmml.dom.modeldefn.CategoricalRelation;
import eu.ddmore.libpharmml.dom.modeldefn.Category;
import eu.ddmore.libpharmml.dom.modeldefn.Censoring;
import eu.ddmore.libpharmml.dom.modeldefn.CommonDiscreteState;
import eu.ddmore.libpharmml.dom.modeldefn.CommonDiscreteVariable;
import eu.ddmore.libpharmml.dom.modeldefn.CommonParameter;
import eu.ddmore.libpharmml.dom.modeldefn.ContinuousCovariate;
import eu.ddmore.libpharmml.dom.modeldefn.Correlation;
import eu.ddmore.libpharmml.dom.modeldefn.CountData;
import eu.ddmore.libpharmml.dom.modeldefn.CountPMF;
import eu.ddmore.libpharmml.dom.modeldefn.CovariateDefinition;
import eu.ddmore.libpharmml.dom.modeldefn.CovariateRelation;
import eu.ddmore.libpharmml.dom.modeldefn.CovariateTransformation;
import eu.ddmore.libpharmml.dom.modeldefn.Dependance;
import eu.ddmore.libpharmml.dom.modeldefn.DiscreteDataParameter;
import eu.ddmore.libpharmml.dom.modeldefn.Distribution;
import eu.ddmore.libpharmml.dom.modeldefn.GeneralObsError;
import eu.ddmore.libpharmml.dom.modeldefn.IndividualParameter;
import eu.ddmore.libpharmml.dom.modeldefn.ObservationError;
import eu.ddmore.libpharmml.dom.modeldefn.ObservationModel;
import eu.ddmore.libpharmml.dom.modeldefn.Parameter;
import eu.ddmore.libpharmml.dom.modeldefn.ParameterModel;
import eu.ddmore.libpharmml.dom.modeldefn.ParameterRandomEffect;
import eu.ddmore.libpharmml.dom.modeldefn.ParameterRandomVariable;
import eu.ddmore.libpharmml.dom.modeldefn.PopulationParameter;
import eu.ddmore.libpharmml.dom.modeldefn.Probability;
import eu.ddmore.libpharmml.dom.modeldefn.ProbabilityAssignment;
import eu.ddmore.libpharmml.dom.modeldefn.StructuralModel;
import eu.ddmore.libpharmml.dom.modeldefn.StructuredModel;
import eu.ddmore.libpharmml.dom.modeldefn.StructuredModel.GeneralCovariate;
import eu.ddmore.libpharmml.dom.modeldefn.StructuredModel.LinearCovariate.PopulationValue;
import eu.ddmore.libpharmml.dom.modeldefn.StructuredObsError;
import eu.ddmore.libpharmml.dom.modeldefn.StructuredObsError.Output;
import eu.ddmore.libpharmml.dom.modeldefn.StructuredObsError.ResidualError;
import eu.ddmore.libpharmml.dom.modeldefn.TTEFunction;
import eu.ddmore.libpharmml.dom.modeldefn.TimeToEventData;
import eu.ddmore.libpharmml.dom.modeldefn.TransformedCovariate;
import eu.ddmore.libpharmml.dom.modeldefn.TransitionRate;
import eu.ddmore.libpharmml.dom.modeldefn.VariabilityDefnBlock;
import eu.ddmore.libpharmml.dom.modeldefn.VariabilityLevelDefinition;
import eu.ddmore.libpharmml.dom.modeldefn.pkmacro.AbsorptionMacro;
import eu.ddmore.libpharmml.dom.modeldefn.pkmacro.AbsorptionOralMacro;
import eu.ddmore.libpharmml.dom.modeldefn.pkmacro.CompartmentMacro;
import eu.ddmore.libpharmml.dom.modeldefn.pkmacro.DepotMacro;
import eu.ddmore.libpharmml.dom.modeldefn.pkmacro.EffectMacro;
import eu.ddmore.libpharmml.dom.modeldefn.pkmacro.EliminationMacro;
import eu.ddmore.libpharmml.dom.modeldefn.pkmacro.IVMacro;
import eu.ddmore.libpharmml.dom.modeldefn.pkmacro.MacroValue;
import eu.ddmore.libpharmml.dom.modeldefn.pkmacro.OralMacro;
import eu.ddmore.libpharmml.dom.modeldefn.pkmacro.PKMacroList;
import eu.ddmore.libpharmml.dom.modeldefn.pkmacro.PeripheralMacro;
import eu.ddmore.libpharmml.dom.modeldefn.pkmacro.TransferMacro;
import eu.ddmore.libpharmml.dom.modellingsteps.CommonModellingStep;
import eu.ddmore.libpharmml.dom.modellingsteps.Estimation;
import eu.ddmore.libpharmml.dom.modellingsteps.InitialEstimate;
import eu.ddmore.libpharmml.dom.modellingsteps.OperationProperty;
import eu.ddmore.libpharmml.dom.modellingsteps.ParameterEstimate;
import eu.ddmore.libpharmml.dom.modellingsteps.Simulation;
import eu.ddmore.libpharmml.dom.probonto.DistributionParameter;
import eu.ddmore.libpharmml.dom.probonto.ProbOnto;
import eu.ddmore.libpharmml.dom.tags.PharmMLObject;
import eu.ddmore.libpharmml.dom.trialdesign.Administration;
import eu.ddmore.libpharmml.dom.trialdesign.ArmDefinition;
import eu.ddmore.libpharmml.dom.trialdesign.Bolus;
import eu.ddmore.libpharmml.dom.trialdesign.DosingTimesPoints;
import eu.ddmore.libpharmml.dom.trialdesign.DosingVariable;
import eu.ddmore.libpharmml.dom.trialdesign.Infusion;
import eu.ddmore.libpharmml.dom.trialdesign.MultipleDVMapping;
import eu.ddmore.libpharmml.dom.trialdesign.Observation;
import eu.ddmore.libpharmml.dom.trialdesign.Occasion;
import eu.ddmore.libpharmml.dom.trialdesign.OccasionSequence;
import eu.ddmore.libpharmml.dom.trialdesign.SteadyStateParameter;
import eu.ddmore.libpharmml.dom.uncertml.AbstractCategoricalMultivariateDistributionType;
import eu.ddmore.libpharmml.dom.uncertml.AbstractCategoricalUnivariateDistributionType;
import eu.ddmore.libpharmml.dom.uncertml.AbstractContinuousUnivariateDistributionType;
import eu.ddmore.libpharmml.dom.uncertml.AbstractDiscreteMultivariateDistributionType;
import eu.ddmore.libpharmml.dom.uncertml.AbstractDiscreteUnivariateDistributionType;
import eu.ddmore.libpharmml.dom.uncertml.BernoulliDistributionType;
import eu.ddmore.libpharmml.dom.uncertml.BetaDistribution;
import eu.ddmore.libpharmml.dom.uncertml.BinomialDistribution;
import eu.ddmore.libpharmml.dom.uncertml.CategoricalDistributionType;
import eu.ddmore.libpharmml.dom.uncertml.CategoricalMultivariateMixtureModelType;
import eu.ddmore.libpharmml.dom.uncertml.CategoricalProbabilityValueType;
import eu.ddmore.libpharmml.dom.uncertml.CategoricalUnivariateMixtureModelType;
import eu.ddmore.libpharmml.dom.uncertml.CauchyDistribution;
import eu.ddmore.libpharmml.dom.uncertml.ChiSquareDistribution;
import eu.ddmore.libpharmml.dom.uncertml.ContinuousUnivariateMixtureModel;
import eu.ddmore.libpharmml.dom.uncertml.ContinuousValueType;
import eu.ddmore.libpharmml.dom.uncertml.CovarianceMatrixType;
import eu.ddmore.libpharmml.dom.uncertml.DiracDeltaDistribution;
import eu.ddmore.libpharmml.dom.uncertml.DiscreteMultivariateMixtureModelType;
import eu.ddmore.libpharmml.dom.uncertml.DiscreteUnivariateMixtureModel;
import eu.ddmore.libpharmml.dom.uncertml.ExponentialDistribution;
import eu.ddmore.libpharmml.dom.uncertml.FDistribution;
import eu.ddmore.libpharmml.dom.uncertml.GammaDistribution;
import eu.ddmore.libpharmml.dom.uncertml.GeometricDistribution;
import eu.ddmore.libpharmml.dom.uncertml.HypergeometricDistribution;
import eu.ddmore.libpharmml.dom.uncertml.InverseGammaDistribution;
import eu.ddmore.libpharmml.dom.uncertml.LaplaceDistribution;
import eu.ddmore.libpharmml.dom.uncertml.LogNormalDistribution;
import eu.ddmore.libpharmml.dom.uncertml.LogisticDistribution;
import eu.ddmore.libpharmml.dom.uncertml.MultinomialDistributionType;
import eu.ddmore.libpharmml.dom.uncertml.NaturalNumberValueType;
import eu.ddmore.libpharmml.dom.uncertml.NegativeBinomialDistribution;
import eu.ddmore.libpharmml.dom.uncertml.NormalDistribution;
import eu.ddmore.libpharmml.dom.uncertml.ParetoDistribution;
import eu.ddmore.libpharmml.dom.uncertml.PoissonDistribution;
import eu.ddmore.libpharmml.dom.uncertml.PositiveNaturalNumber;
import eu.ddmore.libpharmml.dom.uncertml.ProbabilityValueType;
import eu.ddmore.libpharmml.dom.uncertml.StudentTDistribution;
import eu.ddmore.libpharmml.dom.uncertml.UniformDistribution;
import eu.ddmore.libpharmml.dom.uncertml.VarRefType;
import eu.ddmore.libpharmml.dom.uncertml.WeibullDistribution;
import eu.ddmore.libpharmml.dom.uncertml.WishartDistributionType;

/**
 * Type checker for core PharmML data types.
 */
public abstract class PharmMLTypeChecker {
	/**
	 * Test if object is an Absorption Macro.
	 * @param o Object
	 * @return boolean
	 * @see eu.ddmore.libpharmml.dom.modeldefn.pkmacro.AbsorptionMacro 
	 */
	public static boolean isAbsorptionMacro(Object o) { return o instanceof AbsorptionMacro; }
	
	/**
	 * Test if object is an Absorption Oral Macro.
	 * @param o Object
	 * @return boolean
	 * @see eu.ddmore.libpharmml.dom.modeldefn.pkmacro.AbsorptionOralMacro 
	 */
	public static boolean isAbsorptionOralMacro(Object o) { return o instanceof AbsorptionOralMacro; }
	
	/**
	 * Test if object is an Categorical Multivariate Distribution
	 * @param o
	 * @return boolean
	 * @see eu.ddmore.libpharmml.dom.uncertml.AbstractCategoricalMultivariateDistributionType
	 */
	public static boolean isAbstractCategoricalMultivariateDistribution(Object o) {
		return o instanceof AbstractCategoricalMultivariateDistributionType;
	}
	
	/**
	 * Test if object is an Administration dosing event
	 * @param o Object
	 * @return boolean
	 * @see Administration 
	 */
	public static boolean isAdministration(Object o) { return o instanceof Administration; }
	
	/**
	 * Test if object is an Absorption Macro.
	 * @param o Object
	 * @return boolean
	 * @see ArmDefinition
	 */
	public static boolean isArm(Object o) { return o instanceof ArmDefinition; }
	
	/**
	 * Test if object is a Bernoulli Distribution.
	 * @param o Object
	 * @return boolean
	 * @see BernoulliDistributionType
	 */
	public static boolean isBernoulliDistribution(Object o) { return o instanceof BernoulliDistributionType; }
	
	/**
	 * Test if object is a beta distribution
	 * @param o Object
	 * @return boolean
	 * @see BetaDistribution
	 */
	public static boolean isBetaDistribution(Object o) { return o instanceof BetaDistribution; }
	
	/**
	 * Test if object is a binary operation.
	 * @param o Object
	 * @return boolean
	 * @see Binop
	 */
	public static boolean isBinaryOperation(Object o) { return o instanceof Binop; }
	
	/**
	 * Test if object is an Binomial Distribution.
	 * @param o Object
	 * @return boolean
	 * @see eu.ddmore.libpharmml.dom.uncertml.BinomialDistribution 
	 */
	public static boolean isBinomialDistribution(Object o) { return o instanceof BinomialDistribution; }
	
	/**
	 * Test if object a bolus dose scheme.
	 * @param o Object
	 * @return boolean
	 * @see Bolus
	 */
	public static boolean isBolus(Object o) { return o instanceof Bolus; }
	
	/**
	 * Test if object a PharmML boolean value.
	 * @param o Object
	 * @return boolean
	 * @see BooleanValue
	 */
	public static boolean isBooleanValue(Object o) { return o instanceof BooleanValue; }
	
	/**
	 * Test if object is an Categorical Distribution.
	 * @param o
	 * @return boolean
	 * @see eu.ddmore.libpharmml.dom.uncertml.CategoricalDistributionType
	 */
	public static boolean isCategoricalDistribution(Object o) { return o instanceof CategoricalDistributionType; } 
	
	/**
	 * Test if object is a  Categorical Multivariate Distribution.
	 * @param o Object
	 * @return boolean
	 * @see AbstractCategoricalMultivariateDistributionType
	 */
	public static boolean isCategoricalMultivariateDistribution(Object o) { return o instanceof AbstractCategoricalMultivariateDistributionType; } 
	
	/**
	 * Test if object is a Categorical Multivariate Mixture Model.
	 * @param o
	 * @return boolean
	 * @see eu.ddmore.libpharmml.dom.uncertml.CategoricalMultivariateMixtureModelType
	 */
	public static boolean isCategoricalMultivariateMixtureModel(Object o) { return o instanceof CategoricalMultivariateMixtureModelType; }
	
	/**
	 * Test if object is a categorical PMF.
	 * @param o Object
	 * @return boolean
	 * @see CategoricalPMF
	 */
	public static boolean isCategoricalPMF(Object o) { return o instanceof CategoricalPMF; }
	
	/**
	 * Test if object is an Categorical Probability Value.
	 * @param o
	 * @return boolean
	 * @see eu.ddmore.libpharmml.dom.uncertml.CategoricalProbabilityValueType
	 */
	public static boolean isCategoricalProbabilityValue(Object o) { return o instanceof CategoricalProbabilityValueType; }
	
	/**
	 * Test if object is a Categorical Relation.
	 * @param o Object
	 * @return boolean
	 * @see CategoricalRelation 
	 */
	public static boolean isCategoricalRelation(Object o) { return o instanceof CategoricalRelation; }
	
	/**
	 * Test if object is a Categorical Univariate Distribution.
	 * @param o Object
	 * @return boolean
	 * @see AbstractCategoricalUnivariateDistributionType
	 */
	public static boolean isCategoricalUnivariateDistribution(Object o) { return o instanceof AbstractCategoricalUnivariateDistributionType; }
	
	/**
	 * Test if object is a Categorical Univariate Mixture Model.
	 * @param o Object
	 * @return boolean
	 * @see CategoricalUnivariateMixtureModelType
	 */
	public static boolean isCategoricalUnivariateMixtureModel(Object o) { return o instanceof CategoricalUnivariateMixtureModelType; }
	
	/**
	 * Test if object is an Categorical Distribution.
	 * @param o
	 * @return boolean
	 * @see Category
	 */
	public static boolean isCategory(Object o) { return o instanceof Category; }
 
	/**
	 * Test if object is an Categorical Relation.
	 * @param o
	 * @return boolean
	 * @see CategoricalRelation
	 */
	public static boolean isCategoryRelation(Object o) { return o instanceof CategoricalRelation; }
	
	/**
	 * Test if object is a Cauchy Distribution.
	 * @param o Object
	 * @return boolean
	 * @see CauchyDistribution
	 */
	public static boolean isCauchyDistribution(Object o) { return o instanceof CauchyDistribution; }
	
	/**
	 * Test if object is a Censoring type.
	 * @param o Object
	 * @return boolean
	 * @see eu.ddmore.libpharmml.dom.modeldefn.Censoring
	 */
	public static boolean isCensoring(Object o) { return o instanceof Censoring; }
	
	/**
	 * Test if object is a ChiSquare Distribution.
	 * @param o Object
	 * @return boolean
	 * @see ChiSquareDistribution
	 */
	public static boolean isChiSquareDistribution(Object o) { return o instanceof ChiSquareDistribution; }
	
	/**
	 * Test if object is a Column Definition.
	 * @param o Object
	 * @return boolean
	 * @see ColumnDefinition
	 */
	public static boolean isColumnDefinition(Object o) { return o instanceof ColumnDefinition; }
	
	/**
	 * Test if object is a Column Mapping.
	 * @param o Object
	 * @return boolean
	 * @see ColumnMapping
	 */
	public static boolean isColumnMapping(Object o) { return o instanceof ColumnMapping; }
	
	/**
	 * Test if object is a Column Reference.
	 * @param o Object
	 * @return boolean
	 * @see ColumnReference
	 */
	public static boolean isColumnReference(Object o) { return o instanceof ColumnReference; }
	
	/**
	 * Test if object is a logical binary operation.
	 * @param o Object
	 * @return boolean
	 * @see CommonDiscreteState
	 */
	public static boolean isCommonDiscreteState(Object o) { return o instanceof CommonDiscreteState; }
	
	/**
	 * Test if object is a Common Discrete Variable
	 * @param o Object
	 * @return boolean
	 * @see CommonDiscreteVariable
	 */
	public static boolean isCommonDiscreteVariable(Object o) { return o instanceof CommonDiscreteVariable; }
	
	/**
	 * Test if object is a Common Parameter.
	 * @param o Object
	 * @return boolean
	 * @see CommonParameter
	 */
	public static boolean isCommonParameter(Object o) { return o instanceof CommonParameter; }
	
	/**
	 * Test if object is a Common Variable.
	 * @param o Object
	 * @return boolean
	 * @see CommonVariableDefinition
	 */
	public static boolean isCommonVariable(Object o) { return o instanceof CommonVariableDefinition; }

	/**
	 * Test if object is an Compartment Macro.
	 * @param o
	 * @return boolean
	 * @see eu.ddmore.libpharmml.dom.modeldefn.pkmacro.CompartmentMacro 
	 */
	public static boolean isCompartmentMacro(Object o) { return o instanceof CompartmentMacro; }
	
	/**
	 * Test if object is a Condition.
	 * @param o
	 * @return boolean
	 * @see Condition
	 */
	public static boolean isCondition(Object o) { return o instanceof Condition; }
	
	/**
	 * Test if object is a Constant.
	 * @param o Object
	 * @return boolean
	 * @see Constant
	 */
	public static boolean isConstant(Object o) { return o instanceof Constant; }
	
	/**
	 * Test if object is a Continuous Covariate.
	 * @param o Object
	 * @return boolean
	 * @see ContinuousCovariate
	 */
	public static boolean isContinuousCovariate(Object o) { return o instanceof ContinuousCovariate; }
	
	/**
	 * Test if object is a Continuous Univariate Mixture Model.
	 * @param o Object 
	 * @return boolean
	 * @see ContinuousUnivariateMixtureModel
	 */
	public static boolean isContinuousUnivariateMixtureModel(Object o) { return o instanceof ContinuousUnivariateMixtureModel; }
 
	/**
	 * Test if object is a Continuous Value Type.
	 * @param o Object
	 * @return boolean
	 * @see ContinuousValueType
	 */
	public static boolean isContinuousValue(Object o) { return o instanceof ContinuousValueType; }
	
	/**
	 * Test if object is a correlation.
	 * @param o Object
	 * @return boolean
	 * @see Correlation
	 */
	public static boolean isCorrelation(Object o) { return o instanceof Correlation; }
	
	/**
	 * Test if object is a Count Data variable
	 * @param o Object
	 * @return boolean
	 * @see CountData
	 */
	public static boolean isCountData(Object o) { return o instanceof CountData; }
	
	/**
	 * Test if object is a Common Variable.
	 * @param o Object
	 * @return boolean
	 * @see CountPMF
	 */
	public static boolean isCountPMF(Object o) { return o instanceof CountPMF; }
	
	/**
	 * Test if object is an UncertML Covariance Matrix
	 * @param o Object
	 * @return boolean
	 * @see eu.ddmore.libpharmml.dom.uncertml.CovarianceMatrixType
	 */
	public static boolean isCovarianceMatrix(Object o) { return o instanceof CovarianceMatrixType; }
	
	/**
	 * Test if object is a Covariate Definition.
	 * @param o Object
	 * @return boolean
	 * @see CovariateDefinition
	 */
	public static boolean isCovariate(Object o) { return o instanceof CovariateDefinition; }
   
	/**
	 * Test if object is a Covariate Relation.<br/>
	 * That's a NMLE fixed effect.
	 * @param o Object
	 * @return boolean
	 * @see CovariateDefinition
	 */
	public static boolean isCovariateRelation(Object o) { return o instanceof CovariateRelation; }
	
	/**
	 * Test if object is a Covariate Transformation.
	 * @param o Object 
	 * @return boolean
	 * @see CovariateTransformation
	 */
	public static boolean isCovariateTransform(Object o) { return o instanceof CovariateTransformation; }
	
	/**
	 * Test if object is a DataSetTableType.
	 * @param o Object 
	 * @return boolean
	 * @see DataSetTable
	 */
	public static boolean isDatasetTable(Object o) { return o instanceof DataSetTable; }
	
	/**
	 * Test if object is a delay.
	 * @param o Object
	 * @return boolean
	 * @see Delay
	 */
	public static boolean isDelay(Object o) { return o instanceof Delay; }
	
	/**
	 * Test if object is a delay variable.
	 * @param o Object
	 * @return boolean
	 * @see DelayVariable
	 */
	public static boolean isDelayVariable(Object o) { return o instanceof DelayVariable; }
	
	/**
	 * Test if object is a Dependance.
	 * @param o Object
	 * @return boolean
	 * @see Dependance
	 */
	public static boolean isDependance(Object o) { return o instanceof Dependance; }
	
	/**
	 * Test if object is an Compartment Macro.
	 * @param o
	 * @return boolean
	 * @see eu.ddmore.libpharmml.dom.modeldefn.pkmacro.DepotMacro 
	 */
	public static boolean isDepotMacro(Object o) { return o instanceof DepotMacro; }
	
	/**
	 * Test if object is a derivative.
	 * @param o Object
	 * @return boolean
	 * @see DerivativeVariable
	 */
	public static boolean isDerivative(Object o) { return o instanceof DerivativeVariable; }
	
	/**
	 * Test if object is a Dirac Delta Distribution.
	 * @param o Object
	 * @return boolean
	 * @see DiracDeltaDistribution
	 */
	public static boolean isDiracDeltaDistribution(Object o) { return o instanceof DiracDeltaDistribution; }
	
	/**
	 * Test if object is a Common Parameter.
	 * @param o Object
	 * @return boolean
	 * @see DiscreteDataParameter
	 */
	public static boolean isDiscreteDataParameter(Object o) { return o instanceof DiscreteDataParameter; }
	
	/**
	 * Test if object is an Discrete Multivariate Distribution.
	 * @param o Object
	 * @return boolean
	 * @see eu.ddmore.libpharmml.dom.uncertml.AbstractDiscreteMultivariateDistributionType 
	 */
	public static boolean isDiscreteMultivariateDistribution(Object o) { return o instanceof AbstractDiscreteMultivariateDistributionType; }
    
	/**
	 * Test if object is an Discrete Multivariate Mixture Model.
	 * @param o Object
	 * @return boolean
	 * @see eu.ddmore.libpharmml.dom.uncertml.DiscreteMultivariateMixtureModelType
	 */
	public static boolean isDiscreteMultivariateMixtureModel(Object o) { return o instanceof DiscreteMultivariateMixtureModelType; }
	
	/**
	 * Test if object is an Discrete Univariate Distribution.
	 * @param o
	 * @return boolean
	 * @see eu.ddmore.libpharmml.dom.uncertml.AbstractDiscreteUnivariateDistributionType 
	 */
	public static boolean isDiscreteUnivariateDistribution(Object o) { return o instanceof AbstractDiscreteUnivariateDistributionType; }
	
	/**
	 * Test if object is an Discrete Univariate Mixture Model.
	 * @param o
	 * @return boolean
	 * @see eu.ddmore.libpharmml.dom.uncertml.DiscreteUnivariateMixtureModel 
	 */
	public static boolean isDiscreteUnivariateMixtureModel(Object o) { return o instanceof DiscreteUnivariateMixtureModel; }
	
	/**
	 * Test if object is an Distribution.
	 * @param o Object
	 * @return boolean
	 * @see Distribution
	 */
	public static boolean isDistribution(Object o) { return o instanceof Distribution; }
	
	/**
	 * Test if object is an Distribution Parameter for ProbOnto.
	 * @param o Object
	 * @return boolean
	 * @see eu.ddmore.libpharmml.dom.probonto.DistributionParameter
	 * @see eu.ddmore.libpharmml.dom.probonto.ProbOnto
	 */
	public static boolean isDistributionParameter(Object o) { return o instanceof DistributionParameter; }
	
	/**
	 * Test if object is an Dosing Times Points.
	 * @param o Object
	 * @return boolean
	 * @see DosingTimesPoints
	 */
	public static boolean isDosingTimesPoints(Object o) { return o instanceof DosingTimesPoints; }
	
	/**
	 * Test if object is a Dosing Variable .
	 * @param o
	 * @return boolean
	 * @see eu.ddmore.libpharmml.dom.trialdesign.DosingVariable
	 */
	public static boolean isDosingVariable(Object o) { return o instanceof DosingVariable; }
	
	/**
	 * Test if object is a PharmML Double Valye.
	 * @param o Object
	 * @return boolean
	 * @see eu.ddmore.libpharmml.dom.uncertml.Double
	 */
	public static boolean isDouble_(Object o) { return  o instanceof eu.ddmore.libpharmml.dom.uncertml.Double; }
	
	/**
	 * Test if object is an Effect Macro.
	 * @param o
	 * @return boolean
	 * @see eu.ddmore.libpharmml.dom.modeldefn.pkmacro.EffectMacro 
	 */
	public static boolean isEffectMacro(Object o) { return o instanceof EffectMacro; }
	
	/**
	 * Test if object is a PharmML Element.
	 * @param o Object
	 * @return boolean
	 * @see PharmMLElement
	 */
	public static boolean isElement(Object o) { return o instanceof PharmMLElement;	}
		
	/**
	 * Test if object is an Elimination Macro.
	 * @param o
	 * @return boolean
	 * @see eu.ddmore.libpharmml.dom.modeldefn.pkmacro.EliminationMacro 
	 */
	public static boolean isEliminationMacro(Object o) { return o instanceof EliminationMacro; }
	
	/**
	 * Test if object is an estimation step.
	 * @param o Object
	 * @return boolean
	 * @see Estimation
	 */
	public static boolean isEstimation(Object o) { return o instanceof Estimation; }

	/**
	 * Test if object is an Exponential Distribution.
	 * @param o Object
	 * @return boolean
	 * @see ExponentialDistribution
	 */
	public static boolean isExponentialDistribution(Object o) { return o instanceof ExponentialDistribution; }
	
	/**
	 * Test if object is a PharmML False value.
	 * @param o Object
	 * @return boolean
	 * @see FalseBoolean
	 */
	public static boolean isFalse(Object o) { return o instanceof FalseBoolean; }
	
	/**
	 * Test if object is an FDistribution
	 * @param o Object
	 * @return boolean
	 * @see FDistribution
	 */
	public static boolean isFDistribution(Object o) { return o instanceof FDistribution; }
	
	/**
	 * Test if object is a function definition.
	 * @param o Object
	 * @return boolean
	 */
	public static boolean isFunction(Object o) { return o instanceof FunctionDefinition; }
	
	/**
	 * Test if object is an argument of a function call.
	 * @param o Object
	 * @return boolean
	 * @see FunctionArgumentType
	 */
	public static boolean isFunctionArgument(Object o) { return o instanceof FunctionArgumentType; }
	
	/**
	 * Test if object is a function call.
	 * @param o Object
	 * @return boolean
	 * @see FunctionCallType
	 */
	public static boolean isFunctionCall(Object o) { return o instanceof FunctionCallType; }
	
	/**
	 * Test if object is a function parameter
	 * @param o Object 
	 * @return FunctionParameter
	 * @see FunctionParameter
	 */
	public static boolean isFunctionParameter(Object o) { return o instanceof FunctionParameter; }
	
	/**
	 * Test if object is a Gamma Distribution.
	 * @param o Object
	 * @return boolean
	 * @see GammaDistribution
	 */
	public static boolean isGammaDistribution(Object o) { return o instanceof GammaDistribution; }
	
	/**
	 * Test if object is a General Covariate.
	 * @param o Object
	 * @return boolean
	 * @see GeneralCovariate 
	 */
	public static boolean isGeneralCovariate(Object o) { return o instanceof GeneralCovariate; }
	
	/**
	 * Test if object is a General Observation Error.
	 * @param o Object
	 * @return boolean
	 * @see GeneralObsError
	 */
	public static boolean isGeneralError(Object o) { return o instanceof GeneralObsError; }
	
	/**
	 * Test if object is a Geometric Distribution.
	 * @param o
	 * @return boolean
	 * @see eu.ddmore.libpharmml.dom.uncertml.GeometricDistribution
	 */
	public static boolean isGeometricDistribution(Object o) { return o instanceof GeometricDistribution; }
	
	/**
	 * Test if object is a Geometric Distribution.
	 * @param o
	 * @return boolean
	 * @see eu.ddmore.libpharmml.dom.uncertml.HypergeometricDistribution
	 */
	public static boolean isHypergeometricDistribution(Object o) { return o instanceof HypergeometricDistribution; }
	
	/**
	 * Test if object is an ID value.
	 * @param o Object
	 * @return boolean
	 * @see IdValue
	 */
	public static boolean isId(Object o) { return o instanceof IdValue; }
	
	/**
	 * Test if object implements the Identifiable interface
	 * @param o Object
	 * @return boolean
	 * @see Identifiable 
	 */
	public static boolean isIdentifiable(Object o) { return o instanceof Identifiable; }
	
	/**
	 * Test if object is an Independent Variable.
	 * @param o Object
	 * @return boolean
	 * @see IndependentVariable
	 */
	public static boolean isIndependentVariable(Object o) { return o instanceof IndependentVariable; }
	
	/**
	 * Test if object is an Individual Parameter.
	 * @param o Object
	 * @return boolean
	 * @see IndividualParameter
	 */
	public static boolean isIndividualParameter(Object o) { return o instanceof IndividualParameter; }
	
	/**
	 * Test if object is an Infusion
	 * @param o Object
	 * @return boolean
	 * @see Infusion
	 */
	public static boolean isInfusion(Object o) { return o instanceof Infusion; }
	
	/**
	 * Test if object is an Initial Condition.
	 * @param o Object
	 * @return boolean
	 * @see InitialCondition
	 */
	public static boolean isInitialCondition(Object o) { return o instanceof InitialCondition; }
	
	/**
	 * Test if object is an Initial Estimate.
	 * @param o Object
	 * @return InitialEstimate
	 * @see InitialEstimate
	 */
	public static boolean isInitialEstimate(Object o) { return o instanceof InitialEstimate; }
	
	/**
	 * Test if object is an PharmML integer value.
	 * @param o Object
	 * @return boolean
	 * @see IntValue
	 */
	public static boolean isInt(Object o) { return o instanceof IntValue; }
	
	/**
	 * Test if object is an Interpolation.
	 * @param o Object
	 * @return boolean
	 * @see Interpolation
	 */
	public static boolean isInterpolation(Object o) { return o instanceof Interpolation; }
	
	/**
	 * Test if object is an Interval.
	 * @param o Object
	 * @return boolean
	 * @see Interval
	 */
	public static boolean isInterval(Object o) { return o instanceof Interval; }
	
	/**
	 * Test if object is an Inverse Gamma Distribution.
	 * @param o Object
	 * @return boolean
	 * @see InverseGammaDistribution
	 */
	public static boolean isInverseGammaDistribution(Object o) {
		return o instanceof InverseGammaDistribution;		
	}
	
	/**
	 * Test if object is an Compartment Macro.
	 * @param o
	 * @return boolean
	 * @see eu.ddmore.libpharmml.dom.modeldefn.pkmacro.IVMacro 
	 */
	public static boolean isIVMacro(Object o) { return o instanceof IVMacro; }
	
	/**
	 * Test if object is an JAXB Element.
	 * @param o Object
	 * @return boolean
	 * @see JAXBElement
	 */
	public static boolean isJAXBElement(Object o) { return o instanceof JAXBElement<?> ; }
	
	/**
	 * Test if object is an Laplace Distribution.
	 * @param o Object
	 * @return boolean
	 * @see LaplaceDistribution
	 */
	public static boolean isLaplaceDistribution(Object o) { return o instanceof LaplaceDistribution; }
	
	/**
	 * Test if object is an variability level reference.
	 * @param o Object
	 * @return boolean
	 * @see LevelReference
	 */
	public static boolean isLevelReference(Object o) { return o instanceof LevelReference; }
	
	/**
	 * Test if object is a vector index limit.
	 * @param o Object
	 * @return boolean
	 * @see LowUpLimit
	 */
	public static boolean isLimit(Object o) { return o instanceof LowUpLimit; }
	
	/**
	 * Test if an object is a Link Function
	 * @param o Object
	 * @return boolean
	 * @see eu.ddmore.libpharmml.dom.commontypes.LinkFunction
	 */
	public static boolean isLinkFunction(Object o) { return o instanceof LinkFunction; }
	
	/**
	 * Test if object is a local variable.
	 * @param o Object
	 * @return boolean
	 * @see VariableDefinition
	 */
	public static boolean isLocalVariable(Object o) { return o instanceof VariableDefinition; }

	/**
	 * Test if object is a logical binary operation.
	 * @param o Object
	 * @return boolean
	 * @see LogicBinOp
	 */
	public static boolean isLogicalBinaryOperation(Object o) { return o instanceof LogicBinOp; }
	
	/**
	 * Test if object is a Logical Unary Operation.
	 * @param o Object
	 * @return boolean
	 * @see LogicUniOp
	 */
	public static boolean isLogicalUnaryOperation(Object o) { return o instanceof LogicUniOp; }
	
	/**
	 * Test if object is a Logistic Distribution.
	 * @param o Object
	 * @return boolean
	 * @see LogisticDistribution
	 */
	public static boolean isLogisticDistribution(Object o) { return o instanceof LogisticDistribution; }
	
	/**
	 * Test if object is a Log Normal Distribution
	 * @param o Object
	 * @return boolean
	 * @see LogNormalDistribution
	 */
	public static boolean isLogNormalDistribution(Object o) { return o instanceof LogNormalDistribution; }
	
	/**
	 * Test if object is an Macro Value.
	 * @param o Object
	 * @return boolean
	 * @see eu.ddmore.libpharmml.dom.modeldefn.pkmacro.MacroValue 
	 */
	public static boolean isMacroValue(Object o) { return o instanceof MacroValue; }
	
	/**
	 * Test if object is a category Map Type
	 * @param o Object
	 * @return boolean
	 * @see MapType
	 */
	public static boolean isMap(Object o) { return o instanceof MapType; }
	
	/**
	 * Test if object is an Mapping declaration.
	 * @param o Object
	 * @return boolean
	 * @see MapType
	 */
	public static boolean isMapType(Object o) { return o instanceof MapType; }
	
	/**
	 * Test if object is a Matrix.
	 * @param o Object
	 * @return boolean
	 * @see MatrixSelector
	 */
	public static boolean isMatrix(Object o) { return o instanceof Matrix; }
	
	/**
	 * Test if object is a matrix cell.
	 * @param o Object
	 * @return boolean
	 * @see MatrixCell
	 */
	public static boolean isMatrixCell(Object o) {  return o instanceof MatrixCell; }
	
	/**
	 * Test if an object is a matrix cell numeric reference, i.e. brackets in a cell access statement.
	 * @param o Object
	 * @return boolean
	 * @see MatrixCellSelector
	 */
	public static boolean isMatrixCellSelector(Object o) { return o instanceof MatrixCellSelector; }
	
	/**
	 * Test if object is a Matrix Selector.
	 * @param o Object
	 * @return boolean
	 * @see MatrixSelector
	 */
	public static boolean isMatrixSelector(Object o) { return o instanceof MatrixSelector; }
	
	/**
	 * Test if object is an Matrix Unary operation.
	 * @param o Object
	 * @return boolean
	 * @see MatrixUniOp
	 */
	public static boolean isMatrixUnaryOperation(Object o) { return o instanceof MatrixUniOp; }
	
	/**
	 * Test if object is an Array Indices Variable
	 * @param o Object
	 * @return boolean
	 * @see MatrixVectorIndex
	 */
	public static boolean isMatrixVectorIndex(Object o) { return o instanceof MatrixVectorIndex; }
	
	/**
	 * Test if object is an Multinomial Distribution.
	 * @param o Object
	 * @return boolean
	 * @see eu.ddmore.libpharmml.dom.uncertml.MultinomialDistributionType
	 */
	public static boolean isMultinomialDistribution(Object o) { return o instanceof MultinomialDistributionType; }
	
	/**
	 * Test if an object is a Multiple DV Mapping (derivative-based category list) object.
	 * @param o Object
	 * @return boolean
	 * @see MultipleDVMapping
	 */
	public static boolean isMultipleDVMapping(Object o) { return o instanceof MultipleDVMapping; }
	
	/**
	 * Test if object is a Natural Number Value.
	 * @param o Object
	 * @return boolean
	 * @see NaturalNumberValueType
	 */
	public static boolean isNaturalNumberValue(Object o) { return o instanceof NaturalNumberValueType; }
	
	/**
	 * Test if object is a Negative Binomial Distribution.
	 * @param o
	 * @return boolean
	 * @see eu.ddmore.libpharmml.dom.uncertml.NegativeBinomialDistribution
	 */
	public static boolean isNegativeBinomialDistribution(Object o) { return o instanceof NegativeBinomialDistribution; }
	
	/**
	 * Test if object is a Normal Distribution.
	 * @param o Object
	 * @return boolean
	 * @see NormalDistribution
	 */
	public static boolean isNormalDistribution(Object o) { return o instanceof NormalDistribution; }
	
	/**
	 * Test if object is an PharmML numeric value.
	 * @param o Object
	 * @return boolean
	 * @see IntValue 
	 */
	public static boolean isNumber(Object o) { return isInt(o) || isReal(o); }
	
	/**
	 * Test if object is a Observation associated with a dosing regimen.
	 * @param o Object
	 * @return boolean
	 * @see Observation
	 */
	public static boolean isObservation(Object o) { return o instanceof Observation; }
	
	/**
	 * Test if object is a Observation Error.
	 * @param o Object
	 * @return boolean
	 * @see ObservationError
	 */
	public static boolean isObservationError(Object o) { return o instanceof ObservationError; }
	
	/**
	 * Test if object is a Observation Model.
	 * @param o Object
	 * @return boolean
	 * @see ObservationModel
	 */
	public static boolean isObservationModel(Object o) { return o instanceof ObservationModel; }
	
	/**
	 * Test if object is an Occasion Sequence.
	 * @param o Object
	 * @return boolean
	 * @see Occasion
	 */
	public static boolean isOccasion(Object o) { return o instanceof Occasion; }
	
	/**
	 * Test if object is an Occasion Sequence.
	 * @param o Object
	 * @return boolean
	 * @see OccasionSequence 
	 */
	public static boolean isOccasionSequence(Object o) { return o instanceof OccasionSequence; }
	
	
	/**
	 * Test if object is an Operation Property.
	 * @param o Object
	 * @return boolean
	 * @see OperationProperty
	 */
	public static boolean isOperationProperty(Object o) { return o instanceof OperationProperty; }
	
	/**
	 * Test if object is an Oral Macro.
	 * @param o Object
	 * @return boolean
	 * @see eu.ddmore.libpharmml.dom.modeldefn.pkmacro.AbsorptionMacro 
	 */
	public static boolean isOralMacro(Object o) { return o instanceof OralMacro; }

	/**
	 * Test if object is an Occasion Sequence.
	 * @param o Object
	 * @return boolean
	 * @see OccasionSequence 
	 */
	public static boolean isParameter(Object o) { return o instanceof Parameter; }
	
	/**
	 * Test if object is a Parameter Estimate.
	 * @param o Object
	 * @return boolean
	 * @see ParameterEstimate
	 */
	public static boolean isParameterEstimate(Object o) { return o instanceof ParameterEstimate; }
	
	/**
	 * Test if object is a Parameter Model.
	 * @param o Object
	 * @return boolean
	 * @see ParameterModel
	 */
	public static boolean isParameterModel(Object o) { return o instanceof ParameterModel; }
	
	/**
	 * Test if object is a Parameter Model.
	 * @param o Object
	 * @return boolean
	 * @see ParameterModel
	 */
	public static boolean isParetoDistribution(Object o) { return o instanceof ParetoDistribution; }
	
	
	/**
	 * Test if object is an Effect Peripheral Macro 
	 * @param o
	 * @return boolean
	 * @see eu.ddmore.libpharmml.dom.modeldefn.pkmacro.PeripheralMacro 
	 */
	public static boolean isPeripheralMacro(Object o) { return o instanceof PeripheralMacro; }
	
	/**
	 * Check if an object is an PharmML list type.
	 * @param o Object
	 * @return boolean
	 */
	public static boolean isPharmMLList(Object o) { return isVector(o) || isSequence(o); }
	
	/**
	 * Test if an object is acting an an OID container.
	 * @return boolean
	 * @see PharmMLObject
	 */
	public static boolean isPharmMLObject(Object o) { return o instanceof PharmMLObject; }
	
	/**
	 * Test if object is a Piece of a Piecewise statement.
	 * @param o Object
	 * @return boolean
	 * @see Piece Piecewise
	 */
	public static boolean isPiece(Object o) { return o instanceof Piece; }
	
	/**
	 * Test if object is a Piece wise conditional.
	 * @param o Object
	 * @return boolean
	 * @see Piecewise
	 */
	public static boolean isPiecewise(Object o) { return o instanceof Piecewise; }
	
	/**
	 * Test if object is an Absorption Macro.
	 * @param o Object
	 * @return boolean
	 * @see eu.ddmore.libpharmml.dom.modeldefn.pkmacro.AbsorptionMacro 
	 */
	public static boolean isPKMacroList(Object o) { return o instanceof PKMacroList; }
	
	/**
	 * Test if object is a Negative Binomial Distribution.
	 * @param o
	 * @return boolean
	 * @see eu.ddmore.libpharmml.dom.uncertml.NegativeBinomialDistribution
	 */
	public static boolean isPoissonDistribution(Object o) { return o instanceof PoissonDistribution; }
	
	/**
	 * Test if object is a parameter.
	 * @param o Object
	 * @return boolean
	 * @see PopulationParameter
	 */
	public static boolean isPopulationParameter(Object o) { return o instanceof PopulationParameter; }
	
	/**
	 * Test if object is an population value of a structured error model.
	 * @param o Object
	 * @return boolean
	 * @see PopulationValue
	 */
	public static boolean isPopulationValue(Object o) { return o instanceof PopulationValue; }
	
	/**
	 * Test if object is a Positive Natural Number.
	 * @param o Object
	 * @return boolean
	 * @see PositiveNaturalNumber
	 */
	public static boolean isPositiveNaturalNumber(Object o) { return o instanceof PositiveNaturalNumber; }
	
	/**
	 * Test if object is a Probability.
	 * @param o Object
	 * @return boolean
	 * @see Probability
	 */
	public static boolean isProbability(Object o) { return o instanceof Probability; }
	
	/**
	 * Test if object is a Probability.
	 * @param o Object
	 * @return boolean
	 * @see ProbabilityAssignment
	 */
	public static boolean isProbabilityAssignment(Object o) { return o instanceof ProbabilityAssignment; }
	
	/**
	 * Test if object is an Probability Value of a discrete distribution like Bernoulli.
	 * @param o
	 * @return boolean
	 * @see ProbabilityValueType
	 */
	public static boolean isProbabilityValue(Object o) { return o instanceof ProbabilityValueType; }
	
	/**
	 * Test if object is an Absorption Macro.
	 * @param o Object
	 * @return boolean
	 * @see ProbOnto
	 */
	public static boolean isProbOnto(Object o) { return o instanceof ProbOnto; }
	
	/**
	 * Test if object is a Product.
	 * @param o Object
	 * @return boolean
	 * @see Product
	 */
	public static boolean isProduct(Object o) { return o instanceof Product; }
	
	/**
	 * Test if object is a Random Effect.
	 * @param o Object
	 * @return boolean
	 * @see ParameterRandomEffect
	 */
	public static boolean isRandomEffect(Object o) { return o instanceof ParameterRandomEffect; }
	
	/**
	 * Test if object is a Random Variable.
	 * @param o Object
	 * @return boolean
	 * @see ParameterRandomVariable
	 */
	public static boolean isRandomVariable(Object o) { return o instanceof ParameterRandomVariable; }
	
	/**
	 * Test if object is a PharmML real value.
	 * @param o Object
	 * @return boolean
	 * @see RealValue
	 */
	public static boolean isReal(Object o) { return o instanceof RealValue; }
	
	/**
	 * Test if object is an Residual Error in a Gaussian Error Model
	 * @param o Object
	 * @return boolean
	 * @see ResidualError
	 */
	public static boolean isResidualError(Object o) { return o instanceof StructuredObsError.ResidualError; }
	
	/**
	 * Test if object is an Rhs assignment statement.
	 * @param o Object
	 * @return boolean
	 * @see Rhs
	 */
	public static boolean isRhs(Object o) { return o instanceof Rhs; }
	
	/**
	 * Test if object is a PharmML root type.
	 * @param o Object
	 * @return boolean
	 * @see PharmMLRootType
	 */
	public static boolean isRootType(Object o) { return o instanceof PharmMLRootType; }
	
	/**
	 * Test if object is a scalar.
	 * @param o Object
	 * @return boolean
	 * @see Scalar
	 */
	public static boolean isScalar(Object o) {
		boolean is_scalar = false;

		if (o != null) {
			if (o instanceof JAXBElement) {
				JAXBElement<?> jaxbElement = ((JAXBElement<?>) o);
				Object v = jaxbElement.getValue();

				is_scalar = 
						isFalse(v) || isTrue(v) || isString(v)
						|| isId(v) || isReal(v) || isInt(v);
			}
		}

		return is_scalar;
	}
	
	/**
	 * Test if object implements the Scalar interface.
	 * @param o Object
	 * @return boolean
	 * @see Scalar
	 */
	public static boolean isScalarInterface(Object o) { return o instanceof Scalar; }
	
	/**
	 * Test if object is a sequence.
	 * @param o Object
	 * @return boolean
	 * @see Sequence
	 */
	public static boolean isSequence(Object o) { return o instanceof Sequence; }
	
	/**
	 * Test if object is a Simulation Step.
	 * @param o Object
	 * @return boolean
	 * @see Simulation
	 */
	public static boolean isSimulation(Object o) { return o instanceof Simulation; }
	
	/**
	 * Test if object is a Simulation Output.
	 * @param o Object
	 * @return boolean
	 * @see DerivativeVariable VariableDefinition GaussianObsError GeneralObsError IndependentVariable
	 */
	public static boolean isSimulationOutput(Object o) {
		return isDerivative(o) || isLocalVariable(o) || isStructuredError(o) || isGeneralError(o) || isIndependentVariable(o);
	}
	
	/**
	 * Test if object is a Standard Assignable.
	 * @param o Object
	 * @return boolean
	 * @see eu.ddmore.libpharmml.dom.commontypes.StandardAssignable 
	 */
	public static boolean isStandardAssignable(Object o) { return o instanceof StandardAssignable; }
	
	/**
	 * Test if object is a Steady State Parameter.
	 * @param o Object
	 * @return boolean
	 * @see SteadyStateParameter
	 */
	public static boolean isSteadyStateParameter(Object o) { return o instanceof SteadyStateParameter; }
	
	/**
	 * Test if object is an Step/Task.
	 * @param o Object
	 * @return boolean
	 * @see CommonModellingStep
	 */
	public static boolean isStep(Object o) { return o instanceof CommonModellingStep; }
	
	/**
	 * Test if object is a PharmML string value.
	 * @param o Object
	 * @return boolean
	 * @see StringValue
	 */
	public static boolean isString(Object o) { return o instanceof StringValue; }
	
	/**
	 * Test if object is a Structural Model.
	 * @param o Object
	 * @return boolean
	 * @see StructuralModel
	 */
	public static boolean isStructuralModel(Object o) { return o instanceof StructuralModel; }
	
	/**
	 * Test if object is a Gaussian Observation Error.
	 * @param o Object
	 * @return boolean
	 * @see StructuredObsError
	 */
	public static boolean isStructuredError(Object o) { return o instanceof StructuredObsError; }
	
	/**
	 * Test if object is a Structured Model.
	 * @param o Object
	 * @return boolean
	 * @see StructuredModel
	 */
	public static boolean isStructuredlModel(Object o) { return o instanceof StructuredModel; }
	
	/**
	 * Test if object is the output for a structured Observation Error.
	 * @param o Object
	 * @return boolean
	 * @see Output
	 */
	public static boolean isStructuredModel_Output(Object o) { return o instanceof Output; }
	
	/**
	 * Test if object is an Residual Error.
	 * @param o Object 
	 * @return boolean
	 * @see ResidualError
	 */
	public static boolean isStructuredModel_ResidualError(Object o) { return o instanceof ResidualError; }
	
	/**
	 * Test if object is a Student 'T' Distribution.
	 * @param o Object
	 * @return boolean
	 * @see StudentTDistribution
	 */
	public static boolean isStudentTDistribution(Object o) { return o instanceof StudentTDistribution; }
	
	/**
	 * Test if object is a summation.
	 * @param o Object
	 * @return boolean
	 * @see Sum
	 */
	public static boolean isSum(Object o) { return o instanceof Sum; }
	
	/**
	 * Test if object is a Symbol
	 * @param o Object
	 * @return boolean
	 * @see Symbol
	 */
	public static boolean isSymbol(Object o) {  return o instanceof Symbol;  }
	
	/**
	 * Test if object is a Symbol Reference.
	 * @param o Object
	 * @return boolean
	 * @see SymbolRef
	 */
	public static boolean isSymbolReference(Object o) { return o instanceof SymbolRef; }
	
	/**
	 * Test if object is a TargetMapping.
	 * @param o Object
	 * @return boolean
	 * @see TargetMapping
	 */
	public static boolean isTargetMapping(Object o) { return o instanceof TargetMapping; }
	
	/**
	 * Test if object is a Time To Event Data element;
	 * @param o Object
	 * @return boolean
	 * @see eu.ddmore.libpharmml.dom.modeldefn.TimeToEventData
	 */
	public static boolean isTimeToEventData(Object o) { return o instanceof TimeToEventData; }
	
	/**
	 * Test if object is an Effect TransferMacro
	 * @param o
	 * @return boolean
	 * @see eu.ddmore.libpharmml.dom.modeldefn.pkmacro.TransferMacro 
	 */
	public static boolean isTransferMacro(Object o) { return o instanceof TransferMacro; }
	
	/**
	 * Test if object is a Transformed Covariate reference.
	 * @param o Object
	 * @return boolean
	 * @see eu.ddmore.libpharmml.dom.modeldefn.TransformedCovariate
	 */
	public static boolean isTransformedCovariate(Object o) { return o instanceof TransformedCovariate; }
	
	/**
	 * Test if object is a Transition Rate.
	 * @param o
	 * @return boolean
	 * @see eu.ddmore.libpharmml.dom.modeldefn.TransitionRate 
	 */
	public static boolean isTransitionRate(Object o) { return o instanceof TransitionRate; }
	
	/**
	 * Test if object is a PharmML true value.
	 * @param o Object
	 * @return boolean
	 * @see TrueBoolean
	 */
	public static boolean isTrue(Object o) { return o instanceof TrueBoolean; }
	
	/**
	 * Test if object is a TTEFunction.
	 * @param o Object
	 * @return boolean
	 * @see eu.ddmore.libpharmml.dom.modeldefn.TTEFunction
	 */
	public static boolean isTTEFunction(Object o) { return o instanceof TTEFunction; }
	
	/**
	 * Test if object is a unary operation (i.e. sin, cos).
	 * @param o Object
	 * @return boolean
	 * @see Uniop
	 */
	public static boolean isUnaryOperation(Object o) { return o instanceof Uniop; }
	
	/**
	 * Test if object is a Uniform Distribution.
	 * @param o Object
	 * @return boolean
	 * @see UniformDistribution
	 */
	public static boolean isUniformDistribution(Object o) { return o instanceof UniformDistribution; }
	
	/**
	 * Test if object is a Univariate Distribution.
	 * @param o Object
	 * @return boolean
	 * @see AbstractContinuousUnivariateDistributionType
	 */
	public static boolean isUnivariateDistribution(Object o) { return o instanceof AbstractContinuousUnivariateDistributionType; }
	
	/**
	 * Test if object is a Variability Scope.
	 * @param o Object
	 * @return VariabilityDefnBlock
	 */
	public static boolean isVariabilityDefinitionBlock(Object o) { return o instanceof VariabilityDefnBlock; }
	
	/**
	 * Test if object is a Variability Level Definition.
	 * @param o Object
	 * @return VariabilityLevelDefinition
	 */
	public static boolean isVariabilityLevelDefinition(Object o) { return o instanceof VariabilityLevelDefinition; }
	
	/**
	 * Test if object is an UncertML variable reference.
	 * @param o Object
	 * @return boolean
	 * @see VarRefType
	 */
	public static boolean isVariableReference(Object o) { return o instanceof VarRefType; }
	
	/**
	 * Test if object is a vector.
	 * @param o Object
	 * @return boolean
	 * @see Vector
	 */
	public static boolean isVector(Object o) { return o instanceof Vector; }
	
	/**
	 * Test if object is a vector cell.
	 * @param o Object
	 * @return boolean
	 * @see VectorCell
	 */
	public static boolean isVectorCell(Object o) { return o instanceof VectorCell; }
	
	/**
	 * Test if object is a vector segment.
	 * @param o Object
	 * @return boolean
	 * @see VectorSegment
	 */
	public static boolean isVectorSegment(Object o) { return o instanceof VectorSegment; }
	
	/**
	 * Test if object is a vector segment selector.
	 * @param o Object
	 * @return boolean
	 * @see VectorSegmentSelector
	 */
	public static boolean isVectorSegmentSelector(Object o) { return o instanceof VectorSegmentSelector; }
	
	/**
	 * Test if object is a vector selector.
	 * @param o Object
	 * @return boolean
	 * @see VectorSelector
	 */
	public static boolean isVectorSelector(Object o) { return o instanceof VectorSelector; }
	
	/**
	 * Test if object is a Weibull Distribution.
	 * @param o Object
	 * @return boolean
	 * @see WeibullDistribution
	 */
	public static boolean isWeibullDistribution(Object o) { return o instanceof WeibullDistribution; }
	
	/**
	 * Test if object is an Wishart Distribution.
	 * @param o Object
	 * @return boolean
	 * @see eu.ddmore.libpharmml.dom.uncertml.WishartDistributionType
	 */
	public static boolean isWishartDistribution(Object o) { return o instanceof WishartDistributionType; }
}
