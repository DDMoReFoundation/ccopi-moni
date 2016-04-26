/*******************************************************************************
 * Copyright (C) 2015 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.engine;

import static crx.converter.engine.PharmMLTypeChecker.isCommonParameter;
import static crx.converter.engine.PharmMLTypeChecker.isCommonVariable;
import eu.ddmore.libpharmml.dom.PharmML;
import eu.ddmore.libpharmml.dom.commontypes.AnnotationType;
import eu.ddmore.libpharmml.dom.commontypes.CommonVariableDefinition;
import eu.ddmore.libpharmml.dom.commontypes.PharmMLRootType;
import eu.ddmore.libpharmml.dom.modeldefn.CommonParameter;

/**
 * Converter Engine class.
 *
 */
public abstract class Engine extends BaseEngine {
	/**
	 * PharmML model handle.
	 */
	protected PharmML dom = null;
	
	public Engine() {
		setReferenceClass(getClass());
		init();
	}
		
	/**
	 * Get the DOM (a.k.a.) model bound to a converter.
	 * @return eu.ddmore.libpharmml.dom.PharmML
	 */
	public PharmML getDom() { return dom; }
	
	/**
	 * Read the description type of a model element.
	 * @param v
	 * @return java.lang.String
	 */
	protected String readDescription(PharmMLRootType v) {
		String description = "";
		if (v != null) {
			AnnotationType desc = v.getDescription();
			if (desc != null) description = desc.getValue();
		}
		
		return description;
	}
	
	/**
	 * Read a symbol identifier from a common model type.<br/>
	 * This function should not be confused with the SymbolReader, which turns model names 
	 * into language-specific friendly form.
	 * @param element Common Model element
	 * @return java.lang.String Element Symbol Identifier or NULL
	 * @see eu.ddmore.libpharmml.dom.commontypes.CommonVariableDefinition#getSymbId()
	 */
	protected String readSymbolIdentifier(PharmMLRootType element) {
		if (element == null) return null;
		else if (isCommonVariable(element)) return ((CommonVariableDefinition) element).getSymbId();
		else if (isCommonParameter(element)) return ((CommonParameter) element).getSymbId();
		
		return null;
	}
}
