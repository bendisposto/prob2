package de.prob.model.serialize;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.prob.model.eventb.Context;
import de.prob.model.eventb.EventBMachine;
import de.prob.model.eventb.EventBModel;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.Machine;

public class ModelSerializer {
	private final EventBModel model;
	private final Gson gson;

	public ModelSerializer(final EventBModel model) {
		this.model = model;
		GsonBuilder gsonB = new GsonBuilder();
		gsonB.registerTypeAdapter(EventBMachine.class,
				new EventBMachineSerializer());
		gsonB.registerTypeAdapter(Context.class, new ContextSerializer());
		gson = gsonB.create();
	}

	public String toJson() {
		List<Object> objects = new ArrayList<Object>();
		objects.add(model.getMainComponent().getClass().getSimpleName());
		objects.add(extractName(model.getMainComponent()));
		objects.add(model.getChildrenOfType(Machine.class));
		objects.add(model.getChildrenOfType(Context.class));
		objects.add(model.getModelFile());
		return gson.toJson(objects);
	}

	private Object extractName(final AbstractElement mainComponent) {
		if (mainComponent instanceof EventBMachine) {
			return ((EventBMachine) mainComponent).getName();
		}
		if (mainComponent instanceof Context) {
			return ((Context) mainComponent).getName();
		}
		throw new IllegalArgumentException(
				"Tried to extract component name from an object that was neither an EventBMachine nor a Context");
	}
}
