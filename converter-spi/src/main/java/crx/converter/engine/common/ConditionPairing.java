/*******************************************************************************
 * Copyright (C) 2015 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.engine.common;

import eu.ddmore.libpharmml.dom.commontypes.PharmMLElement;

/**
 * Condition pairing as defined by a probability assignment.
 */
public class ConditionPairing {
	public PharmMLElement lhs = null, rhs = null;
	
	public ConditionPairing(PharmMLElement lhs_, PharmMLElement rhs_) {
		if (lhs_ == null) throw new NullPointerException("Category element is NULL");
		if (rhs_ == null) throw new NullPointerException("Condition element is NULL");
		
		lhs = lhs_;
		rhs = rhs_;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		sb.append("[lhs=");
		if (lhs != null) sb.append(lhs);
		sb.append(",");
		
		sb.append("rhs=");
		if (rhs != null) sb.append(rhs);
		sb.append("]");
		
		return sb.toString();
	}
}
