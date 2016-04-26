/*******************************************************************************
 * Copyright (C) 2015 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.spi.blocks;

import java.util.List;

import crx.converter.engine.Part;
import eu.ddmore.libpharmml.dom.modeldefn.ParameterRandomVariable;
import eu.ddmore.libpharmml.dom.modeldefn.VariabilityDefnBlock;

/**
 * Wrapper class for a PharmML variability block.
 */
public interface VariabilityBlock extends Part {
	/**
	 * See if a putative child level is support by a parent level.
	 * @param levelChild Child Level
	 * @param levelParent Parent Level
	 * @return boolean
	 */
	public boolean dependsUpon(String levelChild, String levelParent);
	
	/**
	 * Get the levels declared in the variability scope.
	 * @return List<String>
	 */
	public List<String> getLevels();
	
	/**
	 * Get the source model element.
	 * @return eu.ddmore.libpharmml.dom.modeldefn.VariabilityDefnBlock
	 */
	public VariabilityDefnBlock getModel();
	
	/**
	 * Check if a random variable has scope in the variability block.
	 * @param rv Random variable
	 * @return boolean
	 */
	public boolean hasScope(ParameterRandomVariable rv);
	
	/**
	 * Check if a random variable has scope in the variability block for a specific level.
	 * @param level Level Name
	 * @param rv Random variable
	 * @return boolean
	 */
	public boolean hasScope(String level, ParameterRandomVariable rv);
	
	/**
	 * Flag if block linked to parameter variability scope, i.e. individual random things.
	 * @return boolean
	 */
	public boolean isParameterVariability();
	

	/**
	 * Flag if block linked to residual scope, i.e. linked to time series error model.
	 * @return boolean
	 */
	public boolean isResidualError();
	
	/**
	 * Read the level/scoping string bound to a random variable.
	 * @param rv Random Variable
	 * @return String Level String
	 */
	public String readLevel(ParameterRandomVariable rv);
}
