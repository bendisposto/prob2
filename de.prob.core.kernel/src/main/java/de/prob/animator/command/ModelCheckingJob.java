package de.prob.animator.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.prob.check.IModelCheckingResult;
import de.prob.check.ModelCheckingOptions;
import de.prob.check.NotYetFinished;
import de.prob.check.StateSpaceStats;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.OpInfo;
import de.prob.statespace.StateId;
import de.prob.statespace.StateSpace;
import de.prob.web.views.ModelCheckingUI;

public class ModelCheckingJob extends AbstractCommand {

	private static final int TIME = 500;
	private final String jobId;
	private final StateSpace s;
	private ModelCheckingOptions options;
	private ModelCheckingStepCommand cmd;
	private boolean completed = false;
	private long last;
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
		if (ui != null) {
			ui.updateStats(jobId, System.currentTimeMillis() - time, res, stats);
		}
		completed = !(res instanceof NotYetFinished);
		if (completed) {
			s.endTransaction();
		}

		addCheckedStates(cmd.getNewOps());
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

	private void addCheckedStates(final List<OpInfo> newOps) {
		HashMap<String, StateId> states = s.getStates();
		HashMap<String, OpInfo> ops = s.getOps();

		long i = s.getLastCalculatedStateId();

		List<OpInfo> toNotify = new ArrayList<OpInfo>();
		for (OpInfo opInfo : newOps) {
			if (!ops.containsKey(opInfo.id)) {
				toNotify.add(opInfo);
				String sK = opInfo.src;
				if (!sK.equals("root")) {
					int value = Integer.parseInt(sK);
					i = Math.max(value, i);
				}

				String dK = opInfo.dest;
				StateId src = states.get(sK);
				if (src == null) {
					src = new StateId(sK, s);
					states.put(sK, src);
				}
				StateId dest = states.get(dK);
				if (dest == null) {
					dest = new StateId(dK, s);
					states.put(dK, dest);
				}
				s.addEdge(opInfo, src, dest);
				ops.put(opInfo.id, opInfo);
			}
		}
		s.updateLastCalculatedStateId(i);
		last = i;

		s.notifyStateSpaceChange(toNotify);
	}

	public StateSpaceStats getStats() {
		return stats;
	}

}
