/*******************************************************************************
 * Copyright (C) 2015 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.engine.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import crx.converter.engine.Accessor;
import crx.converter.engine.SymbolReader;
import crx.converter.engine.SymbolReader.ModifiedSymbol;
import crx.converter.spi.ILexer;
import eu.ddmore.libpharmml.dom.PharmML;
import eu.ddmore.libpharmml.dom.commontypes.PharmMLRootType;
import eu.ddmore.libpharmml.dom.commontypes.SymbolRef;
import eu.ddmore.libpharmml.dom.commontypes.SymbolType;
import eu.ddmore.libpharmml.dom.dataset.ColumnDefinition;
import eu.ddmore.libpharmml.dom.dataset.ColumnReference;
import eu.ddmore.libpharmml.dom.dataset.ColumnType;
import eu.ddmore.libpharmml.dom.dataset.DataSet;
import eu.ddmore.libpharmml.dom.dataset.DataSetTable;
import eu.ddmore.libpharmml.dom.dataset.ExternalFile;
import eu.ddmore.libpharmml.dom.dataset.HeaderColumnsDefinition;
import eu.ddmore.libpharmml.dom.dataset.MapType;

/**
 * Tabular dataset class.<br/>
 * Class holds data in an external reference file (CSV, NONMEM file).
 * @see eu.ddmore.libpharmml.dom.dataset.ExternalFile
 */
public abstract class BaseTabularDataset {
	/**
	 * Column mapping where the column is associated directly with the model element.
	 *
	 */
	public static class ElementMapping {
		private List<MapType> category_mappings = new ArrayList<MapType>();
		private ColumnDefinition col = null;
		private String columnName = null;
		private ColumnReference columnRef = null;
		private String dataSymbol = null;
		private PharmMLRootType element = null;
		private SymbolRef ref = null;
		
		/**
		 * Constructor
		 * @param col_ Column Reference as defined in the PharmML code.
		 * @param element_  Model Element referenced by the column.
		 */
		public ElementMapping(ColumnDefinition col_,  PharmMLRootType element_) {
			if (col_ == null || element_ == null) {
				String colName = "unspecified";
				if (col_ != null) {
					colName = col_.getColumnId();
				}
				throw new NullPointerException("The PharmML element mapped to a data column cannot be found in the model (column='" + colName + "')");
				
			}
			
			col = col_;
			columnName = col.getColumnId();
			element = element_;
		}
		
		/**
		 * Constructor
		 * @param columnRef_ Column Reference as defined in the PharmML code.
		 * @param element_  Model Element referenced by the column.
		 */
		public ElementMapping(ColumnReference columnRef_,  PharmMLRootType element_) {
			if (columnRef == null || element_ == null)  
				throw new NullPointerException("The column reference/element cannot be NULL.");
			
			columnRef = columnRef_;
			columnName = columnRef.getColumnIdRef();
			element = element_;
		}
		
		/**
		 * Constructor
		 * @param columnRef_ Column Reference as defined in the PharmML code.
		 * @param ref_ Model element accessed by a symbol reference.
		 */
		public ElementMapping(ColumnReference columnRef_, SymbolRef ref_) {
			if (ref_ == null)  throw new NullPointerException("The variable reference cannot be NULL.");
			if (columnRef_ == null)  throw new NullPointerException("The column reference cannot be NULL.");
			
			columnRef = columnRef_;
			columnName = columnRef.getColumnIdRef();
			ref = ref_;	
		}
		
		/**
		 * Add a new category mapping to the element mapping record.
		 * @param category_mapping
		 * @return boolean
		 */
		public boolean addCategoryMappings(MapType category_mapping) {
			if (category_mapping != null) {
				if (!category_mappings.contains(category_mapping)) {
					category_mappings.add(category_mapping);
					return true;
				}
			}
			
			return false;
		}
		
		/**
		 * Get the category mapping list for the model element.
		 * @return List<MapType>
		 */
		public List<MapType> getCategoryMappings() { return category_mappings; }
		
		/**
		 * Get the mapped column definition.
		 * @return eu.ddmore.libpharmml.dom.dataset.ColumnDefinition
		 */
		public ColumnDefinition getColumnDefinition() { return col; }
		
		/**
		 * Get the column name
		 * @return java.lang.String
		 */
		public String getColumnName() {
			return columnName;
		}
		
		/**
		 * Get the column  reference as defined in the PharmML model.
		 * @return eu.ddmore.libpharmml.dom.dataset.ColumnReference
		 */
		public ColumnReference getColumnReference() { return columnRef; }
		
