/*******************************************************************************
 * Copyright (C) 2015 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.spi;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import crx.converter.engine.SymbolReader;
import crx.converter.spi.blocks.StructuralBlock;
import crx.converter.spi.steps.EstimationStep;
import crx.converter.spi.steps.SimulationStep;
import crx.converter.tree.BinaryTree;

/**
 * Parser interface, parser transforms model definition to code.
 */
public interface IParser {
	/**
	 * Clean up internal buffers post code generation.
	 * @throws IOException
	 */
	public void cleanUp() throws IOException;
	
	/**
	 * Get a list of source file paths linked to model-scope functions.
	 * @return java.util.List<java.lang.String>
	 */
	public List<String> getGeneratedFunctionPaths();
	
	/**
	 * Get the model function file path as bound to a structural model.
	 * @param output_dir
	 * @param sb Structural Block
	 * @return java.lang.String
	 */
	public String getModelFunctionFilename(String output_dir, StructuralBlock sb);
	
	/**
	 * Get the script file path for the bound PharmML model.
	 * @param output_dir Output directory
	 * @return java.lang.String
	 */
	public String getScriptFilename(String output_dir);
	
	/**
	 * Get the code symbol or variable name associated with a model object.<br/>
	 * For example, if an object is a typed 'local variable', this method returns the variable name.<br>
	 * If a derivative, this method can return an indexed reference to a state vector.
	 * @param o Model Object
	 * @return java.lang.String Code representing the model symbol in the target language. 
	 */
	public String getSymbol(Object o);
	
	/**
	 * Get the symbol reader bound to the parser.
	 * The symbol reader filters model terms for reserved words and illegal characters.
	 * @return SymbolReader
	 */
	public SymbolReader getSymbolReader();
	
	/**
	 * Initialise the language specific settings for a parser.
	 */
	public void initialise();
	
	/**
	 * Parse a AST to generate code for a given model element context.
	 * @param context Context variable
	 * @param bt Binary Tree
	 * @return String Code representing the tree for a given context.
	 */
	public String parse(Object context, BinaryTree bt);
	
	/**
	 * Parse a AST to generate code for a given model element context
	 * and write the code to an output file.
	 * @param context Context variable
	 * @param bt Binary Tree
	 * @param fout Output file stream
	 */
	public void parse(Object context, BinaryTree bt, PrintWriter fout);
	
	/**
	 * Adjusts a path of an output script, excising absolute paths, converting to local paths.<br/>
	 * This is so an output script can be squirted to a remote execution space without much fuss.
	 * @param f Output File as Created by a converter.
	 */
	public void removeAbsolutePaths(File f) throws IOException;
	
	/**
	 * Bind a lexer to the parser instance.<br/>
	 * @param lexer Lexer instance
	 */
	public void setLexer(ILexer lexer);
	
	/**
	 * Set number of steps for an ODE simulation time range.
	 * @param nSteps Number of steps.
	 */
	public void setNumberSimulationSteps(int nSteps);
	
	/**
	 * Set the PharmML version that the parser can support.
	 * @param version_
	 */
	public void setPharmMLWrittenVersion(String version_);
	
	/**
	 * Set the run_id asociated with output filename creation.
	 * @param run_id Run identifier.
	 */
	public void setRunId(String run_id);
	
	/**
	 * Write an estimation block for an ADME/PKPD model. 
	 * @param fout Output file stream
	 * @param output_dir Output directory
	 * @param est Estimation step
	 * @throws java.io.IOException
	 */
	public void writeADMEEstimationBlock(PrintWriter fout, File output_dir, EstimationStep est) throws IOException;
	
	/**
	 * Write an estimation block with dosing for an ADME/PKPD model. 
	 * @param fout Output file stream
	 * @param output_dir Output directory
	 * @param est Estimation step
	 * @throws java.io.IOException
	 */
	public void writeADMEEstimationWithDosingBlock(PrintWriter fout, File output_dir, EstimationStep est) throws IOException;
	
	/**
	 * Write an simulation block for an ADME/PKPD model. 
	 * @param fout Output file stream
	 * @param output_dir Output directory
	 * @param ss Simulation step
	 * @throws java.io.IOException
	 */
	public void writeADMESimulationBlock(PrintWriter fout, File output_dir, SimulationStep ss) throws IOException;
	
	/**
	 * Write an simulation block with dosing for an ADME/PKPD model. 
	 * @param fout Output file stream
	 * @param output_dir Output directory
	 * @param ss Simulation step
	 * @throws java.io.IOException
	 */
	public void writeADMESimulationWithDosingBlock(PrintWriter fout, File output_dir, SimulationStep ss) throws IOException;
	
