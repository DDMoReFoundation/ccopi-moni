/*******************************************************************************
* Copyright (C) 2015 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.engine;

import eu.ddmore.libpharmml.dom.commontypes.PharmMLRootType;

/**
 * A reference type to discrete model category datatype.
 * This class is placed in the base package so that 
 * this data type is available to Accessor to retrieve a data symbol via
 * a symbol reference in a PharmML model.
 * @see Accessor
 * @see eu.ddmore.libpharmml.dom.modeldefn.Discrete#getCategoricalData()
 */
public class CategoryRef_ extends PharmMLRootType {
	private String blkId;
	private String dataSymbol = null;
	private PharmMLRootType element = null; 
	private String modelSymbol = null;
	
	/**
	 * Construct a category reference to a model symbol represented by a data element read from an
	 * external data file.
	 * 
	 * @param blkId_ Category Variable Block (The source observation model).
	 * @param modelSymbol_ Model Symbol (Symbol Identifier)
	 * @param dataSymbol_ Data Symbol (Value read from an External Data File).
	 * @see eu.ddmore.libpharmml.dom.modeldefn.ObservationModel
	 * @see eu.ddmore.libpharmml.dom.dataset.ExternalFile
	 */
	public CategoryRef_(String blkId_, String modelSymbol_, String dataSymbol_) {
		if (modelSymbol_ == null || dataSymbol_ == null) throw new NullPointerException("Category reference map entry cannot be NULL.");
		
		blkId = blkId_;
		modelSymbol = modelSymbol_;
		dataSymbol = dataSymbol_; 
	}
	
	/**
	 * Get the block identifier
	 * @return java.lang.String
	 */
	public String getBlkId() { return blkId; }
	
	/**
	 * Get the data symbol as represented by a model symbol.
	 * @return java.lang.String
	 */
	public String getDataSymbol() { return dataSymbol; }
	
	/**
	 * Access the model element utilising the category reference.
	 * @return PharmMLRootType
	 */
	public PharmMLRootType getElement() { return element; }
	
	/**
	 * Get the model symbol.
	 * @return java.lang.String
	 */
	public String getModelSymbol() { return modelSymbol; }
	
	/**
	 * Check the block identifier is same.
	 * @param blkId_
	 * @return boolean
	 */
	public boolean isBlkId(String blkId_) {
		if (blkId != null) return blkId.equals(blkId);
		return false;
	}
	
	/**
	 * Check the block identifier is same.
	 * @param symbId
	 * @return boolean
	 */
	public boolean isSymbId(String symbId) { return modelSymbol.equals(symbId); }
	
	/**
	 * Assign the model element utilising the category reference.
	 */
	public void setElement(PharmMLRootType element_) { element = element_; }
	
	@Override
	public String toString() {
		String format = "blkId=%s, symbId=%s, dataSymbol=%s";
		return String.format(format, blkId, modelSymbol, dataSymbol);
	}
}
