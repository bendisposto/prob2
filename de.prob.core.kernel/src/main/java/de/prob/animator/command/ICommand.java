package de.prob.animator.command;

import de.prob.parser.ISimplifiedROMap;
import de.prob.parser.ResultParserException;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public interface ICommand {
	void writeCommand(IPrologTermOutput pto);

	void processResult(ISimplifiedROMap<String, PrologTerm> bindings) throws ResultParserException;
}
