package de.prob.animator.prologast;

import java.util.List;
import java.util.Objects;

import com.google.common.base.MoreObjects;

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

	@Override
	public String toString(){
		return MoreObjects.toStringHelper(this)
			.add("subnodes", this.getSubnodes())
			.add("name", this.getName())
			.add("expanded", this.isExpanded())
			.add("propagated", this.isPropagated())
			.toString();
	}
}
