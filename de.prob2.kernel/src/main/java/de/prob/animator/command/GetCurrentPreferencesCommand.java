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

	private final String PREFERENCES = "Preferences";
	private final Map<String, String> preferences = new HashMap<String, String>();

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("list_current_eclipse_preferences")
				.printVariable(PREFERENCES).closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		ListPrologTerm prefs = BindingGenerator.getList(bindings
				.get(PREFERENCES));

		for (PrologTerm prologTerm : prefs) {
			CompoundPrologTerm cpt = BindingGenerator.getCompoundTerm(
					prologTerm, 2);
			preferences.put(cpt.getArgument(1).getFunctor(), cpt.getArgument(2)
					.getFunctor());
		}
	}

	public Map<String, String> getPreferences() {
		return preferences;
	}

}
