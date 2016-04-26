/*******************************************************************************
 * Copyright (C) 2016 Cyprotex Discovery Ltd - All rights reserved.
********************************************************************************/

package crx.converter.engine.common;

import static crx.converter.engine.PharmMLTypeChecker.isBinaryOperation;
import static crx.converter.engine.PharmMLTypeChecker.isColumnDefinition;
import static crx.converter.engine.PharmMLTypeChecker.isCommonParameter;
import static crx.converter.engine.PharmMLTypeChecker.isConstant;
import static crx.converter.engine.PharmMLTypeChecker.isContinuousCovariate;
import static crx.converter.engine.PharmMLTypeChecker.isCovariate;
import static crx.converter.engine.PharmMLTypeChecker.isDelay;
import static crx.converter.engine.PharmMLTypeChecker.isDerivative;
import static crx.converter.engine.PharmMLTypeChecker.isFunction;
import static crx.converter.engine.PharmMLTypeChecker.isFunctionCall;
import static crx.converter.engine.PharmMLTypeChecker.isFunctionParameter;
import static crx.converter.engine.PharmMLTypeChecker.isGeneralError;
import static crx.converter.engine.PharmMLTypeChecker.isIndependentVariable;
import static crx.converter.engine.PharmMLTypeChecker.isIndividualParameter;
import static crx.converter.engine.PharmMLTypeChecker.isInterpolation;
import static crx.converter.engine.PharmMLTypeChecker.isInterval;
import static crx.converter.engine.PharmMLTypeChecker.isJAXBElement;
import static crx.converter.engine.PharmMLTypeChecker.isLocalVariable;
import static crx.converter.engine.PharmMLTypeChecker.isLogicalBinaryOperation;
import static crx.converter.engine.PharmMLTypeChecker.isLogicalUnaryOperation;
import static crx.converter.engine.PharmMLTypeChecker.isMatrix;
import static crx.converter.engine.PharmMLTypeChecker.isMatrixSelector;
import static crx.converter.engine.PharmMLTypeChecker.isMatrixUnaryOperation;
import static crx.converter.engine.PharmMLTypeChecker.isObservationError;
import static crx.converter.engine.PharmMLTypeChecker.isPiecewise;
import static crx.converter.engine.PharmMLTypeChecker.isPopulationParameter;
import static crx.converter.engine.PharmMLTypeChecker.isProbability;
import static crx.converter.engine.PharmMLTypeChecker.isProduct;
import static crx.converter.engine.PharmMLTypeChecker.isRandomVariable;
import static crx.converter.engine.PharmMLTypeChecker.isRhs;
import static crx.converter.engine.PharmMLTypeChecker.isScalarInterface;
import static crx.converter.engine.PharmMLTypeChecker.isSequence;
import static crx.converter.engine.PharmMLTypeChecker.isSum;
import static crx.converter.engine.PharmMLTypeChecker.isSymbolReference;
import static crx.converter.engine.PharmMLTypeChecker.isUnaryOperation;
import static crx.converter.engine.PharmMLTypeChecker.isVariabilityLevelDefinition;
import static crx.converter.engine.PharmMLTypeChecker.isVector;
import static crx.converter.engine.PharmMLTypeChecker.isVectorSelector;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBElement;

