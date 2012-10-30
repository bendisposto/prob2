package de.prob.model.eventb.newdom;

import java.util.List;

import de.prob.model.representation.newdom.AbstractElement;
import de.prob.model.representation.newdom.Axiom;
import de.prob.model.representation.newdom.Constant;
import de.prob.model.representation.newdom.Set;

public class Context extends AbstractElement {

	private final String name;

	public Context(final String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void addRefines(final List<Context> contexts) {
		put(Context.class, contexts);
	}

	public void addSets(final List<EventBSet> sets) {
		put(Set.class, sets);
	}

	public void addConstants(final List<EventBConstant> constants) {
		put(Constant.class, constants);
	}

	public void addAxioms(final List<EventBAxiom> axioms) {
		put(Axiom.class, axioms);
	}
}
