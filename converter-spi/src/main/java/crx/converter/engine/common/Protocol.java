/*******************************************************************************
 * Copyright (C) 2016 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.engine.common;

import java.util.ArrayList;
import java.util.List;

import crx.converter.spi.blocks.ObservationBlock;

/**
 * A protocol reference linking an error model to sampling windows.
 */
public class Protocol {
	/**
	 * Continuous error model
	 */
	public ObservationBlock block = null;
	
	/**
	 * List of sampling protocols for each elementary design.
	 */
	public List<ElementaryDesign> elementary_designs = new ArrayList<ElementaryDesign>();
	
	/**
	 * Protocol list index.
	 */
	public int index = 0;
	
	private double max = 0.0;
	private double min = 0.0;
	
	/**
	 * Constructor
	 * @param block_ Source Observation Block
	 * @param index_ Relative Index for single letter protocol labelling.
	 */
	public Protocol(ObservationBlock block_, int index_) {
		if (block_ == null) throw new NullPointerException("The error model block cannot be null");
		if (block_.getObservationError() == null) throw new IllegalStateException("Observation block does not have the expected continuous error model");
		if (index_ < 0) throw new IllegalStateException("The protocol index cannot be less than 0.");
		
		block = block_;
		index = index_;
	}
	
	/**
	 * Add an elementary design define the required time points to a protocol.
	 * @param protocol Protocol
	 * @return boolean
	 */
	public boolean addElementDesign(ElementaryDesign protocol) {
		if (protocol != null) {
			if (!elementary_designs.contains(protocol)) {
				elementary_designs.add(protocol);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Get the protocol label based on the current protocol list index.
	 * @return String
	 */
	public String getLabel() { return toAlphabetic(index); }
	
	/**
	 * Get the maximum sampling time bound to the protocol.
	 * @return double
	 */
	public double getMax() { return max; }
	
	/**
	 * Get the minimum sampling time bound to the protocol.
	 * @return double
	 */
	public double getMin() { return min; }
	
	/**
	 * Flag if the protocol has bound elementary designs.
	 * @return boolean
	 */
	public boolean hasElementaryDesigns() { return elementary_designs.size() > 0; }
	
	/**
	 * Set the maximum sampling time bound to the protocol.
	 * @param value
	 */
	public void setMax(double value) { if (value > max) max = value; }
	
	/**
	 * Set the maximum sampling time bound to the protocol.
	 * @param value
	 */
	public void setMin(double value) { if (value > min) min = value; }
	
	private String toAlphabetic(int i) {
	    if(i < 0) return "-"+ toAlphabetic(-i-1);
	    
	    int quot = i / 26;
	    int rem = i % 26;
	    char letter = (char)((int) 'A' + rem);
	    if (quot == 0) return ""+letter;
	    else return toAlphabetic(quot-1) + letter;
	}
	
	/**
	 * List of designs bound to the sampling protocol.
	 * @return List<ElementaryDesign>
	 */
	public List<ElementaryDesign> getElementaryDesigns() {return elementary_designs; }
}
