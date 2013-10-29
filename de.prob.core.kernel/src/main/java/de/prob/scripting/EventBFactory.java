package de.prob.scripting;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.prob.animator.command.AbstractCommand;
import de.prob.animator.command.LoadEventBProjectCommand;
import de.prob.animator.command.StartAnimationCommand;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.eventb.EventBModel;
import de.prob.model.eventb.translate.EventBDatabaseTranslator;
import de.prob.model.eventb.translate.EventBModelTranslator;
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

	private void subscribeVariables(final EventBModel m) {
		List<Machine> machines = m.getChildrenOfType(Machine.class);
		for (Machine machine : machines) {
			List<Variable> childrenOfType = machine
					.getChildrenOfType(Variable.class);
			List<IEvalElement> formulas = new ArrayList<IEvalElement>();
			for (Variable variable : childrenOfType) {
				formulas.add(variable.getExpression());
			}
			m.getStatespace().subscribe(this, formulas);
		}
	}
}
