/*******************************************************************************
 * Copyright (C) 2015 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.engine.parts;

/**
 * Wraps a model element so that numerically ordered model elements can be sorted.
 */
public class SortableElement implements Comparable<SortableElement> {
	private Object element = null;
	private double value = 0;
	
	/**
	 * Constructor
	 * @param element_ Model Element
	 */
	public SortableElement(Object element_) { element = element_; }
	
	/**
	 * Constructor
	 * @param element_ Model Element
	 * @param value_ Index Value in a list that needs sorting.
	 */
	public SortableElement(Object element_, double value_) {
		element = element_;
		value = value_;
	}
	
	@Override
	public int compareTo(SortableElement o) {
		if (o == null) throw new NullPointerException();
		else {
			double other_number = o.getValue();
			if (value == other_number) return 0;
			else if (value < other_number) return -1;
			else if (value > other_number) return 1;
		}
		
		return 0;
	}
	
	/**
	 * Retrieve the element bound to the sortable element.
	 * @return java.lang.Object
	 */
	public Object getElement() {
		return element;
	}

	private double getValue() { return value; }
	
	/**
	 * Set the numerical value of the sortable element.
	 * @param value_
	 */
	public void setValue(double value_) { value = value_; }
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("{value=");
		sb.append(value);
		sb.append(",element=");
		sb.append(element);
		sb.append("}");
		
		return sb.toString();
	}
}
