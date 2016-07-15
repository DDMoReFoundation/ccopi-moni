/*******************************************************************************
 * Copyright (C) 2015 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.spi;

import java.io.File;
import java.util.List;
import java.util.Map;

import crx.converter.engine.Accessor;
import crx.converter.engine.ParameterContext;
import crx.converter.engine.ScriptDefinition;
import crx.converter.engine.VariableDeclarationContext;
import crx.converter.engine.common.DataFiles;
import crx.converter.spi.blocks.CovariateBlock;
import crx.converter.spi.blocks.ObservationBlock;
import crx.converter.spi.blocks.ParameterBlock;
import crx.converter.spi.blocks.StructuralBlock;
import crx.converter.spi.blocks.TrialDesignBlock;
import crx.converter.spi.blocks.VariabilityBlock;
import crx.converter.spi.steps.EstimationStep;
import crx.converter.spi.steps.SimulationStep;
import crx.converter.tree.BinaryTree;
import crx.converter.tree.NestedTreeRef;
import crx.converter.tree.TreeMaker;
import eu.ddmore.convertertoolbox.api.spi.ConverterProvider;
import eu.ddmore.libpharmml.IValidationReport;
import eu.ddmore.libpharmml.dom.PharmML;
import eu.ddmore.libpharmml.dom.commontypes.CommonVariableDefinition;
import eu.ddmore.libpharmml.dom.commontypes.SymbolRef;
import eu.ddmore.libpharmml.dom.commontypes.VariableDefinition;
import eu.ddmore.libpharmml.dom.modeldefn.CovariateDefinition;
import eu.ddmore.libpharmml.dom.modeldefn.PopulationParameter;
import eu.ddmore.libpharmml.pkmacro.translation.Translator;

/**
 * Generic methods for a language converter.
 */
public interface ILexer extends ConverterProvider {
	/**
	 * Defines the index variable symbol for a script associated with a vector/sequence type.<br/>
	 * Index value set whether sequence is individual or residual scope.
	 * @param key Vector or Sequence type
	 * @param value Indexing symbol specific to the target language.
	 * @see eu.ddmore.libpharmml.dom.commontypes.Sequence 
	 * @see eu.ddmore.libpharmml.dom.commontypes.Vector
	 * @see eu.ddmore.libpharmml.dom.modeldefn.Variability
	 */
	public void addIndexSymbol(Object key, String value);
	
	/**
	 * Adds a nested tree reference to the Lexer language maps.<br/>
	 * Nested trees created when complex statements converted to AST.
	 * @param ref A nested tree statement.
	 * @return boolean Success or Failure
	 * @see NestedTreeRef
	 */
	public boolean addStatement(NestedTreeRef ref);
	
	/**
	 * Adds a binary tree (AST) to the statement map representing a PharmML model element.
	 * @param element PharmML model element
	 * @param bt AST of the model element.
	 * @see eu.ddmore.libpharmml.dom.commontypes.PharmMLElement
	 * @see BinaryTree
	 */
	public void addStatement(Object element, BinaryTree bt);
	
	/**
	 * Checks if the model has a trial design section with randomised components.
	 * @see eu.ddmore.libpharmml.dom.trialdesign.TrialDesign 
	 * @see eu.ddmore.libpharmml.dom.modeldefn.IndividualParameter
	 */
	public void checkForTrialDesignIfRandomisedModel();
	
	/**
	 * Create the parameter context structure specific to an ILexer
	 * implementation.
	 * @see ParameterContext
	 */
	public void createParameterContext();
	
	/**
	 * Create a tree of a model element and places it in the statement map.
	 * @param o Object that needs an AST
	 * @return BinaryTree
	 */
	public BinaryTree createTree(Object o);
	
	/**
	 * Return the element accessor bound to the current PharmML model.
	 * @return crx.converter.engine.Accessor
	 */
	public Accessor getAccessor();
	
	/**
	 * Get list of covariate blocks bound to the current model.
	 * @return List<CovariateBlock>
	 */
	public CovariateBlock getCovariateBlock();
	
