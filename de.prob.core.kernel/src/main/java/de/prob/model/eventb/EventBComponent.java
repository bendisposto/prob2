package de.prob.model.eventb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eventb.emf.core.EventBNamedCommentedComponentElement;
import org.eventb.emf.core.context.Constant;
import org.eventb.emf.core.context.Context;
import org.eventb.emf.core.machine.Machine;
import org.eventb.emf.core.machine.Parameter;
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
			events = new ArrayList<Event>();
			EList<org.eventb.emf.core.machine.Event> emfEvents = m.getEvents();
			for (org.eventb.emf.core.machine.Event event : emfEvents) {
				List<String> params = new ArrayList<String>();
				EList<Parameter> parameters = event.getParameters();
				for (Parameter parameter : parameters) {
					params.add(parameter.doGetName());
				}				
				events.add(new Event(event.doGetName(), params));
			}
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
			ops.add(event.getName());
		}
		return ops;
	}
	
	public Map<String,Event> getEvents() {
		Map<String,Event> map = new HashMap<String, Event>();
		for (Event event : events) {
			map.put(event.getName(), event);
		}
		return map;
	}

	public boolean isContext() {
		return emfComponent instanceof Context;
	}

	public boolean isMachine() {
		return emfComponent instanceof Machine;
	}
	
	public EventBNamedCommentedComponentElement getEmfComponent() {
		return emfComponent;
	}
	
}
