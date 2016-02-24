package de.prob.animator.command;

import de.prob.check.CheckInterrupted;
import de.prob.check.IModelCheckListener;
import de.prob.check.IModelCheckingResult;
import de.prob.check.ModelCheckingOptions;
import de.prob.check.NotYetFinished;
import de.prob.check.StateSpaceStats;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public class ModelCheckingJob extends AbstractCommand {

	private static final int TIME = 500;
	private final String jobId;
	private ModelCheckingOptions options;
	private ModelCheckingStepCommand cmd;
	private IModelCheckingResult res;
	private StateSpaceStats stats;
	private final IModelCheckListener ui;

	private long time = -1;

	public ModelCheckingJob(final ModelCheckingOptions options,
			final String jobId, final IModelCheckListener ui) {
		this.options = options;
		this.jobId = jobId;
		this.ui = ui;
		this.completed = false;
		cmd = new ModelCheckingStepCommand(TIME, options);
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		if (time == -1) {
			time = System.currentTimeMillis();
		}
		cmd.writeCommand(pto);
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		cmd.processResult(bindings);
		res = cmd.getResult();
		stats = cmd.getStats();
		if (ui != null && res != null && stats != null) {
			ui.updateStats(jobId, System.currentTimeMillis() - time, res, stats);
		}
		completed = !(res instanceof NotYetFinished) || res == null;

		options = options.recheckExisting(false);
		cmd = new ModelCheckingStepCommand(TIME, options);
	}

	public IModelCheckingResult getResult() {
		return res == null && interrupted ? new CheckInterrupted() : res;
	}

	@Override
	public boolean blockAnimator() {
		return true;
	}

	public StateSpaceStats getStats() {
		return stats;
	}

}
