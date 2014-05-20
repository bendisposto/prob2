package de.prob.web.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.AsyncContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.annotations.PublicSession;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.IAnimationChangeListener;
import de.prob.statespace.OpInfo;
import de.prob.statespace.Trace;
import de.prob.statespace.TraceElement;
import de.prob.web.AbstractSession;
import de.prob.web.WebUtils;

@Singleton
@PublicSession
public class CurrentTrace extends AbstractSession implements
		IAnimationChangeListener {

	private final AnimationSelector selector;
	private final Logger logger = LoggerFactory.getLogger(CurrentTrace.class);
	List<Map<String, String>> ops = new ArrayList<Map<String, String>>();
	private boolean sortDown = true;

	@Inject
	public CurrentTrace(final AnimationSelector selector) {
		this.selector = selector;
		selector.registerAnimationChangeListener(this);
	}

	@Override
	public void traceChange(final Trace trace,
			final boolean currentAnimationChanged) {
		if (currentAnimationChanged) {
			logger.trace("Trace has changed. Submitting");
			ops = new ArrayList<Map<String, String>>();
			if (trace == null) {
				Map<String, String> wrap = WebUtils.wrap("cmd",
						"CurrentTrace.setTrace", "trace", WebUtils.toJson(ops));
				submit(wrap);
				return;
			}

			trace.ensureOpInfosEvaluated();
			TraceElement element = trace.getHead();
			TraceElement current = trace.getCurrent();
			String group = "future";
			while (element.getPrevious() != null) {
				OpInfo op = element.getOp();
				String rep = op.getRep(trace.getModel());
				if (element == current) {
					group = "current";
					ops.add(WebUtils.wrap("id", element.getIndex(), "rep", rep,
							"group", group));

					// After this point, all elements are in the past
					group = "past";
				} else {
					ops.add(WebUtils.wrap("id", element.getIndex(), "rep", rep,
							"group", group));
				}
				element = element.getPrevious();
			}
			ops.add(WebUtils.wrap("id", 0, "rep", "-- root --", "group",
					"start"));
			if (!sortDown) {
				Collections.reverse(ops);
			}

			Map<String, String> wrap = WebUtils.wrap("cmd",
					"CurrentTrace.setTrace", "trace", WebUtils.toJson(ops));
			submit(wrap);
		}
	}

	public Object gotoPos(final Map<String, String[]> params) {
		logger.trace("Goto Position in Trace");
		Trace trace = selector.getCurrentTrace();
		int id = Integer.parseInt(params.get("id")[0]);
		int currentIndex = trace.getCurrent().getIndex();
		if (id == currentIndex) {
			return null;
		} else if (id > currentIndex) {
			while (!(id == trace.getCurrent().getIndex())) {
				trace = trace.forward();
			}
		} else if (id < currentIndex) {
			while (!(id == trace.getCurrent().getIndex())) {
				trace = trace.back();
			}
		}
		selector.replaceTrace(selector.getCurrentTrace(), trace);
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

	public List<OpInfo> getElements(final Trace trace) {
		return trace.getHead().getOpList();
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
