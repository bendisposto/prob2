package de.prob.animator.command;

import java.util.ArrayList;
import java.util.List;

import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class SimpleTextCommand implements IRawCommand, ICommand {

	private final String command;
	private final String resultVar;
	private List<String> results = null;

	public SimpleTextCommand(final String command, final String resultVar) {
		this.command = command;
		this.resultVar = resultVar;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		throw new UnsupportedOperationException(
				"This is a raw command. It cannot write to a IPrologTermOutput");

	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		results = new ArrayList<String>();
		ListPrologTerm list = BindingGenerator.getList(bindings.get(resultVar));

		for (PrologTerm prologTerm : list) {
			results.add(prologTerm.getFunctor());
		}
	}

	@Override
	public String getCommand() {
		return command;
	}

	public List<String> getResults() {
		if (results == null) {
			throw new IllegalStateException("Command was not executed");
		}
		return results;
	}

}
