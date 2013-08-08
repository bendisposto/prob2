package de.prob.animator;

import de.prob.animator.command.AbstractCommand;
import de.prob.animator.command.IRawCommand;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.term.PrologTerm;

class CommandProcessor {

	public ISimplifiedROMap<String, PrologTerm> sendCommand(
			final AbstractCommand command) {

		Query qry = new Query();
		if (command instanceof IRawCommand) {
			throw new RuntimeException("not supported yet");
		} else {
			command.writeCommand(qry);
		}

		qry.execute();

		return new SimplifiedROMap<String, PrologTerm>(qry.getBinding());
	}

	
}