import crx.converter.engine.Accessor;
import crx.converter.engine.Engine;
import crx.converter.engine.SymbolReader;
import crx.converter.spi.ILexer;
import crx.converter.spi.IParser;
import crx.converter.spi.blocks.StructuralBlock;
import crx.converter.spi.steps.EstimationStep;
import crx.converter.spi.steps.SimulationStep;
import crx.converter.tree.BinaryTree;
import crx.converter.tree.Node;
import crx.converter.tree.TreeMaker;
import crx.converter.tree.Utils;
import eu.ddmore.libpharmml.dom.IndependentVariable;
import eu.ddmore.libpharmml.dom.commontypes.Delay;
import eu.ddmore.libpharmml.dom.commontypes.DerivativeVariable;
import eu.ddmore.libpharmml.dom.commontypes.FalseBoolean;
import eu.ddmore.libpharmml.dom.commontypes.FunctionDefinition;
import eu.ddmore.libpharmml.dom.commontypes.FunctionParameter;
import eu.ddmore.libpharmml.dom.commontypes.IntValue;
import eu.ddmore.libpharmml.dom.commontypes.Interpolation;
import eu.ddmore.libpharmml.dom.commontypes.Interval;
import eu.ddmore.libpharmml.dom.commontypes.Matrix;
import eu.ddmore.libpharmml.dom.commontypes.MatrixSelector;
import eu.ddmore.libpharmml.dom.commontypes.PharmMLRootType;
import eu.ddmore.libpharmml.dom.commontypes.Product;
import eu.ddmore.libpharmml.dom.commontypes.RealValue;
import eu.ddmore.libpharmml.dom.commontypes.Rhs;
import eu.ddmore.libpharmml.dom.commontypes.Scalar;
import eu.ddmore.libpharmml.dom.commontypes.Sequence;
import eu.ddmore.libpharmml.dom.commontypes.StringValue;
import eu.ddmore.libpharmml.dom.commontypes.Sum;
import eu.ddmore.libpharmml.dom.commontypes.SymbolRef;
import eu.ddmore.libpharmml.dom.commontypes.TrueBoolean;
import eu.ddmore.libpharmml.dom.commontypes.VariableDefinition;
import eu.ddmore.libpharmml.dom.commontypes.Vector;
import eu.ddmore.libpharmml.dom.dataset.ColumnDefinition;
import eu.ddmore.libpharmml.dom.maths.Binop;
import eu.ddmore.libpharmml.dom.maths.Constant;
import eu.ddmore.libpharmml.dom.maths.FunctionCallType;
import eu.ddmore.libpharmml.dom.maths.LogicBinOp;
import eu.ddmore.libpharmml.dom.maths.LogicUniOp;
import eu.ddmore.libpharmml.dom.maths.MatrixUniOp;
import eu.ddmore.libpharmml.dom.maths.Piecewise;
import eu.ddmore.libpharmml.dom.maths.Uniop;
import eu.ddmore.libpharmml.dom.maths.Unioperator;
import eu.ddmore.libpharmml.dom.modeldefn.CommonParameter;
import eu.ddmore.libpharmml.dom.modeldefn.ContinuousCovariate;
import eu.ddmore.libpharmml.dom.modeldefn.CovariateDefinition;
import eu.ddmore.libpharmml.dom.modeldefn.CovariateTransformation;
import eu.ddmore.libpharmml.dom.modeldefn.GeneralObsError;
import eu.ddmore.libpharmml.dom.modeldefn.IndividualParameter;
import eu.ddmore.libpharmml.dom.modeldefn.ObservationError;
import eu.ddmore.libpharmml.dom.modeldefn.ParameterRandomVariable;
import eu.ddmore.libpharmml.dom.modeldefn.PopulationParameter;
import eu.ddmore.libpharmml.dom.modeldefn.Probability;
import eu.ddmore.libpharmml.dom.modeldefn.TransformedCovariate;
import eu.ddmore.libpharmml.dom.modeldefn.VariabilityLevelDefinition;

/**
 * Basic parser.
 */
public abstract class BaseParser extends Engine implements IParser {
	/**
	 * Generic unassigned scripting variable value.<br/>
	 * Default value for code generation event handlers.
	 */
	public static String unassigned_symbol = "@";
	
