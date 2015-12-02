package de.prob.model.eventb;

import com.github.krukow.clj_lang.PersistentHashMap;

import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.BEvent;
import de.prob.model.representation.Invariant;
import de.prob.model.representation.Machine;
import de.prob.model.representation.ModelElementList;
import de.prob.model.representation.Variable;

public class EventBMachine extends Machine {

	public EventBMachine(final String name) {
		super(
				name,
				PersistentHashMap
				.<Class<? extends AbstractElement>, ModelElementList<? extends AbstractElement>> emptyMap());
	}

	private EventBMachine(
			final String name,
			PersistentHashMap<Class<? extends AbstractElement>, ModelElementList<? extends AbstractElement>> children) {
		super(name, children);
	}

	public EventBMachine set(Class<? extends AbstractElement> clazz,
			ModelElementList<? extends AbstractElement> elements) {
		return new EventBMachine(name, assoc(clazz, elements));
	}

	public <T extends AbstractElement> EventBMachine addTo(Class<T> clazz,
			T element) {
		ModelElementList<T> list = getChildrenOfType(clazz);
		return new EventBMachine(name, assoc(clazz, list.addElement(element)));
	}

	public <T extends AbstractElement> EventBMachine removeFrom(Class<T> clazz,
			T element) {
		ModelElementList<T> list = getChildrenOfType(clazz);
		return new EventBMachine(name,
				assoc(clazz, list.removeElement(element)));
	}

	public <T extends AbstractElement> EventBMachine replaceIn(Class<T> clazz,
			T oldElement, T newElement) {
		ModelElementList<T> list = getChildrenOfType(clazz);
		return new EventBMachine(name, assoc(clazz,
				list.replaceElement(oldElement, newElement)));
	}

	public ModelElementList<EventBMachine> getRefines() {
		return getChildrenAndCast(Machine.class, EventBMachine.class);
	}

	public ModelElementList<Context> getSees() {
		return getChildrenOfType(Context.class);
	}

	public ModelElementList<EventBVariable> getVariables() {
		return getChildrenAndCast(Variable.class, EventBVariable.class);
	}

	public ModelElementList<EventBInvariant> getInvariants() {
		return getChildrenAndCast(Invariant.class, EventBInvariant.class);
	}

	public ModelElementList<EventBInvariant> getAllInvariants() {
		ModelElementList<EventBInvariant> invs = new ModelElementList<EventBInvariant>();
		for (EventBMachine m : getRefines()) {
			invs = invs.addMultiple(m.getAllInvariants());
		}
		invs = invs.addMultiple(getInvariants());
		return invs;
	}

	public Variant getVariant() {
		ModelElementList<Variant> variant = getChildrenOfType(Variant.class);
		return variant.isEmpty() ? null : variant.get(0);
	}

	public ModelElementList<ProofObligation> getProofs() {
		return getChildrenOfType(ProofObligation.class);
	}

	public ModelElementList<Event> getEvents() {
		return getChildrenAndCast(BEvent.class, Event.class);
	}

	public ModelElementList<Event> getOperations() {
		return getChildrenAndCast(BEvent.class, Event.class);
	}

	public Event getEvent(String name) {
		return getEvents().getElement(name);
	}
}
