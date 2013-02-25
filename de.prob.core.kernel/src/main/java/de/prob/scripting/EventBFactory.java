package de.prob.scripting;

import java.io.File;
import java.util.Collection;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.prob.model.eventb.Context;
import de.prob.model.eventb.EventBMachine;
import de.prob.model.eventb.EventBModel;
import de.prob.model.representation.AbstractElement;

public class EventBFactory {

	private final Provider<EventBModel> modelProvider;

	@Inject
	public EventBFactory(final Provider<EventBModel> modelProvider) {
		this.modelProvider = modelProvider;
	}

	public EventBModel load(final AbstractElement mainComponent,
			final Collection<EventBMachine> machines,
			final Collection<Context> contexts, final File modelFile) {
		EventBModel model = modelProvider.get();

		model.setMainComponent(mainComponent);
		model.addMachines(machines);
		model.addContexts(contexts);
		model.setModelFile(modelFile);

		model.isFinished();

		return model;
	}
}
