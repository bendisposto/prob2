package de.prob.web.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.AsyncContext;

import org.eclipse.jetty.util.ajax.JSON;

import com.google.common.base.Joiner;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.animator.domainobjects.EventB;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.check.CBCDeadlockChecker;
import de.prob.check.CBCInvariantChecker;
import de.prob.check.ConsistencyChecker;
import de.prob.check.IModelCheckingResult;
import de.prob.check.ModelCheckOk;
import de.prob.check.ModelChecker;
import de.prob.check.ModelCheckingOptions;
import de.prob.check.ModelCheckingOptions.Options;
import de.prob.check.StateSpaceStats;
import de.prob.model.representation.BEvent;
import de.prob.model.representation.CSPModel;
import de.prob.model.representation.ModelElementList;
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
	IEvalElement cbcFormula = null;

	List<String> selectedEvents = new ArrayList<String>();

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

		boolean hasStats = stats != null;

		if (hasStats) {
			int nrProcessedNodes = stats.getNrProcessedNodes();
			int nrTotalNodes = stats.getNrTotalNodes();
			int percent = nrProcessedNodes * 100 / nrTotalNodes;
			submit(WebUtils.wrap("cmd", "ModelChecking.updateJob", "id", id,
					"stats", hasStats, "processedNodes", nrProcessedNodes,
					"totalNodes", nrTotalNodes, "totalTransitions",
					stats.getNrTotalTransitions(), "percent", percent, "time",
					timeElapsed));
		} else {
			submit(WebUtils.wrap("cmd", "ModelChecking.updateJob", "id", id,
					"stats", hasStats, "percent", 100, "time", timeElapsed));
		}
	}

	public void isFinished(final String id, final long timeElapsed,
			final IModelCheckingResult result, final StateSpaceStats stats) {
		results.put(id, result);

		String res = (result instanceof ModelCheckOk) ? "success"
				: ((result instanceof ITraceDescription) ? "danger" : "warning");
		boolean hasTrace = result instanceof ITraceDescription;

		jobs.remove(id);
		boolean hasStats = stats != null;
		if (hasStats) {
			submit(WebUtils.wrap("cmd", "ModelChecking.finishJob", "id", id,
					"time", timeElapsed, "stats", hasStats, "processedNodes",
					stats.getNrProcessedNodes(), "totalNodes",
					stats.getNrTotalNodes(), "totalTransitions",
					stats.getNrTotalTransitions(), "result", res, "hasTrace",
					hasTrace, "message", result.getMessage()));
		} else {
			Map<String, String> wrap = WebUtils.wrap("cmd",
					"ModelChecking.finishJob", "id", id, "time", timeElapsed,
					"stats", hasStats, "result", res, "hasTrace", hasTrace,
					"message", result.getMessage());
			submit(wrap);
		}

	}

	public Object startJob(final Map<String, String[]> params) {
		if (currentStateSpace != null) {
			String mode = params.get("check-mode")[0];
			if (mode.equals("cc-check")) {
				return startConsistencyChecking();
			}
			if (mode.equals("cbc-inv")) {
				return startCBCInvariant();
			}
			if (mode.equals("cbc-deadlock")) {
				return startDBCDeadlock();
			}
		} else {
			// FIXME handle error
		}
		return null;
	}

	public Object startConsistencyChecking() {
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
		return WebUtils.wrap("cmd", "ModelChecking.jobStarted", "name", name,
				"id", checker.getJobId(), "ssId", currentStateSpace.getId());
	}

	public Object startCBCInvariant() {
		ModelChecker checker = new ModelChecker(new CBCInvariantChecker(
				currentStateSpace, selectedEvents.isEmpty() ? null
						: selectedEvents, this));
		jobs.put(checker.getJobId(), checker);
		checker.start();
		String name = "CBC invariant check with ";
		if (selectedEvents.isEmpty()) {
			name += "all events";
		} else {
			name += Joiner.on(", ").join(selectedEvents);
		}
		return WebUtils.wrap("cmd", "ModelChecking.jobStarted", "name", name,
				"id", checker.getJobId(), "ssId", currentStateSpace.getId());
	}

	private Object startDBCDeadlock() {
		ModelChecker checker = new ModelChecker(new CBCDeadlockChecker(
				currentStateSpace, cbcFormula, this));
		jobs.put(checker.getJobId(), checker);
		checker.start();
		String name = "CBC deadlock check ";
		if (cbcFormula != null) {
			name += "with constraint " + cbcFormula.getCode();
		}
		return WebUtils.wrap("cmd", "ModelChecking.jobStarted", "name", name,
				"id", checker.getJobId(), "ssId", currentStateSpace.getId());
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

	// SET EVENTS FOR CBC INVARIANT CHECKING
	public Object removeEvent(final Map<String, String[]> params) {
		String eventName = params.get("event")[0];
		selectedEvents.remove(eventName);
		return null;
	}

	public Object selectEvent(final Map<String, String[]> params) {
		String eventName = params.get("event")[0];
		selectedEvents.add(eventName);
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
		boolean b_model = !(currentStateSpace.getModel() instanceof CSPModel);
		List<String> eventNames = b_model ? extractEventNames(currentStateSpace)
				: new ArrayList<String>();
		selectedEvents = new ArrayList<String>();
		submit(WebUtils.wrap("cmd", "ModelChecking.changeStateSpaces", "ssId",
				sId, "events", JSON.toString(eventNames), "withCBC", b_model));
	}

	/**
	 * @param s
	 *            Current state space
	 * @return List of the names of the events for the main component in the
	 *         model corresponding to the state space
	 */
	private List<String> extractEventNames(final StateSpace s) {
		List<String> sts = new ArrayList<String>();
		if (s == null) {
			return sts;
		}
		ModelElementList<BEvent> events = s.getModel().getMainComponent()
				.getChildrenOfType(BEvent.class);
		for (BEvent bEvent : events) {
			sts.add(bEvent.getName());
		}
		return sts;
	}

	public Object parse(final Map<String, String[]> params) {
		String f = params.get("formula")[0];
		String id = params.get("id")[0];

		try {
			IEvalElement e = currentStateSpace.getModel().parseFormula(f);
			if (e instanceof EventB) {
				((EventB) e).getAst();
			}
			if (id.equals("cbc-deadlock-input")) {
				cbcFormula = e;
			}
			return WebUtils.wrap("cmd", "ModelChecking.parseOk", "id", id);
		} catch (Exception e) {
			if (id.equals("cbc-deadlock-input")) {
				cbcFormula = null;
			}
			if ("".equals(f)) {
				return WebUtils.wrap("cmd", "ModelChecking.parseOk", "id", id);
			}
			return WebUtils.wrap("cmd", "ModelChecking.parseError", "id", id);
		}
	}
}
