package de.prob.units;

import com.google.inject.Inject;

import de.prob.animator.command.ActivateUnitPluginCommand;
import de.prob.animator.command.GetPluginResultCommand;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.scripting.Api;
import de.prob.scripting.ModelTranslationError;
import de.prob.statespace.StateSpace;

import java.io.IOException;

public class UnitAnalysis {
	private final Api api;

	@Inject
	public UnitAnalysis(final Api api) {
		this.api = api;
	}

	public CompoundPrologTerm run(final String filename) throws IOException, ModelTranslationError{
		StateSpace s = api.eventb_load(filename);

		final ActivateUnitPluginCommand activatePlugin = new ActivateUnitPluginCommand();
		GetPluginResultCommand pluginResultCommand = new GetPluginResultCommand(
				"Grounded Result State");

		s.execute(activatePlugin, pluginResultCommand);
		return pluginResultCommand.getResult();
	}
}
