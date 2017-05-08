package de.prob.check;

import java.util.List;

import de.prob.animator.command.ConstraintBasedInvariantCheckCommand;
import de.prob.statespace.StateSpace;

/**
 * This {@link IModelCheckJob} performs constraint based invariant checking on a
 * {@link StateSpace} given an (optional) list of events to check. This class
 * should be used with the {@link ModelChecker} wrapper class to perform model
 * checking. Communications with the ProB kernel take place via the
 * {@link ConstraintBasedInvariantCheckCommand}.
 * 
 * @author joy
 * 
 */
public class CBCInvariantChecker implements IModelCheckJob {

	private final ConstraintBasedInvariantCheckCommand command;
	private final StateSpace s;
	private final IModelCheckListener ui;
	private final String jobId;

	/**
	 * Calls {@link #CBCInvariantChecker(StateSpace, List)} with null as the
	 * second parameter.
	 * 
	 * @param s
	 *            StateSpace which should be checked with this
	 *            {@link CBCInvariantChecker}
	 */
	public CBCInvariantChecker(final StateSpace s) {
		this(s, null);
	}

	/**
	 * Calls {@link #CBCInvariantChecker(StateSpace, List, IModelCheckListener)}
	 * with null for the UI element
	 * 
	 * @param s
	 *            StateSpace which should be checked with this
	 *            {@link CBCInvariantChecker}
	 * @param eventNames
	 *            List of events that are to be checked or <code>null</code> if
	 *            they are all to be checked
	 */
	public CBCInvariantChecker(final StateSpace s, final List<String> eventNames) {
		this(s, eventNames, null);
	}

	/**
	 * @param s
	 *            StateSpace which should be checked with this
	 *            {@link CBCInvariantChecker}
	 * @param eventNames
	 *            List of events which are to be checked or <code>null</code> if
	 *            they are all to be checked
	 * @param ui
	 *            {@link IModelCheckListener} object if the UI should be notified of
	 *            changes, or null if not
	 */
	public CBCInvariantChecker(final StateSpace s,
			final List<String> eventNames, final IModelCheckListener ui) {
		this.s = s;
		this.ui = ui;
		jobId = ModelChecker.generateJobId();
		command = new ConstraintBasedInvariantCheckCommand(s, eventNames);
	}

	@Override
	public IModelCheckingResult call() throws Exception {
		long time = System.currentTimeMillis();
		if (ui != null) {
			ui.updateStats(jobId, 0, new NotYetFinished(
					"Deadlock check started", 0), null);
		}
		s.execute(command);
		if (ui != null) {
			ui.isFinished(jobId, System.currentTimeMillis() - time,
					command.getResult(), null);
		}
		return command.getResult();
	}

	@Override
	public IModelCheckingResult getResult() {
		if (command.getResult() == null) {
			return new NotYetFinished("No result was calculated", -1);
		}
		return command.getResult();
	}

	@Override
	public String getJobId() {
		return jobId;
	}

	@Override
	public StateSpace getStateSpace() {
		return s;
	}

}
