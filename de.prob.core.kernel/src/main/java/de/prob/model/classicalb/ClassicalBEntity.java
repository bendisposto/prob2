package de.prob.model.classicalb;

import java.util.ArrayList;
import java.util.List;

import de.be4.classicalb.core.parser.node.Node;
import de.prob.model.representation.FormulaUUID;
import de.prob.model.representation.IFormula;

public class ClassicalBEntity implements IFormula {

	private final String identifier;
	private final Node astPart;
	private final FormulaUUID uuid = new FormulaUUID();

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
	public FormulaUUID getId() {
		return uuid;
	}

	@Override
	public List<IFormula> getSubcomponents() {
		// FIXME Implement method to get subformulas
		return new ArrayList<IFormula>();
	}

	@Override
	public boolean toEvaluate() {
		return true;
	}

}
