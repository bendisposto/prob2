package de.prob.web.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.AsyncContext;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.AbstractModel;
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
	private boolean sorted = false;
	private boolean reverse = false;

	@Inject
	public Events(final AnimationSelector selector) {
		this.selector = selector;
		selector.registerAnimationChangeListener(this);

	}

	// used in JS
	@SuppressWarnings("unused")
	private static class Operation implements Comparable<Operation> {
		public final String name;
		public final List<String> params;
		public final String id;

		public Operation(final String id, final String name,
				final List<String> params) {
			this.id = id;
			this.name = name;
			this.params = params;
		}

		@Override
		public int compareTo(final Operation arg0) {
			if (name.compareTo(arg0.name) == 0) {
				if (params.size() != arg0.params.size()) {
					throw new IllegalStateException(
							"Events with the same name must have the same number of parameters");
				}
				for (int i = 0; i < params.size(); i++) {
					String p1 = stripString(params.get(i));
					String p2 = stripString(arg0.params.get(i));
					if (p1.compareTo(p2) != 0) {
						return p1.compareTo(p2);
					}
				}
				return 0;
			} else {
				return name.compareTo(arg0.name);
			}
		}

		private String stripString(final String param) {
			return param.replaceAll("\\{", "").replaceAll("\\}", "");
		}
	}

	@Override
	public void traceChange(final Trace trace) {
		currentTrace = trace;
		Set<OpInfo> ops = trace.getNextTransitions();
		List<Operation> res = new ArrayList<Operation>(ops.size());
		for (OpInfo opInfo : ops) {
			String name = opInfo.name;
			Operation o = new Operation(opInfo.id, name, opInfo.params);
			res.add(o);
		}
		if (sorted) {
			Collections.sort(res);
			if (reverse) {
				Collections.reverse(res);
			}
		} else {
			AbstractModel m = currentTrace.getModel();
			AbstractElement mainComponent = m.getMainComponent();
			Collections.sort(res, new Comparator<Operation>() {

				@Override
				public int compare(final Operation o1, final Operation o2) {
					// TODO Auto-generated method stub
					return 0;
				}
			});
			// Order list according to order that appears in model
		}
		String json = WebUtils.toJson(res);
		Map<String, String> wrap = WebUtils.wrap("cmd", "Events.setContent",
				"ops", json, "canGoBack", currentTrace.canGoBack(),
				"canGoForward", currentTrace.canGoForward(), "sortMode",
				getSortMode());
		submit(wrap);
	}

	@Override
	public String html(final String clientid,
			final Map<String, String[]> parameterMap) {
		return simpleRender(clientid, "ui/eventview/index.html");
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

	public Object sort(final Map<String, String[]> params) {
		String mode = params.get("sortMode")[0];
		if ("normal".equals(mode)) {
			sorted = false;
			reverse = false;
		} else if ("aToZ".equals(mode)) {
			sorted = true;
			reverse = false;
		} else if ("zToA".equals(mode)) {
			sorted = true;
			reverse = true;
		}
		traceChange(currentTrace);
		return null;
	}

	public String getSortMode() {
		if (sorted && reverse) {
			return "zToA";
		}
		if (sorted) {
			return "aToZ";
		}
		return "normal";
	}

	private class EventComparator implements Comparator<Operation> {

		public EventComparator(final AbstractModel m) {
			AbstractElement main = m.getMainComponent();
			List<String> eventNames = new ArrayList<String>();
		}

		@Override
		public int compare(final Operation o1, final Operation o2) {
			// TODO Auto-generated method stub
			return 0;
		}

	}
}
