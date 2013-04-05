package de.prob.rodin.translate;

import java.lang.reflect.Type;

import org.eventb.core.IConvergenceElement.Convergence;
import org.eventb.core.ISCAction;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCGuard;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ISCInvariant;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.ISCParameter;
import org.eventb.core.ISCRefinesEvent;
import org.eventb.core.ISCRefinesMachine;
import org.eventb.core.ISCVariable;
import org.eventb.core.ISCVariant;
import org.eventb.core.ISCWitness;
import org.rodinp.core.RodinDBException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class MachineRootSerializer implements JsonSerializer<ISCMachineRoot> {

	public JsonObject serializeEvent(final ISCEvent event)
			throws RodinDBException {
		JsonObject serialized = new JsonObject();

		serialized.addProperty("name", event.getLabel());
		serialized.addProperty("type", calculateEventType(event
				.getConvergence().getCode()));

		JsonArray refined = new JsonArray();
		for (ISCRefinesEvent ev : event.getSCRefinesClauses()) {
			refined.add(new JsonPrimitive(ev.getAbstractSCEvent().getLabel()));
		}
		serialized.add("refines", refined);

		JsonArray gds = new JsonArray();
		for (ISCGuard guard : event.getSCGuards()) {
			JsonObject o = new JsonObject();
			o.addProperty("name", guard.getElementName());
			o.addProperty("code", guard.getPredicateString());
			o.addProperty("theorem", guard.isTheorem());
			gds.add(o);
		}
		serialized.add("guards", gds);

		JsonArray acts = new JsonArray();
		for (ISCAction action : event.getSCActions()) {
			JsonObject o = new JsonObject();
			o.addProperty("name", action.getElementName());
			o.addProperty("code", action.getAssignmentString());
			acts.add(o);
		}
		serialized.add("actions", acts);

		JsonArray w = new JsonArray();
		for (ISCWitness wit : event.getSCWitnesses()) {
			JsonObject o = new JsonObject();
			o.addProperty("name", wit.getElementName());
			o.addProperty("code", wit.getPredicateString());
			w.add(o);
		}
		serialized.add("witness", w);

		JsonArray parameters = new JsonArray();
		for (ISCParameter eventParameter : event.getSCParameters()) {
			parameters.add(new JsonPrimitive(eventParameter
					.getIdentifierString()));
		}
		serialized.add("parameters", parameters);

		return serialized;
	}

	private String calculateEventType(final int typeId) {
		Convergence valueOf = Convergence.valueOf(typeId);
		if (valueOf.equals(Convergence.ORDINARY)) {
			return "ORDINARY";
		}
		if (valueOf.equals(Convergence.CONVERGENT)) {
			return "CONVERGENT";
		}
		if (valueOf.equals(Convergence.ANTICIPATED)) {
			return "ANTICIPATED";
		}
		return null;
	}

	@Override
	public JsonElement serialize(final ISCMachineRoot src,
			final Type typeOfSrc, final JsonSerializationContext context) {
		JsonObject obj = new JsonObject();

		try {
			obj.addProperty("name", src.getComponentName());

			JsonArray refines = new JsonArray();
			for (ISCRefinesMachine machine : src.getSCRefinesClauses()) {
				refines.add(new JsonPrimitive(machine.getAbstractSCMachine()
						.getBareName()));
			}
			obj.add("refines", refines);

			JsonArray sees = new JsonArray();
			for (ISCInternalContext ctxt : src.getSCSeenContexts()) {
				sees.add(new JsonPrimitive(ctxt.getComponentName()));
			}
			obj.add("sees", sees);

			JsonArray variables = new JsonArray();
			for (ISCVariable variable : src.getSCVariables()) {
				variables.add(new JsonPrimitive(variable.getElementName()));
			}
			obj.add("variables", variables);

			JsonArray invariants = new JsonArray();
			for (ISCInvariant invariant : src.getSCInvariants()) {
				JsonObject ebi = new JsonObject();
				ebi.addProperty("name", invariant.getElementName());
				ebi.addProperty("code", invariant.getPredicateString());
				ebi.addProperty("theorem", invariant.isTheorem());
				invariants.add(ebi);

			}
			obj.add("invariants", invariants);

			JsonArray variant = new JsonArray();
			for (ISCVariant v : src.getSCVariants()) {
				variant.add(new JsonPrimitive(v.getExpressionString()));
			}
			obj.add("variant", variant);

			JsonArray events = new JsonArray();
			for (ISCEvent event : src.getSCEvents()) {
				events.add(serializeEvent(event));
			}
			obj.add("events", events);
		} catch (RodinDBException e) {
			e.printStackTrace();
		}

		return obj;
	}

}