	private static void assign(Rhs rhs, Object o, Accessor a) {
		if (rhs == null || o == null) return;
		
		if (isConstant(o)) rhs.setConstant((Constant) o);
		else if (isLocalVariable(o)) rhs.setSymbRef(symbolRef((VariableDefinition) o, a));
		else if (isPopulationParameter(o)) rhs.setSymbRef(symbolRef((PopulationParameter) o, a));
		else if (isBinaryOperation(o)) rhs.setBinop((Binop) o); 
		else if (isDerivative(o)) rhs.setSymbRef(symbolRef((DerivativeVariable) o, a));
		else if (isString_(o)) rhs.setScalar(new StringValue((String) o));
		else if (isBoolean(o)) {
			Boolean b = (Boolean) o;
			if (b.booleanValue()) rhs.setScalar(new TrueBoolean());
			else rhs.setScalar(new FalseBoolean());
		}
		else if (isInteger(o)) rhs.setScalar(new IntValue((Integer) o));
		else if (isBigInteger(o)) {
			BigInteger v = (BigInteger) o;
			rhs.setScalar(new IntValue(v.intValue()));
		}
		else if (isDouble(o)) rhs.setScalar(new RealValue((Double) o));
		else if (isUnaryOperation(o)) rhs.setUniop((Uniop) o);
		else if (isIndependentVariable(o)) rhs.setSymbRef(symbolRef((IndependentVariable) o, a));
		else if (isIndividualParameter(o)) rhs.setSymbRef(symbolRef((IndividualParameter) o, a));
		else if (isFunctionCall(o)) rhs.setFunctionCall((FunctionCallType) o);
		else if (isScalarInterface(o)) rhs.setScalar((Scalar) o);
		else if (isPiecewise(o)) rhs.setPiecewise((Piecewise) o);
		else if (isSymbolReference(o)) rhs.setSymbRef((SymbolRef) o);
		else if (isDelay(o)) rhs.setDelay((Delay) o);
		else if (isSum(o)) rhs.setSum((Sum) o);
		else if (isJAXBElement(o)) {
			JAXBElement<?> element = (JAXBElement<?>) o;
			assign(rhs, element.getValue(), a);
		} else if (isMatrix(o)) rhs.setMatrix((Matrix) o);
		else if (isProduct(o)) rhs.setProduct((Product) o);
		else if (isSequence(o)) rhs.setSequence((Sequence) o);
		else if (isVector(o)) rhs.setVector((Vector) o);
		else if (isInterval(o)) rhs.setInterval((Interval) o);
		else if (isInterpolation(o)) rhs.setInterpolation((Interpolation) o);
		else if (isProbability(o)) rhs.setProbability((Probability) o);
		else if (isMatrixUnaryOperation(o)) rhs.setMatrixUniop((MatrixUniOp) o);
		else if (isMatrixSelector(o)) rhs.setMatrixSelector((MatrixSelector) o);
		else if (isVectorSelector(o)) rhs.setMatrixSelector((MatrixSelector) o);
		else if (isRhs(o)) {
			Rhs assign = (Rhs) o;
			assign(rhs, assign.getContent(), a);
		}
		else
			throw new UnsupportedOperationException("Unsupported Expression Term (value='" + o + "')");
	}
	
	private static Rhs rhs(Object o, Accessor a) {
		if (o == null) throw new NullPointerException("Expression Class NULL");
		Rhs rhs = new Rhs();
		assign(rhs, o, a);
		return rhs;
	}
	
	private static SymbolRef symbolRef(PharmMLRootType o, Accessor a) {
		String symbId = null;
		
		boolean addScope = false;
		
		if (isSymbolReference(o)) {
			return (SymbolRef) o;
		} else if (isCommonParameter(o)) { 
			symbId = ((CommonParameter) o).getSymbId();
			addScope = true;
		} else if (isLocalVariable(o)) {
			symbId = ((VariableDefinition) o).getSymbId();
			addScope = true;
		} else if (isDerivative(o)) { 
			symbId = ((DerivativeVariable) o).getSymbId();
			addScope = true;
		} else if (isIndividualParameter(o)) { 
			symbId = ((IndividualParameter) o).getSymbId();
			addScope = true;
		} else if (isRandomVariable(o)) {
			symbId = ((ParameterRandomVariable) o).getSymbId();
			addScope = true;
		} else if (isIndependentVariable(o)) {
			symbId = ((IndependentVariable) o).getSymbId();
		} else if (isCovariate(o)) {
			symbId = ((CovariateDefinition) o).getSymbId();
			addScope = true;
		} else if (isFunctionParameter(o)) {
			symbId = ((FunctionParameter) o).getSymbId();
		} else if (isFunction(o)) {
			symbId = ((FunctionDefinition) o).getSymbId();
		} else if (isObservationError(o)) {
			symbId = ((ObservationError) o).getSymbId();
			addScope = true;
		} else if (isColumnDefinition(o)) {
			symbId = ((ColumnDefinition) o).getColumnId();
			addScope = false;	
		} else if (isContinuousCovariate(o)) {
			ContinuousCovariate ccov = (ContinuousCovariate) o;
			
			// INFO: Assuming a unary application for this release. 
			for (CovariateTransformation trans : ccov.getListOfTransformation()) {
				if (trans == null) continue;
				
				TransformedCovariate tc = trans.getTransformedCovariate();
				if (tc == null) continue;
				
				symbId = tc.getSymbId();
				addScope = true;
				break;
			}
		} else if (isVariabilityLevelDefinition(o)) {
			VariabilityLevelDefinition level = (VariabilityLevelDefinition) o;
			symbId = level.getSymbId();
			addScope = true;
		}
		else if (isGeneralError(o)) {
			GeneralObsError goe = (GeneralObsError) o;
			symbId = goe.getSymbId();
			addScope = true;
		}
		else 
			throw new UnsupportedOperationException("Unsupported Symbol reference (src='" + o + "')");
		
		if (symbId == null) throw new NullPointerException("SymbId is NULL.");
		
		SymbolRef ref = new SymbolRef();
		ref.setSymbIdRef(symbId);
		
		if (addScope) {
			String blkId = a.getBlockId(o);
			if (blkId == null) {
				throw new NullPointerException("BlkId is not known (symbId='" + symbId + "', class='" + Utils.getClassName(o) + "')");
			}
			
			ref.setBlkIdRef(blkId);
		}
		
		return ref;
	}
	
