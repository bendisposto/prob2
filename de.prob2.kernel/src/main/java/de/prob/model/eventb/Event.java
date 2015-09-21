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
		this(
				name,
				type,
				extended,
				PersistentHashMap
						.<Class<? extends AbstractElement>, ModelElementList<? extends AbstractElement>> emptyMap());
	}

	private Event(
			final String name,
			final EventType type,
			final boolean extended,
			PersistentHashMap<Class<? extends AbstractElement>, ModelElementList<? extends AbstractElement>> children) {
		super(name, children);
		this.type = type;
		this.extended = extended;
	}

	public Event set(Class<? extends AbstractElement> clazz,
			ModelElementList<? extends AbstractElement> elements) {
		return new Event(name, type, extended, assoc(clazz, elements));
	}

	public <T extends AbstractElement> Event addTo(Class<T> clazz, T element) {
		ModelElementList<T> list = getChildrenOfType(clazz);
		return new Event(name, type, extended, assoc(clazz,
				list.addElement(element)));
	}

	public <T extends AbstractElement> Event removeFrom(Class<T> clazz,
			T element) {
		ModelElementList<T> list = getChildrenOfType(clazz);
		return new Event(name, type, extended, assoc(clazz,
				list.removeElement(element)));
	}

	public <T extends AbstractElement> Event replaceIn(Class<T> clazz,
			T oldElement, T newElement) {
		ModelElementList<T> list = getChildrenOfType(clazz);
		return new Event(name, type, extended, assoc(clazz,
				list.replaceElement(oldElement, newElement)));
	}

	/**
	 * The {@link Event} saves a reference to the name of the refined events.
	 * However, this is just a reference, and in order to retrieve the actual
	 * refined events the parent machine needs to be passed in as an argument.
	 *
	 * @param parentMachine
	 *            from which the refined event will be retrieved
	 * @return the event that this event refines, if any. Otherwise null.
	 */
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
}
