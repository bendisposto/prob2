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
	public CurrentTrace(AnimationSelector selector) {
		this.selector = selector;
		selector.registerAnimationChangeListener(this);
	}

	@Override
	public void traceChange(Trace trace) {
		logger.trace("Trace has changed. Submitting");
		OpInfo[] elements = getElements(trace);
		String[] ops = new String[elements.length];
		for (int i = 0; i < elements.length; i++) {
			ops[i] = elements[i].getRep(trace.getModel());
		}

		Map<String, String> wrap = WebUtils.wrap("cmd",
				"CurrentTrace.setTrace", "trace", WebUtils.toJson(ops));
		submit(wrap);
	}

	public Object gotoPos(Map<String, String[]> params) {
		logger.trace("Goto Position in Trace");
		Trace trace = selector.getCurrentTrace();
		int cpos = getElements(trace).length - 1;
		int pos = Integer.parseInt(get(params, "pos"));
		int moves = cpos - pos;
		for (int i = 0; i < moves; i++) {
			trace = trace.back();

		}
		selector.replaceTrace(selector.getCurrentTrace(), trace);
		return null;
	}

	@Override
	public String html(String clientid, Map<String, String[]> parameterMap) {
		String template = "ui/currenttrace/index.html";
		Object scope = WebUtils.wrap("clientid", clientid);
		return WebUtils.render(template, scope);
	}

	public OpInfo[] getElements(final Trace trace) {
		List<OpInfo> ops = new ArrayList<OpInfo>();
		TraceElement current = trace.getCurrent();
		ops.addAll(current.getOpList());
		return ops.toArray(new OpInfo[ops.size()]);
	}

	@Override
	public void outOfDateCall(String client, int lastinfo, AsyncContext context) {
		super.outOfDateCall(client, lastinfo, context);
		traceChange(selector.getCurrentTrace());
	}

}
