package com.upgma.cluster;


/**
 * Branch node class
 * The branch node represents a branching node in the UPGMA tree
 * A branch node has two "child nodes" which can be other branches or leaf nodes
 * Once the tree has been constructed, the Newick format string representing the tree structure can be
 * generated by recursively calling the getPos() on the branch node
 * 
 * @param child1:	The first child node 
 * @param child2:	The second child node
 */
public class Branch extends Node {
	Node branch1;
	Node branch2;
	
	public Branch(Node child1, Node child2) {
		branch1 = child1;
		branch2 = child2;
	}
		
	/**
	 * Get Position
	 * Recursively call the position of all child nodes until a leaf node is reached.
	 * This generates a newick format string representing the tree structure
	 */
	@Override
	public String getPos() {
		String pos = "(" + branch1.getPos() + "," + branch2.getPos() + ")";
		return pos;
	}
	
	@Override
	public String getID() {
		String id = branch1.getID() + "," + branch2.getID();
		return id;
	}
	
}
