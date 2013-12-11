package de.prob.web.views;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.check.IModelCheckingResult;
import de.prob.check.ModelChecker;
import de.prob.check.ModelCheckingOptions;
import de.prob.check.StateSpaceStats;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;
import de.prob.web.AbstractSession;

@Singleton
public class ModelCheckingUI extends AbstractSession {

	private final ModelCheckingOptions options;

	private ModelChecker checker;

	private final AnimationSelector animations;
	private Map<String, StateSpace> spaces;

	@Inject
	public ModelCheckingUI(final AnimationSelector animations) {
		this.animations = animations;
		options = ModelCheckingOptions.DEFAULT;
	}

	@Override
	public String html(final String clientid,
			final Map<String, String[]> parameterMap) {
		// TODO Auto-generated method stub
		return null;
	}

	public void updateStats(final UUID id, final StateSpaceStats stats) {

	}

	public void isFinished(final UUID id, final IModelCheckingResult res) {

	}

	public Object start(final Map<String, String[]> params) {
		if (checker != null) {
			checker.start();
		}
		return null;
	}

	public Object getCurrentStateSpaces(final Map<String, String[]> params) {
		spaces = new HashMap<String, StateSpace>();
		List<Trace> traces = animations.getTraces();
		for (Trace trace : traces) {
			if (!spaces.containsValue(trace.getStateSpace())) {
				spaces.put(UUID.randomUUID().toString(), trace.getStateSpace());
			}
		}
		return null;
	}
}
