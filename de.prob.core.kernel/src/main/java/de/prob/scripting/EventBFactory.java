package de.prob.scripting;

import java.io.File;
import java.util.Collection;
import java.util.Set;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.prob.animator.command.AbstractCommand;
import de.prob.animator.command.LoadEventBCommand;
import de.prob.animator.command.LoadEventBProjectCommand;
import de.prob.animator.command.StartAnimationCommand;
import de.prob.model.eventb.Context;
import de.prob.model.eventb.EventBMachine;
import de.prob.model.eventb.EventBModel;
import de.prob.model.eventb.translate.EventBDatabaseTranslator;
import de.prob.model.eventb.translate.EventBModelTranslator;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.Machine;
import de.prob.model.representation.Variable;
import de.prob.statespace.StateSpace;

public class EventBFactory {

	private final Provider<EventBModel> modelProvider;

	@Inject
	public EventBFactory(final Provider<EventBModel> modelProvider) {
		this.modelProvider = modelProvider;
	}

	public EventBModel load(final String file) {
		EventBModel model = modelProvider.get();

		new EventBDatabaseTranslator(model, file);

		AbstractCommand cmd = new LoadEventBProjectCommand(
				new EventBModelTranslator(model));

		StateSpace s = model.getStatespace();
		s.execute(cmd);
		s.execute(new StartAnimationCommand());

		subscribeVariables(model);

		return model;
	}

	public EventBModel load(final AbstractElement mainComponent,
			final Collection<EventBMachine> machines,
			final Collection<Context> contexts, final File modelFile) {
		EventBModel model = modelProvider.get();

		setModelInformation(mainComponent, machines, contexts, modelFile, model);

		return model;
	}

	private void setModelInformation(final AbstractElement mainComponent,
			final Collection<EventBMachine> machines,
			final Collection<Context> contexts, final File modelFile,
			final EventBModel model) {
		model.setMainComponent(mainComponent);
		model.addMachines(machines);
		model.addContexts(contexts);
		model.setModelFile(modelFile);

		model.isFinished();
	}

	public EventBModel load(final String cmd, final String coded) {
		EventBModel model = modelProvider.get();

		// ModelObject mo = Serializer.deserialize(coded);

		// setModelInformation(mo.getMainComponent(), mo.getMachines(),
		// mo.getContexts(), mo.getModelFile(), model);

		StateSpace s = model.getStatespace();
		s.execute(new LoadEventBCommand(cmd));
		s.execute(new StartAnimationCommand());

		subscribeVariables(model);

		return model;
	}

	private void subscribeVariables(final EventBModel m) {
		Set<Machine> machines = m.getChildrenOfType(Machine.class);
		for (Machine machine : machines) {
			Set<Variable> childrenOfType = machine
					.getChildrenOfType(Variable.class);
			for (Variable variable : childrenOfType) {
				m.getStatespace().subscribe(this, variable.getExpression());
			}
		}
	}
}
