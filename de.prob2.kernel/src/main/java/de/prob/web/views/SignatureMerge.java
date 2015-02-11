package de.prob.web.views;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.AsyncContext;

import com.google.inject.Inject;

import de.prob.animator.command.GetDottyForSigMergeCmd;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.IAnimationChangeListener;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;
import de.prob.web.AbstractSession;
import de.prob.web.WebUtils;

public class SignatureMerge extends AbstractSession implements
		IAnimationChangeListener {

	StateSpace stateSpace;

	@Inject
	public SignatureMerge(final AnimationSelector selector) {
		selector.registerAnimationChangeListener(this);
		incrementalUpdate = false;
	}

	public void draw() {
		GetDottyForSigMergeCmd cmd = new GetDottyForSigMergeCmd(
				new ArrayList<String>());
		stateSpace.execute(cmd);
		String content = cmd.getContent();
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
		if (!(stateSpace == null)) {
			draw();
		}
	}

	@Override
	public void traceChange(final Trace currentTrace,
			final boolean currentAnimationChanged) {
		stateSpace = currentTrace == null ? null : currentTrace.getStateSpace();
		if (!(stateSpace == null)) {
			draw();
		}
	}

	@Override
	public void animatorStatus(final boolean busy) {
		// TODO Auto-generated method stub

	}
}
