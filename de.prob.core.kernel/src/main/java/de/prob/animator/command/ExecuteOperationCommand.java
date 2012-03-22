/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.animator.command;

import de.prob.ProBException;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public final class ExecuteOperationCommand implements ICommand {

	private final OpInfo operation;
	private final boolean fireCurrentStateChanged;
	private final ExploreStateCommand exloreStateCmd;
	private final SetStateCommand setStateCmd;
	private final ICommand cmds;

	private ExecuteOperationCommand(final OpInfo operation) {
		this(operation, false);
	}
	
	

	private ExecuteOperationCommand(final OpInfo operation,
			final boolean silent) {
		this.operation = operation;
		this.fireCurrentStateChanged = !silent;
		final String stateId = operation.dest;
		this.exloreStateCmd = new ExploreStateCommand(stateId);
		this.setStateCmd = new SetStateCommand(stateId);
		this.cmds = new ComposedCommand(exloreStateCmd, setStateCmd);
	}

	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) throws ProBException {
		cmds.processResult(bindings);
		
//		FIXME: REFACTOR!!!
//		final Animator animator = Animator.getAnimator();
//		final State state = exloreStateCmd.getState();
//
//		// Change history in Animator
//		animator.getHistory().add(state, operation);
//
//		if (fireCurrentStateChanged) {
//			animator.announceCurrentStateChanged(state, operation);
//		}
	}

	public void writeCommand(final IPrologTermOutput pto) throws ProBException{
	//	LimitedLogger.getLogger().log("execute operation", operation.getName(),null);
		cmds.writeCommand(pto);
	}

	public OpInfo getOperation() {
		return operation;
	}

	public boolean isFireCurrentStateChanged() {
		return fireCurrentStateChanged;
	}
}
