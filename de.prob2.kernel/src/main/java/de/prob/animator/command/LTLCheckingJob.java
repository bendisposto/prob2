package de.prob.animator.command;

import de.prob.animator.domainobjects.LTL;
import de.prob.check.IModelCheckingResult;
import de.prob.check.LTLNotYetFinished;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.StateSpace;
import de.prob.web.views.ModelCheckingUI;

public class LTLCheckingJob extends AbstractCommand {

	private static final int MAX = 500;

	private final StateSpace s;
	private final LTL formula;
	private final String jobId;
	private final ModelCheckingUI ui;

	private IModelCheckingResult res;
	private LtlCheckingCommand cmd;
	private long time;
	private boolean completed;

	public LTLCheckingJob(final StateSpace s, final LTL formula,
			final String jobId, final ModelCheckingUI ui) {
		this.s = s;
		this.formula = formula;
		this.jobId = jobId;
		this.ui = ui;
		cmd = new LtlCheckingCommand(s, formula, MAX);
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		if (time == -1) {
			time = System.currentTimeMillis();
		}
		if (Thread.interrupted()) {
			completed = true;
			if (ui != null) {
				ui.isFinished(jobId, System.currentTimeMillis() - time, res,
						null);
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
		if (ui != null) {
			ui.updateStats(jobId, System.currentTimeMillis() - time, res, null);
		}
		completed = !(res instanceof LTLNotYetFinished);

		cmd = new LtlCheckingCommand(s, formula, MAX);
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

}