	/**
	 * Default format for combining string element for a binary operator.
	 */
	protected String binary_operator_format_default = "(%s%s%s)";
	
	
	/**
	 * Format for combining 2 elements for a division.
	 */
	protected String binary_operator_format_divide = "(%s%s%s)";
	
	/**
	 * Format for combining 2 elements for a substraction.
	 */
	protected String binary_operator_format_minus = binary_operator_format_default;
	
	/**
	 * Format for combining 2 elements for an addition.
	 */
	protected String binary_operator_format_plus = binary_operator_format_default;
	
	/**
	 * Format for combining 2 elements for a power-based calculation.
	 */
	protected String binary_operator_format_power = "(%s%s%s)";
	
	/**
	 * Format for combining 2 elements for a multiplication.
	 */
	protected String binary_operator_format_times = "(%s%s%s)";
	
	private ByteArrayOutputStream buffer = new ByteArrayOutputStream(10240 * 4);
	
	/**
	 * Buffered print stream.
	 */
	protected PrintWriter buffer_stream = null;
	
	/**
	 * Language specific comment character.
	 */
	protected String comment_char = "";
	
	/**
	 *  Temporary variable name for a piecewise block.
	 *  @see eu.ddmore.libpharmml.dom.maths.Piecewise
	 */
	protected String field_tag = "OVERLY_LONG_FIELD_TAG_AS_VARIABLE_NAME_PLACE_HOLDER_IN_A_STATEMENT_BLOCK";
	
	/**
	 * Lexer instance bound to the parser.
	 */
	protected ILexer lexer = null;
	
	/**
	 * Format to calculate the LOGX binary operator.
	 * @see eu.ddmore.libpharmml.dom.maths.Binoperator#LOGX
	 */
	protected String logx_format = "(log(%s)/log(%s))";
	/**
	 * Format to calculate the MAX binary operator.
	 * @see eu.ddmore.libpharmml.dom.maths.Binoperator#MAX
	 */
	protected String max_format = "max(%s, %s)";
	
	/**
	 * Format to calculate the MIN binary operator.
	 * @see eu.ddmore.libpharmml.dom.maths.Binoperator#MIN
	 */
	protected String min_format = "min(%s, %s)";
	
	/**
	 * Objective data file suffix.
	 */
	protected String objective_dataset_file_suffix = null;
	
	/**
	 * Output file suffix.<br/>
	 * Language specific.
	 */
	protected String output_file_suffix = null;
	
	/**
	 * Format to calculate the REM binary operator.
	 * @see eu.ddmore.libpharmml.dom.maths.Binoperator#REM
	 */
	protected String rem_format = "rem(%s, %s)";
	
	/**
	 * Format to calculate the ROOT binary operator.
	 * @see eu.ddmore.libpharmml.dom.maths.Binoperator#ROOT
	 */
	protected String root_format = "(%s^(1.0/%s))";
	
	/**
	 * Run identifier, used to create output source code file names.
	 */
	protected String run_id = "run_id";
	
	/**
	 * Suffix for a generated code file.
	 */
	protected String script_file_suffix = null;
	
	/**
	 * Default solver symbol for the target language.
	 */
	protected String solver = null;
	
	/**
	 * Version flag written to script file.
	 */
	protected String version = "unspecified";
	
	/**
	 * Reserved word/Illegal Character filter.
	 */
	protected SymbolReader z = null;
	
	/**
	 * Constructor
	 * @throws IOException
	 */
	public BaseParser() throws IOException { buffer_stream = new PrintWriter(buffer); }
	/**
	 * Close any open I/O buffers.
	 * @throws IOException
	 */
	@Override
	public void cleanUp() throws IOException {
		if (buffer_stream != null) buffer_stream.close();
	}
	