		/**
		 * Get the data symbol associated with the model element.
		 * @return java.lang.String
		 */
		public String getDataSymbol() { return dataSymbol; }
		
		/**
		 * Get the model element bound to the input data column.
		 * @return eu.ddmore.libpharmml.dom.commontypes.PharmMLRootType
		 */
		public PharmMLRootType getElement() { return element; }
		
		/**
		 * Get the symbol reference of the model element associated with the input data column.
		 * @return eu.ddmore.libpharmml.dom.commontypes.PharmMLRootType
		 */
		public SymbolRef getSymbolReference() {
			return ref;
		}
		
		/**
		 * Flag if the element mapping has bound categories.
		 * @return boolean
		 */
		public boolean hasCategoryMapping() { return category_mappings.size() > 0; }
		
		/**
		 * Set the column reference associated with the element mapping.
		 * @param colref Column Reference
		 */
		public void setColumnReference(ColumnReference colref) { columnRef = colref; }
		
		/**
		 * Get the data symbol associated with the model element.
		 * @param dataSymbol_ Data Symbol
		 */
		public void setDataSymbol(String dataSymbol_) { dataSymbol = dataSymbol_; }
		
		/**
		 * Specify the model element asociated with a column.
		 * @param element_ Model Element
		 */
		public void setElement(PharmMLRootType element_) {
			if (element_ == null)  throw new NullPointerException("The column reference/element cannot be NULL.");
			
			element = element_;
		}
		
		/**
		 * Get the symbol reference of the model element associated with the input data column.
		 * @param ref_ Variable Reference
		 */
		public void setSymbolReference(SymbolRef ref_) { ref = ref_; }
		
		@Override
		public String toString() {
			String format = "ColName='%s', SymbolId='%s', Element='%s'";
			String symbId = null;
			if (ref != null) symbId = ref.getSymbIdRef();
			
			return String.format(format, columnName,  symbId, element);
		}
	}
	
	/**
	 * A reference to an imported data column.
	 */
	public static class ImportColumnRef {
		public ColumnDefinition def = null;
		public boolean skip = false;
		
		public ImportColumnRef(ColumnDefinition def_) {
			if (def_ == null) throw new NullPointerException("column definition is NULL.");
			def = def_;
		}
	}
	
	private static final String objective_pref = "objective";
	
	/**
	 * An accessor element.
	 */
	protected Accessor a = null;
	
	/**
	 * A map of column definition accessed via column name.
	 */
	protected Map<String, ColumnDefinition> col_name_map = new HashMap<String, ColumnDefinition>();
	
	/**
	 * A list of model elements mapped to data columns.
	 */
	protected List<ElementMapping> column_mappings = new ArrayList<ElementMapping>();
	
	/**
	 * A list of column definitions.
	 */
	protected List<ColumnDefinition> columns = new ArrayList<ColumnDefinition>();
	
	/**
	 * The model.
	 */
	protected PharmML dom = null;
	
	/**
	 * Data set referenced by the model.
	 */
	protected DataSet ds = null;
	
	private Integer id = -1;
	
	/**
	 * A list of data columns imported for usage in a simulation/estimation.
	 */
	protected List<ImportColumnRef> import_columns = new ArrayList<ImportColumnRef>();
	
	/**
	 * The source import data file.
	 */
	protected ExternalFile import_data = null;
	
	/**
	 * Element mapping to the model independent variable.
	 */
	protected ElementMapping iv_mapping = null;
	
	/**
	 * The converter instance.
	 */
	protected ILexer lexer = null;
	
	/**
	 * A PharmML-defined table structure (if any).
	 */
	protected DataSetTable table = null;
	
	/**
	 * Constructor
	 * @param lexer_ A converter instance
	 */
	public BaseTabularDataset(ILexer lexer_) {
		if (lexer_ == null) throw new NullPointerException("Lexer reference is NULL");
		lexer = lexer_;
		a = lexer.getAccessor();
		dom = lexer.getDom();
		
		if (dom == null) throw new NullPointerException("PharmML dom argument is NULL");
	}
	
	/**
	 * Add an element mapping to the data set definition. 
	 * @param mapping
	 */
	public void addElementMapping(ElementMapping mapping) {
		if (mapping != null) {
			column_mappings.add(mapping);
		}
	}
	
	/**
	 * Get the column defintion for the specified column type
	 * @param type Column Type (DV, DVID, TIME...) etc
	 * @return eu.ddmore.libpharmml.dom.dataset.ColumnDefinition
	 */
	public ColumnDefinition getColumn(ColumnType type) {
		if (type == null) return null;
		
		ColumnDefinition col = null;
		for (ColumnDefinition column : columns) {
			if (column == null) continue;
			ColumnType current_type = column.getListOfColumnType().get(0);
			if (current_type == null) continue;
			else if (current_type == type) {
				col = column;
				break;
			}
		}
		
		return col;
	}
	
