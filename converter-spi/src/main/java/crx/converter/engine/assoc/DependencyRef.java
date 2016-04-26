/*******************************************************************************
 * Copyright (C) 2015 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.engine.assoc;

import static crx.converter.engine.PharmMLTypeChecker.isDerivative;
import static crx.converter.engine.PharmMLTypeChecker.isPharmMLObject;
import static crx.converter.engine.PharmMLTypeChecker.isSymbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import crx.converter.engine.common.StateVariableRef;
import eu.ddmore.libpharmml.dom.commontypes.DerivativeVariable;
import eu.ddmore.libpharmml.dom.commontypes.PharmMLElement;
import eu.ddmore.libpharmml.dom.commontypes.PharmMLRootType;
import eu.ddmore.libpharmml.dom.commontypes.Symbol;
import eu.ddmore.libpharmml.dom.tags.PharmMLObject;

/**
 * Reference to a model element calculation dependencies.
 */
public class DependencyRef {
	private static void copyIn(PharmMLElement [] src, List<PharmMLElement> dst) {
		dst.clear();
		for (int i = 0;  i  < src.length; i++) dst.add(src[i]);
	}
	
	private static PharmMLElement[] copyOut(List<PharmMLElement> src) {
		PharmMLElement [] arr = new PharmMLElement[src.size()];
		for (int i = 0; i < src.size(); i++) arr[i] = src.get(i);
		return arr;
	}
	
	/**
	 * Create a distinct list of model elements under consideration.
	 * @param refs Dependency records
	 * @return List<PharmMLElement>
	 */
	public static List<PharmMLElement> createElementsUnderConsideration(List<DependencyRef> refs) {
		List<PharmMLElement> elements_under_consideration = new ArrayList<PharmMLElement>();
			
		if (refs != null) {
			for (DependencyRef ref : refs) {
				if (ref == null) continue;
				PharmMLElement element = (PharmMLRootType) ref.getElement();
				if (element != null) {
					if (!elements_under_consideration.contains(element)) elements_under_consideration.add(element);
				}
			}
		}
			
		return elements_under_consideration;
	}
	
	/**
	 * Read the identifier symbol from a PharmML element.
	 * @param o
	 * @return String
	 */
	public static String readIdentifier(PharmMLElement o) {
		String name = "unknown";
		
		if (isSymbol(o)) {
			Symbol symbol = (Symbol) o;
			name = symbol.getSymbId();
		} else if (isPharmMLObject(o)) {
			PharmMLObject obj = (PharmMLObject) o;
			name = obj.getOid();
		}
		
		return name;
	}
	
	/**
	 * Update the dependency records to only list dependencies in the consideration list.<br/>
	 * Prevents empty branch formation in the dependency graph.
	 * Also adjusts on "state" variable references are considered in a dependency tree.
	 * @param elements_under_consideration
	 * @param refs Element references
	 */
	public static void updateDependencyContext(List<PharmMLElement> elements_under_consideration, List<DependencyRef> refs) {
		List<StateVariableRef> sv_refs = new ArrayList<StateVariableRef>();
		
		if (elements_under_consideration == null || refs == null) return;
		
		// Scan through the dependency lists, looking for derivatives.
		// If find any, create a state variable reference.
		
		Map<DerivativeVariable, StateVariableRef> dv_map = new HashMap<DerivativeVariable, StateVariableRef>();
		for (DependencyRef ref : refs) {
			PharmMLElement element = ref.getElement();
			if (isDerivative(element)) {
				DerivativeVariable dv = (DerivativeVariable) element;
				if (!dv_map.containsKey(dv)) dv_map.put(dv, new StateVariableRef(dv));
			}
		}
		for (StateVariableRef sv_ref : dv_map.values()) sv_refs.add(sv_ref);
		
		// If derivatives in the sort set, update the dependency records to state variable references.
		if (!sv_refs.isEmpty()) {
			// Update the dependency list for derivative, reset to a state variable reference.
			for (DependencyRef ref : refs) {
				if (ref == null) continue;
				
				// Using bounded arrays to stop any heap funnies with dynamic list growth.
				// Enforces 'bounded' variable lists.
				PharmMLElement [] deps = copyOut(ref.getDependsUpon());
				
				for (int i = 0; i < deps.length; i++) {
					PharmMLElement dep = deps[i];
					if (isDerivative(dep)) {
						DerivativeVariable dv = (DerivativeVariable) dep;
						if (dv_map.containsKey(dv)) deps[i] = dv_map.get(dv);
					}
				}
				
				copyIn(deps, ref.getDependsUpon());
			}
			
			// Add the state variable reference to dependency list and the elements under consideration list.
			for (StateVariableRef sv_ref : sv_refs) refs.add(new DependencyRef(sv_ref));
			elements_under_consideration.addAll(sv_refs);
		}
	
		for (DependencyRef ref : refs) {
			if (ref == null) continue;
			
			List<PharmMLElement> depends_upon = ref.getDependsUpon();
			if (depends_upon == null) continue;
			if (depends_upon.isEmpty()) continue;
			
			List<Object> dependencies_to_purge = new ArrayList<Object>(); 
			for (PharmMLElement dependency : depends_upon) {
				if (dependency == null) continue;
				if (!elements_under_consideration.contains(dependency)) dependencies_to_purge.add(dependency);
			}
			
			if (!dependencies_to_purge.isEmpty()) depends_upon.removeAll(dependencies_to_purge);
		}
	}
	
