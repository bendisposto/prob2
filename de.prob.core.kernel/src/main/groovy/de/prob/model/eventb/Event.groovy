package de.prob.model.eventb;

import de.prob.model.representation.AbstractElement
import de.prob.model.representation.Action
import de.prob.model.representation.BEvent
import de.prob.model.representation.Guard
import de.prob.model.representation.ModelElementList

public class Event extends BEvent {

	private final EventType type;

	public enum EventType {
		ORDINARY, CONVERGENT, ANTICIPATED
	}

	public Event(final String name, final EventType type) {
		super(name);
		this.type = type;
	}

	public void addRefines(final List<Event> refines) {
		put(Event.class, refines);
	}

	public void addGuards(final List<EventBGuard> guards) {
		put(Guard.class, guards);
	}

	public void addActions(final List<EventBAction> actions) {
		put(Action.class, actions);
	}

	public void addWitness(final List<Witness> witness) {
		put(Witness.class, witness);
	}

	public void addParameters(final List<EventParameter> parameters) {
		put(EventParameter.class, parameters);
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

	public Event getRefines() {
		Iterator<Event> iterator = getChildrenOfType(Event.class).iterator();
		if (iterator.hasNext()) {
			return iterator.next();
		}
		return null;
	}

	public List<EventBGuard> getGuards() {
		List<EventBGuard> guards = new ModelElementList<EventBGuard>();
		Set<Guard> kids = getChildrenOfType(Guard.class);
		for (Guard guard : kids) {
			if (guard instanceof EventBGuard) {
				guards.add((EventBGuard) guard);
			}
		}
		return guards;
	}

	public List<EventBAction> getActions() {
		List<EventBAction> actions = new ModelElementList<EventBAction>();
		Set<Action> kids = getChildrenOfType(Action.class);
		for (Action action : kids) {
			if (action instanceof EventBAction) {
				actions.add((EventBAction) action);
			}
		}
		return actions;
	}

	public Witness getWitness() {
		Iterator<Witness> iterator = getChildrenOfType(Witness.class)
				.iterator();
		if (iterator.hasNext()) {
			return iterator.next();
		}
		return null;
	}

	public List<EventParameter> getParameters() {
		List<EventParameter> params = new ModelElementList<EventParameter>();
		Set<EventParameter> kids = getChildrenOfType(EventParameter.class);
		params.addAll(kids);
		return params;
	}

	def getProperty(String prop) {
		if(prop == "refines") {
			return getRefines()
		} else if(prop == "guards") {
			return getGuards()
		} else if(prop == "actions") {
			return getActions()
		} else if(prop == "witness") {
			return getWitness()
		} else if(prop == "parameters") {
			return getParameters()
		}
		Event.getMetaClass().getProperty(this, prop)
	}
}
