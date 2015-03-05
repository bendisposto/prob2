package de.prob.web.views;

import java.util.Map;

import javax.servlet.AsyncContext;

import com.google.inject.Inject;

import de.prob.animator.command.GetDotForStateVizCmd;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.Trace;
import de.prob.web.AbstractAnimationBasedView;
import de.prob.web.WebUtils;

public class StateVisualization extends AbstractAnimationBasedView {

	Trace currentTrace;

	@Inject
	public StateVisualization(final AnimationSelector animations) {
		super(animations);
		animations.registerAnimationChangeListener(this);
		incrementalUpdate = false;
	}

	public void draw() {
		GetDotForStateVizCmd cmd = new GetDotForStateVizCmd(
				currentTrace.getCurrentState());
		currentTrace.getStateSpace().execute(cmd);
		Map<String, String> wrap = WebUtils.wrap("cmd", "Dotty.draw",
				"content", cmd.getContent());
		submit(wrap);
	}

	@Override
	public String html(final String clientid,
			final Map<String, String[]> parameterMap) {
		return simpleRender(clientid, "ui/dotty/index.html");
	}

	@Override
	public void reload(final String client, final int lastinfo,
			final AsyncContext context) {
		sendInitMessage(context);
		if (!(currentTrace == null)) {
			draw();
		}
	}

	@Override
	public void performTraceChange(final Trace trace) {
		this.currentTrace = trace;
		if (!(currentTrace == null)) {
			draw();
		}
	}

	@Override
	public void animatorStatus(final boolean busy) {
	}

}
