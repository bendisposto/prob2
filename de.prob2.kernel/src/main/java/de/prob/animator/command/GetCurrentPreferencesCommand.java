package de.prob.animator.command;

import java.util.HashMap;
import java.util.Map;

import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class GetCurrentPreferencesCommand extends AbstractCommand {
	private static final String PROLOG_COMMAND_NAME = "list_current_eclipse_preferences";
	private static final String PREFERENCES_VARIABLE = "Preferences";

	private final Map<String, String> preferences = new HashMap<>();

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME).printVariable(PREFERENCES_VARIABLE).closeTerm();
	}

	@Override
	public void processResult(final ISimplifiedROMap<String, PrologTerm> bindings) {
		ListPrologTerm prefs = BindingGenerator.getList(bindings.get(PREFERENCES_VARIABLE));

		for (PrologTerm prologTerm : prefs) {
			CompoundPrologTerm cpt = BindingGenerator.getCompoundTerm(prologTerm, 2);
			preferences.put(cpt.getArgument(1).getFunctor(), cpt.getArgument(2).getFunctor());
		}
	}

	public Map<String, String> getPreferences() {
		return preferences;
	}
}
