/*******************************************************************************
 * Copyright (C) 2015 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.engine;

/**
 * Class utility functions.
 */
public abstract class Utils {
	/**
	 * Get the class name of an object.
	 * @param o Instance Variable
	 * @return java.lang.String
	 */
	public static String getClassName(Object o) {
		if (o == null) return null;
		
		Class<?> c = o.getClass();
		String FQClassName = c.getName();
		int firstChar;
		firstChar = FQClassName.lastIndexOf ('.') + 1;
		if ( firstChar > 0 ) {
			FQClassName = FQClassName.substring ( firstChar );
		}
		return FQClassName;
	}
	
	/**
	 * Get the class name of an object.
	 * @param o
	 * @return java.lang.String
	 */
	public static String getPackageName(Object o) {
		if (o == null) return "null";
		
		Class<?> c = o.getClass();
		String fullyQualifiedName = c.getName();
		int lastDot = fullyQualifiedName.lastIndexOf ('.');
		if (lastDot==-1){ return ""; }
		return fullyQualifiedName.substring (0, lastDot);
	}
}