	/**
	 * Write code representing a logical binary operator.
	 * @param l_b_op Operator
	 * @param leftStatement Left Statement
	 * @param rightStatement Right Statement
	 * @return String
	 */
	protected String doBinaryLogicalOperation(LogicBinOp l_b_op, String leftStatement, String rightStatement) {
		if (l_b_op == null || leftStatement == null || rightStatement == null) 
			throw new NullPointerException("Logical binary operation tree node data is NULL.");
		
		String format = "%s %s %s";
		String operator = getLogicalOperator(l_b_op.getOp());
		
		return String.format(format, leftStatement, operator, rightStatement);
	}
	
	/**
	 * Write code representing a binary operator.
	 * @param b_op Operator
	 * @param leftStatement Left Statement
	 * @param rightStatement Right Statement
	 * @return String
	 */
	protected String doBinaryOperation(Binop b_op, String leftStatement, String rightStatement) {
		if (b_op == null || leftStatement == null || rightStatement == null) 
			throw new NullPointerException("Binary operation tree node data is NULL.");
		
		String op = convertBinoperator(b_op);
		if (op == null) throw new NullPointerException("The binary operator is NULL.");
		if (op.equals(LOGX)) 
			return String.format(getLogXFormat(), leftStatement, rightStatement);
		else if (op.equals(ROOT)) 
			return String.format(getRootFormat(), leftStatement, rightStatement);
		else if (op.equals(MIN)) 
			return String.format(getMinFormat(), leftStatement, rightStatement);
		else if (op.equals(MAX)) 
			return String.format(getMaxFormat(), leftStatement, rightStatement);
		else if (op.equals(REM)) 
			return String.format(getRemFormat(), leftStatement, rightStatement);
		else {
			String operator = getScriptBinaryOperator(op);
			String format = getBinaryOperatorFormat(op);
			return String.format(format, leftStatement, operator, rightStatement);
		}
	}
	
	/**
	 * Generate code for a unary logical statement.
	 * @param l_u_op Operator
	 * @param leftStatement Left Statement
	 * @return String
	 */
	protected String doLogicicalUnaryOperation(LogicUniOp l_u_op, String leftStatement) {
		if (l_u_op == null || leftStatement == null) 
			throw new NullPointerException("Logical unary operation tree node data is NULL.");
		
		String format = "%s(%s)";
		String operator = getLogicalOperator(l_u_op.getOp());
		
		return String.format(format, operator, leftStatement);
	}
	
	/**
	 * Generate code for a unary function call.
	 * @param u_op
	 * @param leftStatement Statement
	 * @return String
	 */
	protected String doUnaryOperation(Uniop u_op, String leftStatement) {
		if (u_op == null || leftStatement == null) throw new NullPointerException("Unary operation tree node data is NULL.");
		
		Unioperator op = u_op.getOperator();
		String format = "%s(%s)";
		String operator = getUnaryOperator(convertUnioperator(op));
		
		return String.format(format, operator, leftStatement);
	}
	
	/**
	 * Get the formatting statement to combine elements for the standard 5 binary operations (+, *, /, -, ^) or
	 * set-based operators (.+, .*, .-, ./).
	 * @param op Operator Symbol
	 * @return java.lang.String Printf-style formatting macro.
	 * @see BaseParser#binary_operator_format_plus
	 * @see BaseParser#binary_operator_format_minus
	 * @see BaseParser#binary_operator_format_times
	 * @see BaseParser#binary_operator_format_divide
	 */
	protected String getBinaryOperatorFormat(String op) {
		String format = binary_operator_format_default;
		
		if (op.equalsIgnoreCase(PLUS) || op.equalsIgnoreCase(PLUS_SET)) format = binary_operator_format_plus;
		else if (op.equalsIgnoreCase(MINUS) || op.equalsIgnoreCase(MINUS_SET)) format = binary_operator_format_minus;
		else if (op.equalsIgnoreCase(TIMES) || op.equalsIgnoreCase(TIMES_SET)) format = binary_operator_format_times;
		else if (op.equalsIgnoreCase(DIVIDE) || op.equalsIgnoreCase(DIVIDE_SET)) format = binary_operator_format_divide;
		else if	(op.equalsIgnoreCase(POWER)) format = binary_operator_format_power;
		
		return format;
	}
	
