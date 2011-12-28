package de.prob.animator.command;

import de.prob.ProBException;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public interface ICommand {
	void writeCommand(IPrologTermOutput pto) throws ProBException;

	void processResult(ISimplifiedROMap<String, PrologTerm> bindings)
			throws ProBException;
}
