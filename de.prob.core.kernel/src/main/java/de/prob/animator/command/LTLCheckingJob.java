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
		cmd = new LtlCheckingCommand(formula, MAX);
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
		if (completed) {
			s.endTransaction();
		}

		cmd = new LtlCheckingCommand(formula, MAX);
	}

	public IModelCheckingResult getResult() {
		return res;
	}

	@Override
	public boolean isCompleted() {
		return true;
	}

}