	/**
	 * Get list of covariate blocks bound to the current model.
	 * @return List<CovariateBlock>
	 */
	public List<CovariateBlock> getCovariateBlocks();
	
	/**
	 * List the covariates bound to the current PharmML model.
	 * @return List<CovariateDefinition>
	 */
	public List<CovariateDefinition> getCovariates();
	
	/**
	 * Get a list of data files referenced by the current model. 
	 * @return DataFiles
	 */
	public DataFiles getDataFiles();
	
	/**
	 * Get the model read by a converter.
	 * @return eu.ddmore.libpharmml.dom.PharmML
	 */
	public PharmML getDom();
	
	/**
	 * Get list of error models bound to the current model.
	 * @return List<ObservationBlock>
	 */
	public List<ObservationBlock> getErrorModels();
	
	/**
	 * Get the estimation step definition linked to a model.
	 * @return EstimationStep
	 */
	public EstimationStep getEstimationStep();
	
	/**
	 * Get a indexing symbol bound to a PharmML sequence/vector type.
	 * @param key PharmML model element
	 * @return java.lang.String
	 * @see eu.ddmore.libpharmml.dom.commontypes.Sequence 
	 * @see eu.ddmore.libpharmml.dom.commontypes.Vector
	 */
	public String getIndexSymbol(Object key);
	
	/**
	 * Get the index number in model of a named individual parameter. 
	 * @param name Individual parameter name.
	 * @return java.lang.Integer
	 * @see eu.ddmore.libpharmml.dom.modeldefn.IndividualParameter
	 */
	public Integer getIndividualParameterIndex(String name);
	
	/**
	 * Get a list of local variables bound to the current structural model.
	 * @return java.util.List<eu.ddmore.libpharmml.dom.commontypes.VariableDefinition>
	 * @see eu.ddmore.libpharmml.dom.modeldefn.StructuralModel
	 */
	public List<VariableDefinition> getLocalVariables();
		
	/**
	 * Get the filename the current structural model.
	 * @return java.lang.String Filename specific to the target language.
	 * @see eu.ddmore.libpharmml.dom.modeldefn.StructuralModel
	 */
	public String getModelFilename();
	
	/**
	 * Get the parsed name of the structural model. 
	 * @return java.lang.String Structural model name.
	 * @see eu.ddmore.libpharmml.dom.modeldefn.StructuralModel
	 */
	public String getModelName();
	
	/**
	 * Get the index of a named global parameter in the parameter model.
	 * @param name Parameter name
	 * @see eu.ddmore.libpharmml.dom.modeldefn.ParameterModel
	 * @see eu.ddmore.libpharmml.dom.modeldefn.SimpleParameter
	 * @return java.lang.Integer Array Index
	 */
	public Integer getModelParameterIndex(String name);
	
	/**
	 * Get a list of 'simple' parameters from the parameter model.
	 * @return java.util.List<eu.ddmore.libpharmml.dom.modeldefn.SimpleParameter>
	 * @see eu.ddmore.libpharmml.dom.modeldefn.ParameterModel
	 * @see eu.ddmore.libpharmml.dom.modeldefn.SimpleParameter
	 */
	public List<PopulationParameter> getModelParameters();
	
	/**
	 * Get the name of the Lexer usage.
	 */
	String getName();
	
	/**
	 * Return a list of the parsed observation blocks in a PharmML model.
	 * @return java.util.List<ObservationBlock>
	 * @see eu.ddmore.libpharmml.dom.modeldefn.ObservationModel
	 */
	public List<ObservationBlock> getObservationBlocks();
	
	/**
	 * Applies a typecast to a simple parameter to a observation model scoped parameter.
	 * @param p A parameter bound to an observation model.
	 * @see eu.ddmore.libpharmml.dom.modeldefn.ContinuousObservationModel
	 * @return ObservationParameter
	 */
	public crx.converter.engine.common.ObservationParameter getObservationParameter(PopulationParameter p);
	
	/**
	 * Get the output directory for the converter.
	 * @return java.lang.String
	 */
	public String getOutputDirectory();
	
