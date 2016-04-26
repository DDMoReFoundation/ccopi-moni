/*******************************************************************************
* Copyright (C) 2015 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.engine;

import static crx.converter.engine.PharmMLTypeChecker.isDerivative;
import static crx.converter.engine.PharmMLTypeChecker.isLocalVariable;
import static crx.converter.engine.PharmMLTypeChecker.isPopulationParameter;
import static crx.converter.engine.PharmMLTypeChecker.isRootType;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import eu.ddmore.libpharmml.dom.PharmML;
import eu.ddmore.libpharmml.dom.commontypes.DerivativeVariable;
import eu.ddmore.libpharmml.dom.commontypes.Matrix;
import eu.ddmore.libpharmml.dom.commontypes.Rhs;
import eu.ddmore.libpharmml.dom.commontypes.VariableDefinition;
import eu.ddmore.libpharmml.dom.maths.Binop;
import eu.ddmore.libpharmml.dom.maths.Binoperator;
import eu.ddmore.libpharmml.dom.maths.LogicBinOp;
import eu.ddmore.libpharmml.dom.maths.Unioperator;
import eu.ddmore.libpharmml.dom.modeldefn.PopulationParameter;

/**
 * Super class of the converter classes.
 *
 */
public abstract class BaseEngine {
	/**
	 * ATAN2
	 * @see Binoperator#ATAN2
	 */
	public static final String ATAN2 = Binoperator.ATAN2.toString();
	
	/**
	 * DIVIDE
	 * @see Binoperator#DIVIDE
	 */
	public static final String DIVIDE = Binoperator.DIVIDE.toString();
	
	/**
	 * Set-based DIVIDE
	 * @see Binoperator#DIVIDE
	 */
	public static final String DIVIDE_SET = "divide_set";
	
	/**
	 * Converter operation field name.<br/>
	 * Flag if dosing value should be added or replace a numeric value in the state vector. 
	 */
	public static final String DoseReplaceCurrentValue = "DoseReplaceCurrentValue";
	
	/**
	 * Logical Unary Not
	 */
	public static final String LOGICAL_UNARY_NOT = "not";
	
	/**
	 * LOGX
	 * @see Binoperator#LOGX
	 */
	public static final String LOGX = Binoperator.LOGX.toString();
	
	/**
	 * MAX
	 * @see Binoperator#MAX
	 */
	public static final String MAX =  Binoperator.MAX.toString();
	
	/**
	 * Maximum step permitted when parsing mathematical expressions.
	 */
	public static final int MAX_STEP_COUNT = 20000;
	
	/**
	 * MIN
	 * @see Binoperator#MIN
	 */
	public static final String MIN =  Binoperator.MIN.toString();
	
	/**
	 * MINUS
	 * @see Binoperator#MINUS
	 */
	public static final String MINUS = Binoperator.MINUS.toString();
	
	/**
	 * Set-based MINUS
	 * @see Binoperator#MINUS
	 */
	public static final String MINUS_SET = "minus_set";
	
	/**
	 * PLUS
	 * @see Binoperator#PLUS
	 */
	public static final String PLUS = Binoperator.PLUS.toString();

	/**
	 * Set-based PLUS
	 * @see Binoperator#PLUS
	 */
	public static final String PLUS_SET = "plus_set";
	
	/**
	 * POWER
	 * @see Binoperator#POWER
	 */
	public static final String POWER = Binoperator.POWER.toString();
	
	/**
	 * REM Modulus Operator
	 * @see Binoperator#REM
	 */
	public static final String REM = Binoperator.REM.toString();
	
	/**
	 * ROOT
	 * @see Binoperator#ROOT
	 */
	public static final String ROOT = Binoperator.ROOT.toString();
	
	/**
	 * TIMES
	 * @see Binoperator#TIMES
	 */
	public static final String TIMES = Binoperator.TIMES.toString();
	
	/**
	 * Set-based TIMES
	 * @see Binoperator#TIMES
	 */
	public static final String TIMES_SET = "times_set";
	
