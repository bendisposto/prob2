package de.prob.model.eventb;

import de.prob.model.representation.AbstractElement
import de.prob.model.representation.Action
import de.prob.model.representation.BEvent
import de.prob.model.representation.Guard

public class Event extends BEvent {

	def EventType type
	def List<Event> refines
	def List<EventBAction> actions
	def List<EventBGuard> guards
	def List<EventParameter> parameters
	def List<Witness> witnesses

	public enum EventType {
		ORDINARY, CONVERGENT, ANTICIPATED
	}

	public Event(final String name, final EventType type) {
		super(name);
		this.type = type;
	}

	public void addRefines(final List<Event> refines) {
		put(Event.class, refines);
		this.refines = refines
	}

	public void addGuards(final List<EventBGuard> guards) {
		put(Guard.class, guards);
		this.guards = guards
	}

	public void addActions(final List<EventBAction> actions) {
		put(Action.class, actions);
		this.actions = actions
	}

	public void addWitness(final List<Witness> witness) {
		put(Witness.class, witness);
		this.witnesses = witness
	}

	public void addParameters(final List<EventParameter> parameters) {
		put(EventParameter.class, parameters);
		this.parameters = parameters
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
			final Set<? extends AbstractElement> childrenOfType,
			final StringBuilder sb) {
		if (!childrenOfType.isEmpty()) {
			sb.append(name + ": \n");
			for (AbstractElement abstractElement : childrenOfType) {
				sb.append("\t" + abstractElement.toString() + "\n");
			}
		}
	}

	def EventBAction getAction(String name) {
		return actions[name]
	}

	def EventBGuard getGuard(String name) {
		return guards[name]
	}

	def EventParameter getParameter(String name) {
		return parameters[name]
	}

	def Event getRefinedEvent(String name) {
		return refines[name]
	}

	def Witness getWitness(String name) {
		return witnesses[name]
	}
}
