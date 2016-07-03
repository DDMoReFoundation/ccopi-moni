/*******************************************************************************
 * Copyright (C) 2016 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.spi;

import java.util.Map;

import eu.ddmore.libpharmml.dom.commontypes.PharmMLRootType;
import eu.ddmore.libpharmml.dom.modellingsteps.Estimation;
import eu.ddmore.libpharmml.dom.modellingsteps.OperationProperty;
import eu.ddmore.libpharmml.dom.modellingsteps.OptimalDesignStep;
import eu.ddmore.libpharmml.dom.modellingsteps.Simulation;

/**
 * Set of basic methods to read a batch of settings bound to a modelling step.<br/>
 * Using an interface as every language is different.
 */
public interface ISettingReader {
	/**
	 * Named property map.
	 * @return Map<String, OperationProperty>
	 */
	public Map<String, OperationProperty> getPropertyMap();
	
	/**
	 * Get a named setting parsed to the target value.
	 * @param name
	 * @return String
	 */
	public String getValue(String name);
	
	/**
	 * Flag that the reader has successfully settings for the target language. 
	 * @return boolean
	 */
	public boolean hasReadSettings();
	
	/**
	 * Check if the settings reader has a value.
	 * @param name
	 * @return boolean
	 */
	public boolean hasValue(String name);
	
	/**
	 * Read all settings so PharmML values are converted to the target language,
	 */
	public void readSettings();
	
	/**
	 * Converter/Lexer handle for the target language.
	 * @param c Converter Handle
	 */
	public void setLexer(ILexer c);
	
	/**
	 * Parser to generate settings code for a target language.
	 * @param p Parser handle
	 */
	public void setParser(IParser p);
	
	/**
	 * Set the PharmML step to read settings from.
	 * @param step Modelling Step
	 * @see Estimation
	 * @see Simulation 
	 * @see OptimalDesignStep
	 */
	public void setStep(PharmMLRootType step);
}
