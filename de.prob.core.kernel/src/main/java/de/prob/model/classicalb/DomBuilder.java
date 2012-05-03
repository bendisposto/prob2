package de.prob.model.classicalb;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Joiner;

import de.be4.classicalb.core.parser.analysis.DepthFirstAdapter;
import de.be4.classicalb.core.parser.node.AConstantsMachineClause;
import de.be4.classicalb.core.parser.node.AConstraintsMachineClause;
import de.be4.classicalb.core.parser.node.ADeferredSetSet;
import de.be4.classicalb.core.parser.node.AEnumeratedSetSet;
import de.be4.classicalb.core.parser.node.AIdentifierExpression;
import de.be4.classicalb.core.parser.node.AInvariantMachineClause;
import de.be4.classicalb.core.parser.node.AMachineHeader;
import de.be4.classicalb.core.parser.node.APropertiesMachineClause;
import de.be4.classicalb.core.parser.node.AVariablesMachineClause;
import de.be4.classicalb.core.parser.node.Node;
import de.be4.classicalb.core.parser.node.PExpression;
import de.be4.classicalb.core.parser.node.PPredicate;
import de.be4.classicalb.core.parser.node.Start;
import de.be4.classicalb.core.parser.node.TIdentifierLiteral;

public class DomBuilder extends DepthFirstAdapter {

	private final ClassicalBMachine machine;

	@Override
	public void outStart(Start node) {
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
		addIdentifiers(node.getParameters(), machine.parameters());
	}

	@Override
	public void outAConstraintsMachineClause(AConstraintsMachineClause node) {
		addPredicates(node, machine.constraints());
	}

	@Override
	public void outAConstantsMachineClause(final AConstantsMachineClause node) {
		addIdentifiers(node.getIdentifiers(), machine.constants());
	}

	@Override
	public void outAPropertiesMachineClause(APropertiesMachineClause node) {
		addPredicates(node, machine.properties());
	}

	@Override
	public void outAVariablesMachineClause(final AVariablesMachineClause node) {
		addIdentifiers(node.getIdentifiers(), machine.variables());
	}

	@Override
	public void outAInvariantMachineClause(AInvariantMachineClause node) {
		addPredicates(node, machine.invariant());
	}
	
	@Override
	public void outADeferredSetSet(ADeferredSetSet node) {
		machine.sets().add(new ClassicalBEntity(extractIdentifierName(node.getIdentifier()), node));
	}
	
	@Override
	public void outAEnumeratedSetSet(AEnumeratedSetSet node) {
		machine.sets().add(new ClassicalBEntity(extractIdentifierName(node.getIdentifier()), node));
	}
	
	// -------------------

	private String prettyprint(PPredicate predicate) {
		return predicate.toString();
	}

	private String extractIdentifierName(
			final LinkedList<TIdentifierLiteral> nameL) {
		String text;
		if (nameL.size() == 1) {
			text = nameL.get(0).getText();
		} else {
			ArrayList<String> list = new ArrayList<String>();
			for (TIdentifierLiteral t : nameL) {
				list.add(t.getText());
			}
			text = Joiner.on(".").join(list);
		}
		return text;
	}

	private void addPredicates(Node node, List<ClassicalBEntity> to) {
		PredicateConjunctionSplitter s = new PredicateConjunctionSplitter();
		node.apply(s);
		for (PPredicate predicate : s.getPredicates()) {
			to.add(new ClassicalBEntity(prettyprint(predicate), predicate));
		}
	}

	private void addIdentifiers(LinkedList<PExpression> identifiers,
			List<ClassicalBEntity> to) {
		for (PExpression pExpression : identifiers) {
			if (pExpression instanceof AIdentifierExpression) {
				AIdentifierExpression id = (AIdentifierExpression) pExpression;
				String name = extractIdentifierName(id.getIdentifier());
				to.add(new ClassicalBEntity(name, id));
			}
		}
	}

}
