package de.prob.model.serialize;

import java.lang.reflect.Type;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import de.prob.model.eventb.Context;
import de.prob.model.eventb.Event;
import de.prob.model.eventb.EventBAction;
import de.prob.model.eventb.EventBGuard;
import de.prob.model.eventb.EventBInvariant;
import de.prob.model.eventb.EventBMachine;
import de.prob.model.eventb.EventParameter;
import de.prob.model.eventb.Variant;
import de.prob.model.eventb.Witness;
import de.prob.model.representation.Action;
import de.prob.model.representation.BEvent;
import de.prob.model.representation.Guard;
import de.prob.model.representation.Invariant;
import de.prob.model.representation.Machine;
import de.prob.model.representation.Variable;

public class EventBMachineSerializer implements JsonSerializer<EventBMachine> {

	public JsonObject serializeEvent(final Event event) {
		JsonObject serialized = new JsonObject();

		serialized.addProperty("name", event.getName());
		serialized.addProperty("type", event.getType().toString());

		JsonArray refined = new JsonArray();
		Set<Event> refines = event.getChildrenOfType(Event.class);
		for (Event ev : refines) {
			refined.add(new JsonPrimitive(ev.getName()));
		}
		serialized.add("refines", refined);

		JsonArray gds = new JsonArray();
		Set<Guard> guards = event.getChildrenOfType(Guard.class);
		for (Guard guard : guards) {
			if (guard instanceof EventBGuard) {
				JsonObject o = new JsonObject();
				EventBGuard eventBGuard = (EventBGuard) guard;
				o.addProperty("name", eventBGuard.getName());
				o.addProperty("code", eventBGuard.getPredicate().getCode());
				o.addProperty("theorem", eventBGuard.isTheorem());
				gds.add(o);
			}
		}
		serialized.add("guards", gds);

		JsonArray acts = new JsonArray();
		Set<Action> actions = event.getChildrenOfType(Action.class);
		for (Action action : actions) {
			if (action instanceof EventBAction) {
				JsonObject o = new JsonObject();
				EventBAction ebAction = ((EventBAction) action);
				o.addProperty("name", ebAction.getName());
				o.addProperty("code", ebAction.getCode());
				acts.add(o);
			}
		}
		serialized.add("actions", acts);

		JsonArray w = new JsonArray();
		Set<Witness> witness = event.getChildrenOfType(Witness.class);
		for (Witness wit : witness) {
			JsonObject o = new JsonObject();
			o.addProperty("name", wit.getName());
			o.addProperty("code", wit.getPredicate().getCode());
			w.add(o);
		}
		serialized.add("witness", w);

		JsonArray parameters = new JsonArray();
		Set<EventParameter> params = event
				.getChildrenOfType(EventParameter.class);
		for (EventParameter eventParameter : params) {
			parameters.add(new JsonPrimitive(eventParameter.getName()));
		}
		serialized.add("parameters", parameters);

		return serialized;
	}

	@Override
	public JsonElement serialize(final EventBMachine src, final Type typeOfSrc,
			final JsonSerializationContext context) {
		JsonObject obj = new JsonObject();

		obj.addProperty("name", src.getName());

		JsonArray refines = new JsonArray();
		Set<Machine> machines = src.getChildrenOfType(Machine.class);
		for (Machine machine : machines) {
			refines.add(new JsonPrimitive(machine.getName()));
		}
		obj.add("refines", refines);

		JsonArray sees = new JsonArray();
		Set<Context> contexts = src.getChildrenOfType(Context.class);
		for (Context ctxt : contexts) {
			sees.add(new JsonPrimitive(ctxt.getName()));
		}
		obj.add("sees", sees);

		JsonArray variables = new JsonArray();
		Set<Variable> vars = src.getChildrenOfType(Variable.class);
		for (Variable variable : vars) {
			variables.add(new JsonPrimitive(variable.getName()));
		}
		obj.add("variables", variables);

		JsonArray invariants = new JsonArray();
		Set<Invariant> invs = src.getChildrenOfType(Invariant.class);
		for (Invariant invariant : invs) {
			if (invariant instanceof EventBInvariant) {
				EventBInvariant eventBInvariant = (EventBInvariant) invariant;
				JsonObject ebi = new JsonObject();
				ebi.addProperty("name", eventBInvariant.getName());
				ebi.addProperty("code", eventBInvariant.getPredicate()
						.getCode());
				ebi.addProperty("theorem", eventBInvariant.isTheorem());
				invariants.add(ebi);
			}
		}
		obj.add("invariants", invariants);

		JsonArray variant = new JsonArray();
		Set<Variant> vs = src.getChildrenOfType(Variant.class);
		for (Variant v : vs) {
			variant.add(new JsonPrimitive(v.getExpression().getCode()));
		}
		obj.add("variant", variant);

		JsonArray events = new JsonArray();
		Set<BEvent> evs = src.getChildrenOfType(BEvent.class);
		for (BEvent bEvent : evs) {
			if (bEvent instanceof Event) {
				Event event = (Event) bEvent;
				events.add(serializeEvent(event));
			}

		}
		obj.add("events", events);

		return obj;
	}

}
