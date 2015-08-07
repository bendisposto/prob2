package de.prob.check;

import de.prob.animator.command.LTLCheckingJob;
import de.prob.animator.domainobjects.LTL;
import de.prob.statespace.StateSpace;

public class LTLChecker implements IModelCheckJob {

	private final StateSpace s;
	private final IModelCheckListener ui;
	private final String jobId;
	private final LTLCheckingJob job;

	public LTLChecker(final StateSpace s, final LTL formula) {
		this(s, formula, null);
	}

	public LTLChecker(final StateSpace s, final LTL formula,
			final IModelCheckListener ui) {
		if (formula == null) {
			throw new IllegalArgumentException(
					"Cannot perform LTL checking without a correctly parsed LTL Formula");
		}
		this.s = s;
		this.ui = ui;
		this.jobId = ModelChecker.generateJobId();
		job = new LTLCheckingJob(s, formula, jobId, ui);
	}

	@Override
	public IModelCheckingResult call() throws Exception {
		long time = System.currentTimeMillis();
		s.execute(job);
		IModelCheckingResult result = job.getResult();
		if (ui != null) {
			ui.isFinished(jobId, System.currentTimeMillis() - time, result,
					null);
		}
		return result;
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
