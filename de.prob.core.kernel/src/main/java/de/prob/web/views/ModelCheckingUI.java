package de.prob.web.views;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.AsyncContext;

import com.google.common.base.Joiner;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.check.IModelCheckingResult;
import de.prob.check.ModelCheckErrorUncovered;
import de.prob.check.ModelCheckOk;
import de.prob.check.ModelChecker;
import de.prob.check.ModelCheckingOptions;
import de.prob.check.ModelCheckingOptions.Options;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.IModelChangedListener;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;
import de.prob.web.AbstractSession;
import de.prob.web.WebUtils;

@Singleton
public class ModelCheckingUI extends AbstractSession implements
		IModelChangedListener {

	private ModelCheckingOptions options;

	private final AnimationSelector animations;

	Map<String, WeakReference<ModelChecker>> jobs = new HashMap<String, WeakReference<ModelChecker>>();
	Map<String, IModelCheckingResult> results = new HashMap<String, IModelCheckingResult>();

	private StateSpace currentStateSpace;

	@Inject
	public ModelCheckingUI(final AnimationSelector animations) {
		this.animations = animations;
		animations.registerModelChangedListener(this);
		options = ModelCheckingOptions.DEFAULT;
	}

	@Override
	public String html(final String clientid,
			final Map<String, String[]> parameterMap) {
		return simpleRender(clientid, "ui/modelchecking/index.html");
	}

	public void updateStats(final String id, final long timeElapsed,
			final IModelCheckingResult result) {
		int nrProcessedNodes = result.getStats().getNrProcessedNodes();
		int nrTotalNodes = result.getStats().getNrTotalNodes();
		int percent = nrProcessedNodes * 100 / nrTotalNodes;
		submit(WebUtils.wrap("cmd", "ModelChecking.updateJob", "id", id,
				"processedNodes", nrProcessedNodes, "totalNodes", nrTotalNodes,
				"totalTransitions", result.getStats().getNrTotalTransitions(),
				"percent", percent, "time", timeElapsed));
	}

	public void isFinished(final String id, final long timeElapsed,
			final IModelCheckingResult res) {
		int nrProcessedNodes = res.getStats().getNrProcessedNodes();
		int nrTotalNodes = res.getStats().getNrTotalNodes();
		int nrTotalTransitions = res.getStats().getNrTotalTransitions();
		String result = (res instanceof ModelCheckOk) ? "success"
				: ((res instanceof ModelCheckErrorUncovered) ? "danger"
						: "warning");
		boolean hasTrace = res instanceof ModelCheckErrorUncovered;

		if (!hasTrace) {
			jobs.remove(id); // if there is no trace to be opened, there is no
								// reason to keep the model checker
		}

		submit(WebUtils.wrap("cmd", "ModelChecking.finishJob", "id", id,
				"time", timeElapsed, "processedNodes", nrProcessedNodes,
				"totalNodes", nrTotalNodes, "totalTransitions",
				nrTotalTransitions, "result", result, "hasTrace", hasTrace,
				"message", res.getMessage()));
	}

	public Object startJob(final Map<String, String[]> params) {
		if (currentStateSpace != null) {
			ModelChecker checker = new ModelChecker(currentStateSpace, options,
					this);
			jobs.put(checker.getJobId(), new WeakReference<ModelChecker>(
					checker));
			checker.start();
			String name = currentStateSpace.getModel().getMainComponent()
					.toString();
			List<String> ss = new ArrayList<String>();
			for (Options opts : options.getPrologOptions()) {
				ss.add(opts.getDescription());
			}
			if (!ss.isEmpty()) {
				name += " with " + Joiner.on(", ").join(ss);
			}
			return WebUtils.wrap("cmd", "ModelChecking.jobStarted", "name",
					name, "id", checker.getJobId(), "ssId",
					currentStateSpace.getId());
		} else {
			// FIXME handle error
		}
		return null;
	}

	public Object cancel(final Map<String, String[]> params) {
		String jobId = params.get("jobId")[0];
		ModelChecker modelChecker = jobs.get(jobId).get();
		if (modelChecker != null) {
			modelChecker.cancel();
		} else {
			// FIXME handle error
		}
		return null;
	}

	public Object openTrace(final Map<String, String[]> params) {
		String jobId = params.get("jobId")[0];
		ModelChecker modelChecker = jobs.get(jobId).get();
		if (modelChecker != null) {
			IModelCheckingResult result = modelChecker.getResult();
			StateSpace stateSpace = modelChecker.getStateSpace();
			if (result instanceof ModelCheckErrorUncovered) {
				Trace trace = ((ModelCheckErrorUncovered) result)
						.getTraceToErrorState(stateSpace);
				animations.addNewAnimation(trace);
			}
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
	public void reload(final String client, final int lastinfo,
			final AsyncContext context) {
		super.reload(client, lastinfo, context);
		resend(client, lastinfo, context);
	}

	@Override
	public void modelChanged(final StateSpace stateSpace) {
		currentStateSpace = stateSpace;
		if (stateSpace == null) {
			return;
		}
		submit(WebUtils.wrap("cmd", "ModelChecking.changeStateSpaces", "ssId",
				currentStateSpace.getId()));
	}
}
