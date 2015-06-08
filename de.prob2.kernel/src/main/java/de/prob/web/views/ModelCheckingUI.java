package de.prob.web.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.AsyncContext;

import org.eclipse.jetty.util.ajax.JSON;

import com.google.common.base.Joiner;
import com.google.inject.Inject;

import de.prob.animator.domainobjects.EventB;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.animator.domainobjects.LTL;
import de.prob.annotations.OneToOne;
import de.prob.annotations.PublicSession;
import de.prob.check.CBCDeadlockChecker;
import de.prob.check.CBCInvariantChecker;
import de.prob.check.ConsistencyChecker;
import de.prob.check.IModelCheckingResult;
import de.prob.check.LTLChecker;
import de.prob.check.LTLOk;
import de.prob.check.ModelCheckOk;
import de.prob.check.ModelChecker;
import de.prob.check.ModelCheckingOptions;
import de.prob.check.ModelCheckingOptions.Options;
import de.prob.check.StateSpaceStats;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.BEvent;
import de.prob.model.representation.ModelElementList;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.FormalismType;
import de.prob.statespace.IModelChangedListener;
import de.prob.statespace.ITraceDescription;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;
import de.prob.web.AbstractAnimationBasedView;
import de.prob.web.WebUtils;

@PublicSession
@OneToOne
public class ModelCheckingUI extends AbstractAnimationBasedView implements
		IModelChangedListener {

	private ModelCheckingOptions options;

	private final AnimationSelector animations;

	Map<String, ModelChecker> jobs = new HashMap<String, ModelChecker>();
	Map<String, IModelCheckingResult> results = new HashMap<String, IModelCheckingResult>();
	IEvalElement cbcFormula = null;

	List<String> selectedEvents = new ArrayList<String>();

	private StateSpace currentStateSpace;

	private LTL ltlFormula;

	@Inject
	public ModelCheckingUI(final AnimationSelector animations) {
		super(animations);
		this.animations = animations;
		incrementalUpdate = false;
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

		String res = result instanceof ModelCheckOk || result instanceof LTLOk ? "success"
				: result instanceof ITraceDescription ? "danger" : "warning";
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
			if (mode.equals("ltl-check") && ltlFormula != null) {
				return startLTLCheck();
			}
		} else {
			// FIXME handle error
		}
		return null;
	}

	private Object startConsistencyChecking() {
		ModelChecker checker = new ModelChecker(new ConsistencyChecker(
				currentStateSpace, options, null, this));
		jobs.put(checker.getJobId(), checker);
		checker.start();
		AbstractElement main = currentStateSpace.getModel().getMainComponent();
		String name = main == null ? "Model Check" : main.toString();
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

	private Object startCBCInvariant() {
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

	private Object startLTLCheck() {
		ModelChecker checker = new ModelChecker(new LTLChecker(
				currentStateSpace, ltlFormula, this));
		jobs.put(checker.getJobId(), checker);
		checker.start();
		String name = "LTL check " + ltlFormula.getCode();
		return WebUtils.wrap("cmd", "ModelChecking.jobStarted", "name", name,
				"id", checker.getJobId(), "ssId", currentStateSpace.getId());
	}

	public Object cancel(final Map<String, String[]> params) {
		String jobId = params.get("jobId")[0];
		ModelChecker modelChecker = jobs.get(jobId);
		if (modelChecker != null) {
			modelChecker.cancel();
			return WebUtils.wrap("cmd", "ModelChecking.cancelJob", "id", jobId);
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
			if (trace != null) {
				animations.addNewAnimation(trace, false);
			}
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
		sendInitMessage(context);
		Trace ofInterest = animationOfInterest == null ? animationsRegistry
				.getCurrentTrace() : animationsRegistry
				.getTrace(animationOfInterest);
		if (ofInterest != null) {
			modelChanged(ofInterest.getStateSpace());
		}
	}

	@Override
	public void modelChanged(final StateSpace stateSpace) {
		boolean changed = false;
		if (animationOfInterest != null) {
			Trace t = animationsRegistry.getTrace(animationOfInterest);
			if (t == null) {
				changed = currentStateSpace == null;
				currentStateSpace = null;
			} else {
				StateSpace old = currentStateSpace;
				currentStateSpace = t.getStateSpace();
				changed = !currentStateSpace.equals(old);
			}
		} else {
			currentStateSpace = stateSpace;
			changed = true;
		}

		if (changed) {
			currentStateSpace = stateSpace;
			String sId = currentStateSpace == null ? "none" : currentStateSpace
					.getId();

			boolean b_model = currentStateSpace == null ? false
					: currentStateSpace.getModel().getFormalismType()
							.equals(FormalismType.B);
			List<String> eventNames = b_model ? extractEventNames(currentStateSpace)
					: new ArrayList<String>();
			selectedEvents = new ArrayList<String>();
			submit(WebUtils.wrap("cmd", "ModelChecking.changeStateSpaces",
					"ssId", sId, "events", JSON.toString(eventNames), "bType",
					b_model));
		}
	}

	/**
	 * @param s
	 *            Current state space
	 * @return List of the names of the events for the main component in the
	 *         model corresponding to the state space
	 */
	private List<String> extractEventNames(final StateSpace s) {
		List<String> sts = new ArrayList<String>();
		if (s == null || s.getModel().getMainComponent() == null) {
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

		if ("cbc-deadlock-input-parent".equals(id)) {
			return parseCBC(f, id);
		}
		if ("ltl-check-input-parent".equals(id)) {
			return parseLTL(f, id);
		}
		return null;
	}

	public Object parseCBC(final String formula, final String id) {
		try {
			IEvalElement e = currentStateSpace.getModel().parseFormula(formula);
			if (e instanceof EventB) {
				((EventB) e).getAst();
			}
			cbcFormula = e;
			return WebUtils.wrap("cmd", "ModelChecking.parseOk", "id", id);
		} catch (Exception e) {
			cbcFormula = null;
			if ("".equals(formula)) {
				return WebUtils.wrap("cmd", "ModelChecking.parseOk", "id", id);
			}
			return WebUtils.wrap("cmd", "ModelChecking.parseError", "id", id);
		}
	}

	public Object parseLTL(final String formula, final String id) {
		try {
			ltlFormula = new LTL(formula);
			return WebUtils.wrap("cmd", "ModelChecking.parseOk", "id", id);
		} catch (Exception e) {
			ltlFormula = null;
			return WebUtils.wrap("cmd", "ModelChecking.parseError", "id", id);
		}
	}

	@Override
	public void animatorStatus(final boolean busy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void performTraceChange(final Trace trace) {
		// TODO Auto-generated method stub

	}

}
