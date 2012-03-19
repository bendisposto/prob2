package de.prob.model.representation;

import de.be4.classicalb.core.parser.node.AIdentifierExpression;

public class NamedEntity {

	private String identifier;
	private final AIdentifierExpression astPart;

	public NamedEntity(final String name, final AIdentifierExpression id) {
		this.identifier = name;
		this.astPart = id;
	}

	public String getIdentifier() {
		return identifier;
	}

	public AIdentifierExpression getIdentifierExpression() {
		return astPart;
	}

}
