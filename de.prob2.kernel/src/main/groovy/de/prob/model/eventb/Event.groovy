package de.prob.model.eventb;

import com.github.krukow.clj_lang.PersistentHashMap

import de.prob.model.representation.AbstractElement
import de.prob.model.representation.Action
import de.prob.model.representation.BEvent
import de.prob.model.representation.Guard
import de.prob.model.representation.ModelElementList

public class Event extends BEvent {

	def EventType type
	def boolean extended

	public enum EventType {
		ORDINARY, CONVERGENT, ANTICIPATED
	}

	public Event(final String name, final EventType type, final boolean extended) {
		this(name, type, extended, PersistentHashMap.emptyMap())
	}

	private Event(final String name, final EventType type, final boolean extended, children) {
		super(name, children)
		this.type = type
		this.extended = extended
	}

	def Event set(Class<? extends AbstractElement> clazz, ModelElementList<? extends AbstractElement> elements) {
		new Event(name, type, extended, children.assoc(clazz, elements))
	}

	public ModelElementList<Event> getRefines() {
		getChildrenOfType(Event.class)
	}

	public ModelElementList<EventBGuard> getGuards() {
		getChildrenOfType(Guard.class)
	}

	public ModelElementList<EventBAction> getActions() {
		getChildrenOfType(Action.class)
	}

	public ModelElementList<Witness> getWitnesses() {
		getChildrenOfType(Witness.class)
	}

	public ModelElementList<EventParameter> getParameters() {
		getChildrenOfType(EventParameter.class)
	}

	public EventType getType() {
		return type;
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
