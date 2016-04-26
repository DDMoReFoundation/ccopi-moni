/*******************************************************************************
 * Copyright (C) 2015 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.engine.common;

import static crx.converter.engine.PharmMLTypeChecker.isDatasetTable;
import static crx.converter.engine.PharmMLTypeChecker.isFalse;
import static crx.converter.engine.PharmMLTypeChecker.isId;
import static crx.converter.engine.PharmMLTypeChecker.isInt;
import static crx.converter.engine.PharmMLTypeChecker.isReal;
import static crx.converter.engine.PharmMLTypeChecker.isString;
import static crx.converter.engine.PharmMLTypeChecker.isTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import crx.converter.engine.parts.SortableElement;
import crx.converter.spi.ILexer;
import eu.ddmore.libpharmml.dom.commontypes.IdValue;
import eu.ddmore.libpharmml.dom.commontypes.IntValue;
import eu.ddmore.libpharmml.dom.commontypes.RealValue;
import eu.ddmore.libpharmml.dom.commontypes.Scalar;
import eu.ddmore.libpharmml.dom.commontypes.StringValue;
import eu.ddmore.libpharmml.dom.dataset.ColumnDefinition;
import eu.ddmore.libpharmml.dom.dataset.ColumnReference;
import eu.ddmore.libpharmml.dom.dataset.ColumnType;
import eu.ddmore.libpharmml.dom.dataset.DataSet;
import eu.ddmore.libpharmml.dom.dataset.DataSetTable;
import eu.ddmore.libpharmml.dom.dataset.DatasetRow;
import eu.ddmore.libpharmml.dom.dataset.HeaderColumnsDefinition;

/**
 * An old utility class wrapped a tabular dataset (CSV, NONMEM).<br/>
 * Mostly utility functions to process PharmML declared datasets 
 * rather than data declared in an externally referenced CSV file. 
 * @see eu.ddmore.libpharmml.dom.dataset.DataSet
 */
public class TabularDataset extends BaseTabularDataset  {
	/**
	 * Constructor
	 * @param ds_ Data set
	 * @param lexer_ Converter instance
	 */
	public TabularDataset(DataSet ds_, ILexer lexer_) {
		super(lexer_);
		init(ds_);
	}
	
	/**
	 * Get a column via a reference.
	 * @param ref Column Reference
	 * @return ColumnDefinition
	 */
	public ColumnDefinition getColumn(ColumnReference ref) {
		if (ref == null) return null;
		else return getColumn(ref.getColumnIdRef());
	}
	
	/**
	 * Get a named column from a PharmML data set as a boolean array.
	 * @param name Column name
	 * @return boolean []
	 */
	public boolean [] getColumnAsBooleanArray(String name) {
		boolean [] data = null;
		
		if (name != null) {
			int idx = -1;
			for (ColumnDefinition column : columns) {
				if (column.getColumnId().equals(name)) {
					idx = column.getColumnNum().intValue();
					break;
				}
			}
			
			if (idx >= 1 && table != null) {
				ArrayList<Boolean> values = new ArrayList<Boolean>(); 
				for (DatasetRow row : table.getListOfRow()) {
					if (row == null) continue;
					Scalar value = row.getListOfValue().get(idx - 1);
					if (value == null) continue;
					
					if (isFalse(value)) values.add(Boolean.FALSE);
					else if (isTrue(value)) values.add(Boolean.TRUE);
					else if (isString(value)) {
						StringValue v = (StringValue) value; 
						values.add(Boolean.parseBoolean(v.getValue()));
					}  else if (isId(value)) {
						IdValue v = (IdValue) value; 
						values.add(Boolean.parseBoolean(v.getValue()));
					} else if (isReal(value)) {
						RealValue v = (RealValue) value;
						if (v.getValue() == 0.0) values.add(Boolean.FALSE);
						else values.add(Boolean.TRUE);
					} else if (isInt(value)) {
						IntValue v = (IntValue) value;
						if (v.getValue().intValue() == 0) values.add(Boolean.FALSE);
						else values.add(Boolean.TRUE);
					} else if (isDatasetTable(value)) {
						throw new UnsupportedOperationException("Nested datasets not supported at present.");
					} else 
						throw new IllegalStateException("Scalar type not recognised.");
				}
				
				idx = 0;
				data = new boolean[values.size()];
				for (Boolean b : values) data[idx++] = b.booleanValue();
			}
		}
		
		return data;
	}
	
	/**
	 * Get a named column from a PharmML data set as a identifier array.<br/>
	 * In Java world, identifiers are just string.
	 * @param name Column name
	 * @return java.lang.String []
	 */
	public String [] getColumnAsIdArray(String name) {
		return getColumnAsStringArray(name);
	}
	
