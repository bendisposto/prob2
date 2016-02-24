package de.prob.model.eventb;

import com.github.krukow.clj_lang.PersistentHashMap;

import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.Axiom;
import de.prob.model.representation.Constant;
import de.prob.model.representation.ModelElementList;
import de.prob.model.representation.Set;

public class Context extends AbstractElement {

	private final String name;

	public Context(final String name) {
		this(
				name,
				PersistentHashMap
				.<Class<? extends AbstractElement>, ModelElementList<? extends AbstractElement>> emptyMap());
	}

	private Context(
			final String name,
			PersistentHashMap<Class<? extends AbstractElement>, ModelElementList<? extends AbstractElement>> children) {
		super(children);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Context set(Class<? extends AbstractElement> clazz,
			ModelElementList<? extends AbstractElement> elements) {
		return new Context(name, assoc(clazz, elements));
	}

	public <T extends AbstractElement> Context addTo(Class<T> clazz, T element) {
		ModelElementList<T> list = getChildrenOfType(clazz);
		return new Context(name, assoc(clazz, list.addElement(element)));
	}

	public <T extends AbstractElement> Context removeFrom(Class<T> clazz,
			T element) {
		ModelElementList<T> list = getChildrenOfType(clazz);
		return new Context(name, assoc(clazz, list.removeElement(element)));
	}

	public <T extends AbstractElement> Context replaceIn(Class<T> clazz,
			T oldElement, T newElement) {
		ModelElementList<T> list = getChildrenOfType(clazz);
		return new Context(name, assoc(clazz,
				list.replaceElement(oldElement, newElement)));
	}

	public ModelElementList<Context> getExtends() {
		return getChildrenOfType(Context.class);
	}

	public ModelElementList<EventBConstant> getConstants() {
		return getChildrenAndCast(Constant.class, EventBConstant.class);
	}

	public ModelElementList<EventBAxiom> getAxioms() {
		return getChildrenAndCast(Axiom.class, EventBAxiom.class);
	}

	public ModelElementList<EventBAxiom> getAllAxioms() {
		ModelElementList<EventBAxiom> axms = new ModelElementList<EventBAxiom>();
		for (Context ctx : getExtends()) {
			axms = axms.addMultiple(ctx.getAllAxioms());
		}
		axms = axms.addMultiple(getAxioms());
		return axms;
	}

	public ModelElementList<Set> getSets() {
		return getChildrenOfType(Set.class);
	}

	public ModelElementList<ProofObligation> getProofs() {
		return getChildrenOfType(ProofObligation.class);
	}

	@Override
	public String toString() {
		return getName();
	}

}
