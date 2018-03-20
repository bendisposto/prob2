package de.prob.animator.command;

import de.prob.statespace.State;

public class GetAllTableCommands extends AbstractGetDynamicCommands {

	static final String PROLOG_COMMAND_NAME = "get_table_commands_in_state";

	public GetAllTableCommands(State id) {
		super(id, PROLOG_COMMAND_NAME);
	}

}
