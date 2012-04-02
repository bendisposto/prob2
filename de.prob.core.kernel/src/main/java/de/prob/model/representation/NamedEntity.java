package de.prob.model.representation;

import de.be4.classicalb.core.parser.node.Node;

public class NamedEntity {

	private String identifier;
	private final Node astPart;

	public NamedEntity(final String name, final Node id) {
		this.identifier = name;
		this.astPart = id;
	}

	public String getIdentifier() {
		return identifier;
	}

	public Node getIdentifierExpression() {
		return astPart;
	}

	@Override
	public String toString() {
		return identifier + "->" + astPart.getClass().getSimpleName();
	}

}
