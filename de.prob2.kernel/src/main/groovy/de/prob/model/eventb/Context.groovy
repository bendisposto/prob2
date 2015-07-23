package de.prob.model.eventb;

import com.github.krukow.clj_lang.PersistentHashMap

import de.prob.model.representation.AbstractElement
import de.prob.model.representation.Axiom
import de.prob.model.representation.Constant
import de.prob.model.representation.ModelElementList
import de.prob.model.representation.Set

public class Context extends AbstractElement {

	private final String name;

	public Context(final String name) {
		this(name, PersistentHashMap.emptyMap())
	}

	private Context(final String name, children) {
		super(children)
		this.name = name
	}

	public String getName() {
		return name;
	}

	def Context set(Class<? extends AbstractElement> clazz, ModelElementList<? extends AbstractElement> elements) {
		new Context(name, children.assoc(clazz, elements))
	}

	def ModelElementList<Context> getExtends() {
		getChildrenOfType(Context)
	}

	def ModelElementList<EventBConstant> getConstants() {
		getChildrenOfType(Constant)
	}

	def ModelElementList<EventBAxiom> getAxioms() {
		getChildrenOfType(Axiom)
	}

	def ModelElementList<Set> getSets() {
		getChildrenOfType(Set)
	}

	def ModelElementList<ProofObligation> getProofs() {
		getChildrenOfType(ProofObligation)
	}


}
