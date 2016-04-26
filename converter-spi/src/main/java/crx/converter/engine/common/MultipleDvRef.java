/*******************************************************************************
 * Copyright (C) 2016 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.engine.common;

import crx.converter.spi.ILexer;
import crx.converter.tree.BinaryTree;
import crx.converter.tree.Node;
import eu.ddmore.libpharmml.dom.commontypes.PharmMLElement;
import eu.ddmore.libpharmml.dom.dataset.ColumnDefinition;
import eu.ddmore.libpharmml.dom.maths.Piece;

/**
 * A process/interpreted MDV tag as read from the mapping section bound to an external
 * data set column.
 * The MDV construct is a PharmML mechanism to switch a participant in a clinical
 * trial to different error models based on the content of a data value read from a 
 * CSV file. 
 * This class is a processed instance of the MultipleDVMapping class in PharmML.
 * @see eu.ddmore.libpharmml.dom.trialdesign.MultipleDVMapping
 * @see eu.ddmore.libpharmml.dom.trialdesign.ExternalDataSet#getListOfColumnMappingOrColumnTransformationOrMultipleDVMapping()
 * @see eu.ddmore.libpharmml.dom.modeldefn.ObservationError
 */
public class MultipleDvRef extends InputColumnEvent {
	public MultipleDvRef(ILexer lexer, ColumnDefinition col_, Piece piece) { super(lexer, col_, piece); }
	
	/**
	 * Create a condition pair representing the logical statement forming 
	 * part of the MDV declaration.
	 * @return ConditionPairing
	 */
	public ConditionPairing createConditionPairing() {
		BinaryTree bt =  lexer.getTreeMaker().newInstance(getCondition());
		
		PharmMLElement lhs = null, rhs = null;
		if (bt.nodes.size() != 3) throw new IllegalStateException("MDV conditional not of the expected syntax"); 
		
		int count = 0;
		for (Node node : bt.nodes) {
			if (count == 1) lhs = (PharmMLElement) node.data;
			else if (count == 2) rhs = (PharmMLElement) node.data;
			
			count++;
		}
		
		return new ConditionPairing(lhs, rhs);
	}
	
	/**
	 * Flag if the column name is referenced by the MDV.
	 * @param columnName
	 * @return boolean
	 */
	public boolean referencesColumn(String columnName) {
		if (columnName == null) return false;
		return columnName.equals(getColumnName());
	}
}
