/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.animator.command;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.animator.domainobjects.ProBPreference;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

//FIXME: Is this what this command does?
/**
 * Gets the eclipse preferences from ProB
 * 
 * @author joy
 * 
 */
public final class GetPreferencesCommand implements ICommand {

	Logger logger = LoggerFactory.getLogger(GetPreferencesCommand.class);
	private static final String PREFS_VARIABLE = "Prefs";
	private List<ProBPreference> prefs;

	public List<ProBPreference> getPreferences() {
		return prefs;
	}

	private ProBPreference verifyTerm(final PrologTerm term)
			 {
		CompoundPrologTerm compoundTerm;
		compoundTerm = BindingGenerator.getCompoundTerm(term, "preference", 5);
		return new ProBPreference(compoundTerm);
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			 {

		ListPrologTerm p = BindingGenerator.getList(bindings
				.get(PREFS_VARIABLE));
		prefs = new ArrayList<ProBPreference>();
		for (PrologTerm term : p) {
			ProBPreference preference = null;
			preference = verifyTerm(term);
			prefs.add(preference);
		}

	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("list_eclipse_preferences").printVariable(PREFS_VARIABLE)
				.closeTerm();
	}
}
