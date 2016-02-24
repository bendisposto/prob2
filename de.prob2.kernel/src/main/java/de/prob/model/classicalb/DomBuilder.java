package de.prob.model.classicalb;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
import de.be4.classicalb.core.parser.node.APreconditionSubstitution;
import de.be4.classicalb.core.parser.node.APredicateParseUnit;
import de.be4.classicalb.core.parser.node.APropertiesMachineClause;
import de.be4.classicalb.core.parser.node.ASelectSubstitution;
import de.be4.classicalb.core.parser.node.ASubstitutionParseUnit;
import de.be4.classicalb.core.parser.node.AVariablesMachineClause;
import de.be4.classicalb.core.parser.node.EOF;
import de.be4.classicalb.core.parser.node.Node;
import de.be4.classicalb.core.parser.node.PExpression;
import de.be4.classicalb.core.parser.node.PPredicate;
import de.be4.classicalb.core.parser.node.PSubstitution;
import de.be4.classicalb.core.parser.node.Start;
import de.be4.classicalb.core.parser.node.TIdentifierLiteral;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.model.representation.Action;
import de.prob.model.representation.BEvent;
import de.prob.model.representation.Constant;
import de.prob.model.representation.Guard;
import de.prob.model.representation.Invariant;
import de.prob.model.representation.ModelElementList;
import de.prob.model.representation.Variable;

public class DomBuilder extends DepthFirstAdapter {

	private static final EOF EOF = new EOF();
	private String name;
	private final List<Parameter> parameters = new ArrayList<Parameter>();
	private final List<Constraint> constraints = new ArrayList<Constraint>();
	private final List<ClassicalBConstant> constants = new ArrayList<ClassicalBConstant>();
	private final List<Property> properties = new ArrayList<Property>();
	private final List<ClassicalBVariable> variables = new ArrayList<ClassicalBVariable>();
	private final List<ClassicalBInvariant> invariants = new ArrayList<ClassicalBInvariant>();
	private final List<de.prob.model.representation.Set> sets = new ArrayList<de.prob.model.representation.Set>();
	private final List<Assertion> assertions = new ArrayList<Assertion>();
	private final List<Operation> operations = new ArrayList<Operation>();
	private final Set<String> usedIds = new HashSet<String>();
	private String prefix;
	private LinkedList<TIdentifierLiteral> machineId;

	public DomBuilder(final String prefix) {
		this.prefix = prefix;
	}

	@Override
	public void outStart(final Start node) {
		super.outStart(node);
	}

	public ClassicalBMachine build(final Start ast) {
		((Start) ast.clone()).apply(this);
		return getMachine();
	}

	public LinkedList<TIdentifierLiteral> getMachineId() {
		return machineId;
	}

	public ClassicalBMachine getMachine() {
		ClassicalBMachine machine = new ClassicalBMachine(name);
		machine = machine.set(Assertion.class, new ModelElementList<Assertion>(assertions));
		machine = machine.set(Constant.class, new ModelElementList<ClassicalBConstant>(constants));
		machine = machine.set(Constraint.class, new ModelElementList<Constraint>(constraints));
		machine = machine.set(Property.class, new ModelElementList<Property>(properties));
		machine = machine.set(Invariant.class, new ModelElementList<ClassicalBInvariant>(invariants));
		machine = machine.set(Parameter.class, new ModelElementList<Parameter>(parameters));
		machine = machine.set(de.prob.model.representation.Set.class, new ModelElementList<de.prob.model.representation.Set>(sets));
		machine = machine.set(Variable.class, new ModelElementList<ClassicalBVariable>(variables));
		machine = machine.set(BEvent.class, new ModelElementList<Operation>(operations));
		return machine;
	}

	@Override
	public void outAMachineHeader(final AMachineHeader node) {
		name = extractIdentifierName(node.getName());
		machineId = node.getName();
		if (prefix != null && !prefix.equals(name)) {
			name = prefix + "." + name;
		}
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
			if (prefix != null) {
				usedIds.add(((AIdentifierExpression) pExpression)
						.getIdentifier().get(0).getText());
			}
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
		sets.add(new de.prob.model.representation.Set(new ClassicalB(
				extractIdentifierName(node.getIdentifier()))));
	}

	@Override
	public void outAEnumeratedSetSet(final AEnumeratedSetSet node) {
		sets.add(new de.prob.model.representation.Set(new ClassicalB(
				extractIdentifierName(node.getIdentifier()))));
	}

	@Override
	public void outAAssertionsMachineClause(final AAssertionsMachineClause node) {
		for (PPredicate pPredicate : getPredicates(node)) {
			assertions.add(new Assertion(createPredicateAST(pPredicate)));
		}
	}

	@Override
	public void inAOperation(final AOperation node) {
		String name = extractIdentifierName(node.getOpName());
		if (prefix != null && !prefix.equals(name)) {
			name = prefix + "." + name;
		}
		LinkedList<PExpression> paramIds = node.getParameters();
		final List<String> params = extractIdentifiers(paramIds);
		final List<String> output = extractIdentifiers(node.getReturnValues());
		Operation operation = new Operation(name, params, output);
		PSubstitution body = node.getOperationBody();
		List<ClassicalBGuard> guards = new ArrayList<ClassicalBGuard>();
		if (body instanceof ASelectSubstitution) {
			PPredicate condition = ((ASelectSubstitution) body).getCondition();
			List<PPredicate> predicates = getPredicates(condition);
			for (PPredicate pPredicate : predicates) {
				guards.add(new ClassicalBGuard(createPredicateAST(pPredicate)));
			}
		}
		if (body instanceof APreconditionSubstitution) {
			PPredicate condition = ((APreconditionSubstitution) body)
					.getPredicate();
			List<PPredicate> predicates = getPredicates(condition);
			for (PPredicate pPredicate : predicates) {
				guards.add(new ClassicalBGuard(createPredicateAST(pPredicate)));
			}
		}
		List<ClassicalBAction> actions = new ArrayList<ClassicalBAction>();
		actions.add(new ClassicalBAction(createSubstitutionAST(body)));
		operation = operation.set(Action.class,new ModelElementList<ClassicalBAction>(actions));
		operation = operation.set(Guard.class, new ModelElementList<ClassicalBGuard>(guards));

		operations.add(operation);
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
		start.setEOF(EOF);
		node.setExpression((PExpression) expression.clone());
		node.getExpression().apply(new RenameIdentifiers());
		return start;
	}

	private Start createPredicateAST(final PPredicate pPredicate) {
		Start start = new Start();
		APredicateParseUnit node2 = new APredicateParseUnit();
		start.setPParseUnit(node2);
		start.setEOF(EOF);
		node2.setPredicate((PPredicate) pPredicate.clone());
		node2.getPredicate().apply(new RenameIdentifiers());
		return start;
	}

	private Start createSubstitutionAST(final PSubstitution pSub) {
		Start start = new Start();
		ASubstitutionParseUnit node2 = new ASubstitutionParseUnit();
		start.setPParseUnit(node2);
		start.setEOF(EOF);
		node2.setSubstitution((PSubstitution) pSub.clone());
		node2.getSubstitution().apply(new RenameIdentifiers());
		return start;
	}

	private class RenameIdentifiers extends DepthFirstAdapter {
		@Override
		public void inAIdentifierExpression(final AIdentifierExpression node) {
			if (prefix != null) {
				String id = node.getIdentifier().get(0).getText();

				if (usedIds.contains(id)) {
					node.getIdentifier().set(0,
							new TIdentifierLiteral(prefix + "." + id));
				}
			}
		}
	}
}
