package de.prob.animator.command;

import de.prob.animator.domainobjects.LTL;
import de.prob.check.CheckInterrupted;
import de.prob.check.IModelCheckListener;
import de.prob.check.IModelCheckingResult;
import de.prob.check.LTLNotYetFinished;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.StateSpace;

public class LTLCheckingJob extends AbstractCommand {

	private static final int MAX = 500;

	private final StateSpace s;
	private final LTL formula;
	private final String jobId;
	private final IModelCheckListener ui;

	private IModelCheckingResult res;
	private LtlCheckingCommand cmd;
	private long time = -1;

	public LTLCheckingJob(final StateSpace s, final LTL formula,
			final String jobId, final IModelCheckListener ui) {
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
		return res == null && interrupted ? new CheckInterrupted() : res;
	}

	@Override
	public boolean blockAnimator() {
		return true;
	}

}