	/**
	 * Get a named column from a PharmML data set as a integer array.
	 * @param name Column name
	 * @return int []
	 */
	public int [] getColumnAsIntArray(String name) {
		int [] data = null;
		
		if (name != null) {
			int idx = -1;
			for (ColumnDefinition column : columns) {
				if (column.getColumnId().equals(name)) {
					idx = column.getColumnNum().intValue();
					break;
				}
			}
			
			if (idx >= 1 && table != null) {
				ArrayList<Integer> values = new ArrayList<Integer>(); 
				for (DatasetRow row : table.getListOfRow()) {
					if (row == null) continue;
					Scalar value = row.getListOfValue().get(idx - 1);
					if (value == null) continue;
					
					if (isFalse(value)) values.add(0);
					else if (isTrue(value) ) values.add(1);
					else if (isString(value)) {
						StringValue v = (StringValue) value; 
						values.add(Integer.parseInt(v.getValue()));
					}  else if (isId(value)) {
						IdValue v = (IdValue) value; 
						values.add(Integer.parseInt(v.getValue()));
					} else if (isReal(value)) {
						RealValue v = (RealValue) value;
						values.add((int) v.getValue());
					} else if (isInt(value)) {
						IntValue v = (IntValue) value;
						values.add(v.getValue().intValue());
					} else if (isDatasetTable(value)) {
						throw new UnsupportedOperationException("Nested datasets not supported at present.");
					} else 
						throw new IllegalStateException("Scalar type not recognised.");
				}
				
				idx = 0;
				data = new int[values.size()];
				for (Integer d : values) data[idx++] = d.intValue();
			}
		}
		
		return data;
	}
	
	/**
	 * Get a named column from a PharmML data set as a real number (double) array.
	 * @param name Column name
	 * @return double []
	 */
	public double [] getColumnAsRealArray(String name) {
		double [] data = null;
		
		if (name != null) {
			int idx = -1;
			for (ColumnDefinition column : columns) {
				if (column.getColumnId().equals(name)) {
					idx = column.getColumnNum().intValue();
					break;
				}
			}
			
			if (idx >= 1 && table != null) {
				ArrayList<Double> values = new ArrayList<Double>(); 
				for (DatasetRow row : table.getListOfRow()) {
					if (row == null) continue;
					Scalar value = row.getListOfValue().get(idx - 1);
					if (value == null) continue;
					
					if (isFalse(value)) values.add(0.0);
					else if (isTrue(value)) values.add(1.0);
					else if (isString(value)) {
						StringValue v = (StringValue) value;
						values.add(Double.valueOf(v.getValue()));
					} else if (isId(value)) {
						IdValue v = (IdValue) value;
						values.add(Double.valueOf(v.getValue()));
					} else if (isReal(value)) {
						RealValue v = (RealValue) value;
						values.add(v.getValue());
					} else if (isInt(value)) {
						IntValue v = (IntValue) value;
						values.add(v.getValue().doubleValue());
					} else if (isDatasetTable(value)) 
						throw new UnsupportedOperationException("Nested datasets not supported at present.");
					else 
						throw new IllegalStateException("Scalar type not recognised.");
				}
				
				idx = 0;
				data = new double[values.size()];
				for (Double d : values) data[idx++] = d.doubleValue();
			}
		}
		
		return data;
	}
	
	/**
	 * Get a named column from a PharmML data set as a string array.
	 * @param name Column name
	 * @return java.lang.String []
	 */
	public String [] getColumnAsStringArray(String name) {
		String [] data = null;
		
		if (name != null) {
			int idx = -1;
			for (ColumnDefinition column : columns) {
				if (column.getColumnId().equals(name)) {
					idx = column.getColumnNum().intValue();
					break;
				}
			}
			
			if (idx >= 1 && table != null) {
				ArrayList<String> values = new ArrayList<String>(); 
				for (DatasetRow row : table.getListOfRow()) {
					if (row == null) continue;
					Scalar value = row.getListOfValue().get(idx - 1);
					if (value == null) continue;
					
					if (isFalse(value)) values.add(Boolean.FALSE.toString());
					else if (isTrue(value)) values.add(Boolean.TRUE.toString());
					else if (isString(value)) {
						StringValue v = (StringValue) value;
						values.add(v.getValue());
					}  else if (isId(value)) {
						IdValue v = (IdValue) value;
						values.add(v.getValue());
					} else if (isReal(value)) {
						RealValue v = (RealValue) value;
						values.add(Double.toString(v.getValue()));
					} else if (isInt(value)) {
						IntValue v = (IntValue) value;
						values.add(Integer.toString(v.getValue().intValue()));
					} else if (isDatasetTable(value)) {
						throw new UnsupportedOperationException("Nested datasets not supported at present.");
					} else 
						throw new IllegalStateException("Scalar type not recognised.");
				}
				
				data = new String[values.size()];
				data = values.toArray(data);
			}
		}
		
		return data;
	}
	
	/**
	 * Get the dose column.
	 * @return ColumnDefinition
	 */
	public ColumnDefinition getDose() { return getColumn(ColumnType.DOSE); }
	
	/**
	 * Get the DV column
	 * @return ColumnDefinition
	 */
	public ColumnDefinition getDV() { return getColumn(ColumnType.DV); }
	
