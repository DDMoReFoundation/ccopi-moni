/*******************************************************************************
 * Copyright (C) 2015 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.engine.common;

import eu.ddmore.libpharmml.dom.commontypes.PharmMLRootType;

/**
 * Artifact is a script generated entity that needs to be saved to file.
 */
public class Artifact {
	public String independentVariable = null, outputVariable = null, samplingVariable = null;
	public PharmMLRootType element = null;
	
	/**
	 * Constructor
	 * @param independentVariable_ The independent variable associated with the artifact (if any).
	 * @param outputVariable_ The output variable name
	 * @param samplingVariable_ The sampling variable (if output variable is a time series).
	 */
	public Artifact(String independentVariable_, String outputVariable_, String samplingVariable_) {
		independentVariable = independentVariable_;
		outputVariable = outputVariable_;
		samplingVariable = samplingVariable_;
	}
	
	/**
	 * Flag if variable has a sampling array.
	 * @return boolean
	 */
	public boolean hasSamplingVariable() {
		return samplingVariable != null;
	}
	
	/**
	 * Flag if variable has an independent variable associated with it.
	 * @return boolean
	 */
	public boolean hasIndependentVariable() {
		return independentVariable != null;
	}
	
	@Override 
	public String toString() {
		String format = "Artifact: outputVariable=%s, independentVariable=%s, element=%s";
		return String.format(format, outputVariable, independentVariable, element);
	}
}