	/**
	 * Get the lexed parameter model.
	 * @return ParameterBlock
	 * @see eu.ddmore.libpharmml.dom.modeldefn.ParameterModel
	 */
	public ParameterBlock getParameterBlock();
	
	/**
	 * Get the context map for model parameters.
	 * @return java.util.Map<SimpleParameter, ParameterContext>
	 * @see eu.ddmore.libpharmml.dom.modeldefn.ParameterModel
	 * @see eu.ddmore.libpharmml.dom.modeldefn.SimpleParameter
	 */
	public Map<PopulationParameter, ParameterContext> getParameterContextMap();
	
	/**
	 * Get the instance of the parser bound to the lexer.<br/>
	 * The parser transforms a model to code.
	 * @return IParser
	 */
	public IParser getParser();
	
	/**
	 * Get a condensed list of model elements required for code generation.
	 * @return ScriptDefinition
	 */
	public ScriptDefinition getScriptDefinition();
	
	/**
	 * Return list of simulation outputs as string.
	 * @return java.util.List<String>
	 */
	public List<String> getSimulationOutputNames();
	
	/**
	 * Get a map of indices for model elements associated with simulation outputs.
	 * @return java.util.Map<java.lang.Integer, eu.ddmore.libpharmml.dom.modeldefn.CommonVariableDefinition>
	 */
	public Map<Integer, CommonVariableDefinition> getSimulationOutputs();
	
	/**
	 * Get simulation outputs for the specified structural block.
	 * @param block Specified Structural block
	 * @return java.util.Map<java.lang.Integer, eu.ddmore.libpharmml.dom.modeldefn.CommonVariableDefinition>
	 */
	public Map<Integer, CommonVariableDefinition> getSimulationOutputs(StructuralBlock block);
	
	/**
	 * Get the simulation step bound to a model.
	 * @return SimulationStep
	 */
	public SimulationStep getSimulationStep();
	
	/**
	 * Get sorted list of variability levels.
	 * @return List<String> List is empty if sort did not occur.
	 */
	public List<String> getSortedVariabilityLevels();
	
	/**
	 * Get the binary tree bound to a model element. 
	 * @param key
	 * @return BinaryTree
	 * @see eu.ddmore.libpharmml.dom.commontypes.PharmMLElement
	 */
	public BinaryTree getStatement(Object key);
	
	/**
	 * Get the index of an simulation variable in the model output state vector.
	 * @param name
	 * @return java.lang.Integer Output State vector index.
	 * @see eu.ddmore.libpharmml.dom.commontypes.DerivativeVariable
	 * @see eu.ddmore.libpharmml.dom.commontypes.VariableDefinition
	 */
	public Integer getStateVariableIndex(String name);
	
	/**
	 * Get list of lexed structural blocks bound to a model.
	 * @return java.util.List<StructuralBlock>
	 */
	public List<StructuralBlock> getStructuralBlocks();
	
	/**
	 * Get the current lexed structural block.
	 * @return StructuralBlock
	 */
	public StructuralBlock getStrucuturalBlock();
	
	/**
	 * Get the PK macro translator instance being used by a converter.
	 * @return eu.ddmore.libpharmml.pkmacro.translation.Translator
	 */
	public Translator getTranslator();
	
	/**
	 * Get the tree maker bound to the converter instance.
	 * @return ITreeMaker
	 * @see BinaryTree
	 */
	public TreeMaker getTreeMaker();
	
	/**
	 * Get the lexed trial design block bound to the model.
	 * @return TrialDesignBlock
	 */
	public TrialDesignBlock getTrialDesign();
	
	/**
	 * Loads a validation report generated when XML loaded from file.
	 * @return eu.ddmore.libpharmml.IValidationError
	 * @see ILexer#setValidateXML(boolean)
	 */
	public IValidationReport getValidationReport();
	
	/**
	 * Determines the variability scope of model element linked to a symbol reference.
	 * @param ref Symbol Reference
	 * @return VariabilityBlock VariabilityBlock or NULL if scope undetermined.
	 */
	public VariabilityBlock getVariabilityBlock(SymbolRef ref);
	
