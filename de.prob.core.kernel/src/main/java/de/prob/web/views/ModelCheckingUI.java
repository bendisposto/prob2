package de.prob.web.views;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.check.IModelCheckingResult;
import de.prob.check.ModelChecker;
import de.prob.check.ModelCheckingOptions;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.IModelChangedListener;
import de.prob.statespace.StateSpace;
import de.prob.web.AbstractSession;

@Singleton
public class ModelCheckingUI extends AbstractSession implements
		IModelChangedListener {

	private final Map<String, WeakReference<StateSpace>> stateSpaceMapping = new HashMap<String, WeakReference<StateSpace>>();
	private ModelCheckingOptions options;

	private final AnimationSelector animations;

	Map<String, ModelChecker> workingJobs = new HashMap<String, ModelChecker>();

	private StateSpace currentStateSpace;

	@Inject
	public ModelCheckingUI(final AnimationSelector animations) {
		this.animations = animations;
		options = ModelCheckingOptions.DEFAULT;
	}

	@Override
	public String html(final String clientid,
			final Map<String, String[]> parameterMap) {
		return simpleRender(clientid, "ui/modelchecking/index.html");
	}

	public void updateStats(final String stateSpaceId, final String id,
			final long timeElapsed, final IModelCheckingResult result) {

	}

	public void isFinished(final String stateSpaceId, final String id,
			final long timeElapsed, final IModelCheckingResult res) {
		workingJobs.remove(id);
	}

	public Object startJob(final Map<String, String[]> params) {
		if (currentStateSpace != null) {
			ModelChecker checker = new ModelChecker(currentStateSpace, options,
					this);
			workingJobs.put(checker.getJobId(), checker);
			checker.start();
			// Notify UI
		} else {
			// FIXME handle error
		}
		return null;
	}

	// SET MODEL CHECKING OPTIONS
	public Object breadthFirst(final Map<String, String[]> params) {
		boolean isSet = Boolean.valueOf(params.get("set")[0]);
		options = options.breadthFirst(isSet);
		return null;
	}

	public Object checkDeadlocks(final Map<String, String[]> params) {
		boolean isSet = Boolean.valueOf(params.get("set")[0]);
		options = options.checkDeadlocks(isSet);
		return null;
	}

	public Object checkInvariantViolations(final Map<String, String[]> params) {
		boolean isSet = Boolean.valueOf(params.get("set")[0]);
		options = options.checkInvariantViolations(isSet);
		return null;
	}

	public Object checkAssertions(final Map<String, String[]> params) {
		boolean isSet = Boolean.valueOf(params.get("set")[0]);
		options = options.checkAssertions(isSet);
		return null;
	}

	public Object recheckExisting(final Map<String, String[]> params) {
		boolean isSet = Boolean.valueOf(params.get("set")[0]);
		options = options.recheckExisting(isSet);
		return null;
	}

	public Object stopAtFullCoverage(final Map<String, String[]> params) {
		boolean isSet = Boolean.valueOf(params.get("set")[0]);
		options = options.stopAtFullCoverage(isSet);
		return null;
	}

	@Override
	public void modelChanged(final StateSpace stateSpace) {
		currentStateSpace = stateSpace;
		if (stateSpace == null) {
			return;
		}

		if (stateSpaceMapping.containsKey(stateSpace.getId())) {
			// TODO do something
		} else {
			stateSpaceMapping.put(stateSpace.getId(),
					new WeakReference<StateSpace>(stateSpace));
		}
	}
}