	/**
	 * Get the DVID column
	 * @return ColumnDefinition
	 */
	public ColumnDefinition getDVID() { return getColumn(ColumnType.DVID); }
	
	/**
	 * Get the identifier column.
	 * @return ColumnDefinition
	 */
	public ColumnDefinition getID() { return getColumn(ColumnType.ID); }
	
	/**
	 * Get the independent variable/time column.
	 * @return ColumnDefinition
	 */
	public ColumnDefinition getIDV() { 
		ColumnDefinition col = getColumn(ColumnType.IDV);
		if (col != null) return col;
		return getColumn(ColumnType.TIME); 
	}
	
	/**
	 * Get the MDV column
	 * @return ColumnDefinition
	 */
	public ColumnDefinition getMDV() { return getColumn(ColumnType.MDV); }
	
	/**
	 * Create a unique list of string from a PharmML data set column.<br/>
	 * Useful for processing an identifier declarations into a unique list.
	 * @param columnName Column name
	 * @return java.util.List<java.lang.String>
	 */
	public List<String> getUniqueList(String columnName) {
		ArrayList<String> values = new ArrayList<String>(); 
		
		if (columnName != null) {
			boolean colExists = false;
			for (ColumnDefinition column : columns) {
				if (column == null) continue;
				if (column.getColumnId() == null) continue;
				if (column.getColumnId().equals(columnName)) {
					colExists = true;
					break;
				}
			}
			
			if (colExists) {
				String [] values_ = getColumnAsStringArray(columnName);
				for (String value : values_) {
					if (!values.contains(value)) values.add(value);
				}
			}
		}
		
		return values;
	}
	
	/**
	 * Initialise the instance.
	 * @param ds_ Data set
	 */
	protected void init(DataSet ds_) {
		if (ds_ == null) throw new NullPointerException("Dataset is NULL.");
		ds = ds_;
		
		HeaderColumnsDefinition def = ds.getDefinition();
		if (def == null) throw new NullPointerException("Column defintio is NULL");
		List<ColumnDefinition> cols = def.getListOfColumn();
		if (cols.size() == 0) throw new NullPointerException("Column Definition empty");
		
		List<SortableElement> unsorted_cols = new ArrayList<SortableElement>();
		for (ColumnDefinition col : cols) {
			if (col == null) throw new NullPointerException("A column defintion is NULL");
			if (col.getColumnId() == null || col.getColumnNum() == null || 
				col.getValueType() == null)
				throw new IllegalStateException("Column description incomplete in table dataset.");
				
			columns.add(col);
			col_name_map.put(col.getColumnId(), col);
			
			unsorted_cols.add(new SortableElement(col, col.getColumnNum().doubleValue()));
		}
		
		Collections.sort(unsorted_cols);
		columns.clear();
		for (SortableElement unsorted_col : unsorted_cols) columns.add((ColumnDefinition) unsorted_col.getElement());
		
		DataSetTable table = ds.getTable();
		boolean hasRows = false;
		if (table != null) hasRows = table.getListOfRow().size() > 0;
		
		if (!hasRows && ds.getExternalFile() == null) throw new NullPointerException("The dataset table is NULL.");
		if (ds.getExternalFile() != null) import_data = ds.getExternalFile();
	}
	
	/**
	 * Flag if a column definition is a covariate.
	 * @param col Column definition
	 * @return boolean
	 */
	public boolean isCovariate(ColumnDefinition col) {
		if (col != null) return isA(col, ColumnType.COVARIATE);
		return false;
	}
	
	/**
	 * Flag if a referenced column is a covariate.
	 * @param ref Referenced column
	 * @return boolean
	 */
	public boolean isCovariate(ColumnReference ref) {
		if (ref != null) return isA(ref, ColumnType.COVARIATE);
		return false;
	}
	
	/**
	 * Flag if a referenced column is a covariate.
	 * @param column_name Column Name
	 * @return boolean
	 */
	public boolean isCovariate(String column_name) {
		if (column_name != null) return isA(column_name, ColumnType.COVARIATE);
		return false;
	}
	
	/**
	 * Flag if a column definition can be dropped from a dataset.
	 * @param col Column definition
	 * @return boolean
	 */
	public boolean isDropped(ColumnDefinition col) {
		if (col != null) return isA(col, ColumnType.UNDEFINED);
		return false;
	}
	
	/**
	 * Flag if a referenced column can be dropped from a dataset.
	 * @param ref Referenced column
	 * @return boolean
	 */
	public boolean isDropped(ColumnReference ref) {
		if (ref != null) return isA(ref, ColumnType.UNDEFINED);
		return false;
	}
	
	/**
	 * Flag if a referenced column can be dropped from a dataset.
	 * @param column_name Column Name
	 * @return boolean
	 */
	public boolean isDropped(String column_name) {
		if (column_name != null) return isA(column_name, ColumnType.UNDEFINED);
		return false;
	}
}
