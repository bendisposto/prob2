package de.prob.animator.command;

import de.prob.check.IModelCheckingResult;
import de.prob.check.ModelCheckingOptions;
import de.prob.check.NotYetFinished;
import de.prob.check.StateSpaceStats;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.StateSpace;
import de.prob.web.views.ModelCheckingUI;

public class ModelCheckingJob extends AbstractCommand {

	private static final int TIME = 500;
	private final String jobId;
	private final StateSpace s;
	private ModelCheckingOptions options;
	private ModelCheckingStepCommand cmd;
	private boolean completed = false;
	private final long last;
	private IModelCheckingResult res;
	private StateSpaceStats stats;
	private final ModelCheckingUI ui;

	private long time = -1;

	public ModelCheckingJob(final StateSpace s,
			final ModelCheckingOptions options, final String jobId,
			final ModelCheckingUI ui) {
		this.s = s;
		this.options = options;
		this.jobId = jobId;
		this.ui = ui;
		last = s.getLastCalculatedStateId();
		cmd = new ModelCheckingStepCommand(TIME, options, last);
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		if (!s.isBusy()) {
			s.startTransaction();
		}
		if (time == -1) {
			time = System.currentTimeMillis();
		}
		if (Thread.interrupted()) {
			completed = true;
			Thread.currentThread().interrupt();
			s.endTransaction();
			if (ui != null) {
				ui.isFinished(jobId, System.currentTimeMillis() - time, res,
						stats);
			}
			return;
		}

		cmd.writeCommand(pto);
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		cmd.processResult(bindings);
		res = cmd.getResult();
		stats = cmd.getStats();
		if (ui != null) {
			ui.updateStats(jobId, System.currentTimeMillis() - time, res, stats);
		}
		completed = !(res instanceof NotYetFinished);
		if (completed) {
			s.endTransaction();
		}

		options = options.recheckExisting(false);
		cmd = new ModelCheckingStepCommand(TIME, options, last);
	}

	public IModelCheckingResult getResult() {
		return res;
	}

	@Override
	public boolean isCompleted() {
		return completed;
	}

	public StateSpaceStats getStats() {
		return stats;
	}

}
