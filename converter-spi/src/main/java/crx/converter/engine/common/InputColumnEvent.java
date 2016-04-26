/*******************************************************************************
 * Copyright (C) 2015 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.engine.common;

import static crx.converter.engine.PharmMLTypeChecker.isSymbolReference;
import crx.converter.engine.Accessor;
import crx.converter.spi.ILexer;
import eu.ddmore.libpharmml.dom.commontypes.PharmMLRootType;
import eu.ddmore.libpharmml.dom.commontypes.SymbolRef;
import eu.ddmore.libpharmml.dom.dataset.ColumnDefinition;
import eu.ddmore.libpharmml.dom.maths.Condition;
import eu.ddmore.libpharmml.dom.maths.ExpressionValue;
import eu.ddmore.libpharmml.dom.maths.Piece;

/**
 * A model event definition whose mapping to a model is defined by a value read from a flat file, e.g. a
 * data column in a CSV.
 */
public class InputColumnEvent {
	private  ExpressionValue assignment = null;
	private ColumnDefinition col = null;
	private int column_idx = -1;
	private String column_name = null;
	private Condition condition = null;
	private PharmMLRootType element = null;
	private SymbolRef ref = null;
	private Piece src = null;
	protected ILexer lexer = null;
	
	/**
	 * Constructor
	 * @param lexer_	Converter
	 * @param col_ Column Definition
	 * @param piece Mapping Rule to associate column to model element.
	 */
	public InputColumnEvent(ILexer lexer_, ColumnDefinition col_, Piece piece) {
		if (lexer_ == null || col_ == null || piece == null) throw new NullPointerException();
		
		lexer = lexer_; 
		
		col = col_;
		condition = piece.getCondition();
		assignment = piece.getValue();
		column_name = col.getColumnId();
		src = piece;
		
		Integer idx = col.getColumnNum();
		if (idx != null) column_idx = idx.intValue();
		
		// If value is a symbol reference, see if can get the model element.
		Object value = piece.getValue();
		if (value == null) throw new NullPointerException("The piece statement has no assignment statement.");
		if (isSymbolReference(value)) {
			ref = (SymbolRef) value;
			Accessor a = lexer.getAccessor();
			element = a.fetchElement(ref);
		}
	}
	
	/**
	 * Get the assignment tree.
	 * @return eu.ddmore.libpharmml.dom.maths.ExpressionValue
	 */
	public ExpressionValue getAssignment() { return assignment; }
	
	/**
	 * Get the bound column definition.
	 * @return eu.ddmore.libpharmml.dom.dataset.ColumnDefinition
	 */
	public ColumnDefinition getColumnDefinition() { return col; }
	
	/**
	 * Get the column name containing the dosing data.
	 * @return int Minus -1 if unassigned
	 */
	public int getColumnIndex() { return column_idx; }
	
	/**
	 * Get the column name containing the dosing data.
	 * @return java.lang.String
	 */
	public String getColumnName() { return column_name; }
	
	/**
	 * Get the condition for the dose event
	 * @return eu.ddmore.libpharmml.dom.maths.Condition
	 */
	public Condition getCondition() { return condition; }
	
	/**
	 * Get the source, that is parent element of the conditional dose event.
	 * This class is the result when the source element is processed.
	 * @return eu.ddmore.libpharmml.dom.maths.Piece
	 */
	public Piece getSource() { return src; }
	
	/**
	 * Get the model element (if any) mapped to an input column.
	 * Typically an unassigned variable definition awaiting assignment from a value
	 * read from a flat file.
	 * @return eu.ddmore.libpharmml.dom.commontypes.PharmMLRootType
	 */
	public PharmMLRootType getTargetElement() { return element; }
}
