package de.prob.animator.command;

import de.prob.check.IModelCheckingResult;
import de.prob.check.ModelCheckingOptions;
import de.prob.check.NotYetFinished;
import de.prob.check.StateSpaceStats;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;
import de.prob.web.views.ModelCheckingUI;

public class ModelCheckingJob extends AbstractCommand {

	private static final int TIME = 500;
	private final String jobId;
	private ModelCheckingOptions options;
	private ModelCheckingStepCommand cmd;
	private boolean completed = false;
	private IModelCheckingResult res;
	private StateSpaceStats stats;
	private final ModelCheckingUI ui;

	private long time = -1;

	public ModelCheckingJob(final ModelCheckingOptions options,
			final String jobId, final ModelCheckingUI ui) {
		this.options = options;
		this.jobId = jobId;
		this.ui = ui;
		cmd = new ModelCheckingStepCommand(TIME, options);
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		if (time == -1) {
			time = System.currentTimeMillis();
		}
		if (Thread.interrupted()) {
			completed = true;
			Thread.currentThread().interrupt();
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

		options = options.recheckExisting(false);
		cmd = new ModelCheckingStepCommand(TIME, options);
	}

	public IModelCheckingResult getResult() {
		return res;
	}

	@Override
	public boolean isCompleted() {
		return completed;
	}

	@Override
	public boolean blockAnimator() {
		return true;
	}

	public StateSpaceStats getStats() {
		return stats;
	}

}
