package de.prob.rodin.translate;

import org.eventb.core.IEventBRoot;
import org.eventb.core.ISCContextRoot;
import org.eventb.core.ISCMachineRoot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class EventBRootToJSON {
	private final IEventBRoot model;
	private final Gson gson;

	public EventBRootToJSON(final IEventBRoot model) {
		this.model = model;
		GsonBuilder gsonB = new GsonBuilder();
		gsonB.registerTypeAdapter(IEventBRoot.class, new EventBRootSerializer());
		gsonB.registerTypeAdapter(ISCContextRoot.class,
				new ContextRootSerializer());
		gsonB.registerTypeAdapter(ISCMachineRoot.class,
				new MachineRootSerializer());
		gson = gsonB.create();
	}

	public String toJson() {
		return gson.toJson(model);
	}
}
