/*******************************************************************************
 * Copyright (C) 2015 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.tree;

import java.util.List;

import crx.converter.engine.Accessor;

/**
 * Base TreeMaker.
 * Focuses exclusively on building trees for core PharmML data types.
 */
public interface TreeMaker {	
	/**
	 * Get the nested trees created by AST creation for a complex statements.
	 * @return java.util.List<NestedTreeReference>
	 */
	public List<NestedTreeRef> getNestedTrees();
	
	/**
	 * Flag if tree maker permits declaration only variables
	 * @return boolean
	 */
	public boolean isPermitDeclarationOnlyVariables();
	
	/**
	 * Flag if tree maker permits parameter tree creation with an assignment statement.
	 * @return boolean
	 */
	public boolean isPermitParameterWithoutAssignment();
	
	/**
	 * Convert a source object into a binary tree.
	 * @param o Source Object
	 * @return BinaryTree
	 * @throws UnsupportedOperationException If source object cannot be converted to AST.
	 */
	public BinaryTree newInstance(Object o);
	
	/**
	 * Set the accessor instance bound to the Tree Maker.
	 * @param a_
	 */
	public void setAccessor(Accessor a_);

	/**
	 * Set the default parameter value if parameter has no assignment statement.
	 * @param value Default value
	 */
	public void setDefaultParameterValue(double value);
	
	/**
	 * Instruct whether the TreeMaker should flush the nested tree buffer at each call of newInstance().
	 * @param decision Decision
	 */
	public void setFlushNestedTreeReferences(boolean decision);
	
	/**
	 * Instruct the tree maker to allow declaration only binary trees.
	 * @param decision Decision
	 */
	public void setPermitDeclarationOnlyVariables(boolean decision);
	
	/**
	 * Set whether tree maker can create parameter trees with assignment block.
	 * @param decision Decision
	 */
	public void setPermitParameterWithoutAssignment(boolean decision);
}
