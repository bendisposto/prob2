package de.prob.animator.command;

import java.util.ArrayList;
import java.util.List;

import de.prob.animator.domainobjects.DotCommandItem;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.State;

public class GetAllDotCommands extends AbstractCommand {
	
	private static final String PROLOG_COMMAND_NAME = "get_dot_commands_in_state";
	
	private static final String LIST = "List";
	
	private static final String COMMAND_VARIABLE = "Command";
	
	private static final String DESCRIPTION_VARIABLE = "Description";
	
	private static final String NUMBER_ARGS_VARIABLE = "Number_Args";
	
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
				
	}
	
	public List<DotCommandItem> getCommands() {
		return commands;
	}

}