	private List<PharmMLElement> depends_upon = new ArrayList<PharmMLElement>();
	private PharmMLElement element = null;
	private String id = "unassigned";
	public int index = -1;
	
	/**
	 * Constructor
	 * @param element_ Model Element under consideration.
	 */
	public DependencyRef(PharmMLElement element_) {
		if (element_ == null) throw new NullPointerException("Model element is NULL");
		element = (PharmMLElement) element_;
		id = readIdentifier(element);
	}
	
	/**
	 * Add a dependency to an element reference.
	 * @param e Model Element the referenced element depends upon.
	 */
	public void addDependency(PharmMLElement e) {
		if (e  != null) {
			if (!e.equals(element)) {
				if (!depends_upon.contains(e)) depends_upon.add(e);
			}
		}
	}
	
	/**
	 * Flag if the referenced variable depends upon a model element.
	 * @return boolean 
	 */
	public boolean depends(DependencyRef ref) {
		if (ref == null) return false;
		
		PharmMLElement element_ = ref.getElement();
		if (element.equals(element_)) return true;
		
		if (element_ != null) return depends_upon.contains(element_);
		else return false;
	}
	
	/**
	 * Get a list of model elements the referenced element depends upon.
	 * @return java.util.List<eu.ddmore.libpharmml.dom.commontypes.PharmMLElement>
	 */
	public List<PharmMLElement> getDependsUpon() { return depends_upon; }

	/**
	 * Get the referenced element.
	 * @return eu.ddmore.libpharmml.dom.commontypes.PharmMLElement
	 */
	public PharmMLElement getElement() { return element; }
	
	/**
	 * Get the name of the model element under consideration.
	 * @return String
	 */
	public String getName() {
		if (element instanceof StateVariableRef) return "REF_2_" + id;
		return id; 
	}
	
	/**
	 * Return a list of all the model elements contained in the dependency record.
	 * @return java.util.List<PharmMLElement>
	 */
	public List<PharmMLElement> getReferencedElements() {
		List<PharmMLElement> list = new ArrayList<PharmMLElement>(); 
		list.add(getElement());
		if (hasDependendsUpon()) list.addAll(getDependsUpon());
		
		return list;
	}
	
	/**
	 * Flag to indicate if referenced elements needs something else in a model
	 * @return boolean
	 */
	public boolean hasDependendsUpon() { return depends_upon.size() > 0; }
	
	/**
	 * Get the referenced element.
	 * @param element_ Model Element under consideration
	 */
	public void setElement(PharmMLElement element_) { 
		if (element_ == null) throw new NullPointerException("The referenced element cannot be NULL.");
		element = element_;
	}
	
	/**
	 * Get the size of the dependency list.
	 * @return int
	 */
	public int size() { return depends_upon.size(); }
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("dependency_ref("); 
		sb.append(hashCode());
		sb.append(")=");
			
		sb.append("(Element=");
		sb.append(getName());
		sb.append(", ");
		sb.append("Depends=[");
		
		int i = 0;
		for (PharmMLElement o : depends_upon) {
			if (i > 0) sb.append(", ");
			sb.append(readIdentifier(o));
			i++;
		}
		
		sb.append("])");
			
		return sb.toString();
	}
}