package de.prob.check;

import de.prob.animator.command.ConstraintBasedDeadlockCheckCommand;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.statespace.StateSpace;

/**
 * This {@link IModelCheckJob} performs constraint based deadlock checking on
 * the given {@link StateSpace} using an optional {@link IEvalElement}
 * constraint. This class should be used in conjunction with the
 * {@link ModelChecker} wrapper class in order to perform model checking.
 * Communication with the ProB kernel takes place via the
 * {@link ConstraintBasedDeadlockCheckCommand} command.
 * 
 * @author joy
 * 
 */
public class CBCDeadlockChecker implements IModelCheckJob {

	private final StateSpace s;
	private final IModelCheckListener ui;
	private final ConstraintBasedDeadlockCheckCommand job;
	String jobId;

	/**
	 * Calls {@link #CBCDeadlockChecker(StateSpace, IEvalElement)} with
	 * <code>null</code> as the optional constraint.
	 * 
	 * @param s
	 *            StateSpace for which the checking should take place
	 */
	public CBCDeadlockChecker(final StateSpace s) {
		this(s, new ClassicalB("1=1"));
	}

	/**
	 * Calls
	 * {@link #CBCDeadlockChecker(StateSpace, IEvalElement, IModelCheckListener)}
	 * with <code>null</code> as the UI component
	 * 
	 * @param s
	 *            StateSpace for which the checking should take place
	 * @param constraint
	 *            {@link IEvalElement} formula constraint or <code>null</code>
	 *            if no constraint is specified
	 */
	public CBCDeadlockChecker(final StateSpace s, final IEvalElement constraint) {
		this(s, constraint, null);
	}

	/**
	 * @param s
	 *            StateSpace for which the checking should take place
	 * @param constraint
	 *            {@link IEvalElement} formula constraint or <code>null</code>
	 *            if no constraint is specified
	 * @param ui
	 *            {@link IModelCheckListener} ui component if the checker should
	 *            communicate with the UI or <code>null</code> if not.
	 */
	public CBCDeadlockChecker(final StateSpace s,
			final IEvalElement constraint, final IModelCheckListener ui) {
		this.s = s;
		job = new ConstraintBasedDeadlockCheckCommand(s, constraint);
		this.ui = ui;
		jobId = ModelChecker.generateJobId();
	}

	@Override
	public IModelCheckingResult call() throws Exception {
		long time = System.currentTimeMillis();
		if (ui != null) {
			ui.updateStats(jobId, 0, new NotYetFinished(
					"Deadlock check started", 0), null);
		}
		s.execute(job);
		if (ui != null) {
			ui.isFinished(jobId, System.currentTimeMillis() - time,
					job.getResult(), null);
		}
		return job.getResult();
	}

	@Override
	public IModelCheckingResult getResult() {
		if (job.getResult() == null) {
			return new NotYetFinished("No result was calculated", -1);
		}
		return job.getResult();
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
