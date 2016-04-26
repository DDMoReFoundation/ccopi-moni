/*******************************************************************************
 * Copyright (C) 2016 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.tree;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Binary tree wrapping a mathematical or data type primitives.
 */
public class BinaryTree {
	/**
	 * Model element bound to the tree.
	 */
	public Object element = null;
	
	private boolean isReversed = false;
	
	/**
	 * A list of nodes contained within the tree.
	 */
	public ArrayList<Node> nodes = new ArrayList<Node>(); 
	
	public BinaryTree() {}
	
	/**
	 * Add a node to the tree.
	 * @param o Tree node
	 */
	public void add(Node o) { nodes.add(o); }
	
	/**
	 * Echo the node list to an output stream.
	 * @param out Output stream.
	 */
	public void echo(PrintStream out) { for (Node node : nodes) out.println(node); }
	
	/**
	 * Return the next node in node list.
	 * @return Node
	 */
	public Node nextLeafNode() {
		if (!isReversed) {
			Collections.reverse(nodes);
			isReversed = true;
		}
		
		Node node = null;
		if (nodes.size() == 1)
			node = nodes.get(0); // Root node.
		else if (nodes.size() > 0) {
			for (Node o : nodes) {
				if (o.left == null && o.parent != null) {
					node = o;
					break;
				}
			}
		} 
		
		return node;
	}
	
	/**
	 * Remove a node from the node list.
	 * @param node Node
	 */
	public void remove(Node node) {
		if (node == null) return;
		if (nodes.contains(node)) nodes.remove(node);
	}
	
	/**
	 * Number of nodes in the binary tree.
	 * @return int
	 */
	public int size() { return nodes.size(); }
}