	/**
	 * Hazard a guess at the variable usage in a PharmML model based on the overall model content.
	 * @param v Variable to try and guess the context.
	 * @return VariableDeclarationContext
	 */
	public VariableDeclarationContext guessContext(VariableDefinition v);
	
	/**
	 * Flag if a previous modelling step is an estimation.
	 * @return boolean
	 * @see eu.ddmore.libpharmml.dom.modellingsteps.CommonModellingStep
	 */
	public boolean hasDoneEstimation();
	
	/**
	 * Flag if a converter needs to access external data sets.
	 * @return boolean 
	 * @see eu.ddmore.libpharmml.dom.modellingsteps.ExternalDataSet
	 * @see DataFiles
	 */
	//public boolean hasExternalDataSets();
	
	/**
	 * Flag if model has a dosing regimen.
	 * @return boolean
	 */
	public boolean hasDosing();
	
	/**
	 * Flag if model has a bound estimation step.
	 * @return boolean
	 * @see eu.ddmore.libpharmml.dom.modellingsteps.Estimation
	 */
	public boolean hasEstimation();
	
	/**
	 * Check if model needs a event to modify execution state.
	 * @return boolean
	 */
	public boolean hasEvents();
	
	/**
	 * Flag if a converter needs to access external data sets.
	 * @return boolean 
	 * @see eu.ddmore.libpharmml.dom.trialdesign.ExternalDataSet
	 * @see DataFiles
	 */
	public boolean hasExternalDatasets();
	
	/**
	 * Flag to instruct parser to create a plotting block for a simulation.
	 * @return boolean
	 * @see IParser
	 */
	public boolean hasPlottingBlock();
	
	/**
	 * Flag if model has a bound simulation step.
	 * @return boolean
	 */
	public boolean hasSimulation();
	
	/**
	 * Flag if model element has an AST.
	 * @param key Model element
	 * @return boolean
	 * @see BinaryTree
	 */
	public boolean hasStatement(Object key);
	
	/**
	 * Flag that the converter is using a PK Macro model that is translated.
	 * @return boolean
	 */
	public boolean hasTranslatedPKMacros();
	
	/**
	 * Flag if model has a trial design section.
	 * @return boolean
	 */
	public boolean hasTrialDesign();
	
	/**
	 * Flag that the converter is using a PK Macro model that is not translated.
	 * @return boolean
	 */
	public boolean hasUntranslatedPKMacros();
	
	/**
	 * Flag if model has dose washout.
	 * @return boolean
	 */
	public boolean hasWashout();
	
	/**
	 * Flag if model is a standard PKPD/ADME model.
	 * @return boolean
	 */
	public boolean isADMEScript();
	
	/**
	 * Flag is a converter is reading from the last structural 
	 * block in a model.
	 * @return boolean
	 */
	public boolean isAtLastStructuralBlock();
	
	/**
	 * Flag model is a categorical covariate type.
	 * @return boolean
	 * @see eu.ddmore.libpharmml.dom.modeldefn.CovariateDefinition#getCategorical()
	 */
	public boolean isCategoricalCovariate();
	
	/**
	 * Flag model is a categorical discrete type.
	 * @return boolean
	 * @see eu.ddmore.libpharmml.dom.modeldefn.Discrete#getCategoricalData()
	 */
	public boolean isCategoricalDiscrete();
	
	/**
	 * Flag model is a DDE type.
	 * @return boolean
	 */
	public boolean isDDE();
	
	/**
	 * Flag model is a discrete type.
	 * @return boolean
	 * @see eu.ddmore.libpharmml.dom.modeldefn.Discrete 
	 */
	public boolean isDiscrete();
	
	/**
	 * Flag if converter permits duplicated named local variables in
	 * a structural model.
	 * @return boolean
	 */
	public boolean isDuplicateVariablesPermitted();
	
	/**
	 * Flag that a parser should filter for reserved words 
	 * when generating code.
	 * @return boolean
	 */
	public boolean isFilterReservedWords();

