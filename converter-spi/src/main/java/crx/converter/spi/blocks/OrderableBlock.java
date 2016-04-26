/*******************************************************************************
 * Copyright (C) 2016 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.spi.blocks;

import java.util.List;

import crx.converter.engine.assoc.Cluster;
import eu.ddmore.libpharmml.dom.commontypes.PharmMLElement;
import eu.ddmore.libpharmml.dom.commontypes.PharmMLRootType;

/**
 * Methods that allows blocks to maintain an ordered set of variables.
 */
public interface OrderableBlock {
	/**
	 * Add a declaration cluster to the Block.
	 * This record is for reference purposes only.
	 * @param cluster Cluster
	 * @return boolean 
	 */
	public boolean addCluster(Cluster cluster);
	
	/**
	 * Get a list of declaration clusters registered with a block.
	 * @return List<Cluster> 
	 */
	public List<Cluster> getClusters();
	
	/**
	 * All of the declared variables in a converter block.
	 * This method returns the cache declaration list if the variable order already assigned by a converter instance.
	 * @return java.util.List<PharmMLRootType>
	 */
	public List<PharmMLRootType> getListOfDeclarations();
	
	/**
	 * Set the ordered parameter list within the parameter block.<br/>
	 * This is set outside of the ParameterBlock, hence this accessor function.
	 * The input array would be that created by a dependency graph, which could contain variables.
	 * @param ordered_variables Ordered parameter List.
	 */
	public void setOrderedVariableList(List<PharmMLElement> ordered_variables);
}
