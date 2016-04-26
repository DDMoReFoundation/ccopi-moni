/*******************************************************************************
 * Copyright (C) 2015 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import crx.converter.spi.blocks.CovariateBlock;
import crx.converter.spi.blocks.ObservationBlock;
import crx.converter.spi.blocks.ParameterBlock;
import crx.converter.spi.blocks.StructuralBlock;
import crx.converter.spi.blocks.TrialDesignBlock;
import crx.converter.spi.blocks.VariabilityBlock;
import crx.converter.tree.BinaryTree;
import eu.ddmore.libpharmml.dom.commontypes.FunctionDefinition;

/**
 * Script Definition<br/>
 * Condensed lists of elements read from PharmML required to generate model code.
 *
 */
public class ScriptDefinition {
	private Map<String, Part> blocksMap = new HashMap<String, Part>();	
	private List<CovariateBlock> covariateBlocks = new ArrayList<CovariateBlock>();
	private List<FunctionDefinition> functions = new ArrayList<FunctionDefinition>();
	private List<ObservationBlock> observationBlocks = new ArrayList<ObservationBlock>();
	private List<ParameterBlock> parameterBlocks = new ArrayList<ParameterBlock>();
	private Map<Object, BinaryTree> statementsMap = new HashMap<Object, BinaryTree>();
	private Map<String, Part> stepsMap = new HashMap<String, Part>();
	private List<StructuralBlock> structuralBlocks = new ArrayList<StructuralBlock>();
	private TrialDesignBlock trialDesignBlock = null;
	private List<VariabilityBlock> variabilityBlocks = new ArrayList<VariabilityBlock>();

	/**
	 * Flush all the buffers and lists associated with a script definition.
	 */
	public void flushAllSymbols() {
		getParameterBlocks().clear();
		getStructuralBlocks().clear();
		getObservationBlocks().clear();
		getFunctions().clear();
		getStatementsMap().clear();
		getStepsMap().clear();
		getBlocksMap().clear();
		setTrialDesignBlock(null);
		getCovariateBlocks().clear();
		getVariabilityBlocks().clear();
	}
	
	/**
	 * Get a map of the 'named' blocks read from the PharmML model definition. 
	 * @return java.util.Map<String, Part>
	 */
	public Map<String, Part> getBlocksMap() {
		return blocksMap;
	}
	
	/**
	 * Get a list of the covariate blocks.
	 * @return java.util.List<CovariateBlock>
	 */
	public List<CovariateBlock> getCovariateBlocks() {
		return covariateBlocks;
	}
	
	/**
	 * A list of the function definitions.
	 * @return java.util.List<FunctionDefinition>
	 */
	public List<FunctionDefinition> getFunctions() {
		return functions;
	}
	
	/**
	 * A list of the observation blocks
	 * @return java.util.List<ObservationBlock>
	 */
	public List<ObservationBlock> getObservationBlocks() {
		return observationBlocks;
	}
	
	/**
	 * Get a list of parameters block in the model.
	 * @return java.util.List<ParameterBlock>
	 */
	public List<ParameterBlock> getParameterBlocks() {
		return parameterBlocks;
	}
	
	/**
	 * Get a tree map of AST mapped to model elements.
	 * @return java.util.Map<Object, BinaryTree>
	 */
	public Map<Object, BinaryTree> getStatementsMap() {
		return statementsMap;
	}
	
	/**
	 * Get a list of analysis steps associated with the current model. 
	 * @return java.util.Map<String, Part>
	 */
	public Map<String, Part> getStepsMap() {
		return stepsMap;
	}
	
	/**
	 * Get the index number of a structural block
	 * @param sb
	 * @return java.lang.Integer
	 */
	public Integer getStructuralBlockIndex(StructuralBlock sb) {
		Integer idx = -1;
		if (sb == null) return idx;
		
		int i = 0;
		for (StructuralBlock sb_ : structuralBlocks) {
			if (sb_ != null) {
				if (sb_.equals(sb)) {
					idx = i;
					break;
				}
			}
			i++;
		}
		
		return idx;
	}
	
	/**
	 * Get a list of all the structural blocks in the model.
	 * @return java.util.List<StructuralBlock>
	 */
	public List<StructuralBlock> getStructuralBlocks() {
		return structuralBlocks;
	}
	
	/**
	 * Get the trial design block associated with a model.
	 * @return TrialDesignBlock
	 */
	public TrialDesignBlock getTrialDesignBlock() {
		return trialDesignBlock;
	}
	
	/**
	 * Get a list of variability blocks associated with a model.
	 * @return java.util.List<VariabilityBlock>
	 */
	public List<VariabilityBlock> getVariabilityBlocks() {
		return variabilityBlocks;
	}
	
	/**
	 * Assign a block map to a script definition.
	 * @param blocksMap_ Block Map
	 */
	public void setBlocksMap(Map<String, Part> blocksMap_) {
		blocksMap = blocksMap_;
	}
	
	/**
	 * Assign a covariate map to a script definition.
	 * @param covariateBlocks_ Covariate Map
	 */
	public void setCovariateBlocks(List<CovariateBlock> covariateBlocks_) {
		covariateBlocks = covariateBlocks_;
	}
	
	/**
	 * Assign a function list to a script definition.
	 * @param functions_ Function List
	 */
	public void setFunctions(List<FunctionDefinition> functions_) {
		functions = functions_;
	}
	
	/**
	 * Assign set of observation blocks in the script definition.
	 * @param observationBlocks_ Observation Blocks
	 */
	public void setObservationBlocks(List<ObservationBlock> observationBlocks_) {
		observationBlocks = observationBlocks_;
	}
	
	/**
	 * List of parameter blocks.
	 * @param parameterBlocks_ List
	 */
	public void setParameterBlocks(List<ParameterBlock> parameterBlocks_) {
		parameterBlocks = parameterBlocks_;
	}
	
	/**
	 * Assign a state map to a script definition.
	 * @param statementsMap_ State map
	 */
	public void setStatementsMap(HashMap<Object, BinaryTree> statementsMap_) {
		statementsMap = statementsMap_;
	}
	
	/**
	 * Assign a step map to a script definition
	 * @param stepsMap_
	 */
	public void setStepsMap(HashMap<String, Part> stepsMap_) {
		stepsMap = stepsMap_;
	}
	
	/**
	 * Set the stuctural blocks associated with a script definition.
	 * @param structuralBlocks_
	 */
	public void setStructuralBlocks(List<StructuralBlock> structuralBlocks_) {
		structuralBlocks = structuralBlocks_;
	}
	
	/**
	 * Assign a trial design block to a script definition.
	 * @param trialDesignBlock_ TD
	 */
	public void setTrialDesignBlock(TrialDesignBlock trialDesignBlock_) {
		trialDesignBlock = trialDesignBlock_;
	}
	
	/**
	 * Set the variability blocks to a script definition.
	 * @param variabilityBlocks_ Variability Definition List.
	 */
	public void setVariabilityBlocks(List<VariabilityBlock> variabilityBlocks_) {
		variabilityBlocks = variabilityBlocks_;
	}
}
