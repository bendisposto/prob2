package de.prob.model.classicalb;

import java.util.ArrayList;
import java.util.List;

import de.be4.classicalb.core.parser.node.Node;
import de.prob.model.representation.AbstractDomTreeElement;

public class ClassicalBEntity extends AbstractDomTreeElement {

	private final String identifier;
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
		final PrettyPrinter prettyPrinter = new PrettyPrinter();
		astPart.apply(prettyPrinter);
		return prettyPrinter.getPrettyPrint();
	}

	@Override
	public String getLabel() {
		return toString();
	}


	@Override
	public List<AbstractDomTreeElement> getSubcomponents() {
		return new ArrayList<AbstractDomTreeElement>();
	}

	@Override
	public boolean toEvaluate() {
		return true;
	}

}