	/**
	 * Write an estimation block with dosing for a categorical model. 
	 * @param fout Output file stream
	 * @param output_dir Output directory
	 * @param est Estimation step
	 * @throws java.io.IOException
	 */
	public void writeCategoricalEstimationBlock(PrintWriter fout, File output_dir, EstimationStep est) throws IOException;
	
	/**
	 * Write an simulation block with dosing for a categorical model. 
	 * @param fout Output file stream
	 * @param output_dir Output directory
	 * @param ss Simulation step
	 * @throws java.io.IOException
	 */
	public void writeCategoricalSimulationBlock(PrintWriter fout, File output_dir, SimulationStep ss) throws IOException;
	
	/**
	 * Write an estimation block with dosing for a discrete model. 
	 * @param fout Output file stream
	 * @param output_dir Output directory
	 * @param est Estimation step
	 * @throws java.io.IOException
	 */
	public void writeDiscreteEstimationBlock(PrintWriter fout, File output_dir, EstimationStep est) throws IOException;
	
	/**
	 * Write an simulation block with dosing for an discrete model. 
	 * @param fout Output file stream
	 * @param output_dir Output directory
	 * @param ss Simulation step
	 * @throws java.io.IOException
	 */
	public void writeDiscreteSimulationBlock(PrintWriter fout, File output_dir, SimulationStep ss) throws IOException;
	
	/**
	 * Write end of a script file. 
	 * @param fout Output file stream
	 * @throws java.io.IOException
	 */
	public void writeEOF(PrintWriter fout) throws IOException;
	
	/**
	 * Write start elements of script main block 
	 * @param fout Output file stream
	 * @throws java.io.IOException
	 */
	public void writeMainBlockBOF(PrintWriter fout) throws IOException;
	
	/**
	 * Write end elements of script main block 
	 * @param fout Output file stream
	 * @throws java.io.IOException
	 */
	public void writeMainBlockEOF(PrintWriter fout) throws IOException;
	
	/**
	 * Write pre-main block script elements. 
	 * @param fout Output file stream
	 * @throws java.io.IOException
	 */
	public void writeMainBlockInitialisation(PrintWriter fout) throws IOException;
	
	/**
	 * Write an estimation block for a NLME model. 
	 * @param fout Output file stream
	 * @param output_dir Output directory
	 * @param est Estimation step
	 * @throws java.io.IOException
	 */
	public void writeMixedEffectEstimationBlock(PrintWriter fout, File output_dir, EstimationStep est) throws IOException;
	
	/**
	 * Write a simulation block with dosing for an NLME model. 
	 * @param fout Output file stream
	 * @param output_dir Output directory
	 * @param ss Estimation step
	 * @throws java.io.IOException
	 */
	public void writeMixedEffectSimulationBlock(PrintWriter fout, File output_dir, SimulationStep ss) throws IOException;
	
	/**
	 * Write an estimation block with dosing for an NONMEM model. 
	 * @param fout Output file stream
	 * @param output_dir Output directory
	 * @param est Estimation step
	 * @throws java.io.IOException
	 */
	public void writeNONMEMMixedEffectEstimationBlock(PrintWriter fout, File output_dir, EstimationStep est) throws IOException;
	
	/**
	 * Write an simulation block with dosing for an NONMEM model. 
	 * @param fout Output file stream
	 * @param output_dir Output directory
	 * @param ss Simulation step
	 * @throws java.io.IOException
	 */
	public void writeNONMEMMixedEffectSimulationBlock(PrintWriter fout, File output_dir, SimulationStep ss) throws IOException;
	
	/**
	 * Write an simulation block with dosing for an ADME/PKPD model. 
	 * @param fout Output file stream
	 * @param output_dir Output directory
	 * @throws java.io.IOException
	 */
	public void writePreMainBlockElements(PrintWriter fout, File output_dir) throws IOException;
	
	/**
	 * Write an estimation block for an untranslated PK Macro model. 
	 * @param fout Output file stream
	 * @param output_dir Output directory
	 * @param est Estimation step
	 * @throws java.io.IOException
	 */
	public void writeUntranslatedPKMacroEstimationBlock(PrintWriter fout, File output_dir, EstimationStep est) throws IOException;
	
	/**
	 * Write an simulation block for an untranslated PK Macro model. 
	 * @param fout Output file stream
	 * @param output_dir Output directory
	 * @param est Estimation step
	 * @throws java.io.IOException
	 */
	public void writeUntranslatedPKMacroSimulationBlock(PrintWriter fout, File output_dir, SimulationStep est) throws IOException;
}