	/**
	 * Get a list of functions created from the model.
	 * @return java.util.List<String>
	 */
	@Override
	public List<String> getGeneratedFunctionPaths() { return null; }
	
	/**
	 * Formatting string for LOGX
	 * @return String
	 */
	protected String getLogXFormat() { return logx_format; }
	
	/**
	 * Formatting string for MAX.
	 * @return String
	 */
	protected String getMaxFormat() { return max_format; }
	
	/**
	 * Formatting string for MIN.
	 * @return String
	 */
	protected String getMinFormat() { return min_format; }
	
	@Override
	public String getModelFunctionFilename(String output_dir, StructuralBlock sb) { return null; }
	
	/**
	 * Formatting for the REM binary operator.
	 * @return java.lang.String
	 */
	protected String getRemFormat() { return rem_format; }
	
	/**
	 * Formatting for the Root binary operator.
	 * @return java.lang.String
	 */
	protected String getRootFormat() { return root_format; }
	
	/**
	 * Get the language specific symbol for a named 'PharmML' operator.
	 * @param symbol
	 * @return java.lang.String A maths operator (e.g. '+', '-' ...).
	 */
	protected String getScriptBinaryOperator(String symbol) {
		String operation = null;

		if (symbol.equals(PLUS))  operation = " + ";
		else if (symbol.equals(DIVIDE))	operation = " / ";
		else if (symbol.equals(TIMES)) operation = " * "; 
		else if (symbol.equals(MINUS)) operation = " - ";
		else if (symbol.equals(POWER)) operation = "^";
		
		if (operation == null) throw new IllegalStateException("Operation not recognised.");

		return operation;
	}
	
	@Override
    public String getScriptFilename(String output_dir) {
        String format = "%s/%s.%s";
        return String.format(format, output_dir, run_id, script_file_suffix);
    }
	
	@Override
	public SymbolReader getSymbolReader() { return z; }
	
	@Override
	public String parse(Object context, BinaryTree bt) {
		String value = "";
		
		parse(context, bt, buffer_stream);
		buffer_stream.flush();
		value = buffer.toString();
		buffer.reset();
		
		return value;
	}
	
	@Override
	public void parse(Object context, BinaryTree bt, PrintWriter fout) {
		if (context == null || bt == null || fout == null) 
			throw new NullPointerException("Parse symbol NULL (" + context + ", " + bt + ", " + fout + ")");
		
		if (bt.size() == 0) throw new IllegalStateException("Binary tree is empty.");
		
		Node leaf = null;
		while ((leaf = bt.nextLeafNode()) != null && bt.size() > 0) {
			if (leaf.root) rootLeafHandler(context, leaf, fout);
			else if (leaf.parent != null) {
				if (isBinaryOperation(leaf.parent.data)) parseBinaryOperation(bt, leaf);
				else if (isUnaryOperation(leaf.parent.data)) parseUnaryOperation(bt, leaf);
				else if (isLogicalBinaryOperation(leaf.parent.data)) parseLogicalBinaryOperation(bt, leaf);
				else if (isLogicalUnaryOperation(leaf.parent.data)) parseLogicalUnaryOperation(bt, leaf);
			}
			
			bt.remove(leaf);
		}
	}
	
	protected void parseBinaryOperation(BinaryTree bt, Node leaf) {
		Binop b_op = (Binop) leaf.parent.data;
		Node parent = leaf.parent;
		if (parent.left != null && parent.right != null) {
			String leftStatement = "";
			if (parent.left.stmt != null) leftStatement = parent.left.stmt;
			else leftStatement = getSymbol(parent.left.data);
			parent.left = null;
			bt.remove(parent.left);

			String rightStatement = "";
			if (parent.right.stmt != null) rightStatement = parent.left.stmt;
			else rightStatement = getSymbol(parent.right.data);
			parent.right = null;
			bt.remove(parent.right);

			parent.data = doBinaryOperation(b_op, leftStatement, rightStatement);
		}
	}
	
