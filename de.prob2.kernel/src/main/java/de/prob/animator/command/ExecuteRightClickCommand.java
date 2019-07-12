package de.prob.animator.command;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public class ExecuteRightClickCommand extends AbstractCommand {

	private static final String PROLOG_COMMAND_NAME = "react_to_item_right_click_option_for_state";
	private static final String TRANSITION = "TransitionId";
	private static final String NEW_STATE = "NewStateId";
	
	private String stateId;
	private int row;
	private int column;
	private String option;
	private String transitionID;

	public ExecuteRightClickCommand(String stateId, int row, int column, String option) {
		this.stateId = stateId;
		this.row = row;
		this.column = column;
		this.option = option;
	}

	@Override
	public void writeCommand(IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME);
		pto.printAtomOrNumber(stateId);
		pto.printNumber(row);
		pto.printNumber(column);
		pto.printAtom(option);
		pto.printVariable(TRANSITION);
		pto.printVariable(NEW_STATE);
		pto.closeTerm();
	}

	@Override
	public void processResult(ISimplifiedROMap<String, PrologTerm> bindings) {
		transitionID = bindings.get(TRANSITION).getFunctor();
	}

	public String getTransitionID() {
		return transitionID;
	}

}
