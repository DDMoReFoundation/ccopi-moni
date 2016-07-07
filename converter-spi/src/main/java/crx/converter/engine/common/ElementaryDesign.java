/*******************************************************************************
 * Copyright (C) 2016 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.engine.common;

import static crx.converter.engine.PharmMLTypeChecker.isSymbol;

import java.util.ArrayList;
import java.util.List;

import crx.converter.spi.blocks.ObservationBlock;
import eu.ddmore.libpharmml.dom.commontypes.PharmMLRootType;
import eu.ddmore.libpharmml.dom.commontypes.Symbol;

/**
 * An elementary design specifying sampling points for a study.
 */
public class ElementaryDesign {
	/**
	 * Study Arm that defined the sampling window.
	 */
	public String arm_oid = "unspecified";
	
	/**
	 * Continuous error model
	 */
	public ObservationBlock block = null;
	
	/**
	 * Maximum sampling time.
	 */
	public double max = 0.0;
	
	/**
	 * Minimum sampling time.
	 */
	public double min = 0.0;
	
	/**
	 * Window/Observation that act as the source for the sampling points 
	 */
	public String observation_oid = "unspecified";
	
	/**
	 * Model Element associated with the protocol  (Response variable).
	 */
	public PharmMLRootType output = null;
	
	/**
	 * Start time offset for the elementary design.
	 * Read from value bound to the source study arm if available. 
	 */
	public double start_time_offset = 0.0;
	
	/**
	 * Timepoint/sampling expressions as read from an observation.
	 */
	public List<PharmMLRootType> timepoint_expressions = new ArrayList<PharmMLRootType>();
	
	/**
	 * 
	 * @param block_ Error/Response Model
	 */
	public ElementaryDesign(ObservationBlock block_) {
		if (block_ == null) throw new NullPointerException("The error model block cannot be null");
		if (block_.getObservationError() == null) throw new IllegalStateException("Observation block does not have the expected continuous error model");
		block = block_;
	}
	
	/**
	 * Add a time point expression to the protocol
	 * @param o Timepoint expression
	 * @return boolean
	 */
	public boolean addTimepointExpression(PharmMLRootType o) {
		if (o != null) {
			if (!timepoint_expressions.contains(o)) {
				timepoint_expressions.add(o);
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		String format = "%s=%s,";
		
		sb.append("{");
		sb.append(String.format(format, "protocol", block.getName()));
		
		if (output != null) {
			if (isSymbol(output)) {
				Symbol symbol = (Symbol) output;
				sb.append(String.format(format, "output", symbol.getSymbId()));
			}
		}
		
		sb.append(String.format(format, "min", min));
		sb.append(String.format(format, "max", max));
		sb.append(String.format(format, "timepoint_expressions", timepoint_expressions));
		sb.append(String.format(format, "start_time_offset", start_time_offset));
		sb.append(String.format(format, "arm_oid", arm_oid));
		sb.append(String.format(format, "window_oid", observation_oid));
		sb.append("}");
		
		return sb.toString();
	}
}