	protected void parseLogicalBinaryOperation(BinaryTree bt, Node leaf) {
		LogicBinOp l_b_op = (LogicBinOp) leaf.parent.data;
		Node parent = leaf.parent;
		if (parent.left != null && parent.right != null) {
			String leftStatement = "";
			if (parent.left.stmt != null) leftStatement = parent.left.stmt;
			else leftStatement = getSymbol(parent.left.data);
			parent.left = null;
			bt.remove(parent.left);

			String rightStatement = "";
			if (parent.right.stmt != null) rightStatement = parent.left.stmt;
			else rightStatement = getSymbol(parent.right.data);
			parent.right = null;
			bt.remove(parent.right);
			
			parent.data = doBinaryLogicalOperation(l_b_op, leftStatement, rightStatement);
		}
	}
	
	protected void parseLogicalUnaryOperation(BinaryTree bt, Node leaf) {
		LogicUniOp l_u_op = (LogicUniOp) leaf.parent.data;
		Node parent = leaf.parent;
		if (parent.left != null) {
			String leftStatement = "";
			
			if (parent.left.stmt != null) leftStatement = parent.left.stmt;
			else leftStatement = getSymbol(parent.left.data);
			parent.left = null;
			bt.remove(parent.left);

			parent.data = doLogicicalUnaryOperation(l_u_op, leftStatement);
		}
	}
	
	protected String parseRawEquation(Object op) {
		String symbol = unassigned_symbol;
		
		Accessor a = lexer.getAccessor();
		Rhs assign = null;
		if (isBinaryOperation(op) || isUnaryOperation(op)) assign = rhs(op, a);
		else throw new UnsupportedOperationException("Operator not recognised in raw equation (op='" + op +  "')");
		
		TreeMaker tm = lexer.getTreeMaker();
		symbol = parse(new Object(), tm.newInstance(assign));
				
		return symbol;
	}
	
	protected void parseUnaryOperation(BinaryTree bt, Node leaf) {
		Uniop u_op = (Uniop) leaf.parent.data;
		Node parent = leaf.parent;
		if (parent.left != null) {
			String leftStatement = "";
			
			if (parent.left.stmt != null) leftStatement = parent.left.stmt;
			else leftStatement = getSymbol(parent.left.data);
			parent.left = null;
			bt.remove(parent.left);

			parent.data = doUnaryOperation(u_op, leftStatement);
		}
	}
	
	@Override
	public void removeAbsolutePaths(File f) throws IOException {
		// Do nothing as paths, string delimiters language specific
		// so method needs to be overridden in a parser instance.
	}
	
	abstract protected void rootLeafHandler(Object context, Node leaf, PrintWriter fout);
	
	@Override
	public void setLexer(ILexer lexer_) { 
		if (lexer_ == null) throw new NullPointerException("The lexer is NULL.");
		lexer = lexer_;
		z = new SymbolReader(lexer); 
	}
	
	@Override
	public void setNumberSimulationSteps(int nSteps) { throw new UnsupportedOperationException(); }
	
	@Override
	public void setPharmMLWrittenVersion(String version_) {
		if (version != null) version = version_; 
	}
	
	@Override
	public void setRunId(String id) {
		if (id == null)
			throw new IllegalStateException("Run ID cannot be NULL");
		else
			id = id.trim();

		run_id = id.replaceAll("[^a-zA-Z0-9.-]", "_");
		if (run_id.length() == 0)
			throw new IllegalStateException("The Run ID cannot be a zero-length string.");
	}
	
	/**
	 * Set the solver handle.
	 * @param function_name
	 */
	public void setSolver(String function_name) {
		if (function_name != null) solver = function_name;
	}
	
	/**
	 * Use binary operator formatting with a more conservative scheme where all calculation elements are
	 * bracketed. This scheme will run any PharmML model without precedence issues.
	 * Models generated by computer, i.e. SBML Algebraic rule, need the conservative scheme to avoid
	 * funnies in the generated equations such as nested unaries.
	 * Override as appropriate. 
	 */
	protected void useConversativeBinaryOperatorFormats() {
		binary_operator_format_default = "%s%s%s";
		binary_operator_format_divide = "((%s)%s(%s))";
		binary_operator_format_minus = binary_operator_format_default;
		binary_operator_format_plus = binary_operator_format_default;
		binary_operator_format_power = "((%s)%s(%s))";
		binary_operator_format_times = "((%s)%s(%s))";
	}
	
	@Override
	public void writeADMEEstimationBlock(PrintWriter fout, File output_dir, EstimationStep est) throws IOException {
		// Empty method, add language specific content.
	}
	
	@Override
	public void writeADMEEstimationWithDosingBlock(PrintWriter fout, File output_dir, EstimationStep est) throws IOException {}
	
