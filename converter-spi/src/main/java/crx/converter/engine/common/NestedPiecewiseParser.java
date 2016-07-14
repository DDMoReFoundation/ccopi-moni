/*******************************************************************************
 * Copyright (C) 2016 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.engine.common;

import java.io.IOException;

import crx.converter.tree.BinaryTree;
import crx.converter.tree.TreeMaker;
import eu.ddmore.libpharmml.dom.maths.Piece;
import eu.ddmore.libpharmml.dom.maths.Piecewise;

/**
 * Base Parser with utility functions for the processing of nested piecewise statements.
 */
public abstract class NestedPiecewiseParser extends BaseParser {
	protected static final String fake_symbol = "_FAKE_FAKE_FAKE_FAKE_";
	private int current_indent = 0;
	
	/**
	 * Constructor
	 * @throws IOException
	 */
	public NestedPiecewiseParser() throws IOException {
		super();
	}
	
	/**
	 * Increment the current index
	 */
	public void decrementPiecewiseIndent() { current_indent--; }
	
	/**
	 * Process a piece block explicitly
	 * @param piece Piece block
	 * @return String
	 */
	protected String doPiece(Piece piece) {
		TreeMaker tm = lexer.getTreeMaker();
		BinaryTree bt = tm.newInstance(piece.getValue());
		lexer.updateNestedTrees();
		return parse(new Object(), bt).trim();
	}
	
	/**
	 * Generic code event handler for parsing nested piecewise statements.
	 * @param pw Piecewise Block
	 * @return String
	 */
	protected String doPiecewise(Piecewise pw) { return comment_char + "Piecewise assignment block.\n"; }
	
	/**
	 * Get the required indent for logical syntax in an nested piecewise.
	 * @return String
	 */
	public int getAssignmentStatementIndent() { return current_indent + 1; }
	
	/**
	 * Write a basic assignment statement based on the model element specified as 
	 * the piecewise assignment context.
	 * @param element Model element
	 * @param value Default Value
	 * @see NestedPiecewiseParser#setCurrentPiecewiseContext(Object)
	 */
	abstract public String getDefaultPiecewiseAssignmentValue(Object element, Object value);
	
	/**
	 * Get the required indent for logical syntax in an nested piecewise.
	 * @return String
	 */
	public int getLogicalStatementIndent() {
		return current_indent;
	}
	
	/**
	 * Get the current piecewise increment.
	 * @return int
	 */
	public int getPiecewiseIndent() { return current_indent; }
	
	/**
	 * Increment the current index
	 */
	public void incrementPiecewiseIndent() { current_indent++; }
	
	/**
	 * Get the indentation linked to a indentation level number
	 * @param level Indentation Level
	 * @return String
	 */
	abstract public String indent(int level); 
}
