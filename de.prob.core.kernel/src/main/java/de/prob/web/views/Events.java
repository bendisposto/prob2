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

	private final AnimationSelector selector;

	@Inject
	public Events(AnimationSelector selector) {
		this.selector = selector;
		selector.registerAnimationChangeListener(this);

	}

	// used in JS
	@SuppressWarnings("unused")
	private static class Operation {
		public final String name;
		public final List<String> params;
		public final String id;

		public Operation(String id, String name, List<String> params) {
			this.id = id;
			this.name = name;
			this.params = params;
		}
	}

	@Override
	public void traceChange(Trace trace) {
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
				"ops", json);
		submit(wrap);
	}

	@Override
	public String html(String clientid, Map<String, String[]> parameterMap) {
		return simpleRender(clientid, "/ui/eventview/index.html");
	}

	public Object execute(Map<String, String[]> params) {
		String id = params.get("id")[0];
		Trace currentTrace = selector.getCurrentTrace();
		final Trace newTrace = currentTrace.add(id);
		selector.replaceTrace(currentTrace, newTrace);
		return null;
	}

	@Override
	public void outOfDateCall(String client, int lastinfo, AsyncContext context) {
		super.outOfDateCall(client, lastinfo, context);
		traceChange(selector.getCurrentTrace());

	}
}
