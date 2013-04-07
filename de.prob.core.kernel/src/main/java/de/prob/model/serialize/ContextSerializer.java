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
import de.prob.model.eventb.EventBAxiom;
import de.prob.model.representation.Axiom;
import de.prob.model.representation.BSet;
import de.prob.model.representation.Constant;

public class ContextSerializer implements JsonSerializer<Context> {

	@Override
	public JsonElement serialize(final Context src, final Type typeOfSrc,
			final JsonSerializationContext context) {
		JsonObject object = new JsonObject();

		object.addProperty("name", src.getName());

		JsonArray exts = new JsonArray();
		Set<Context> extended = src.getChildrenOfType(Context.class);
		for (Context ctxt : extended) {
			exts.add(new JsonPrimitive(ctxt.getName()));
		}
		object.add("extends", exts);

		JsonArray set = new JsonArray();
		Set<BSet> sets = src.getChildrenOfType(BSet.class);
		for (BSet bSet : sets) {
			set.add(new JsonPrimitive(bSet.getName()));
		}
		object.add("sets", set);

		JsonArray csts = new JsonArray();
		Set<Constant> constants = src.getChildrenOfType(Constant.class);
		for (Constant constant : constants) {
			csts.add(new JsonPrimitive(constant.getExpression().getCode()));
		}
		object.add("constants", csts);

		JsonArray axms = new JsonArray();
		Set<Axiom> axioms = src.getChildrenOfType(Axiom.class);
		for (Axiom axiom : axioms) {
			if (axiom instanceof EventBAxiom) {
				JsonObject axm = new JsonObject();
				EventBAxiom ebAxiom = (EventBAxiom) axiom;
				axm.addProperty("name", ebAxiom.getName());
				axm.addProperty("code", ebAxiom.getPredicate().getCode());
				axm.addProperty("theorem", ebAxiom.isTheorem());
			}
		}
		object.add("axioms", axms);

		return object;
	}

}