	@Override
	public void writeADMESimulationBlock(PrintWriter fout, File output_dir, SimulationStep ss) throws IOException {}
	
	@Override
	public void writeADMESimulationWithDosingBlock(PrintWriter fout, File output_dir, SimulationStep ss) throws IOException {
		// Empty method, add language specific content.
	}
	
	@Override
	public void writeCategoricalEstimationBlock(PrintWriter fout, File output_dir, EstimationStep est) throws IOException {}
	
	@Override
	public void writeCategoricalSimulationBlock(PrintWriter fout, File output_dir, SimulationStep ss) throws IOException {}
	
	@Override
	public void writeDiscreteEstimationBlock(PrintWriter fout, File output_dir, EstimationStep est) throws IOException {}
	
	@Override 
	public void writeDiscreteSimulationBlock(PrintWriter fout, File output_dir, SimulationStep ss) throws IOException {}

	@Override
	public void writeEOF(PrintWriter fout) {
		if (fout == null) throw new NullPointerException();
		fout.write("\n\n");
	}
	
	@Override
	public void writeMainBlockBOF(PrintWriter fout) {}
	
	@Override
	public void writeMainBlockEOF(PrintWriter fout) {}
	
    @Override
	public void writeMainBlockInitialisation(PrintWriter fout) throws IOException {}
    
    @Override
	public void writeMixedEffectEstimationBlock(PrintWriter fout, File output_dir, EstimationStep est) throws IOException {
		String format = "\n%s INFO: Code block for Mixed effect model = ESTIMATION\n";
		fout.write(String.format(format, comment_char));
	}
    
    @Override
	public void writeMixedEffectSimulationBlock(PrintWriter fout, File output_dir, SimulationStep ss) throws IOException {}
	
	@Override
	public void writeNONMEMMixedEffectEstimationBlock(PrintWriter fout, File output_dir, EstimationStep est) throws IOException {
		if (fout == null) return;
		String format = "\n%s INFO: code block for estimation with NONMEM dastaset.\n";
		fout.write(String.format(format, comment_char));
	}
	
	@Override
	public void writeNONMEMMixedEffectSimulationBlock(PrintWriter fout, File output_dir, SimulationStep est) throws IOException {
		if (fout == null) return;
		String format = "\n%s INFO: code block for simulation with NONMEM dastaset.\n";
		fout.write(String.format(format, comment_char));
	}
	
	@Override
	public void writePreMainBlockElements(PrintWriter fout, File src) throws IOException{
		// Empty method, add language specific content.
	}
	
	/**
	 * Generic script header.<br/>
	 * @param fout Output stream to the output file.
	 * @param model_file Path to PharmML source file. Set to NULL if doing in-memory conversion.
	 * @throws IOException
	 */
	protected void writeScriptHeader(PrintWriter fout, String model_file) throws IOException {
		if (fout == null) return;

		String format = "%s Script generated by the 'Cyprotex Converter Provider Interface' (CCoPI)\n";
		fout.write(String.format(format, comment_char));

		format = "%s 'CCoPI' copyright (c) Cyprotex Discovery Ltd (2016)\n";
		fout.write(String.format(format, comment_char));
		
		format = "%s Converter Version: %s\n";
		fout.write(String.format(format, comment_char, lexer.getConverterVersion()));
		
		format = "%s Source: %s\n";
		fout.write(String.format(format, comment_char, lexer.getSource()));
		
		format = "%s Target: %s\n";
		fout.write(String.format(format, comment_char, lexer.getTarget()));

		format = "%s Run ID: %s\n";
		fout.write(String.format(format, comment_char, run_id));
		
		String title = lexer.getModelName();
		if (title != null) {
			format = "%s Model: %s\n";
			fout.write(String.format(format, comment_char, title));
		}

		format = "%s File: %s\n";
		fout.write(String.format(format, comment_char, model_file));

		format = "%s Dated: %s\n\n";
		fout.write(String.format(format, comment_char, new Date()));
	}
	
	@Override
	public void writeUntranslatedPKMacroEstimationBlock(PrintWriter fout, File output_dir, EstimationStep est) {
		// Empty method, add language specific content.
	}
	
	@Override
	public void writeUntranslatedPKMacroSimulationBlock(PrintWriter fout, File output_dir, SimulationStep est) 
	throws IOException 
	{
		// Empty method, add language specific content.
	}
}
