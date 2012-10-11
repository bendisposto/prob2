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

import de.prob.model.representation.AbstractDomTreeElement;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.Label;

public class EventBComponent extends AbstractDomTreeElement implements AbstractElement {

	private final EventBNamedCommentedComponentElement emfComponent;
	private final String name;
	private final Label variables;
	private final Label constants;
	private List<Event> events;

	public EventBComponent(
			final EventBNamedCommentedComponentElement emfComponent) {
		this.emfComponent = emfComponent;
		this.name = emfComponent.doGetName();
		this.variables = new Label("Variables");
		this.constants = new Label("Constants");
		if (emfComponent instanceof Context) {
			final Context ctx = (Context) emfComponent;
			final EList<Constant> constants2 = ctx.getConstants();
			for (final Constant constant : constants2) {
				constants.addFormula(new EventBFormula(constant.doGetName(),
						constant));
			}
			events = new ArrayList<Event>();
		}
		if (emfComponent instanceof Machine) {
			final Machine m = (Machine) emfComponent;
			final EList<Variable> mVars = m.getVariables();
			for (final Variable variable : mVars) {
				variables.addFormula(new EventBFormula(variable.doGetName(),
						variable));
			}
			events = new ArrayList<Event>();
			final EList<org.eventb.emf.core.machine.Event> emfEvents = m
					.getEvents();
			for (final org.eventb.emf.core.machine.Event event : emfEvents) {
				final List<String> params = new ArrayList<String>();
				final EList<Parameter> parameters = event.getParameters();
				for (final Parameter parameter : parameters) {
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
	public List<String> getConstantNames() {
		final ArrayList<String> cons = new ArrayList<String>();
		for (final AbstractDomTreeElement con : constants.getSubcomponents()) {
			cons.add(con.getLabel());
		}
		return cons;
	}

	@Override
	public List<String> getVariableNames() {
		final ArrayList<String> vars = new ArrayList<String>();
		for (final AbstractDomTreeElement var : variables.getSubcomponents()) {
			vars.add(var.getLabel());
		}
		return vars;
	}

	@Override
	public List<String> getOperationNames() {
		final ArrayList<String> ops = new ArrayList<String>();
		for (final Event event : events) {
			ops.add(event.getName());
		}
		return ops;
	}

	public Map<String, Event> getEvents() {
		final Map<String, Event> map = new HashMap<String, Event>();
		for (final Event event : events) {
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

	@Override
	public String getLabel() {
		return name;
	}

	

	@Override
	public List<AbstractDomTreeElement> getSubcomponents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean toEvaluate() {
		return false;
	}
}
