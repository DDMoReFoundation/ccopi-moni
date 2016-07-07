/*******************************************************************************
 * Copyright (C) 2016 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.spi;

import java.util.List;

import crx.converter.spi.steps.OptimalDesignStep_;
import eu.ddmore.libpharmml.dom.commontypes.Symbol;

/**
 * Common methods required by an Optimal Design (OD) converter.
 */
public interface OptimalDesignLexer extends ILexer {
	/**
	 * Get a list of model elements listed in the declared error model outputs.
	 * @return List<Symbol>
	 */
	public List<Symbol> getContinuousOutputs();
	
	/**
	 * Get the lexed OD step bound to an PharmML model.
	 * @return OptimalDesignStep_
	 */
	public OptimalDesignStep_ getOptimalDesignStep();
}
