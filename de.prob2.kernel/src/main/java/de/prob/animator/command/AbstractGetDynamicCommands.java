package de.prob.animator.command;

import java.util.ArrayList;
import java.util.List;

import de.prob.animator.domainobjects.DynamicCommandItem;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.IntegerPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.State;

public abstract class AbstractGetDynamicCommands extends AbstractCommand {

	private static final String LIST = "List";
	private List<DynamicCommandItem> commands = new ArrayList<>();
	private final State id;
	private final String commandName;

	public AbstractGetDynamicCommands(State id, String commandName) {
		this.id = id;
		this.commandName = commandName;
	}

	private static DynamicCommandItem toCommandItem(final PrologTerm commandTerm) {
		final String command = PrologTerm.atomicString(commandTerm.getArgument(1));
		final String name = PrologTerm.atomicString(commandTerm.getArgument(2));
		final String description = PrologTerm.atomicString(commandTerm.getArgument(3));
		final int arity = ((IntegerPrologTerm) commandTerm.getArgument(4)).getValue().intValue();
		ListPrologTerm listTerm = (ListPrologTerm) commandTerm.getArgument(5);
		final List<String> relevantPreferences = new ArrayList<>();
		for(PrologTerm term : listTerm) {
			relevantPreferences.add(PrologTerm.atomicString(term));
		}
		final String available = PrologTerm.atomicString(commandTerm.getArgument(6));
		return new DynamicCommandItem(command, name, description, arity, relevantPreferences, available);
	}

	@Override
	public void writeCommand(IPrologTermOutput pto) {
		pto.openTerm(commandName);
		pto.printAtomOrNumber(id.getId());
		pto.printVariable(LIST);
		pto.closeTerm();
	}

	@Override
	public void processResult(ISimplifiedROMap<String, PrologTerm> bindings) {
		ListPrologTerm res = (ListPrologTerm) bindings.get(LIST);
		for (PrologTerm prologTerm : res) {
			commands.add(toCommandItem(prologTerm));
		}		
	}

	public List<DynamicCommandItem> getCommands() {
		return commands;
	}

}