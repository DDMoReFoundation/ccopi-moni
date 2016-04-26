/*******************************************************************************
 * Copyright (C) 2015 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.tree;

/**
 * A nested tree reference.
 */
public class NestedTreeRef {
	/**
	 * Binary reference of the model element. 
	 */
	public BinaryTree bt = null;
	
	/**
	 * Model element associated with a nested tree reference.
	 */
	public Object element = null;
	
	/**
	 * Constructor
	 * @param element_ Model element
	 * @param bt_ Binary tree representing the model element.
	 */
	public NestedTreeRef(Object element_, BinaryTree bt_) {
		if (element_ == null) throw new NullPointerException("The element is NULL.");
		if (bt_ == null) throw new NullPointerException("The binary tree is NULL.");
		
		element = element_;
		bt = bt_;
	}
	
	@Override
	public String toString() {
		String format = "element=%s, Tree=%s";
		return String.format(format, element, bt);
	}
}
