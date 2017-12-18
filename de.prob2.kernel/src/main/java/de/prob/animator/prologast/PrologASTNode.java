package de.prob.animator.prologast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * PrologASTNode used to simplify the structure given by prolog
 */
public abstract class PrologASTNode {
	private final List<PrologASTNode> subnodes;

	PrologASTNode(List<PrologASTNode> subnodes) {
		Objects.requireNonNull(subnodes, "subnodes");
		
		this.subnodes = new ArrayList<>(subnodes);
	}

	public List<PrologASTNode> getSubnodes() {
		return Collections.unmodifiableList(subnodes);
	}

	public String toString(){
		return "\n[Node]";
	}
}
