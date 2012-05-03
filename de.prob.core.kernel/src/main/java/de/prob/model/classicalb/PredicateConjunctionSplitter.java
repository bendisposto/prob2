package de.prob.model.classicalb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.be4.classicalb.core.parser.analysis.DepthFirstAdapter;
import de.be4.classicalb.core.parser.node.AConjunctPredicate;
import de.be4.classicalb.core.parser.node.Node;
import de.be4.classicalb.core.parser.node.PPredicate;

public class PredicateConjunctionSplitter extends DepthFirstAdapter {

	List<PPredicate> predicates = new ArrayList<PPredicate>();
	boolean skip;

	@Override
	public void caseAConjunctPredicate(AConjunctPredicate node) {
		if (!skip) {
			process(node.getLeft());
			process(node.getRight());
		}
	}


	
	@Override
	public void defaultIn(Node node) {
		if (node instanceof PPredicate) {
			PPredicate p = (PPredicate) node;
			predicates.add(p);
			skip = true; 
		}
	}

	@Override
	public void defaultCase(Node node) {
		super.defaultCase(node);
	}

	private void process(PPredicate p) {
		if (p instanceof AConjunctPredicate)
			p.apply(this);
		else
			predicates.add(p);
	}

	public List<PPredicate> getPredicates() {
		return Collections.unmodifiableList(predicates);
	}
}
