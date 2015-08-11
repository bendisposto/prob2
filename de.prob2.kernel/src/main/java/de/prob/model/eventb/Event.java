package de.prob.model.eventb;

import com.github.krukow.clj_lang.PersistentHashMap;

import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.Action;
import de.prob.model.representation.BEvent;
import de.prob.model.representation.Guard;
import de.prob.model.representation.ModelElementList;

public class Event extends BEvent {

	private final EventType type;
	private final boolean extended;

	public enum EventType {
		ORDINARY, CONVERGENT, ANTICIPATED
	}

	public Event(final String name, final EventType type, final boolean extended) {
		this(name, type, extended, PersistentHashMap.<Class<? extends AbstractElement>, ModelElementList<? extends AbstractElement>>emptyMap());
	}

	private Event(final String name, final EventType type, final boolean extended, PersistentHashMap<Class<? extends AbstractElement>, ModelElementList<? extends AbstractElement>> children) {
		super(name, children);
		this.type = type;
		this.extended = extended;
	}

	public Event set(Class<? extends AbstractElement> clazz, ModelElementList<? extends AbstractElement> elements) {
		return new Event(name, type, extended, assoc(clazz, elements));
	}

	public <T extends AbstractElement> Event addTo(Class<T> clazz, T element) {
		ModelElementList<T> list = getChildrenOfType(clazz);
		return new Event(name, type, extended, assoc(clazz, list.addElement(element)));
	}

	public <T extends AbstractElement> Event removeFrom(Class<T> clazz, T element) {
		ModelElementList<T> list = getChildrenOfType(clazz);
		return new Event(name, type, extended, assoc(clazz, list.removeElement(element)));
	}

	public <T extends AbstractElement> Event replaceIn(Class<T> clazz, T oldElement, T newElement) {
		ModelElementList<T> list = getChildrenOfType(clazz);
		return new Event(name, type, extended, assoc(clazz, list.replaceElement(oldElement, newElement)));
	}

	public ModelElementList<Event> getRefines() {
		return getChildrenOfType(Event.class);
	}

	public ModelElementList<EventBGuard> getGuards() {
		return getChildrenAndCast(Guard.class, EventBGuard.class);
	}

	public ModelElementList<EventBAction> getActions() {
		return getChildrenAndCast(Action.class, EventBAction.class);
	}

	public ModelElementList<Witness> getWitnesses() {
		return getChildrenOfType(Witness.class);
	}

	public ModelElementList<EventParameter> getParameters() {
		return getChildrenOfType(EventParameter.class);
	}

	public EventType getType() {
		return type;
	}

	public boolean isExtended() {
		return extended;
	}

	public Event changeType(EventType type) {
		return new Event(name, type, extended, children);
	}

	public Event toggleExtended(boolean extended) {
		if (extended == this.extended) {
			return this;
		}
		return new Event(name, type, extended, children);
	}

	@Override
	public String toString() {
		return getName();
	}

	public String print() {
		StringBuilder sb = new StringBuilder();
		sb.append("Name: " + getName() + "\n");
		sb.append("Type: " + type.toString() + "\n");
		printChildren("Refines", getChildrenOfType(Event.class), sb);
		printChildren("Any", getChildrenOfType(EventParameter.class), sb);
		printChildren("Where", getChildrenOfType(Guard.class), sb);
		printChildren("With", getChildrenOfType(Witness.class), sb);
		printChildren("Then", getChildrenOfType(Action.class), sb);
		return sb.toString();
	}

	private void printChildren(final String name,
			final ModelElementList<? extends AbstractElement> childrenOfType,
			final StringBuilder sb) {
		if (!childrenOfType.isEmpty()) {
			sb.append(name + ": \n");
			for (AbstractElement abstractElement : childrenOfType) {
				sb.append("\t" + abstractElement.toString() + "\n");
			}
		}
	}
}
