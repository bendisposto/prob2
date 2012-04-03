/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.animator.command.notImplemented;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.animator.command.ICommand;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;
import de.prob.ProBException;

public final class GetCurrentStateIdCommand implements ICommand {

	Logger logger = LoggerFactory.getLogger(GetCurrentStateIdCommand.class);
	
	private String getStateID(final PrologTerm term) {
		final String result;
		if (term.isAtom()) {
			result = PrologTerm.atomicString(term);
		} else {
			result = term.toString();
		}
		return result;
	}

	public List<String> getStateIDs(final Collection<PrologTerm> terms) {
		final List<String> ids = new ArrayList<String>(terms.size());
		for (final PrologTerm term : terms) {
			ids.add(getStateID(term));
		}
		return ids;
	}

	private String currentID;

	public GetCurrentStateIdCommand() {
	}

	public String getCurrentStateId() {
		return currentID;
	}

	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws ProBException {
		currentID = getStateID(bindings.get("ID"));
	}

	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("getCurrentStateID").printVariable("ID").closeTerm();
	}
}