	/**
	 * Get the definition for a named column.
	 * @param name Column name
	 * @return eu.ddmore.libpharmml.dom.dataset.ColumnDefinition
	 */
	public ColumnDefinition getColumn(String name) {
		ColumnDefinition col = null;
		
		if (name != null) {
			for (ColumnDefinition column : columns) {
				if (column == null) continue;
				String current_name = column.getColumnId();
				if (current_name == null) continue;
				if (current_name.equals(name)) {
					col = column;
					break;
				}
			}
		}
		
		return col;
	}
	
	/**
	 * Get the index for a named column.
	 * @param columnName Column Index
	 * @return int
	 */
	public int getColumnIndex(String columnName) {
		int i = -1;
		
		if (columnName != null) {
			List<String> col_names = getColumnNames();
			if (!col_names.isEmpty()) {
				int j = 0;
				for (String col_name : col_names) {
					if (col_name != null) {
						if (col_name.equals(columnName)) {
							i = j;
							break;
						}
					}
					j++;
				}
			}
		}
		
		return i;
	}
	
	/**
	 * Get the column defintion for the specified column type
	 * @param type Column Type (DV, DVID, TIME...) etc
	 * @return String Column Name
	 */
	public String getColumnName(ColumnType type) {
		if (type == null) return null;
		
		ColumnDefinition col = getColumn(type);
		if (col != null) return col.getColumnId();
		
		return null;
	}
	
	/**
	 * Get a list of data column names
	 * @return java.util.List<String>
	 */
	public List<String> getColumnNames() {
		List<String> col_names = new ArrayList<String>();
		
		for (ColumnDefinition col : columns) col_names.add(col.getColumnId());
		
		return col_names;
	}
	
	/**
	 * Get the dataset definition
	 * @return eu.ddmore.libpharmml.dom.dataset.DataSet
	 */
	public DataSet getDataSet() { return ds; }
	
	/**
	 * Get the element mapping for a specific column type.
	 * @param type
	 * @return ElementMapping
	 */
	public ElementMapping getElementMapping(ColumnType type) {
		if (type == null) return null;
		
		ElementMapping cm = null;
		
		if (ds != null) {
			HeaderColumnsDefinition def = ds.getDefinition();
			if (def == null) throw new NullPointerException("Column defintio is NULL");
			List<ColumnDefinition> cols = def.getListOfColumn();
			
			String colName = null;
			for (ColumnDefinition col : cols) {
				if (col == null) continue;
				if (col.getListOfColumnType().get(0) == type) {
					colName = col.getColumnId();
					break;
				}
			}
			cm = getElementMapping(colName);
		}

		return cm;
	}
	
	/**
	 * Get the element mapping for a named column.
	 * @param colName
	 * @return ElementMapping
	 */
	public ElementMapping getElementMapping(String colName) {
		ElementMapping cm = null;
		
		if (colName != null) {
			for (ElementMapping column_mapping : column_mappings) {
				if (column_mapping == null) continue;
				if (column_mapping.getColumnName().equals(colName)) {
					cm = column_mapping;
					break;
				}
			}
		}
		
		return cm;
	}
	
	/**
	 * Get a list of element mappings.
	 * @return java.util.List<ElementMapping>
	 */
	public List<ElementMapping> getElementMappings() { return column_mappings; }
	
	/**
	 * Get the object id for a dataset.
	 * @return java.lang.Integer
	 */
	public Integer getId() { return id; }
	
	/**
	 * Get the name of the dataset identifier column.
	 * @return java.lang.String
	 */
	public String getIdColumnName() {
		String colName = null;
		for (ColumnDefinition column : columns) {
			if (column == null) continue;
			if (column.getColumnId() == null) continue;
			if (isA(column.getColumnId(), ColumnType.ID)) {
				colName = column.getColumnId();
				break;
			}
		}
		
		return colName;
	}
	
	/**
	 * Get a list of imported data columns.
	 * @return java.utli.List<ImportColumnReference>
	 */
	public List<ImportColumnRef> getImportColumns() {
		if (import_columns.isEmpty()) {
			if (ds != null) {
				HeaderColumnsDefinition def = ds.getDefinition();
				if (def == null) throw new NullPointerException("Column defintio is NULL");
				List<ColumnDefinition> cols = def.getListOfColumn();
				for (ColumnDefinition col : cols) import_columns.add(new ImportColumnRef(col));
			}
		}
			
		return import_columns;
	}
	
