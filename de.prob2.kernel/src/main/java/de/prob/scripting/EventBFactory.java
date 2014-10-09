package de.prob.scripting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.prob.animator.command.AbstractCommand;
import de.prob.animator.command.ComposedCommand;
import de.prob.animator.command.LoadEventBProjectCommand;
import de.prob.animator.command.SetPreferenceCommand;
import de.prob.animator.command.StartAnimationCommand;
import de.prob.model.eventb.Context;
import de.prob.model.eventb.EventBMachine;
import de.prob.model.eventb.EventBModel;
import de.prob.model.eventb.theory.Theory;
import de.prob.model.eventb.translate.EventBDatabaseTranslator;
import de.prob.model.eventb.translate.EventBModelTranslator;
import de.prob.model.representation.RefType;
import de.prob.statespace.StateSpace;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.Pair;

public class EventBFactory extends ModelFactory {

	private final Provider<EventBModel> modelProvider;

	@Inject
	public EventBFactory(final Provider<EventBModel> modelProvider,
			final FileHandler fileHandler) {
		super(fileHandler);
		this.modelProvider = modelProvider;
	}

	public EventBModel load(final String file, final Map<String, String> prefs,
			final boolean loadVariables) {
		EventBModel model = modelProvider.get();

		new EventBDatabaseTranslator(model, file);
		model.isFinished();

		return load2(model, prefs, loadVariables);
	}

	public EventBModel reload(final EventBModel model,
			final Map<String, String> prefs, final boolean loadVariables) {
		EventBModel newModel = modelProvider.get();

		for (EventBMachine eventBMachine : model.getMachines()) {
			newModel.addMachine(eventBMachine);
		}
		for (Context context : model.getContexts()) {
			newModel.addContext(context);
		}
		newModel.addTheories(model.getChildrenOfType(Theory.class));
		DirectedSparseMultigraph<String, RefType> graph = model.getGraph();
		for (RefType refType : graph.getEdges()) {
			Pair<String> endpoints = graph.getEndpoints(refType);
			newModel.addRelationship(endpoints.getFirst(),
					endpoints.getSecond(), refType);
		}
		newModel.setModelFile(model.getModelFile());
		newModel.isFinished();

		return load2(newModel, prefs, loadVariables);
	}

	private EventBModel load2(final EventBModel model,
			final Map<String, String> prefs, final boolean loadVariables) {
		List<AbstractCommand> cmds = new ArrayList<AbstractCommand>();

		for (Entry<String, String> pref : getPreferences(model, prefs)
				.entrySet()) {
			cmds.add(new SetPreferenceCommand(pref.getKey(), pref.getValue()));
		}

		AbstractCommand loadcmd = new LoadEventBProjectCommand(
				new EventBModelTranslator(model));

		cmds.add(loadcmd);
		cmds.add(new StartAnimationCommand());
		StateSpace s = model.getStateSpace();
		s.execute(new ComposedCommand(cmds));
		s.setLoadcmd(loadcmd);

		if (loadVariables) {
			model.subscribeFormulasOfInterest();
		}
		return model;
	}
}
