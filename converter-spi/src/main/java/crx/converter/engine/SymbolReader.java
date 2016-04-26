/*******************************************************************************
 * Copyright (C) 2015 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.engine;

import static crx.converter.engine.PharmMLTypeChecker.isCommonDiscreteVariable;
import static crx.converter.engine.PharmMLTypeChecker.isCovariate;
import static crx.converter.engine.PharmMLTypeChecker.isCovariateTransform;
import static crx.converter.engine.PharmMLTypeChecker.isDerivative;
import static crx.converter.engine.PharmMLTypeChecker.isDiscreteDataParameter;
import static crx.converter.engine.PharmMLTypeChecker.isElement;
import static crx.converter.engine.PharmMLTypeChecker.isFunction;
import static crx.converter.engine.PharmMLTypeChecker.isFunctionCall;
import static crx.converter.engine.PharmMLTypeChecker.isFunctionParameter;
import static crx.converter.engine.PharmMLTypeChecker.isGeneralError;
import static crx.converter.engine.PharmMLTypeChecker.isIndependentVariable;
import static crx.converter.engine.PharmMLTypeChecker.isIndividualParameter;
import static crx.converter.engine.PharmMLTypeChecker.isLevelReference;
import static crx.converter.engine.PharmMLTypeChecker.isLocalVariable;
import static crx.converter.engine.PharmMLTypeChecker.isPopulationParameter;
import static crx.converter.engine.PharmMLTypeChecker.isRandomVariable;
import static crx.converter.engine.PharmMLTypeChecker.isStructuredError;
import static crx.converter.engine.PharmMLTypeChecker.isStructuredModel_Output;
import static crx.converter.engine.PharmMLTypeChecker.isStructuredModel_ResidualError;
import static crx.converter.engine.PharmMLTypeChecker.isTransformedCovariate;
import static crx.converter.engine.PharmMLTypeChecker.isVariabilityLevelDefinition;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import crx.converter.engine.common.ObservationParameter;
import crx.converter.engine.common.BaseTabularDataset.ElementMapping;
import crx.converter.spi.ILexer;
import crx.converter.spi.IParser;
import crx.converter.spi.blocks.ParameterBlock;
import crx.converter.spi.blocks.StructuralBlock;
import eu.ddmore.libpharmml.dom.IndependentVariable;
import eu.ddmore.libpharmml.dom.commontypes.DerivativeVariable;
import eu.ddmore.libpharmml.dom.commontypes.FunctionDefinition;
import eu.ddmore.libpharmml.dom.commontypes.FunctionParameter;
import eu.ddmore.libpharmml.dom.commontypes.LevelReference;
import eu.ddmore.libpharmml.dom.commontypes.VariableDefinition;
import eu.ddmore.libpharmml.dom.maths.FunctionCallType;
import eu.ddmore.libpharmml.dom.modeldefn.CommonDiscreteVariable;
import eu.ddmore.libpharmml.dom.modeldefn.CovariateDefinition;
import eu.ddmore.libpharmml.dom.modeldefn.CovariateTransformation;
import eu.ddmore.libpharmml.dom.modeldefn.DiscreteDataParameter;
import eu.ddmore.libpharmml.dom.modeldefn.GeneralObsError;
import eu.ddmore.libpharmml.dom.modeldefn.IndividualParameter;
import eu.ddmore.libpharmml.dom.modeldefn.ParameterRandomVariable;
import eu.ddmore.libpharmml.dom.modeldefn.PopulationParameter;
import eu.ddmore.libpharmml.dom.modeldefn.StructuredObsError;
import eu.ddmore.libpharmml.dom.modeldefn.StructuredObsError.Output;
import eu.ddmore.libpharmml.dom.modeldefn.StructuredObsError.ResidualError;
import eu.ddmore.libpharmml.dom.modeldefn.TransformedCovariate;
import eu.ddmore.libpharmml.dom.modeldefn.VariabilityLevelDefinition;

/**
 * Symbol Reader formats model element names to a more language specific 'friendly' form.
 *
 */
public class SymbolReader {
	/**
	 * Modified model element name.
	 */
	public static class ModifiedSymbol {
		public String original_value = null, modified_value = null;
		public Object src = null; 
		
		/**
		 * Default constructor
		 * @param src_ Model Element to read an indentifier.
		 * @param original_value_ Original Name
		 * @param modified_value_ Modified Name
		 */
		public ModifiedSymbol(Object src_, String original_value_, String modified_value_) {
			src = src_;
			original_value = original_value_;
			modified_value = modified_value_;
		}
		
