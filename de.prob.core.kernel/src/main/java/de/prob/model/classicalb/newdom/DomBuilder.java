package de.prob.model.classicalb.newdom;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Joiner;

import de.be4.classicalb.core.parser.analysis.DepthFirstAdapter;
import de.be4.classicalb.core.parser.node.AAssertionsMachineClause;
import de.be4.classicalb.core.parser.node.AConstantsMachineClause;
import de.be4.classicalb.core.parser.node.AConstraintsMachineClause;
import de.be4.classicalb.core.parser.node.ADeferredSetSet;
import de.be4.classicalb.core.parser.node.AEnumeratedSetSet;
import de.be4.classicalb.core.parser.node.AExpressionParseUnit;
import de.be4.classicalb.core.parser.node.AIdentifierExpression;
import de.be4.classicalb.core.parser.node.AInvariantMachineClause;
import de.be4.classicalb.core.parser.node.AMachineHeader;
import de.be4.classicalb.core.parser.node.AOperation;
import de.be4.classicalb.core.parser.node.APredicateParseUnit;
import de.be4.classicalb.core.parser.node.APropertiesMachineClause;
import de.be4.classicalb.core.parser.node.AVariablesMachineClause;
import de.be4.classicalb.core.parser.node.Node;
import de.be4.classicalb.core.parser.node.PExpression;
import de.be4.classicalb.core.parser.node.PPredicate;
import de.be4.classicalb.core.parser.node.Start;
import de.be4.classicalb.core.parser.node.TIdentifierLiteral;
import de.prob.model.classicalb.PredicateConjunctionSplitter;

public class DomBuilder extends DepthFirstAdapter {

	private String name;
	private final List<Parameter> parameters = new ArrayList<Parameter>();
	private final List<Constraint> constraints = new ArrayList<Constraint>();
	private final List<ClassicalBConstant> constants = new ArrayList<ClassicalBConstant>();
	private final List<Property> properties = new ArrayList<Property>();
	private final List<ClassicalBVariable> variables = new ArrayList<ClassicalBVariable>();
	private final List<ClassicalBInvariant> invariants = new ArrayList<ClassicalBInvariant>();
	private final List<ClassicalBSet> sets = new ArrayList<ClassicalBSet>();
	private final List<Assertion> assertions = new ArrayList<Assertion>();
	private final List<Operation> operations = new ArrayList<Operation>();

	@Override
	public void outStart(final Start node) {
		super.outStart(node);
	}

	public ClassicalBMachine build(final Start ast) {
		ast.apply(this);
		return getMachine();
	}

	public ClassicalBMachine getMachine() {
		ClassicalBMachine machine = new ClassicalBMachine(name);
		machine.addAssertions(assertions);
		machine.addConstants(constants);
		machine.addConstraints(constraints);
		machine.addProperties(properties);
		machine.addInvariants(invariants);
		machine.addParameters(parameters);
		machine.addSets(sets);
		machine.addVariables(variables);
		machine.addOperations(operations);
		return machine;
	}

	@Override
	public void outAMachineHeader(final AMachineHeader node) {
		name = extractIdentifierName(node.getName());
		for (PExpression expression : node.getParameters()) {
			parameters.add(new Parameter(createExpressionAST(expression)));
		}
	}

	@Override
	public void outAConstraintsMachineClause(
			final AConstraintsMachineClause node) {
		List<PPredicate> predicates = getPredicates(node);
		for (PPredicate pPredicate : predicates) {
			constraints.add(new Constraint(createPredicateAST(pPredicate)));
		}
	}

	@Override
	public void outAConstantsMachineClause(final AConstantsMachineClause node) {
		for (PExpression pExpression : node.getIdentifiers()) {
			constants.add(new ClassicalBConstant(
					createExpressionAST(pExpression)));
		}
	}

	@Override
	public void outAPropertiesMachineClause(final APropertiesMachineClause node) {
		for (PPredicate pPredicate : getPredicates(node)) {
			properties.add(new Property(createPredicateAST(pPredicate)));
		}
	}

	@Override
	public void outAVariablesMachineClause(final AVariablesMachineClause node) {
		for (PExpression pExpression : node.getIdentifiers()) {
			variables.add(new ClassicalBVariable(
					createExpressionAST(pExpression)));
		}
	}

	@Override
	public void outAInvariantMachineClause(final AInvariantMachineClause node) {
		List<PPredicate> predicates = getPredicates(node);
		for (PPredicate pPredicate : predicates) {
			invariants.add(new ClassicalBInvariant(
					createPredicateAST(pPredicate)));
		}
	}

	@Override
	public void outADeferredSetSet(final ADeferredSetSet node) {
		sets.add(new ClassicalBSet(extractIdentifierName(node.getIdentifier())));
	}

	@Override
	public void outAEnumeratedSetSet(final AEnumeratedSetSet node) {
		sets.add(new ClassicalBSet(extractIdentifierName(node.getIdentifier())));
	}

	@Override
	public void outAAssertionsMachineClause(final AAssertionsMachineClause node) {
		for (PPredicate pPredicate : getPredicates(node)) {
			assertions.add(new Assertion(createPredicateAST(pPredicate)));
		}
	}

	@Override
	public void inAOperation(final AOperation node) {
		final String name = extractIdentifierName(node.getOpName());
		final List<String> params = extractIdentifiers(node.getParameters());
		final List<String> output = extractIdentifiers(node.getReturnValues());
		operations.add(new Operation(name, params, output));
	}

	// -------------------

	private String extractIdentifierName(
			final LinkedList<TIdentifierLiteral> nameL) {
		String text;
		if (nameL.size() == 1) {
			text = nameL.get(0).getText();
		} else {
			final ArrayList<String> list = new ArrayList<String>();
			for (final TIdentifierLiteral t : nameL) {
				list.add(t.getText());
			}
			text = Joiner.on(".").join(list);
		}
		return text;
	}

	private List<String> extractIdentifiers(
			final LinkedList<PExpression> identifiers) {
		final List<String> params = new ArrayList<String>();
		for (PExpression pExpression : identifiers) {
			if (pExpression instanceof AIdentifierExpression) {
				params.add(extractIdentifierName(((AIdentifierExpression) pExpression)
						.getIdentifier()));
			}
		}
		return params;
	}

	private List<PPredicate> getPredicates(final Node node) {
		final PredicateConjunctionSplitter s = new PredicateConjunctionSplitter();
		node.apply(s);
		return s.getPredicates();
	}

	private Start createExpressionAST(final PExpression expression) {
		Start start = new Start();
		AExpressionParseUnit node = new AExpressionParseUnit();
		start.setPParseUnit(node);
		node.setExpression(expression);
		return start;
	}

	private Start createPredicateAST(final PPredicate pPredicate) {
		Start start = new Start();
		APredicateParseUnit node2 = new APredicateParseUnit();
		start.setPParseUnit(node2);
		node2.setPredicate(pPredicate);
		return start;
	}
}
