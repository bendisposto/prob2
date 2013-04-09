package de.prob.rodin.translate;

import java.lang.reflect.Type;

import org.eventb.core.ISCAxiom;
import org.eventb.core.ISCCarrierSet;
import org.eventb.core.ISCConstant;
import org.eventb.core.ISCContextRoot;
import org.eventb.core.ISCExtendsContext;
import org.rodinp.core.RodinDBException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ContextRootSerializer implements JsonSerializer<ISCContextRoot> {

	@Override
	public JsonElement serialize(final ISCContextRoot src,
			final Type typeOfSrc, final JsonSerializationContext context) {
		JsonObject object = new JsonObject();

		try {
			object.addProperty("name", src.getComponentName());

			JsonArray exts = new JsonArray();
			for (ISCExtendsContext ctxt : src.getSCExtendsClauses()) {
				exts.add(new JsonPrimitive(ctxt.getAbstractSCContext()
						.getRodinFile().getBareName()));
			}
			object.add("extends", exts);

			JsonArray set = new JsonArray();

			for (ISCCarrierSet iscCarrierSet : src.getSCCarrierSets()) {
				set.add(new JsonPrimitive(iscCarrierSet.getIdentifierString()));
			}

			object.add("sets", set);

			JsonArray csts = new JsonArray();
			for (ISCConstant constant : src.getSCConstants()) {
				csts.add(new JsonPrimitive(constant.getElementName()));
			}
			object.add("constants", csts);

			JsonArray axms = new JsonArray();
			for (ISCAxiom axiom : src.getSCAxioms()) {
				JsonObject axm = new JsonObject();
				axm.addProperty("name", axiom.getRodinFile().getBareName());
				axm.addProperty("code", axiom.getPredicateString());
				axm.addProperty("theorem", axiom.isTheorem());
			}
			object.add("axioms", axms);

		} catch (RodinDBException e) {
			e.printStackTrace();
		}

		return object;
	}

}
