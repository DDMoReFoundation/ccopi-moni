/*******************************************************************************
 * Copyright (C) 2016 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.spi;

import crx.converter.spi.steps.OptimalDesignStep_;

/**
 * Common methods required by an Optimal Design (OD) converter.
 */
public interface OptimalDesignLexer extends ILexer {
	/**
	 * Get the lexed OD step bound to an PharmML model.
	 * @return OptimalDesignStep_
	 */
	public OptimalDesignStep_ getOptimalDesignStep();
}
