package de.prob.web.views;

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
	public CurrentTrace(AnimationSelector selector) {
		this.selector = selector;
		selector.registerAnimationChangeListener(this);
	}

	@Override
	public void traceChange(Trace trace) {
		logger.trace("Trace has changed. Submitting");
		List<OpInfo> elements = getElements(trace);
		String[] ops = new String[elements.size()];
		for (int i = 0; i < elements.size(); i++) {
			ops[i] = elements.get(i).getRep(trace.getModel());
		}

		Map<String, String> wrap = WebUtils.wrap("cmd",
				"CurrentTrace.setTrace", "trace", WebUtils.toJson(ops));
		submit(wrap);
	}

	public Object gotoPos(Map<String, String[]> params) {
		logger.trace("Goto Position in Trace");
		Trace trace = selector.getCurrentTrace();
		int cpos = getElements(trace).size() - 1;
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
		return simpleRender(clientid, "ui/currenttrace/index.html");
	}

	public List<OpInfo> getElements(final Trace trace) {
		return trace.getCurrent().getOpList();
	}

	@Override
	public void outOfDateCall(String client, int lastinfo, AsyncContext context) {
		super.outOfDateCall(client, lastinfo, context);
		Trace currentTrace = selector.getCurrentTrace();
		if (currentTrace != null) {
			traceChange(currentTrace);
		}
	}

}
