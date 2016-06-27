/*******************************************************************************
 * Copyright (C) 2016 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.engine.assoc;

import static crx.converter.engine.PharmMLTypeChecker.isBinaryOperation;
import static crx.converter.engine.PharmMLTypeChecker.isContinuousValue;
import static crx.converter.engine.PharmMLTypeChecker.isIndividualParameter;
import static crx.converter.engine.PharmMLTypeChecker.isInt;
import static crx.converter.engine.PharmMLTypeChecker.isNormalDistribution;
import static crx.converter.engine.PharmMLTypeChecker.isPiecewise;
import static crx.converter.engine.PharmMLTypeChecker.isProbOnto;
import static crx.converter.engine.PharmMLTypeChecker.isReal;
import static crx.converter.engine.PharmMLTypeChecker.isSymbolReference;
import static crx.converter.engine.PharmMLTypeChecker.isUnaryOperation;
import static crx.converter.engine.PharmMLTypeChecker.isVariableReference;
import static crx.converter.engine.PharmMLTypeChecker.isVector;
import static crx.converter.engine.PharmMLTypeChecker.isVectorSelector;
import static crx.converter.engine.Utils.getClassName;

import java.util.List;

import crx.converter.engine.Accessor;
import crx.converter.engine.BaseEngine;
import crx.converter.engine.FixedEffectCategoryRef;
import crx.converter.spi.ILexer;
import crx.converter.tree.BinaryTree;
import crx.converter.tree.NestedTreeRef;
import crx.converter.tree.Node;
import crx.converter.tree.TreeMaker;
import eu.ddmore.libpharmml.dom.commontypes.PharmMLRootType;
import eu.ddmore.libpharmml.dom.commontypes.SymbolRef;
import eu.ddmore.libpharmml.dom.uncertml.ContinuousValueType;
import eu.ddmore.libpharmml.dom.uncertml.VarRefType;

/**
 * Basic dependency methods for a model lexer.
 */
public abstract class DependencyLexer extends BaseEngine implements ILexer {
	/**
	 * Accessor handle
	 */
	protected Accessor accessor = null;
	
	/**
	 * TreeMaker handle
	 */
	protected TreeMaker tm = null;
	
	/**
	 * Find a dependency for a model element from a binary tree of another model element.
	 * @param ref The model element looking for dependencies.
	 * @param bt Binary Tree representation of another model element.
	 */
	public void addDependency(DependencyRef ref, BinaryTree bt) {
		for (Node node : bt.nodes) {
			Object e = node.data; 
			if (isReal(e) || isNormalDistribution(e) || isInt(e) || 
				e instanceof FixedEffectCategoryRef || 
				isIndividualParameter(e) || isBinaryOperation(e) || isUnaryOperation(e)
				|| isProbOnto(e) || isVector(e) || isVectorSelector(e)) 
				continue;
			else if (isContinuousValue(e)) {
				ContinuousValueType value = (ContinuousValueType) e;
				if (value.getVar() != null) { 
					PharmMLRootType element = accessor.fetchElement(value.getVar());
					if (element != null) ref.addDependency(element);
				}
			} else if (isSymbolReference(e)) {
				SymbolRef sref = (SymbolRef) e;
				PharmMLRootType element = accessor.fetchElement(sref);
				if (element != null) ref.addDependency(element);
			} else if (isVariableReference(e)) {
				VarRefType vref = (VarRefType) e;
				PharmMLRootType element = accessor.fetchElement(vref);
				if (element != null) ref.addDependency(element);
			} else if (isPiecewise(e)) {
				tm.newInstance(e);
				List<NestedTreeRef> ntrefs = tm.getNestedTrees();
	 			for (NestedTreeRef ntref : ntrefs) addDependency(ref, ntref.bt);
			} 
			else 
				throw new UnsupportedOperationException("Not recognised dependency class (name='" + getClassName(e) + "')");
		}
	}
}
