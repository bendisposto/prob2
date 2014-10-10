package de.prob.model.eventb;

import javax.swing.text.AbstractDocument.AbstractElement

import de.prob.model.representation.Action
import de.prob.model.representation.BEvent
import de.prob.model.representation.Guard
import de.prob.model.representation.ModelElementList

public class Event extends BEvent {

	def EventBMachine parentMachine
	def EventType type
	def ModelElementList<Event> refines
	def ModelElementList<EventBAction> actions
	def ModelElementList<EventBGuard> guards
	def ModelElementList<EventParameter> parameters
	def ModelElementList<Witness> witnesses

	public enum EventType {
		ORDINARY, CONVERGENT, ANTICIPATED
	}

	public Event(EventBMachine parentMachine, final String name, final EventType type) {
		super(name);
		this.parentMachine = parentMachine
		this.type = type;
	}

	public void addRefines(final ModelElementList<Event> refines) {
		put(Event.class, refines);
		this.refines = refines
	}

	public void addGuards(final ModelElementList<EventBGuard> guards) {
		put(Guard.class, guards);
		this.guards = guards
	}

	public void addActions(final ModelElementList<EventBAction> actions) {
		put(Action.class, actions);
		this.actions = actions
	}

	public void addWitness(final ModelElementList<Witness> witness) {
		put(Witness.class, witness);
		this.witnesses = witness
	}

	public void addParameters(final ModelElementList<EventParameter> parameters) {
		put(EventParameter.class, parameters);
		this.parameters = parameters
	}

	public EventType getType() {
		return type;
	}

	@Override
	public boolean equals(Object that) {
		if (that instanceof Event) {
			return this.parentMachine.getName() == that.getParentMachine().getName() &&
			this.getName() == that.getName()
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(parentMachine.getName(), this.name)
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