	/**
	 * Test if a parameter declaration is a dosing parameter element
	 * generated by the converter engine.
	 * @return boolean
	 */
	public boolean isGeneratedDosingParameter(PopulationParameter p);
	
	/**
	 * Flag that the language converter must index arrays from zero.
	 * @return boolean
	 */
	public boolean isIndexFromZero();
	
	/**
	 * Check if named model element is an individual parameter.
	 * @param symbol
	 * @return boolean
	 * @see eu.ddmore.libpharmml.dom.modeldefn.IndividualParameter
	 */
	public boolean isIndividualParameter_(String symbol);
	
	/**
	 * Flag whether a conditional dose variable should be isolated from the locals
	 * list in a structural block
	 * return boolean
	 */
	public boolean isIsolateConditionalDoseVariable(); 
	
	/**
	 * Flag whether the converter should isolate globally scoped variables.
	 * @return boolean
	 */
	public boolean isIsolateGloballyScopedVariables();
	
	/**
	 * Flafg whether the NONMEM-style DT variable should be isolated from the variable list in a model.
	 * @return boolean
	 */
	public boolean isIsolatingDoseTimingVariable();
	
	/**
	 * Flag that a model is an NMLE.
	 * @return boolean
	 */
	public boolean isMixedEffect();
	
	/**
	 * Flag if named variable is in the global parameter model.
	 * @param name
	 * @return boolean
	 * @see eu.ddmore.libpharmml.dom.modeldefn.ParameterModel
	 */
	public boolean isModelParameter(String name);
	
	/**
	 * Check if parameter has an observation error model scope. 
	 * @param p Simple Parameter
	 * @return boolean
	 * @see eu.ddmore.libpharmml.dom.modeldefn.SimpleParameter
	 */
	public boolean isObservationParameter(PopulationParameter p);
	
	/**
	 * Check if symbol reference has an observation error model scope. 
	 * @param ref Simple Parameter
	 * @return boolean
	 * @see eu.ddmore.libpharmml.dom.commontypes.SymbolRef
	 */
	public boolean isObservationParameter(SymbolRef ref);
	
	/**
	 * Check if a converter permits an empty trial design block.
	 * @return boolean
	 */
	public boolean isPermitEmptyTrialDesignBlock();

	/**
	 * Flag that the converter should filter illegal characters from generated code.
	 * @return boolean
	 */
	public boolean isRemoveIllegalCharacters();
	
	/**
	 * Flag that the parser should write a persistence call for simulation output.<br/>
	 * No need to write output to file if just doing an estimation.
	 * @return boolean
	 */
	public boolean isSaveSimulationOutput();
	
	/**
	 * Flag if the named model symbol is a derivative (a.k.a State variable).
	 * @param symbol Variable name
	 * @return boolean 
	 * @see eu.ddmore.libpharmml.dom.commontypes.DerivativeVariable
	 */
	public boolean isStateVariable(String symbol);
	
	/**
	 * Flag if structural block experiences dosing.
	 * @param sb Structural block
	 * @return boolean
	 */
	public boolean isStructuralBlockWithDosing(StructuralBlock sb);
	
	/**
	 * Flag whether to translate the PK macros or 
	 * follow the route to generate scripts mapped directly on the Macros definitions.
	 * For example MATLAB/Octave would follow the translation route,
	 * MLXTrans however would use the Macro definitions directly.
	 * @return boolean
	 */
	public boolean isTranslate();
	
	/**
	 * flag if the loaded model is a Time-To-Event model (TTE).
	 * @return boolean
	 */
	public boolean isTTE();
	
	/**
	 * Flag whether the lexer wants to a Conditional Dose Event reference.<br/>
	 * This permits a dose variable to be associated with a 'global' type as found in 
	 * languages like R or MATLAB.
	 * @return boolean
	 * @see crx.converter.engine.common.ConditionalDoseEventRef
	 */
	public boolean isUseGlobalConditionalDoseVariable();
	