	/**
	 * Checks is Object is a Java BigInteger type.
	 * @param o
	 * @return boolean
	 * @see java.math.BigInteger
	 */
	public static boolean isBigInteger(Object o) {
		return (o instanceof BigInteger) ;
	}
	
	/**
	 * Checks is Object is a Java Boolean type.
	 * @param o
	 * @return boolean
	 * @see java.lang.Boolean
	 */
	public static boolean isBoolean(Object o) {
		return (o instanceof Boolean) ;
	}
	
	/**
	 * Checks is Object is a Java Double type.
	 * @param o
	 * @return boolean
	 * @see java.lang.Double
	 */
	public static boolean isDouble(Object o) {
		return (o instanceof Double) ;
	}
	/**
	 * Checks is Object is a Java Integer type.
	 * @param o
	 * @return boolean
	 * @see java.lang.Integer
	 */
	public static boolean isInteger(Object o) {
		return (o instanceof Integer) ;
	}
	/**
	 * Checks is Object is a Java String type.
	 * @param o
	 * @return boolean
	 * @see java.lang.String
	 */
	public static boolean isString_(Object o) {
		return (o instanceof String) ;
	}
	
	/**
	 * Read a matrix (if any) from a model element.
	 * @param src
	 * @return Matrix or null
	 */
	public static Matrix readMatrix(Object src) {
		if (src == null) return null;
		if (!isRootType(src)) return null;
		
		if (isDerivative(src)) {
			DerivativeVariable dv = (DerivativeVariable) src;
			Rhs rhs = dv.getAssign();
			if (rhs != null) return rhs.getMatrix();
		} else if (isLocalVariable(src)) {
			VariableDefinition v = (VariableDefinition) src;
			Rhs rhs = v.getAssign();
			if (rhs != null) return rhs.getMatrix();
		} else if (isPopulationParameter(src)) {
			PopulationParameter p = (PopulationParameter) src;
			Rhs rhs = p.getAssign();
			if (rhs != null) return rhs.getMatrix();
		}
		
		return null;
	}
	
	private ArrayList<String> binary_operators_list = new ArrayList<String>();
	
	/**
	 * A binary operators map.<br/>
	 * PharmML symbol to target symbol.
	 * @see Binop
	 */
	protected Map<Binop, String> binop_set_map = new HashMap<Binop, String>();
	
	/**
	 * The reference class where resource links are evaluated.
	 */
	protected Class<? extends BaseEngine> cls = null;
	
	/**
	 * PharmML model referenced by a converter engine.
	 */
	protected PharmML dom = null;
	
	private HashMap<String, String> function_map = new HashMap<String, String>();
	
	private ArrayList<String> logic_operators_list = new ArrayList<String>();
	
	/**
	 * A logical operators map.<br/>
	 * PharmML symbol to target tool symbol.
	 * @see LogicBinOp
	 */
	protected HashMap<String, String> logic_operators_map = new HashMap<String, String>();
	private ArrayList<String> unary_operators_list = new ArrayList<String>();
	
	/**
	 * Default constructor.
	 */
	public BaseEngine() {
		setReferenceClass(getClass());
		init();
	}
	
