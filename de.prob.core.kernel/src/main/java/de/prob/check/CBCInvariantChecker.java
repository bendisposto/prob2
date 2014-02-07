package de.prob.check;

import java.util.List;

import de.prob.animator.command.ConstraintBasedInvariantCheckCommand;
import de.prob.statespace.StateSpace;
import de.prob.web.views.ModelCheckingUI;

public class CBCInvariantChecker implements IModelCheckJob {

	private final ConstraintBasedInvariantCheckCommand command;
	private final StateSpace s;
	private final ModelCheckingUI ui;
	private final String jobId;

	public CBCInvariantChecker(final StateSpace s) {
		this(s, null);
	}

	public CBCInvariantChecker(final StateSpace s, final List<String> eventNames) {
		this(s, eventNames, null);
	}

	public CBCInvariantChecker(final StateSpace s,
			final List<String> eventNames, final ModelCheckingUI ui) {
		this.s = s;
		this.ui = ui;
		jobId = ModelChecker.generateJobId();
		command = new ConstraintBasedInvariantCheckCommand(eventNames);
	}

	@Override
	public IModelCheckingResult call() throws Exception {
		long time = System.currentTimeMillis();
		s.execute(command);
		if (ui != null) {
			ui.isFinished(jobId, System.currentTimeMillis() - time,
					command.getResult(), null);
		}
		return command.getResult();
	}

	@Override
	public IModelCheckingResult getResult() {
		return command.getResult() == null ? new NotYetFinished(
				"Invariant Checking Interrupted", -1) : command.getResult();
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
