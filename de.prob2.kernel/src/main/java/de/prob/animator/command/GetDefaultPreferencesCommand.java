/*
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 */

package de.prob.animator.command;

import java.util.List;
import java.util.stream.Collectors;

import de.prob.animator.domainobjects.ProBPreference;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

/**
 * Gets the eclipse preferences from ProB.
 * 
 * @author joy
 */
public final class GetDefaultPreferencesCommand extends AbstractCommand {
	private static final String PROLOG_COMMAND_NAME = "list_all_eclipse_preferences";
	private static final String PREFS_VARIABLE = "Prefs";

	private List<ProBPreference> prefs;

	public List<ProBPreference> getPreferences() {
		return prefs;
	}

	private ProBPreference verifyTerm(final PrologTerm term) {
		return new ProBPreference(BindingGenerator.getCompoundTerm(term, "preference", 5));
	}

	@Override
	public void processResult(final ISimplifiedROMap<String, PrologTerm> bindings) {
		this.prefs = BindingGenerator.getList(bindings.get(PREFS_VARIABLE)).stream()
			.map(this::verifyTerm)
			.collect(Collectors.toList());
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME).printVariable(PREFS_VARIABLE).closeTerm();
	}
}