	/**
	 * Flag for converter to use global piecewise declarations as events.
	 * @return boolean
	 */
	public boolean isUsePiecewiseAsEvents();
	
	/**
	 * Load PharmML function library.
	 * @param f Path to the PharmML XML library path
	 * @return boolean`
	 */
	public boolean loadFunctionLibrary(File f);
	
	/**
	 * Permit ETA declaration in the observation model.
	 * If individual parameters declared in the observation model, remaps elements to the parameter block.
	 * @param decision
	 */
	public void permitObjectiveETAs(boolean decision);
	
	/**
	 * Instruct parser to add plotting block post a simulation block.
	 * @param addPlottingBlock Decision
	 */
	public void setAddPlottingBlock(boolean addPlottingBlock);
	
	/**
	 * Set the current structural block.
	 * @param sb The current structural block.
	 */
	public void setCurrentStructuralBlock(StructuralBlock sb);
	
	/**
	 * Select whether to deactivate the default identifier factory within libPharmML.<br/>
	 * If models are programmatically created directly with libPharmML, there is a tendency for
	 * libPharmML to produce an excess of superfluous warning messages during model creation.
	 * @param decision
	 * @see eu.ddmore.libpharmml.IPharmMLResource#setIdFactory
	 */
	public void setDeactivateIdFactory(boolean decision);
	
	/**
	 * Set the model referenced by a converter.<br/>
	 * Convenience method for unit testing.
	 * @param dom Model Handle
	 */
	public void setDom(PharmML dom);
	
	/**
	 * Set whether duplicated local variable declarations permitted in a structural block.
	 * @param decision Decision
	 */
	public void setDuplicateVariablesPermitted(boolean decision);
	
	/**
	 * Set whether converter should echo exceptions to STDERR.
	 * @param decision Decision
	 */
	public void setEchoException(boolean decision);
	
	/**
	 * Set whether the converter should filter the model for reserved words.
	 * @param decision Decision
	 */
	public void setFilterReservedWords(boolean decision);
	
	/**
	 * Set whether the converter should index arrays from zero.
	 * @param decision Decision
	 */
	public void setIndexFromZero(boolean decision);
	
	/**
	 * Decide whether a target variable for a conditional dose event
	 * should be kept in the local list of a Structural Block.
	 * @param decision Decision
	 */
	public void setIsolateConditionalDoseVariable(boolean decision);
	
	/**
	 * Set whether to isolate the dose timing (DT) variable from the local variable declaration list.<br/>
	 * The default setting is true.
	 */
	public void setIsolateDoseTimingVariable(boolean decision);
	
	/**
	 * Set whether the converter should isolate globally scoped variables
	 * @param decision Decision
	 */
	public void setIsolateGloballyScopedVariables(boolean decision);
	
	/**
	 * Flag that the converter should 'lex' the PharmML model but does not attempt to create any code.
	 * @param decision Decision
	 */
	public void setLexOnly(boolean decision);
	
	/**
	 * The load only settings instructs the converter to load/read a PharmML model, translate PK Macros if necessary
	 * and re-map dose targets. 
	 * The converter however will not however do any further processing associated with a target language conversion.
	 * Setting this to true will cause the converter to throw an run-time exception, which stops
	 * the converter dead when reading PharmML input from file.
	 * @param decision
	 */
	public void setLoadOnly(boolean decision);
	
	/**
	 * Set that model is a NLME.
	 * @param value Value
	 */
	public void setMixedEffect(boolean value);
	
	/**
	 * Bind a parser to a lexer instance.
	 * @param parser Parser instance
	 */
	public void setParser(IParser parser);
	
	/**
	 * Set whether converter ignores an empty trial design block.
	 * @param decision Decision
	 */
	public void setPermitEmptyTrialDesignBlock(boolean decision);
	
	/**
	 * Set whether converter should remove illegal characters from model variable declarations.<br/>
	 * Target specific features.
	 * @param decision  Decision
	 */
	public void setRemoveIllegalCharacters(boolean decision);
	
