package de.prob.animator.command;

import de.prob.statespace.State;

public class GetAllDotCommands extends AbstractGetDynamicCommands {

	static final String PROLOG_COMMAND_NAME = "get_dot_commands_in_state";

	public GetAllDotCommands(State id) {
		super(id, PROLOG_COMMAND_NAME);
	}

}