	/**
	 * Adds a PharmML symbol to the logical operators map for a target language.
	 * @param pharmmml_symbol
	 * @param operator
	 * @throws UnsupportedOperationException If operator symbol not valid.
	 * @return boolean Success or Failure
	 */
	protected boolean addLogicalOperator(String pharmmml_symbol, String operator) {
		if (pharmmml_symbol != null && operator != null) {
			if (!(isSupportedLogicalUnaryOp(pharmmml_symbol) || isSupportedLogicalBinaryOp(pharmmml_symbol)))
				throw new UnsupportedOperationException("The symbol '" + pharmmml_symbol + "' is not defined in PharmML.");
			
			if (!logic_operators_map.containsKey(pharmmml_symbol)) logic_operators_map.put(pharmmml_symbol, operator);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Applies a set-based conversion to binary operator if required by an error model.
	 * @param b_op Binary operator
	 * @return String
	 * @see #PLUS_SET
	 * @see #MINUS_SET
	 * @see #TIMES_SET
	 * @see #DIVIDE_SET
	 */
	protected String convertBinoperator(Binop b_op) {
		if (binop_set_map.containsKey(b_op)) return binop_set_map.get(b_op);
		
		Binoperator op = b_op.getOperator();
		String symbol = null;
		if (op != null) symbol = op.toString();
		return symbol;
	}
	
	/**
	 * Applies a string lower-case typecast to a PharmML unsry operator.
	 * @param op PharmML unary operator (a.k.a. maths function).
	 * @return String
	 * @see Unioperator
	 */
	protected String convertUnioperator(Unioperator op) {
		String symbol = null;
		if (op != null) symbol = op.toString().toLowerCase();
		return symbol;
	}
	
	/**
	 * Returns the PharmML model bound to a engine.
	 * @return PharmML
	 */
	public PharmML getDom() { return dom; }
	
	/**
	 * Get logical operator symbol for a target language.
	 * @param pharmml_symbol PharmML logical operator symbol.
	 * @return String
	 * @see LogicBinOp
	 */
	protected String getLogicalOperator(String pharmml_symbol) {
		String operation = null;
		if (pharmml_symbol == null) throw new NullPointerException();
		if (!(isSupportedLogicalBinaryOp(pharmml_symbol) || isSupportedLogicalUnaryOp(pharmml_symbol))) 
			throw new UnsupportedOperationException("The logical operator (" + pharmml_symbol + ") is not supported.");
		else
			operation = logic_operators_map.get(pharmml_symbol);
		
		return operation;
	}
	
	/**
	 * Get a target specific maths function name when given a PharmML operator symbol.
	 * @param op PharmML unary operator (a.k.a. maths function).
	 * @return String
	 * @throws UnsupportedOperationException If operator symbol not implemented in the target language.
	 * @see Unioperator
	 */
	protected String getUnaryFunction(String op) {
		String func = "";
		if (op == null) throw new NullPointerException();
		if (!isSupportedUnaryOp(op)) 
			throw new UnsupportedOperationException("The operation (" + op + ") is not supported.");
		else 
			func = function_map.get(op);
		
		return func;
	}
	
	/**
	 * Get a target specific unary operator (maths function) based on a PharmML symbol.
	 * @param name PharmML name of a standard maths function.
	 * @return String
	 */
	protected String getUnaryOperator(String name) {
		if (name == null) throw new NullPointerException("Unary operator name is NULL.");
		String op = null;
		
		if (function_map.containsKey(name)) op = function_map.get(name);
		if (op == null) op = name.toLowerCase();
		if (op.equalsIgnoreCase("minus")) op = "-"; // Unary minus adjustment.
		
		return op;
	}
	
	/**
	 * Load the local configuration files and operator maps.
	 */
	protected void init() {
		initUnaryOperatorsList();
		initBinaryOperatorsList();
		initLogicalOperatorsList();
		
		// Read the local operator files.
		initSupportedUnaryOperators();
		initSupportedLogicOperators();
	}
	
	private void initBinaryOperatorsList() {
		String [] operators = {PLUS, MINUS, TIMES, DIVIDE, POWER, LOGX, ROOT, MIN, MAX, REM, ATAN2};
		for (String operator : operators) binary_operators_list.add(operator);
	}
	
	private void initLogicalOperatorsList() {
		String [] operators = {  "lt", "leq", "gt", "geq",  "eq", "neq", "and", "or", "xor" };
		for (String operator : operators) logic_operators_list.add(operator);
	}
	
	private void initSupportedLogicOperators() {
		try {
			InputStream resource = cls.getResourceAsStream("logic_operators_list.txt");
			if (resource == null) return;
			
			BufferedReader in= new BufferedReader(new InputStreamReader(resource));
			String line = null;
			ArrayList<String> lines = new ArrayList<String>(); 
			while ((line = in.readLine()) != null) lines.add(line);
			in.close();
			in = null;
			
			for (String line_ : lines) {
				if (line_ == null) continue;
				line_ = line_.trim();
				String [] ops = line_.split(" ");
				if (ops.length != 2) continue;
				ops[0] = ops[0].trim();
				ops[1] = ops[1].trim();
				if (logic_operators_list.contains(ops[0])) logic_operators_map.put(ops[0], ops[1]);
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	private void initSupportedUnaryOperators() {
		try {
			InputStream resource = cls.getResourceAsStream("unary_operators_list.txt");
			if (resource == null) return;
			
			BufferedReader in = new BufferedReader(new InputStreamReader(resource));
			String line = null;
			ArrayList<String> lines = new ArrayList<String>(); 
			while ((line = in.readLine()) != null) lines.add(line);
			in.close();
			in = null;
			
			for (String line_ : lines) {
				if (line_ == null) continue;
				line_ = line_.trim();
				String [] functions = line_.split(" ");
				if (functions.length != 2) continue;
				functions[0] = functions[0].trim();
				functions[1] = functions[1].trim();
				
				if (unary_operators_list.contains(functions[0])) function_map.put(functions[0], functions[1]);
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void initUnaryOperatorsList() {
		String [] operators = { 
			"abs", "exp", "factorial", "factln", "gammaln", "ln", "log",
			"logistic", "logit", "normcdf", "probit",
			"minus", "sqrt", "sin", "cos", "tan", "sec", "csc",
			"cot", "sinh", "cosh", "tanh", "sech", "csch", "coth",
			"arcsin", "arccos", "arctan", "arcsec", "arccsc", "arccot", "arcsinh",
			"arccosh", "arctanh", "arcsech", "arccsch", "arccoth", "floor", "ceiling", "sleep"
			};
		
		for (String operator : operators) unary_operators_list.add(operator);
	}
	
	/**
	 * Check if the binary operator is supported in a target language.
	 * @param op Operator name (PharmML)
	 * @return boolean
	 * @see Binoperator
	 */
	protected boolean isSupportedBinaryOp(String op) {
		boolean isSupported = false;

		if (op != null) isSupported = binary_operators_list.contains(op);
		
		return isSupported;
	}
	
	/**
	 * Check if the logical binary operator is supported in a target language.
	 * @param op Operator name (PharmML)
	 * @return boolean
	 * @see LogicBinOp
	 */
	protected boolean isSupportedLogicalBinaryOp(String op) {
		boolean isSupported = false;
		if (op != null) isSupported = logic_operators_list.contains(op);
		return isSupported;
	}
	
	/**
	 * Check if the logical binary operator is supported in a target language.
	 * @param op Operator name (PharmML)
	 * @return boolean
	 * @see eu.ddmore.libpharmml.dom.maths.LogicUniOp
	 */
	protected boolean isSupportedLogicalUnaryOp(String op) {
		boolean isSupported = false;
		if (op != null) isSupported = op.equals(LOGICAL_UNARY_NOT);
		return isSupported;
	}
	
	/**
	 * Check if the unary operator is supported in a target language.
	 * @param op Operator name (PharmML)
	 * @return boolean
	 * @see Unioperator
	 */
	protected boolean isSupportedUnaryOp(String op) {
		boolean isSupported = false;

		if (op != null) isSupported = unary_operators_list.contains(op);
		
		return isSupported;
	}
	
	/**
	 * Set the reference class for loading resource and configuration files.
	 * @param cls_ BaseEngine derived class.
	 */
	protected void setReferenceClass(Class<? extends BaseEngine> cls_) { cls = cls_; }
	
	/**
	 * Try to remove at least 1 set of superfluous brackets from a statement.
	 * @return String
	 */
	protected String stripOuterBrackets(String stmt) {
		if (stmt == null) return null;
		stmt = stmt.trim();
		
		if (stmt.startsWith("(") && stmt.endsWith(")")) {
			int indexOfOpenBracket = stmt.indexOf("(");
			int indexOfLastBracket = stmt.lastIndexOf(")");
			stmt = stmt.substring(indexOfOpenBracket+1, indexOfLastBracket);
		}
		
		return stmt;
	}
}
