/*******************************************************************************
 * Copyright (C) 2015 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.engine;

/**
 * Enumeration on how an empty variable declaration in a structural model is used.<br/>
 * For example flag whether a variable is used in a PK macro, or a dose time or a dose target.
 */
public enum VariableDeclarationContext {
	/**
	 * The variable has an assignment so the context in the model is known.
	 */
	ASSIGNED("assigned"),
	
	/**
	 * The variable is acting as a dose target variable.
	 */
	DOSE("dose"),
	
	/**
	 * The variable is acting as a NONMEM style dosing time (DT) variable.
	 */
	DT("dosing_time"),
	
	/**
	 * Flag that a variable is an externally assigned local.
	 * Basically that means a globally accessible variable set with a value read from a file.
	 * In CCoPI-speak, the variable usage has global scope.
	 */
	GLOBAL_SCOPE("global_scope"),
	
	/**
	 * The scope of the variable is not known.<br>
	 * This may indicate that the input model might be malformed.
	 * Typically this will be trapped by a sub-system in the CC.
	 */
	UNKNOWN("unknown");
	
	/**
	 * Create enumeration value from a string.
	 * @param v
	 * @return VariableDeclarationContext
	 */
	public static VariableDeclarationContext fromValue(String v) {
        for (VariableDeclarationContext c: VariableDeclarationContext.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
	
	private final String value;
	
	private VariableDeclarationContext(String v) { value = v; }

    public String value() { return value; }
}