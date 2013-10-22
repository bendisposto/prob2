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
		List<OpInfo> elements = getElements(trace);
		OpInfo currentOp = trace.getCurrent().getOp();
		boolean pastCurrentOp = false;
		List<Map<String, String>> ops = new ArrayList<Map<String, String>>();
		for (OpInfo opInfo : elements) {
			String group = "past";
			if (opInfo.equals(currentOp)) {
				group = "current";
				pastCurrentOp = true;
			} else {
				if (pastCurrentOp) {
					group = "future";
				}
			}
			String id = opInfo.getId();
			String rep = opInfo.getRep(trace.getModel());
			ops.add(WebUtils.wrap("id", id, "rep", rep, "group", group));
		}

		Map<String, String> wrap = WebUtils.wrap("cmd",
				"CurrentTrace.setTrace", "trace", WebUtils.toJson(ops));
		submit(wrap);
	}

	public Object gotoPos(final Map<String, String[]> params) {
		logger.trace("Goto Position in Trace");
		Trace trace = selector.getCurrentTrace();
		String id = get(params, "id");
		String group = get(params, "group");
		if ("current".equals(group)) {
			return null;
		}
		if ("future".equals(group)) {
			OpInfo op = trace.getCurrent().getOp();
			while (!op.getId().equals(id)) {
				trace = trace.forward();
				op = trace.getCurrent().getOp();
			}
		}
		if ("past".equals(group)) {
			OpInfo op = trace.getCurrent().getOp();
			while (!op.getId().equals(id)) {
				trace = trace.back();
				op = trace.getCurrent().getOp();
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
