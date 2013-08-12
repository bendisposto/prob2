package de.prob.web.views;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.AsyncContext;

import com.google.common.base.Joiner;
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
	private ArrayList<OpInfo> ops;

	@Inject
	public Events(AnimationSelector selector) {
		this.selector = selector;
		selector.registerAnimationChangeListener(this);
	}

	@Override
	public void traceChange(Trace trace) {
		ops = new ArrayList<OpInfo>();
		ops.addAll(trace.getNextTransitions());
		String[] res = new String[ops.size()];
		int c = 0;
		for (OpInfo opInfo : ops) {
			res[c++] = opInfo.name + "("
					+ Joiner.on(", ").join(opInfo.getParams()) + ")";
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
		OpInfo info = ops.get(Integer.parseInt(id));
		Trace currentTrace = selector.getCurrentTrace();
		final Trace newTrace = currentTrace.add(info.id);
		selector.replaceTrace(currentTrace, newTrace);
		return null;
	}

	@Override
	public void outOfDateCall(String client, int lastinfo, AsyncContext context) {
		super.outOfDateCall(client, lastinfo, context);
		traceChange(selector.getCurrentTrace());
	}
}
