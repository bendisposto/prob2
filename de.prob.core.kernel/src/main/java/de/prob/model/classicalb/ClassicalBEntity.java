package de.prob.model.classicalb;

import de.be4.classicalb.core.parser.node.Node;

public class ClassicalBEntity {

	private String identifier;
	private final Node astPart;

	public ClassicalBEntity(final String name, final Node id) {
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
		return identifier;
	}

}
