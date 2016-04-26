/*******************************************************************************
 * Copyright (C) 2016 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.spi.blocks;

import java.util.List;

import crx.converter.engine.assoc.Cluster;
import crx.converter.engine.common.ParameterEvent;
import eu.ddmore.libpharmml.dom.commontypes.PharmMLElement;
import eu.ddmore.libpharmml.dom.commontypes.PharmMLRootType;
import eu.ddmore.libpharmml.dom.commontypes.SymbolRef;
import eu.ddmore.libpharmml.dom.modeldefn.ParameterModel;
import eu.ddmore.libpharmml.dom.modeldefn.ParameterRandomVariable;
import eu.ddmore.libpharmml.dom.modeldefn.PopulationParameter;

/**
 * A class representing the code for a parameter model.
 */
public interface ParameterBlock extends BaseRandomVariableBlock  {	
	/**
	 * Add a cluster to the parameter model.
	 * @param cluster
	 * @return boolean
	 */
	public boolean addCluster(Cluster cluster);
	
	/**
	 * Check if this parameter block contains this element.
	 * @param v Model Element
	 * @return boolean
	 */
	public boolean contains(PharmMLRootType v);
	
	/**
	 * Get the parameter assignment events linked to global parameter model.
	 * @return java.util.List<ParameterEvent>
	 * @see ParameterBlock#hasEvents()
	 */
	public List<ParameterEvent> getEvents();
	
	/**
	 * All of the declared variables in the parameter model.
	 * This method returns the cache declaration list if the variable order already assigned by a converter instance.
	 * @return List<PharmMLRootType>
	 */
	public List<PharmMLRootType> getListOfDeclarations();
	
	/**
	 * Get the parameter model.
	 * @return eu.ddmore.libpharmml.dom.modeldefn.ParameterModel
	 */
	public ParameterModel getModel();
	
	/**
	 * Get the index of a parameter in the numeric parameter array passed to a model function.
	 * @param name Parameter Name
	 * @return java.lang.Integer
	 */
	public Integer getParameterIndex(String name);
 	
	/**
	 * Get the index of a parameter in the numeric parameter array passed to a model function.
	 * @param ref Reference to the parameter
	 * @return java.lang.Integer
	 */
	public Integer getParameterIndex(SymbolRef ref);

	/**
	 * Get a list of numeric parameters.
	 * @return java.util.List<eu.ddmore.libpharmml.dom.modeldefn.PopulationParameter>
	 */
	public List<PopulationParameter> getParameters();

	/**
	 * Get a list of random variables.
	 * @return java.util.List<eu.ddmore.libpharmml.dom.modeldefn.ParameterRandomVariable>
	 */
	public List<ParameterRandomVariable> getRandomVariables();
	
	/**
	 * Flag if the parameter model has events.
	 * Event is an piecewise assignment cued on the IDV or a derivative.
	 * @return boolean
	 * @see ParameterEvent
	 */
	public boolean hasEvents();
	
	/**
	 * Flag if the parameter model has matrix assigned parameters.
	 * @return boolean
	 */
	public boolean hasMatrixAssignedParameters();
		
	@Override
	public boolean hasSymbolId(String name);
	
	/**
	 * Flag if a random variable is correlated, i.e. linked.<br/>
	 * Acts as a filter flag to avoid duplicated random variable assignment blocks.
	 * @param rv
	 * @return boolean
	 */
	public boolean isLinkedRandomVariable(ParameterRandomVariable rv);

	/**
	 * Set the ordered parameter list within the parameter block.<br/>
	 * This is set outside of the ParameterBlock, hence this accessor function.
	 * @param ordered_variables Ordered parameter List.
	 */
	public void setOrderedVariableList(List<PharmMLElement> ordered_variables);
}
