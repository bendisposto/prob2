package de.prob.visualization;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.prob.Main;
import de.prob.animator.command.FilterStatesForPredicateCommand;
import de.prob.animator.domainobjects.EvalElementFactory;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.statespace.IStatesCalculatedListener;
import de.prob.statespace.OpInfo;
import de.prob.statespace.StateId;
import de.prob.statespace.StateSpace;

public class DynamicTransformer extends Transformer implements
		IStatesCalculatedListener {

	private transient final IEvalElement predicate;
	private transient final StateSpace space;
	private transient final List<StateId> filtered;

	public DynamicTransformer(final IEvalElement predicate,
			final StateSpace space) {
		super("");
		this.predicate = predicate;
		this.space = space;
		filtered = space.getStatesFromPredicate(predicate);
		space.registerStateSpaceListener(this);
		List<String> toConvert = new ArrayList<String>();
		for (StateId id : filtered) {
			toConvert.add("#s" + id.getId());
		}
		updateSelector(Joiner.on(",").join(toConvert));
	}

	@Override
	public void newTransitions(final List<OpInfo> newOps) {
		List<StateId> toFilter = new ArrayList<StateId>();
		for (OpInfo opInfo : newOps) {
			StateId src = space.getVertex(opInfo.getSrc());
			StateId dest = space.getVertex(opInfo.getDest());
			if (!filtered.contains(src)) {
				toFilter.add(src);
			}
			if (!filtered.contains(dest)) {
				toFilter.add(dest);
			}
		}
		if (!toFilter.isEmpty()) {
			FilterStatesForPredicateCommand cmd = new FilterStatesForPredicateCommand(
					predicate, toFilter);
			space.execute(cmd);
			List<String> filteredIds = cmd.getFiltered();
			String newSelector = recalculateSelector(filteredIds);
			updateSelector(newSelector);
		}
	}

	private String recalculateSelector(final List<String> f) {
		if (f.isEmpty()) {
			return selector;
		}

		List<String> toConvert = new ArrayList<String>();
		for (String string : f) {
			filtered.add(space.getVertex(string));
			toConvert.add("#s" + string);
		}
		String newSelector = Joiner.on(",").join(toConvert);
		if (selector.equals("")) {
			return newSelector;
		}
		return selector + "," + newSelector;
	}

	public String serialize() {
		ToSerialize toSerialize = new ToSerialize(predicate.serialized(),
				attributes, styles);

		Gson g = new Gson();
		return g.toJson(toSerialize);
	}

	public static Transformer deserialize(final String json, final StateSpace s) {
		JsonParser parser = new JsonParser();
		JsonObject object = parser.parse(json).getAsJsonObject();

		EvalElementFactory deserializer = Main.getInjector()
				.getInstance(EvalElementFactory.class);
		DynamicTransformer transformer = new DynamicTransformer(
				deserializer.deserialize(object.get("predicate").getAsString()),
				s);

		JsonElement e = object.get("attrs");
		if (e != null) {
			JsonArray array = e.getAsJsonArray();
			for (JsonElement jsonElement : array) {
				JsonObject asJsonObject = jsonElement.getAsJsonObject();
				transformer.set(asJsonObject.get("name").getAsString(),
						asJsonObject.get("value").getAsString());
			}
		}
		e = object.get("styles");
		if (e != null) {
			JsonArray array = e.getAsJsonArray();
			for (JsonElement jsonElement : array) {
				JsonObject asJsonObject = jsonElement.getAsJsonObject();
				transformer.set(asJsonObject.get("name").getAsString(),
						asJsonObject.get("value").getAsString());
			}
		}

		return transformer;
	}

	class ToSerialize {

		public final String predicate;
		public final List<Attribute> attrs;
		public final List<Style> styles;

		public ToSerialize(final String predicate, final List<Attribute> attrs,
				final List<Style> styles) {
			this.predicate = predicate;
			this.attrs = attrs;
			this.styles = styles;
		}
	}
}
