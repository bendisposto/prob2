package de.prob.model.classicalb;

import java.util.ArrayList;
import java.util.LinkedList;

import com.google.common.base.Joiner;

import de.be4.classicalb.core.parser.analysis.DepthFirstAdapter;
import de.be4.classicalb.core.parser.exceptions.BException;
import de.be4.classicalb.core.parser.node.AAssertionsMachineClause;
import de.be4.classicalb.core.parser.node.AConstantsMachineClause;
import de.be4.classicalb.core.parser.node.AConstraintsMachineClause;
import de.be4.classicalb.core.parser.node.ADeferredSetSet;
import de.be4.classicalb.core.parser.node.AEnumeratedSetSet;
import de.be4.classicalb.core.parser.node.AIdentifierExpression;
import de.be4.classicalb.core.parser.node.AInvariantMachineClause;
import de.be4.classicalb.core.parser.node.AMachineHeader;
import de.be4.classicalb.core.parser.node.AOperation;
import de.be4.classicalb.core.parser.node.APropertiesMachineClause;
import de.be4.classicalb.core.parser.node.AVariablesMachineClause;
import de.be4.classicalb.core.parser.node.Node;
import de.be4.classicalb.core.parser.node.PExpression;
import de.be4.classicalb.core.parser.node.PPredicate;
import de.be4.classicalb.core.parser.node.Start;
import de.be4.classicalb.core.parser.node.TIdentifierLiteral;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.model.representation.Label;

public class DomBuilder extends DepthFirstAdapter {

	private final ClassicalBMachine machine;

	@Override
	public void outStart(final Start node) {
		super.outStart(node);
		machine.close();
	}

	public DomBuilder(final ClassicalBMachine machine) {
		this.machine = machine;
	}

	public ClassicalBMachine build(final Start ast) {
		ast.apply(this);
		return machine;
	}

	@Override
	public void outAMachineHeader(final AMachineHeader node) {
		machine.setName(extractIdentifierName(node.getName()));
		addIdentifiers(node.getParameters(), machine.parameters);
	}

	@Override
	public void outAConstraintsMachineClause(
			final AConstraintsMachineClause node) {
		addPredicates(node, machine.constraints);
	}

	@Override
	public void outAConstantsMachineClause(final AConstantsMachineClause node) {
		addIdentifiers(node.getIdentifiers(), machine.constants);
	}

	@Override
	public void outAPropertiesMachineClause(final APropertiesMachineClause node) {
		addPredicates(node, machine.properties);
	}

	@Override
	public void outAVariablesMachineClause(final AVariablesMachineClause node) {
		addIdentifiers(node.getIdentifiers(), machine.variables);
	}

	@Override
	public void outAInvariantMachineClause(final AInvariantMachineClause node) {
		addPredicates(node, machine.invariants);
	}

	@Override
	public void outADeferredSetSet(final ADeferredSetSet node) {
		try {
			machine.sets.addChild(new ClassicalB(extractIdentifierName(node
					.getIdentifier())));
		} catch (final BException e) {
			// Will not be reached because the set is syntactically correct
			e.printStackTrace();
		}
	}

	@Override
	public void outAEnumeratedSetSet(final AEnumeratedSetSet node) {
		try {
			machine.sets.addChild(new ClassicalB(extractIdentifierName(node
					.getIdentifier())));
		} catch (final BException e) {
			// Should not reach this point because the set is syntactically
			// correct
			e.printStackTrace();
		}
	}

	@Override
	public void outAAssertionsMachineClause(final AAssertionsMachineClause node) {
		addPredicates(node, machine.assertions);
	}

	@Override
	public void inAOperation(final AOperation node) {
		final String name = extractIdentifierName(node.getOpName());
		final Label params = addIdentifiers(node.getParameters(), new Label(
				"Parameters"));
		final Label output = addIdentifiers(node.getReturnValues(), new Label(
				"Output"));
		machine.operations.addChild(new Operation(name, params, output));
	}

	// -------------------

	private String prettyprint(final Node predicate) {
		final PrettyPrinter prettyPrinter = new PrettyPrinter();
		predicate.apply(prettyPrinter);
		return prettyPrinter.getPrettyPrint();
	}

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

	private void addPredicates(final Node node, final Label section) {
		final PredicateConjunctionSplitter s = new PredicateConjunctionSplitter();
		node.apply(s);
		for (final PPredicate predicate : s.getPredicates()) {
			try {
				section.addChild(new ClassicalB(prettyprint(predicate)));
			} catch (final BException e) {
				// It should not reach this point because the predicate is valid
				// Classical B code
				e.printStackTrace();
			}
		}
	}

	private Label addIdentifiers(final LinkedList<PExpression> identifiers,
			final Label section) {
		for (final PExpression pExpression : identifiers) {
			if (pExpression instanceof AIdentifierExpression) {
				final AIdentifierExpression id = (AIdentifierExpression) pExpression;
				final String name = extractIdentifierName(id.getIdentifier());
				try {
					section.addChild(new ClassicalB(name));
				} catch (final BException e) {
					// It should not reach this point because parsing the name
					// will not create any exceptions
					e.printStackTrace();
				}
			}
		}
		return section;
	}

}
