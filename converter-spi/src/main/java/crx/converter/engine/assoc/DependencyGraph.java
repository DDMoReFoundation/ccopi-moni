/*******************************************************************************
 * Copyright (C) 2015 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.engine.assoc;

import static crx.converter.engine.PharmMLTypeChecker.isRootType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import crx.converter.engine.Accessor;
import crx.converter.engine.common.StateVariableRef;
import eu.ddmore.libpharmml.dom.commontypes.PharmMLElement;
import eu.ddmore.libpharmml.dom.commontypes.PharmMLRootType;

/**
 * Topological graph.
 * Original code from this page.
 * http://www.java2s.com/Code/Java/Collections-Data-Structure/Topologicalsorting.htm
 */
public class DependencyGraph {
	private class Vertex {
		public String label;

		  public Vertex(String lab) {
		    label = lab;
		  }
	}
	
	private Accessor a = null;
	
	private Map<String, DependencyRef> label_map = new HashMap<String, DependencyRef>();
	private Map<Object, DependencyRef> map = new HashMap<Object, DependencyRef>();
	private int matrix[][]; // adjacency matrix
	private int MAX_VERTS = 0;
	private DependencyRef [] num = null;
	private int numVerts; // current number of vertices
	private List<String> sorted_labels = new ArrayList<String>();
	private String sortedArray[];
	private Vertex vertexList[]; // list of vertices

	/**
	 * Constructor
	 * @param refs List of dependency references
	 */
	public DependencyGraph(List<DependencyRef> refs) {
		num = toArray(refs);
		MAX_VERTS = num.length;
		vertexList = new Vertex[MAX_VERTS];
		matrix = new int[MAX_VERTS][MAX_VERTS];
		numVerts = 0;
		for (int i = 0; i < MAX_VERTS; i++)
			for (int k = 0; k < MAX_VERTS; k++)
				matrix[i][k] = 0;
		
		sortedArray = new String[MAX_VERTS]; // vertex labels
	}
	
	/**
	 * Add edge to the graph
	 * @param start Index of Vertex
	 * @param end Index of Vertrix
	 */
	private void addEdge(int start, int end) { matrix[start][end] = 1; }

	/**
	 * Add Vertex to the graph
	 * @param lab Label of the Vertex
	 */
	private void addVertex(String lab) { vertexList[numVerts++] = new Vertex(lab); }

	/**
	 * Add edges to the dependency graph.
	 * @return int Number of edges.
	 */
	public int createEdges() {
		int edgeCount = 0;
		for (int i = 0; i < num.length; i++) {
			if (num[i].hasDependendsUpon()) {
				for (PharmMLElement dep : num[i].getDependsUpon()) {
					DependencyRef dep_ref = map.get(dep);
					addEdge(num[i].index, dep_ref.index);
					edgeCount++;
				}
			}
		}
		
		return edgeCount;
	}

	/**
	 * Add vertices to the dependency graph.<br/>
	 * Vertex name is a combination of the symbol identifier combined with the bulk identifier.
	 */
	public void createVertices() {
		for (int i = 0; i < num.length; i++) {
			addVertex(num[i].getName());
			map.put(num[i].getElement(), num[i]);
			
			String label = num[i].getName();
			
			if (a != null && isRootType(num[i].getElement())) {
				String blkId = a.getBlockId((PharmMLRootType) num[i].getElement());
				if (blkId != null) label = blkId + "_" + label ;
			}
			
			label_map.put(label, num[i]);
		}
	}
	
	/**
	 * Delete a vertex
	 * @param delVert Vertex Index
	 */
	private void deleteVertex(int delVert) {
		if (delVert != numVerts - 1) // if not last vertex, delete from vertexList
		{
			for (int j = delVert; j < numVerts - 1; j++) vertexList[j] = vertexList[j + 1];
			for (int row = delVert; row < numVerts - 1; row++) moveRowUp(row, numVerts);
			for (int col = delVert; col < numVerts - 1; col++) moveColLeft(col, numVerts - 1);
		}
		
		numVerts--; // one less vertex
	}
	
	/**
	 * Get the label map, associating an object name to a dependency record.
	 * @return Map<String, DependencyRef>
	 */
	public Map<String, DependencyRef> getLabelMap() { return label_map; }
	
	/**
	 * Get the list of sorted elements.
	 * @return List<PharmMLElement>
	 */
	public List<PharmMLElement> getSortedElements() {
		List<PharmMLElement> list = new ArrayList<PharmMLElement>();
		
		for (String label : getSortedLabels()) {
			if (label_map.containsKey(label)) {
				DependencyRef dep = label_map.get(label);
				if (dep.getElement() instanceof StateVariableRef) continue;
				list.add(dep.getElement());
			}
		}
		
		return list;
	}
	
	/**
	 * Get the list of sorted vertex labels.
	 * @return List<String>
	 */
	private List<String> getSortedLabels() {
		Collections.reverse(sorted_labels);
		return sorted_labels; 
	}
	
	private void moveColLeft(int col, int length) {
		for (int row = 0; row < length; row++)
			matrix[row][col] = matrix[row][col + 1];
	}
	
	private void moveRowUp(int row, int length) {
		for (int col = 0; col < length; col++) matrix[row][col] = matrix[row + 1][col];
	}
	
	private int noSuccessors() // returns vert with no successors (or -1 if no such verts)
	{ 
		boolean isEdge; // edge from row to column in adjMat

		for (int row = 0; row < numVerts; row++) {
			isEdge = false; // check edges
			for (int col = 0; col < numVerts; col++) {
				if (matrix[row][col] > 0) // if edge to another,
				{
					isEdge = true;
					break; // this vertex has a successor try another
				}
			}
			if (!isEdge) // if no edges, has no successors
				return row;
		}
		
		return -1; // no
	}

	/**
	 * Set the accessor handle for model element.
	 * @param a_ Accessor handle
	 */
	public void setAccessor(Accessor a_) { if (a_ != null) a = a_; }
	
	/**
	 * Sort the dependency graph.
	 */
	public void sort() // toplogical sort
	{
		int orig_nVerts = numVerts; 

		while (numVerts > 0) // while vertices remain,
		{
			// get a vertex with no successors, or -1
			int currentVertex = noSuccessors();
			if (currentVertex == -1) // must be a cycle
			{
				throw new IllegalStateException("ERROR: Graph has cycles");
			}
			// insert vertex label in sorted array (start at end)
			sortedArray[numVerts - 1] = vertexList[currentVertex].label;

			deleteVertex(currentVertex); // delete vertex
		}

		// vertices all gone; display sortedArray
		sorted_labels.clear();
		for (int j = 0; j < orig_nVerts; j++) sorted_labels.add(sortedArray[j]);
	}

	private DependencyRef [] toArray(List<DependencyRef> refs) {
		DependencyRef [] arr = new DependencyRef [refs.size()];
		for (int i = 0; i < refs.size(); i++) {
			arr[i] = refs.get(i);
			arr[i].index = i;
		}
		
		return arr;
	}
}
