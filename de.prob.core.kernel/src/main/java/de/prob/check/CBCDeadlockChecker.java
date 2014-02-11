package de.prob.check;

import de.prob.animator.command.ConstraintBasedDeadlockCheckCommand;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.statespace.StateSpace;
import de.prob.web.views.ModelCheckingUI;

public class CBCDeadlockChecker implements IModelCheckJob {

	private final StateSpace s;
	private final ModelCheckingUI ui;
	private final ConstraintBasedDeadlockCheckCommand job;
	String jobId;

	public CBCDeadlockChecker(final StateSpace s) {
		this(s, null);
	}

	public CBCDeadlockChecker(final StateSpace s, final IEvalElement constraint) {
		this(s, constraint, null);
	}

	public CBCDeadlockChecker(final StateSpace s,
			final IEvalElement constraint, final ModelCheckingUI ui) {
		this.s = s;
		job = new ConstraintBasedDeadlockCheckCommand(constraint);
		this.ui = ui;
		jobId = ModelChecker.generateJobId();
	}

	@Override
	public IModelCheckingResult call() throws Exception {
		long time = System.currentTimeMillis();
		s.execute(job);
		if (ui != null) {
			ui.isFinished(jobId, System.currentTimeMillis() - time,
					job.getResult(), null);
		}
		return job.getResult();
	}

	@Override
	public IModelCheckingResult getResult() {
		return job.getResult() == null ? new NotYetFinished(
				"Deadlock Checking Interrupted", -1) : job.getResult();
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
