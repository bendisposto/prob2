package de.prob.model.eventb;

import java.util.ArrayList;
import java.util.List;

import org.eventb.emf.core.EventBNamedCommentedComponentElement;
import org.eventb.emf.core.context.Constant;
import org.eventb.emf.core.context.Context;
import org.eventb.emf.core.machine.Event;
import org.eventb.emf.core.machine.Machine;
import org.eventb.emf.core.machine.Variable;

import de.prob.model.representation.AbstractElement;

public class EventBComponent implements AbstractElement {

	private final EventBNamedCommentedComponentElement emfComponent;
	private final String name;
	private List<Variable> variables;
	private List<Constant> constants;
	private List<Event> events;

	public EventBComponent(
			final EventBNamedCommentedComponentElement emfComponent) {
		this.emfComponent = emfComponent;
		this.name = emfComponent.doGetName();
		if (emfComponent instanceof Context) {
			Context ctx = (Context) emfComponent;
			constants = ctx.getConstants();
			variables = new ArrayList<Variable>();
			events = new ArrayList<Event>();
		}
		if (emfComponent instanceof Machine) {
			Machine m = (Machine) emfComponent;
			constants = new ArrayList<Constant>();
			variables = m.getVariables();
			events = m.getEvents();
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<String> getConstants() {
		ArrayList<String> cons = new ArrayList<String>();
		for (Constant con : constants) {
			cons.add(con.doGetName());
		}
		return cons;
	}

	@Override
	public List<String> getVariables() {
		ArrayList<String> vars = new ArrayList<String>();
		for (Variable var : variables) {
			vars.add(var.doGetName());
		}
		return vars;
	}

	@Override
	public List<String> getOperations() {
		ArrayList<String> ops = new ArrayList<String>();
		for (Event event : events) {
			ops.add(event.doGetName());
		}
		return ops;
	}

	public boolean isContext() {
		return emfComponent instanceof Context;
	}

	public boolean isMachine() {
		return emfComponent instanceof Machine;
	}
}
