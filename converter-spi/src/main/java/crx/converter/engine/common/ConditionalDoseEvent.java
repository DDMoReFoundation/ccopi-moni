/*******************************************************************************
 * Copyright (C) 2015 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.engine.common;

import crx.converter.spi.ILexer;
import eu.ddmore.libpharmml.dom.dataset.ColumnDefinition;
import eu.ddmore.libpharmml.dom.maths.Piece;

/**
 * Dose event definition whose mapping to a model is defined by a value read from a flat file.
 */
public class ConditionalDoseEvent extends InputColumnEvent {
	public ConditionalDoseEvent(ILexer lexer, ColumnDefinition col_, Piece piece) { 
		super(lexer, col_, piece);
	}
}