		/**
		 * Flag if model element changed.
		 * @return boolean
		 */
		public boolean isModified() {
			if (original_value != null && modified_value != null) 
				return !original_value.equals(modified_value);
			
			return false;
		}
		
		@Override
		public String toString() {
			String format = "%s = '%s' -> '%s'"; 
			return String.format(format, src.getClass().getName(), original_value, modified_value);
		}
	}
	
	/**
	 * Default name for the reserved word map.
	 */
	public static final String RESERVED_WORD_MAP_FILE = "reserved_words.txt";
	
	/**
	 * Default output filename for renamed variables.
	 */
	public static final String RENAMED_SYMBOL_FILE = "changes.txt";
	
	private char [] duff_chars = new char[]{}; 
	private ILexer lexer = null;
	private Map<Object, ModifiedSymbol> modified_symbols = new HashMap<Object, ModifiedSymbol>();
	private char replacementChar = '_';
	private Map<String, String> reserved_word_map = new HashMap<String, String>();
	
	/**
	 * Default Constructor
	 */
	public SymbolReader() {}
	
	/**
	 * Symbol reader linked to a converter/lexer.
	 * @param lexer_ The converter instance
	 */
	public SymbolReader(ILexer lexer_) {
		if (lexer_ == null) throw new NullPointerException("The lexer cannot be NULL.");
		lexer = lexer_;
	}
	
