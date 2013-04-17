package de.prob.model.eventb;

import de.prob.model.representation.AbstractElement
import de.prob.model.representation.Axiom
import de.prob.model.representation.BSet
import de.prob.model.representation.Constant
import de.prob.model.representation.ModelElementList

public class Context extends AbstractElement {

	private final String name;

	public Context(final String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void addExtends(final List<Context> contexts) {
		put(Context.class, contexts);
	}

	public void addSets(final List<BSet> sets) {
		put(BSet.class, sets);
	}

	public void addConstants(final List<EventBConstant> constants) {
		put(Constant.class, constants);
	}

	public void addAxioms(final List<EventBAxiom> axioms) {
		put(Axiom.class, axioms);
	}

	public List<BSet> getSets() {
		List<BSet> sets = new ModelElementList<BSet>();
		sets.addAll(getChildrenOfType(BSet.class));
		return sets;
	}

	public List<EventBConstant> getConstants() {
		List<EventBConstant> elements = new ModelElementList<EventBConstant>();
		Set<Constant> kids = getChildrenOfType(Constant.class);
		for (Constant kid : kids) {
			if (kid instanceof EventBConstant) {
				elements.add((EventBConstant) kid);
			}
		}
		return elements;
	}

	public List<EventBAxiom> getAxioms() {
		List<EventBAxiom> elements = new ModelElementList<EventBAxiom>();
		Set<Axiom> kids = getChildrenOfType(Axiom.class);
		for (Axiom kid : kids) {
			if (kid instanceof EventBAxiom) {
				elements.add((EventBAxiom) kid);
			}
		}
		return elements;
	}

	@Override
	public String toString() {
		return name;
	}

	def getProperty(String prop) {
		if(prop == "sets") {
			return getSets()
		} else if(prop == "constants") {
			return getConstants()
		} else if(prop == "axioms") {
			return getAxioms()
		}
		Context.getMetaClass().getProperty(this, prop)
	}
}