	/**
	 * Get the external file reference of the dataset.
	 * @return eu.ddmore.libpharmml.dom.dataset.ExternalFile
	 */
	public ExternalFile getImportData() { return import_data; }
	
	/**
	 * Get the element mapping for the independent variable.
	 * @return ElementMapping
	 */
	public ElementMapping getIndependentVariableColumn() {
		return iv_mapping;
	}
	
	/**
	 * Get the name of a object dataset column as defined at the scripting level.
	 * @param mapping
	 * @return java.lang.String
	 */
	public String getObjectiveScriptVariableName(ElementMapping mapping) {
		String format = "%s_%s_%s";
		String col_name = mapping.getColumnName();
		if (lexer != null) {
			if (lexer.isRemoveIllegalCharacters()) {
				SymbolReader sr = lexer.getParser().getSymbolReader();
				ModifiedSymbol result = sr.removeIllegalCharacters(mapping, col_name);
				if (result.isModified()) {
					col_name = result.modified_value;
					
					sr.add(result);
				}
			}
		}
		
		return String.format(format, objective_pref, col_name, id);
	}
	
	/**
	 * Create a unique list of string values.
	 * @param values Source Array
	 * @return java.util.List<String>
	 */
	public List<String> getUniqueList(String [] values) {
		ArrayList<String> unique_values = new ArrayList<String>(); 
		if (values != null) {
			for (String value : values) if (!unique_values.contains(value)) unique_values.add(value);
		}
			
		return unique_values;
	}
	
	/**
	 * Check if the dataset has an external file reference
	 * @return boolean
	 */
	public boolean hasImportData() {
		return import_data != null;
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
		for (ColumnDefinition col : cols) {
			if (col == null) throw new NullPointerException("A column defintion is NULL");
			if (col.getColumnId() == null || col.getColumnNum() == null || 
				col.getValueType() == null)
				throw new IllegalStateException("Column description incomplete in table dataset.");
				
			columns.add(col);
			col_name_map.put(col.getColumnId(), col);
		}
		
		DataSetTable table = ds.getTable();
		boolean hasRows = false;
		if (table != null) hasRows = table.getListOfRow().size() > 0;
		
		if (!hasRows && ds.getExternalFile() == null) throw new NullPointerException("The dataset table is NULL.");
		if (ds.getExternalFile() != null) import_data = ds.getExternalFile();
	}
	
	/**
	 * Test if a column is of a particular usage.
	 * @param col Column name
	 * @param target Target usage
	 * @return boolean
	 */
	public boolean isA(ColumnDefinition col, ColumnType target) {
		if (col == null || target == null) return false;
		else return target.equals(col.getListOfColumnType().get(0));
	}
	
	/**
	 * Test if a column is of a particular usage.
	 * @param ref Column Reference
	 * @param target Target usage
	 * @return boolean
	 */
	public boolean isA(ColumnReference ref, ColumnType target) {
		if (ref == null) return false;
		
		String col_name = ref.getColumnIdRef();
		if (col_name == null) return false;
		
		if (col_name_map.containsKey(col_name)) {
			ColumnDefinition col = col_name_map.get(col_name);
			ColumnType columnType = col.getListOfColumnType().get(0);
			if (columnType != null) {
				if (columnType == target) return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Test if a column is of a particular usage.
	 * @param col_name Column name
	 * @param target Target usage
	 * @return boolean
	 */
	public boolean isA(String col_name, ColumnType target) {
		if (col_name_map.containsKey(col_name)) {
			ColumnDefinition col = col_name_map.get(col_name);
			ColumnType columnType = col.getListOfColumnType().get(0);
			if (columnType != null) {
				if (columnType == target) return true;
			}
		}
		
		return false;
	}

	/**
	 * Test if a column is a particular data type.
	 * @param col_name Column name
	 * @param target Target type
	 * @return ElementMapping
	 */
	public boolean isA(String col_name, SymbolType target) {
		if (col_name_map.containsKey(col_name)) {
			ColumnDefinition col = col_name_map.get(col_name);
			SymbolType valueType = col.getValueType();
			if (valueType != null) {
				if (valueType == target) return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Set the index number of the associated data column.
	 * @param id_
	 */
	public void setId(Integer id_) { if (id_ != null) id = id_; }
	
	/**
	 * Set the element mapping for the independent variable.
	 * @param mapping Element mapping
	 */
	public void setIndependentVariableColumn(ElementMapping mapping) {
		if (mapping != null) iv_mapping = mapping;
	}
}
