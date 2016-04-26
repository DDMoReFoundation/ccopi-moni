/*******************************************************************************
 * Copyright (C) 2016 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.engine.common;

import crx.converter.tree.BinaryTree;
import eu.ddmore.libpharmml.dom.commontypes.PharmMLRootType;
import eu.ddmore.libpharmml.dom.commontypes.SymbolRef;

/**
* An interpreted assignment statement declared in a simulation step.
*/
public class Assignment {
	/**
	 * Block id for the simulation scoped assignment.
	 */
	public String blkId = null;
	
	/**
	 * Binary tree statement.
	 */
	public BinaryTree bt = null;
	
	/**
	 * A cached assignment statement.
	 */
	public String cached_statement = null;
	
	/**
	 * Model element associated with the assignment statement.
	 */
	public PharmMLRootType element = null;
	
	public Assignment(String blkId_, PharmMLRootType element_, BinaryTree bt_) {
		if (blkId_ == null || element_ == null || bt_ == null) 
			throw new NullPointerException("Variable assignment cannot contains NULL members.");
		
		blkId = blkId_;
		element = element_;
		bt = bt_;
	}
	
	public Assignment(SymbolRef ref, PharmMLRootType element_, BinaryTree bt_) {
		if (ref == null || element_ == null || bt_ == null) 
			throw new NullPointerException("Variable assignment cannot contains NULL members.");
		if (ref.getBlkIdRef() == null) 
			throw new NullPointerException("BlkId for the symbol reference cannot be NULL");
		
		blkId = ref.getBlkIdRef();
		element = element_;
		bt = bt_;
	}
	
	@Override
	public String toString() {
		String format = "VA=blkId=%s, element=%s";
		return String.format(format, blkId, element);
	}
}
