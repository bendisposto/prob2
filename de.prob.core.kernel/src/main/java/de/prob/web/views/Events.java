package de.prob.web.views;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.AsyncContext;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.statespace.AnimationSelector;
import de.prob.statespace.IAnimationChangeListener;
import de.prob.statespace.OpInfo;
import de.prob.statespace.Trace;
import de.prob.web.AbstractSession;
import de.prob.web.WebUtils;

@Singleton
public class Events extends AbstractSession implements IAnimationChangeListener {

	Trace currentTrace;
	private final AnimationSelector selector;

	@Inject
	public Events(final AnimationSelector selector) {
		this.selector = selector;
		selector.registerAnimationChangeListener(this);

	}

	// used in JS
	@SuppressWarnings("unused")
	private static class Operation {
		public final String name;
		public final List<String> params;
		public final String id;

		public Operation(final String id, final String name,
				final List<String> params) {
			this.id = id;
			this.name = name;
			this.params = params;
		}
	}

	@Override
	public void traceChange(final Trace trace) {
		currentTrace = trace;
		int c = 0;
		Set<OpInfo> ops = trace.getNextTransitions();
		Operation[] res = new Operation[ops.size()];
		for (OpInfo opInfo : ops) {
			String name = opInfo.name;
			Operation o = new Operation(opInfo.id, name, opInfo.params);
			res[c++] = o;
		}
		String json = WebUtils.toJson(res);
		Map<String, String> wrap = WebUtils.wrap("cmd", "Events.setContent",
				"ops", json, "canGoBack", currentTrace.canGoBack(),
				"canGoForward", currentTrace.canGoForward());
		submit(wrap);
	}

	@Override
	public String html(final String clientid,
			final Map<String, String[]> parameterMap) {
		return simpleRender(clientid, "/ui/eventview/index.html");
	}

	public Object execute(final Map<String, String[]> params) {
		String id = params.get("id")[0];
		final Trace newTrace = currentTrace.add(id);
		selector.replaceTrace(currentTrace, newTrace);
		return null;
	}

	@Override
	public void reload(final String client, final int lastinfo,
			final AsyncContext context) {
		super.reload(client, lastinfo, context);
		traceChange(selector.getCurrentTrace());
	}

	public Object random(final Map<String, String[]> params) {
		int num = Integer.parseInt(params.get("num")[0]);
		Trace newTrace = currentTrace.randomAnimation(num);
		selector.replaceTrace(currentTrace, newTrace);
		return null;
	}

	public Object back(final Map<String, String[]> params) {
		Trace back = currentTrace.back();
		selector.replaceTrace(currentTrace, back);
		return null;
	}

	public Object forward(final Map<String, String[]> params) {
		Trace forward = currentTrace.forward();
		selector.replaceTrace(currentTrace, forward);
		return null;
	}
}