	/**
	 * Set the run identifier.<br/>
	 * If not set, the converter defaults to automatically created value.
	 * @param run_id_ Run Identifier
	 */
	public void setRunId(String run_id_);
	
	/**
	 * Instruct whether converter should save a list of renamed model variables in the generated code.
	 * @param decision
	 */
	public void setSaveRenamedSymbolList(boolean decision);
	
	/**
	 * Flag whether the converter should save output post a simulation.
	 * @param decision Decision
	 */
	public void setSaveSimulationOutput(boolean decision);
	
	/**
	 * Set the script definition associated with a converter.<br/>
	 * A script definition is a condensed list of model variables required for code generation.
	 * @param sd_ Script Definition.
	 */
	public void setScriptDefinition(ScriptDefinition sd_);
	
	/**
	 * Instruct the converter to sort all variables in the parameter model by dependency.
	 * @param decision
	 */
	public void setSortParameterModel(boolean decision);
	
	/**
	 * Sort the parameter model by declaration clustering as opposed to linear sorting based on dependency.
	 * If this property is set, it overrides the default sorting behaviour as specified by
	 * the other sortParameterModel() method.
	 * @param decision Decision
	 */
	public void setSortParameterModelByClustering(boolean decision);
	
	/**
	 * Sort parameter model based on parameter context.
	 * Oder if a parameter is a THETA or OMEGA etc.
	 * @param decision Decision
	 */
	public void setSortParameterModelByContext(boolean decision);
	
	/**
	 * Instruct the converter to sort all variables in the structural model by dependency.
	 * @param decision Decision
	 */
	public void setSortStructuralModel(boolean decision);
	
	/**
	 * Sort the parameter model by declaration clustering as opposed to linear sorting based on dependency.
	 * If this property is set, it overrides the default sorting behaviour as specified by
	 * the other sortStructuralModel() method.
	 * @param decision Decision
	 */
	public void setSortStructuralModelByClustering(boolean decision);
	
	/**
	 * Specify whether a converter should sort the declared variability levels on dependency.
	 * @param decision
	 */
	public void setSortVariabilityLevels(boolean decision);
	
	/**
	 * Set an internal state flag to make the converter die if given 'bad' XML.
	 * @param decision Decision
	 */
	public void setTerminateWithInvalidXML(boolean decision);
	
	/**
	 * Decide whether to translate PK Macros
	 * @param decision Decision
	 */
	public void setTranslate(boolean decision);
	
	/**
	 * Set the PK macro translator instance being used by a converter.
	 * @param tr PK Macro translator
	 */
	public void setTranslator(Translator tr);
	
	/**
	 * Set the Tree Maker class.<br/>
	 * Allows replacement of the default tree maker if need to change the code generation logic.
	 * @param tm Tree Maker instance.
	 */
	public void setTreeMaker(TreeMaker tm);
	
	/**
	 * Decide whether the lexer wants to use a Conditional Dose Event reference.<br/>
	 * This permits a dose variable to be associated with a 'global' type as found in 
	 * languages like R or MATLAB.
	 * @param decision Decision
	 * @see crx.converter.engine.common.ConditionalDoseEventRef
	 */
	public void setUseGlobalConditionalDoseVariable(boolean decision);
	
	/**
	 * Set Flag if converter to use global piecewise declarations as events.
	 * @param decision Decision.
	 */
	public void setUsePiecewiseAsEvents(boolean decision);
	
	/**
	 * State flag instruct the converter to validate an XML model prior code generation.
	 * @param decision Decision
	 */
	public void setValidateXML(boolean decision);
	
	/**
	 * Call to add nested tree references to the statement map if a collection is created by the tree maker.
	 * @see TreeMaker#getNestedTrees()
	 */
	public void updateNestedTrees(); 
	
	/**
	 * Flag whether a block linked to a converter should maintain a cached dependency list.
	 * If set to true, the block returns a cached and ordered variable list.
	 * If set to false, a new unordered declaration list is returned by the block.
	 * @return boolean
	 * @see crx.converter.spi.blocks.OrderableBlock
	 */
	public boolean useCachedDependencyList();
}
