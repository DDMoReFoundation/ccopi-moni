/*******************************************************************************
 * Copyright (C) 2015 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.engine.common;

import java.util.ArrayList;
import java.util.List;

import eu.ddmore.libpharmml.dom.trialdesign.ExternalDataSet;

/**
 * A container class designed to create a list of data files associated with a PharmML model.
 * 
 */
public class DataFiles {
	private List<ExternalDataSet> externalDataSets = new ArrayList<ExternalDataSet>();
	
	/**
	 * Get a list of External data set references as read from the PharmML model.
	 * @return java.util.List<eu.ddmore.libpharmml.dom.modellingsteps.ExternalDataSet>
	 */
	public List<ExternalDataSet> getExternalDataSets() {
		return externalDataSets;
	}
	
	/**
	 * Set a list of External data set references as read from the PharmML model.
	 */
	public void setExternalDataSets(List<ExternalDataSet> externalDataSets_) {
		externalDataSets = externalDataSets_;
	}
}
