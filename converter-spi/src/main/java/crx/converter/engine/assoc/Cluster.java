/*******************************************************************************
 * Copyright (C) 2016 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.engine.assoc;

import java.io.IOException;
import java.util.List;

import eu.ddmore.libpharmml.dom.commontypes.PharmMLElement;

/**
 * A container class representing a cluster of declarations linked by unary or binary dependencies.
 */
public interface Cluster {
	/**
	 * Add a model element to a declaration cluster.
	 * @param o Model Element
	 * @return boolean
	 */
	public boolean add(PharmMLElement o);
	
	/**
	 * Check if a declaration cluster contains a model element.
	 * @param o Model Element
	 * @return boolean
	 */
	public boolean contains(Object o);
	
	/**
	 * Get list of elements.
	 * @return List<PharmMLElement>
	 */
	public List<PharmMLElement> getElements();
	
	/**
	 * Merge the other cluster contents with the current cluster content.
	 * @param other Other Cluster
	 * @return boolean
	 */
	public boolean merge(Cluster other);
	
	/**
	 * Size of the association cluster 
	 * @return int
	 */
	public int size();
	
	/**
	 * Sort the variables in the declaration cluster based on linear dependency.
	 * @return List<PharmMLElement>
	 * @throws IOException 
	 * @throws NullPointerException 
	 */
	public List<PharmMLElement> sort() throws NullPointerException, IOException;
}
