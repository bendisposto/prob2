package de.prob.web.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.AsyncContext;

import com.google.inject.Inject;

import de.prob.animator.command.GetDottyForSigMergeCmd;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.IModelChangedListener;
import de.prob.statespace.IStatesCalculatedListener;
import de.prob.statespace.OpInfo;
import de.prob.statespace.StateSpace;
import de.prob.web.AbstractSession;
import de.prob.web.WebUtils;

public class SignatureMerge extends AbstractSession implements
		IModelChangedListener, IStatesCalculatedListener {

	StateSpace stateSpace;

	@Inject
	public SignatureMerge(final AnimationSelector selector) {
		selector.registerModelChangedListener(this);
		incrementalUpdate = false;
	}

	@Override
	public void newTransitions(final List<OpInfo> newOps) {
		if (!(stateSpace == null)) {
			draw();
		}
	}

	public void draw() {
		GetDottyForSigMergeCmd cmd = new GetDottyForSigMergeCmd(
				new ArrayList<String>());
		stateSpace.execute(cmd);
		Map<String, String> wrap = WebUtils.wrap("cmd", "Dotty.draw",
				"content", cmd.getContent());
		submit(wrap);
	}

	@Override
	public void modelChanged(final StateSpace s) {
		if (!(stateSpace == null)) {
			stateSpace.deregisterStateSpaceListener(this);
		}
		this.stateSpace = s;
		if (!(stateSpace == null)) {
			stateSpace.registerStateSpaceListener(this);
			draw();
		}
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
}
