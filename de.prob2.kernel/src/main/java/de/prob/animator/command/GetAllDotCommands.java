package de.prob.animator.command;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.prob.animator.domainobjects.DotCommandItem;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.IntegerPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.State;

public class GetAllDotCommands extends AbstractCommand {
	
	private static final String PROLOG_COMMAND_NAME = "get_dot_commands_in_state";
	
	private static final String LIST = "List";
	
	private List<DotCommandItem> commands = new ArrayList<>();
	
	private final State id;
	
	public GetAllDotCommands(State id) {
		this.id = id;
	}
	
	@Override
	public void writeCommand(IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME);
		pto.printAtomOrNumber(id.getId());
		pto.printVariable(LIST);
		pto.closeTerm();
	}

	@Override
	public void processResult(ISimplifiedROMap<String, PrologTerm> bindings) {
		ListPrologTerm res = (ListPrologTerm) bindings.get(LIST);
		commands = res.stream().map(GetAllDotCommands::toCommandItem).collect(Collectors.toList());
	}
	
	private static DotCommandItem toCommandItem(final PrologTerm commandTerm) {
		final String command = PrologTerm.atomicString(commandTerm.getArgument(1));
		final String name = PrologTerm.atomicString(commandTerm.getArgument(2));
		final String description = PrologTerm.atomicString(commandTerm.getArgument(3));
		final int arity = ((IntegerPrologTerm) commandTerm.getArgument(4)).getValue().intValue();
		final String available = PrologTerm.atomicString(commandTerm.getArgument(6));
		return new DotCommandItem(command, name, description, arity, available);
	}
	
	public List<DotCommandItem> getCommands() {
		return commands;
	}

}
