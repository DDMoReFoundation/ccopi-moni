/*******************************************************************************
 * Copyright (C) 2016 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.engine.common;

import eu.ddmore.libpharmml.dom.commontypes.DerivativeVariable;
import eu.ddmore.libpharmml.dom.commontypes.PharmMLRootType;
import eu.ddmore.libpharmml.dom.commontypes.Symbol;

/**
 * A reference type to a derivative.
 * Used to differentiate the derivative type so that a state vector 
 * can be referenced in a simulation event.
 */
public class StateVariableRef extends PharmMLRootType implements Symbol {
	private DerivativeVariable dv = null;
	private String symbId = null;
	
	public StateVariableRef(DerivativeVariable dv_) {
		if (dv_ == null) throw new NullPointerException("The derivative in a state variable cannot be NULL.");
		dv = dv_;
		symbId = dv.getSymbId();
		if (symbId == null) throw new NullPointerException("The state variable's symbol ID cannot be NULL.");
	}
	
	/**
	 * Get referenced derivative variable.
	 * @return DerivativeVariable
	 */
	public DerivativeVariable getDerivative() { return dv; }
	
	/**
	 * Get the symbol identifier as read from the source derivative
	 * @return java.lang.String
	 * @see eu.ddmore.libpharmml.dom.commontypes.DerivativeVariable#getSymbId()
	 */
	public String getSymbId() { return symbId; }
	
	@Override
	public void setSymbId(String value) {
		if (value != null) symbId = value;
	}

	@Override
	public String toString() { return symbId; } 
}