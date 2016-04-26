/*******************************************************************************
 * Copyright (C) 2015 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.engine.common;

import static crx.converter.engine.PharmMLTypeChecker.isColumnReference;
import crx.converter.spi.ILexer;
import crx.converter.tree.BinaryTree;
import crx.converter.tree.Node;
import eu.ddmore.libpharmml.dom.dataset.ColumnDefinition;
import eu.ddmore.libpharmml.dom.dataset.ColumnReference;
import eu.ddmore.libpharmml.dom.maths.Piece;

/**
 * A dose event definition via an IDV/TIME column.
 * Message carrier to specify the AMT column to a converter engine.
 */
public class TemporalDoseEvent extends InputColumnEvent {
	private String AMT_col = null;
	
	/**
	 * Constructor
	 * @param lexer Converter Handle
	 * @param col_ Column Definition
	 * @param piece_ Logical Definition of the AMT mapping
	 */
	public TemporalDoseEvent(ILexer lexer, ColumnDefinition col_, Piece piece_) {
		super(lexer, col_, piece_);
		
		Piece piece = getSource();
		BinaryTree bt = lexer.getTreeMaker().newInstance(piece.getCondition());
		for (Node node : bt.nodes) {
			if (isColumnReference(node.data)) {
				ColumnReference cref = (ColumnReference) node.data;
				if (cref != null) AMT_col = cref.getColumnIdRef();
			}
		}
	}
	
	/**
	 * Return the AMT column name.
	 * @return java.lang.String
	 */
	public String getAMTColumnName() { return AMT_col; }
	
	/**
	 * Check to see if specified column name is the AMT column.
	 * @param colName
	 * @return boolean
	 */
	public boolean isAMTColumn(String colName) {
		if (colName == null) return false;
		
		if (AMT_col != null) return AMT_col.equalsIgnoreCase(colName);
		return false;
	}
}
