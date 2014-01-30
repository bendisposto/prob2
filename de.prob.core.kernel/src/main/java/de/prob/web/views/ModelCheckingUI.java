package de.prob.web.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.AsyncContext;

import com.google.common.base.Joiner;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.check.ConsistencyChecker;
import de.prob.check.IModelCheckingResult;
import de.prob.check.ModelCheckErrorUncovered;
import de.prob.check.ModelCheckOk;
import de.prob.check.ModelChecker;
import de.prob.check.ModelCheckingOptions;
import de.prob.check.ModelCheckingOptions.Options;
import de.prob.check.StateSpaceStats;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.IModelChangedListener;
import de.prob.statespace.ITraceDescription;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;
import de.prob.web.AbstractSession;
import de.prob.web.WebUtils;

@Singleton
public class ModelCheckingUI extends AbstractSession implements
		IModelChangedListener {

	private ModelCheckingOptions options;

	private final AnimationSelector animations;

	Map<String, ModelChecker> jobs = new HashMap<String, ModelChecker>();
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
			final IModelCheckingResult result, final StateSpaceStats stats) {
		results.put(id, result);

		int nrProcessedNodes = stats.getNrProcessedNodes();
		int nrTotalNodes = stats.getNrTotalNodes();
		int percent = nrProcessedNodes * 100 / nrTotalNodes;
		submit(WebUtils.wrap("cmd", "ModelChecking.updateJob", "id", id,
				"processedNodes", nrProcessedNodes, "totalNodes", nrTotalNodes,
				"totalTransitions", stats.getNrTotalTransitions(), "percent",
				percent, "time", timeElapsed));
	}

	public void isFinished(final String id, final long timeElapsed,
			final IModelCheckingResult result, final StateSpaceStats stats) {
		results.put(id, result);

		boolean hasStats = stats != null;
		int nrProcessedNodes = hasStats ? stats.getNrProcessedNodes() : null;
		int nrTotalNodes = hasStats ? stats.getNrTotalNodes() : null;
		int nrTotalTransitions = hasStats ? stats.getNrTotalTransitions()
				: null;

		String res = (result instanceof ModelCheckOk) ? "success"
				: ((result instanceof ModelCheckErrorUncovered) ? "danger"
						: "warning");
		boolean hasTrace = result instanceof ModelCheckErrorUncovered;

		jobs.remove(id);

		submit(WebUtils.wrap("cmd", "ModelChecking.finishJob", "id", id,
				"time", timeElapsed, "stats", hasStats, "processedNodes",
				nrProcessedNodes, "totalNodes", nrTotalNodes,
				"totalTransitions", nrTotalTransitions, "result", res,
				"hasTrace", hasTrace, "message", result.getMessage()));
	}

	public Object startJob(final Map<String, String[]> params) {
		if (currentStateSpace != null) {
			ModelChecker checker = new ModelChecker(new ConsistencyChecker(
					currentStateSpace, options, this));
			jobs.put(checker.getJobId(), checker);
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
		ModelChecker modelChecker = jobs.get(jobId);
		if (modelChecker != null) {
			modelChecker.cancel();
		} else {
			// FIXME handle error
		}
		return null;
	}

	public Object openTrace(final Map<String, String[]> params) {
		String jobId = params.get("jobId")[0];

		IModelCheckingResult result = results.get(jobId);
		if (result instanceof ITraceDescription) {
			Trace trace = currentStateSpace
					.getTrace((ITraceDescription) result);
			animations.addNewAnimation(trace);
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

	public Object partialOrderReduction(final Map<String, String[]> params) {
		boolean isSet = Boolean.valueOf(params.get("set")[0]);
		options = options.partialOrderReduction(isSet);
		return null;
	}

	public Object partialGuardEvaluation(final Map<String, String[]> params) {
		boolean isSet = Boolean.valueOf(params.get("set")[0]);
		options = options.partialGuardEvaluation(isSet);
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
		String sId = currentStateSpace == null ? "none" : currentStateSpace
				.getId();
		submit(WebUtils.wrap("cmd", "ModelChecking.changeStateSpaces", "ssId",
				sId));
	}
}