	/**
	 * Add a modified symbol to the list.
	 * @param o Modified symbol
	 * @return boolean
	 */
	public boolean add(ModifiedSymbol o) {
		if (o != null) {
			if (!modified_symbols.containsKey(o.src)) {
				modified_symbols.put(o.src, o);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Add a term to the reserved word list
	 * @param word	Reserved word
	 * @param replacement Replacement term
	 * @return boolean
	 */
	public boolean addReservedWord(String word, String replacement) {
		if (word != null && replacement != null) {
			if (word.length() > 0 && word.length() > 0) {
				if (!reserved_word_map.containsKey(word)) reserved_word_map.remove(word);
				reserved_word_map.put(word, replacement);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Get the identifier symbol associated with a model element.
	 * @param element Model element.
	 * @return java.lang.String
	 */
	public String get(Object element) {
		String symbol =  "@";
		
		if (element instanceof String) {
			symbol = (String) element;
		} else if (element instanceof Part) {
			Part part = (Part) element;
			symbol = part.getName();
		}  else if (isRandomVariable(element)) {
			ParameterRandomVariable rv = (ParameterRandomVariable) element;
			symbol = rv.getSymbId();
		} else if (isFunctionCall(element)) {
			FunctionCallType fc = (FunctionCallType) element;
			symbol = fc.getSymbRef().getSymbIdRef();
		} else if (isStructuredError(element)) {
			StructuredObsError goe = (StructuredObsError) element;
			symbol = goe.getSymbId();
		} else if (isStructuredModel_Output(element)) {
			Output output = (Output) element;
			symbol = output.getSymbRef().getSymbIdRef();
		} else if (isStructuredModel_ResidualError(element)) {
			ResidualError res = (ResidualError) element;
			symbol = res.getSymbRef().getSymbIdRef();
		} else if (isGeneralError(element)) {
			GeneralObsError goe = (GeneralObsError) element;
			symbol = goe.getSymbId();
		} else if (isIndividualParameter(element)) {
			IndividualParameter ip = (IndividualParameter) element;
			symbol = ip.getSymbId();
		} else if (isPopulationParameter(element)) {
			PopulationParameter p = (PopulationParameter) element;
			symbol = p.getSymbId();
		} else if (isCovariate(element)) {
			CovariateDefinition cov = (CovariateDefinition) element;
			symbol = cov.getSymbId();
		} else if (element instanceof crx.converter.engine.common.ObservationParameter) {
			ObservationParameter op = (ObservationParameter) element;
			symbol = op.getName();
		} else if (isFunction(element)) {
			FunctionDefinition fd = (FunctionDefinition) element;
			symbol = fd.getSymbId();
		} else if (isDerivative(element)) {
			DerivativeVariable dv = (DerivativeVariable) element;
			symbol = dv.getSymbId();
		} else if (isLocalVariable(element)) {
			VariableDefinition v = (VariableDefinition) element;
			symbol = v.getSymbId();
		}  else if (element instanceof ElementMapping) {
			ElementMapping cm = (ElementMapping) element;
			symbol = cm.getColumnName();
		} else if (isIndependentVariable(element)) {
			IndependentVariable iv = (IndependentVariable) element;
			symbol = iv.getSymbId();
		} else if (isFunctionParameter(element)) {
			FunctionParameter a = (FunctionParameter) element;
			symbol = a.getSymbId();
		} else if (element instanceof StructuralBlock) {
			StructuralBlock sb = (StructuralBlock) element;
			symbol = sb.getName();
		} else if (isLevelReference(element)) {
			LevelReference scope = (LevelReference) element;
			symbol = scope.getSymbRef().getSymbIdRef();
		} else if (element instanceof ParameterBlock) {
			ParameterBlock pb = (ParameterBlock) element;
			symbol = pb.getName();
		} else if (isCommonDiscreteVariable(element)) {
			CommonDiscreteVariable cdv = (CommonDiscreteVariable) element;
			symbol = cdv.getSymbId();
		} else if (isDiscreteDataParameter(element)) {
			DiscreteDataParameter ddp = (DiscreteDataParameter) element;
			symbol = ddp.getSymbId();
		} else if (isCovariateTransform(element)) {
			CovariateTransformation cdt = (CovariateTransformation) element;
			TransformedCovariate tc = cdt.getTransformedCovariate();
			if (tc != null) symbol = tc.getSymbId();
		} else if (isTransformedCovariate(element)) {
			TransformedCovariate tc = (TransformedCovariate) element;
			symbol = tc.getSymbId();
		} else if (element instanceof CategoryRef_) {
			CategoryRef_ tc = (CategoryRef_) element;
			symbol = tc.getModelSymbol();
		} else if (isVariabilityLevelDefinition(element)) {
			VariabilityLevelDefinition level = (VariabilityLevelDefinition) element;
			symbol = level.getSymbId();
		} else {
			if (element == null) element = "NULL";
			String format = "WARNING: Unknown symbol, %s\n";
			String msg = String.format(format, element.toString());
			throw new IllegalStateException(msg);			 
		}
		
		if (symbol == null) throw new NullPointerException("An expected identifier symbol is NULL (element='" + element + "'");
		
		if (symbol != null && lexer.isRemoveIllegalCharacters()) {
			ModifiedSymbol result = removeIllegalCharacters(element, symbol);
			if (result.isModified()) {
				// Check if illegal character replacement has created any new reserved words.
				// If a decent language this should happen but ...
				if (lexer.isFilterReservedWords() && isReservedWord(result.modified_value)) 
					result.modified_value = replacement4ReservedWord(result.modified_value);
					
				symbol = result.modified_value;
				add(result);
			}
		} 
		
		// Check if the new symbol is a reserved word.
		if (lexer.isFilterReservedWords() && isReservedWord(symbol)) {
			String new_symbol = replacement4ReservedWord(symbol);
			ModifiedSymbol result = new ModifiedSymbol(element, symbol, new_symbol);
			if (result.isModified()){
				symbol = result.modified_value;
				add(result);
			}
		}
		
		return symbol;
	}
	
	/**
	 * Get the path of the renamed variable file.
	 * @param output_directory Output Directory
	 * @return java.lang.String
	 */
	public String getChangesFilepath(String output_directory) {
		return output_directory + File.separator + RENAMED_SYMBOL_FILE;
	}
	
	/**
	 * A collection of modified model symbols.
	 * @return java.util.Collection<ModifiedSymbol>
	 */
	public Collection<ModifiedSymbol> getModifiedSymbols() {
		return modified_symbols.values();
	}
	
	/**
	 * Return the map of reserved words and replacement terms.
	 * @return java.util.Map<String, String>
	 */
	public Map<String, String> getReservedWordMap() {
		return reserved_word_map;
	}
	
	/**
	 * Check if model has modified symbols.
	 * @return boolean
	 */
	public boolean hasModifiedSymbols() {
		return modified_symbols.size() > 0;
	}
	
	/**
	 * Check if a symbol is a reserved word.
	 * @param symbol Model Symbol
	 * @return boolean
	 */
	public boolean isReservedWord(String symbol) {
		if (symbol != null) return reserved_word_map.containsKey(symbol);
		
		return false;
	}
	
	/**
	 * Load the reserve word list from the class resources.
	 * @throws java.io.IOException
	 */
	public void loadReservedWords() throws IOException {
		IParser p = lexer.getParser();
		if (p == null) return;
		
		BufferedReader in= new BufferedReader(new InputStreamReader((p.getClass().getResourceAsStream(RESERVED_WORD_MAP_FILE))));
		String line = null;
		ArrayList<String> lines = new ArrayList<String>(); 
		while ((line = in.readLine()) != null) lines.add(line);
		in.close();
		
		for (String line_ : lines) {
			if (line_ == null) continue;
			if (line_.length() == 0) continue;
			
			line_ = line_.trim();
			String [] tokens = line_.split(" ");
			if (tokens.length != 2) continue;
			
			String original_word = tokens[0].trim();
			String replacement_word = tokens[1] = tokens[1].trim();
			
			if (original_word.length() > 0 && replacement_word.length() > 0) {
				if (reserved_word_map.containsKey(original_word)) reserved_word_map.remove(original_word);
				reserved_word_map.put(original_word, replacement_word);
			}
		}
	}
	
	/**
	 * Parse the changes as listed in a changes file.
	 * @param output_directory Output Directory
	 * @return java.util.List<ModifiedSymbol>
	 * @throws java.io.IOException
	 * @throws java.lang.ClassNotFoundException
	 * @throws java.lang.InstantiationException
	 * @throws java.lang.IllegalAccessException
	 */
	public List<ModifiedSymbol> readChanges(String output_directory) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		List<ModifiedSymbol> changes = new ArrayList<ModifiedSymbol>();
		String input_filename = getChangesFilepath(output_directory);
		
		File f = new File(input_filename);
		
		if (f.exists()) {
			BufferedReader in = new BufferedReader(new FileReader(input_filename));
			String line = null;
			ArrayList<String> lines = new ArrayList<String>(); 
			while ((line = in.readLine()) != null) lines.add(line);
			in.close();
		
			for (String line_ : lines) {
				if (line_ == null) continue;
				line_ = line_.trim();
				if (line_.length() == 0) continue;
				if (line_.startsWith("#")) continue; // Comment line so skip.
				
				// Instantiate the source class so that type of modification can be assessed later on.
				String [] tokens = line_.split("\\s+");
				
				String className = tokens[0].trim();
				String originalValue = tokens[1].trim();
				String modifiedValue = tokens[2].trim();
				
				originalValue = originalValue.replace("***", "");
				modifiedValue = modifiedValue.replace("***", "");
				
				Class<?> cls = Class.forName(className);
				Object o = cls.newInstance();
				if (isElement(o)) { // Only interested in PharmML renames.
					ModifiedSymbol ms = new ModifiedSymbol(o, originalValue, modifiedValue);
					if (ms.isModified()) changes.add(ms);
				}
			}
		}
		
		return changes;
	}
	
	/**
	 * Remove illegal characters from a model element identifier.
	 * @param element Model Element
	 * @param str String Identifier
	 * @return ModifiedSymbol
	 */
	public ModifiedSymbol removeIllegalCharacters(Object element, String str) {
		String output = null;
		
		if (str != null) {
			output = str;
			for (char duff_char : duff_chars) {
				String dc = Character.toString(duff_char);
				if (output.contains(dc)) output = output.replace(duff_char, replacementChar);
			}
		}
		
		return new ModifiedSymbol(element, str, output);
	}
	
	/**
	 * Get the replacement terms for a reserved word.
	 * @param symbol Model symbol
	 * @return java.lang.String
	 */
	public String replacement4ReservedWord(String symbol) {
		String replace_symbol = null;
		
		if (isReservedWord(symbol)) replace_symbol = reserved_word_map.get(symbol);
			
		return replace_symbol;
	}
	
	/**
	 * Save model identifier changes to file.
	 * @param output_directory Output Directory
	 * @return java.lang.String Output File path
	 * @throws IOException
	 */
	public String saveChanges(String output_directory) throws IOException {
		String output_filename = null;
		
		if (hasModifiedSymbols()) {
			output_filename = getChangesFilepath(output_directory);
			
			PrintWriter fout = new PrintWriter(output_filename);
			Date now = new Date();
			
			fout.write("# Dated:" + now.toString() + "\n");
			
			String format = "%s ***%s*** ***%s***\n";
			for (ModifiedSymbol ms : getModifiedSymbols()) {
				if (ms == null) continue;
				if (ms.src != null && ms.original_value != null && ms.modified_value != null) {
					if (ms.isModified()) fout.write(String.format(format, ms.src.getClass().getName(), ms.original_value, ms.modified_value));
				}
			}
			
			fout.close();
		}
		
		return output_filename;
	}
	
	/**
	 * Set the list of illegal characters for a target language.
	 * @param chars Character List
	 */
	public void setIllegalCharacters(char chars[]) {
		if (chars != null) duff_chars = chars;
	}
	
	/**
	 * Set the replacement character for an illegal character.
	 * @param replacement Replacement character.
	 */
	public void setReplacementCharacter(char replacement) {
		replacementChar = replacement;
	}
}
