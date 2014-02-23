package de.prob.ui.api;

import java.util.List;

import de.prob.statespace.IAnimationChangeListener;
import de.prob.statespace.Trace;

public class ProBTool implements ITool, IAnimationChangeListener {

	private Trace currentTrace;
	private VisualizationNotifier notifier;

	@Override
	public String getCurrentState() {
		return null;
	}

	@Override
	public boolean canBacktrack() {
		return true;
	}

	@Override
	public String doStep(String stateref, String event, String... parameters)
			throws ImpossibleStepException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String evaluate(String stateref, String formula)
			throws IllegalFormulaException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getErrors(String state, String Formula) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void animatorStatus(boolean busy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void traceChange(Trace trace, boolean currentAnimationChanged) {
		notifier.notifyStateChange(trace);
	}

	public Trace getCurrentTrace() {
		return currentTrace;
	}

}
