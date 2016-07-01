/*******************************************************************************
 * Copyright (C) 2016 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.engine.common;

import eu.ddmore.libpharmml.dom.commontypes.OidRef;

/**
 * Reference to an intervention sequence.
 */
public class InterventionSequenceRef {
	/**
	 * Administration identifier
	 */
	public String administration_oid = null;
	
	/**
	 * Intervention start time.
	 */
	public double start = 0.0;
	
	/**
	 * Constructor
	 * @param admin_ref OID of an Administration
	 * @param start_ Sequence Start Time
	 */
	public InterventionSequenceRef(OidRef admin_ref, double start_) {
		if (admin_ref == null) throw new NullPointerException("OID Reference is NULL");
		administration_oid = admin_ref.getOidRef();
		if (administration_oid == null) throw new NullPointerException("OID is NULL");
		start = start_;
	}
}
