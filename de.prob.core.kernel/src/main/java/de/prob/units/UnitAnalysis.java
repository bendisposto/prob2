package de.prob.units;

import com.google.inject.Inject;

import de.prob.animator.command.ActivateUnitPluginCommand;
import de.prob.animator.command.GetPluginResultCommand;
import de.prob.model.eventb.EventBModel;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.scripting.Api;

public class UnitAnalysis {
	private final Api api;

	@Inject
	public UnitAnalysis(Api api) {
		this.api = api;
	}

	public CompoundPrologTerm run(String filename) {
		EventBModel model = api.eventb_load(filename);

		final ActivateUnitPluginCommand activatePlugin = new ActivateUnitPluginCommand();
		GetPluginResultCommand pluginResultCommand = new GetPluginResultCommand(
				"Grounded Result State");

		model.getStateSpace().execute(activatePlugin, pluginResultCommand);
		return pluginResultCommand.getResult();
	}
}
