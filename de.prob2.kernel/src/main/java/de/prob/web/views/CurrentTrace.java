package de.prob.web.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.AsyncContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.prob.animator.domainobjects.FormulaExpand;
import de.prob.annotations.OneToOne;
import de.prob.annotations.PublicSession;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.Trace;
import de.prob.statespace.Transition;
import de.prob.web.AbstractAnimationBasedView;
import de.prob.web.WebUtils;

@PublicSession
@OneToOne
public class CurrentTrace extends AbstractAnimationBasedView {

	private final Logger logger = LoggerFactory.getLogger(CurrentTrace.class);
	List<Map<String, String>> ops = new ArrayList<Map<String, String>>();
	private boolean sortDown = true;

	@Inject
	public CurrentTrace(final AnimationSelector animations) {
		super(animations);
		incrementalUpdate = false;
		animations.registerAnimationChangeListener(this);
	}

	@Override
	public void performTraceChange(final Trace trace) {
		logger.trace("Trace has changed. Submitting");
		ops = new ArrayList<Map<String, String>>();
		if (trace == null) {
			Map<String, String> wrap = WebUtils.wrap("cmd",
					"CurrentTrace.setTrace", "trace", WebUtils.toJson(ops));
			submit(wrap);
			return;
		}

		int currentPos = trace.getCurrent().getIndex();
		List<Transition> opList = trace.getTransitionList();
		int startpos = currentPos > 50 ? currentPos - 50 : 0;
		int endpos = opList.size() > currentPos + 20 ? currentPos + 20 : opList
				.size();
		if (startpos == 0) {
			ops.add(WebUtils.wrap("id", -1, "rep", "-- root --", "group",
					"start"));
		}
		trace.getStateSpace().evaluateTransitions(
				opList.subList(startpos, endpos), FormulaExpand.truncate);
		String group = "past";
		for (int i = startpos; i < endpos; i++) {
			String rep = opList.get(i).getPrettyRep();
			if (currentPos == i) {
				group = "current";
				ops.add(WebUtils.wrap("id", i, "rep", rep, "group", group));

				// After this point, all elements are in the future
				group = "future";
			} else {
				ops.add(WebUtils.wrap("id", i, "rep", rep, "group", group));
			}
		}

		if (sortDown) {
			Collections.reverse(ops);
		}

		Map<String, String> wrap = WebUtils.wrap("cmd",
				"CurrentTrace.setTrace", "trace", WebUtils.toJson(ops));
		submit(wrap);
	}

	public Object gotoPos(final Map<String, String[]> params) {
		logger.trace("Goto Position in Trace");
		int id = Integer.parseInt(params.get("id")[0]);
		Trace trace = getCurrentTrace().gotoPosition(id);
		animationsRegistry.traceChange(trace);
		return null;
	}

	public Object changeSort(final Map<String, String[]> params) {
		sortDown = Boolean.valueOf(params.get("sortDown")[0]);
		Collections.reverse(ops);
		return WebUtils.wrap("cmd", "CurrentTrace.setTrace", "trace",
				WebUtils.toJson(ops));
	}

	@Override
	public String html(final String clientid,
			final Map<String, String[]> parameterMap) {
		return simpleRender(clientid, "ui/currenttrace/index.html");
	}

	public List<Transition> getElements(final Trace trace) {
		return trace.getTransitionList();
	}

	@Override
	public void reload(final String client, final int lastinfo,
			final AsyncContext context) {
		sendInitMessage(context);
		Map<String, String> wrap = WebUtils.wrap("cmd",
				"CurrentTrace.setTrace", "trace", WebUtils.toJson(ops));
		submit(wrap);
	}

	@Override
	public void animatorStatus(final boolean busy) {
		if (busy) {
			submit(WebUtils.wrap("cmd", "CurrentTrace.disable"));
		} else {
			submit(WebUtils.wrap("cmd", "CurrentTrace.enable"));
		}
	}

}
