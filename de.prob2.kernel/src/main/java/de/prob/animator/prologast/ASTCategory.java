package de.prob.animator.prologast;

import java.util.List;
import java.util.Objects;

public final class ASTCategory extends PrologASTNode{
	private final String name;
	private final boolean expanded;
	private final boolean propagated;

	ASTCategory(List<PrologASTNode> subnodes, String name, boolean expanded, boolean propagated) {
		super(subnodes);
		
		Objects.requireNonNull(name, "name");
		
		this.name = name;
		this.expanded = expanded;
		this.propagated = propagated;
	}

	public boolean isExpanded(){
		return expanded;
	}

	public boolean isPropagated(){
		return propagated;
	}

	public String getName(){
		return name;
	}

	public String toString(){
		return "\n[Category] : "+this.name+((isExpanded())?("\n[expanded]"):"\n[]")+((isPropagated())?("\n[propagated]"):"\n[]");
	}
}
