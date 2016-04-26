/*******************************************************************************
 * Copyright (C) 2015 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.engine;

/**
 * Simple class to supply numeric identifiers to a converter.
 */
public class Manager {
	private boolean fixedRunId = false;
	private long run_id = 0;
	
	/**
	 * Convenience method to create a run identifier.
	 * @return java.lang.String
	 */
	public String generateRunId() {
		if (!fixedRunId) {
			String id = "run_id_" + Long.toString(run_id);
			run_id++;
			return id;
		} else {
			return "run";
		}
	}
	
	/**
	 * Flag whether the manager is using a fixed run identifier.
	 * @return boolean
	 */
	public boolean isFixedRunId() {
		return fixedRunId; 
	}
	
	/**
	 * Resets the run identifier back to zero.
	 */
	public void resetRunId() {
		run_id = 0;
	}
	
	/**
	 * Create identifiers with a numeric component.
	 * @param fixedRunId_ Decision
	 */
	public void setFixedRunId(boolean fixedRunId_) {
		fixedRunId = fixedRunId_;
	}
	
	/**
	 * Set the number start generating identifiers.
	 * @param run_id_ Number
	 */
	public void setRunId(long run_id_) { run_id = run_id_; }
}
