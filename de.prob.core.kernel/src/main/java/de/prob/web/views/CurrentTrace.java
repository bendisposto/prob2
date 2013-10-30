package de.prob.web.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.AsyncContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.statespace.AnimationSelector;
import de.prob.statespace.IAnimationChangeListener;
import de.prob.statespace.OpInfo;
import de.prob.statespace.Trace;
import de.prob.statespace.TraceElement;
import de.prob.web.AbstractSession;
import de.prob.web.WebUtils;

@Singleton
public class CurrentTrace extends AbstractSession implements
		IAnimationChangeListener {

	private final AnimationSelector selector;
	private final Logger logger = LoggerFactory.getLogger(CurrentTrace.class);

	@Inject
	public CurrentTrace(final AnimationSelector selector) {
		this.selector = selector;
		selector.registerAnimationChangeListener(this);
	}

	@Override
	public void traceChange(final Trace trace) {
		logger.trace("Trace has changed. Submitting");
		List<Map<String, String>> ops = new ArrayList<Map<String, String>>();
		if (trace == null) {
			Map<String, String> wrap = WebUtils.wrap("cmd",
					"CurrentTrace.setTrace", "trace", WebUtils.toJson(ops));
			submit(wrap);
			return;
		}

		TraceElement element = trace.getHead();
		TraceElement current = trace.getCurrent();
		String group = "future";
		while (element.getPrevious() != null) {
			if (element == current) {
				group = "current";
				OpInfo op = element.getOp();
				String rep = op.getRep(trace.getModel());
				ops.add(WebUtils.wrap("id", element.getIndex(), "rep", rep,
						"group", group));

				// After this point, all elements are in the past
				group = "past";
			} else {
				OpInfo op = element.getOp();
				String rep = op.getRep(trace.getModel());
				ops.add(WebUtils.wrap("id", element.getIndex(), "rep", rep,
						"group", group));
			}
			element = element.getPrevious();
		}

		Map<String, String> wrap = WebUtils.wrap("cmd",
				"CurrentTrace.setTrace", "trace", WebUtils.toJson(ops));
		submit(wrap);
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
		super.reload(client, lastinfo, context);
		Trace currentTrace = selector.getCurrentTrace();
		if (currentTrace != null) {
			traceChange(currentTrace);
		}
	}

}
