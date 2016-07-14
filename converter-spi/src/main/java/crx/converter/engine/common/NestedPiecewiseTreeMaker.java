/*******************************************************************************
 * Copyright (C) 2016 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.engine.common;

import crx.converter.tree.BaseTreeMaker;
import crx.converter.tree.BinaryTree;
import eu.ddmore.libpharmml.dom.maths.Piece;

/**
 * Nested piecewise TreeMaker.
 * Support for nested piecewise conditional isolated in this class so 
 * not to  perturb language converters that support only single level piecewise conditionals.
 */
public class NestedPiecewiseTreeMaker extends BaseTreeMaker {
	/**
	 * Constructor
	 */
	public NestedPiecewiseTreeMaker() { super(); }
	
	/**
	 * Create a binary tree of an conditional piecewise section.
	 * Overrides superclass methods to permit language support of nested piecewise conditionals and statements.
	 * @param p Piecewise section
	 * @return BinaryTree
	 */
	protected BinaryTree createTree(Piece p) {
		if (p == null) throw new NullPointerException("The conditional piece cannot be NULL");
		if (p.getPiecewise() != null) {
			return createTree(p.getPiecewise());
		}
		else return super.createTree(p);
	}
}