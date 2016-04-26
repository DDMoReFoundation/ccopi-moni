/*******************************************************************************
 * Copyright (C) 2015 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.engine.common;

import eu.ddmore.libpharmml.dom.commontypes.SymbolRef;

/**
 * A symbol reference for a conditional dose entry read from an external file.
 */
public class ConditionalDoseEventRef extends SymbolRef {
	private ConditionalDoseEvent evt = null;
	private Object value = null;
	
	/**
	 * Constructor
	 * @param evt_	Conditional Dose Event
	 * @param symbId_ Column Definition
	 */
	public ConditionalDoseEventRef(ConditionalDoseEvent evt_, String symbId_) {
		super();
		
		if (evt_ == null) throw new NullPointerException("Event definition is NULL");
		if (symbId_ == null) throw new NullPointerException("Symbol identifier is NULL");
		
		evt = evt_;
		setSymbIdRef(symbId_);
	}
	
	/**
	 * Get the dose event definition
	 * @return ConditionalDoseEvent
	 */
	public ConditionalDoseEvent getEvent() { return evt; }
	
	/**
	 * Assign whatever value (number, string, level) that can be read from a quantity 
	 * read from an external flat file.
	 * @return java.lang.Object
	 */
	public Object getValue() { return value ; }
	
	/**
	 * Assign whatever value (number, string, level) that can be read from a quantity 
	 * read from an external flat file.
	 */
	public void setValue(Object value_) { value = value_; }
}
