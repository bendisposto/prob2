package de.prob.web.views;

import java.util.List;
import java.util.Map;

import javax.servlet.AsyncContext;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.annotations.PublicSession;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.AbstractModel;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.IAnimationChangeListener;
import de.prob.statespace.Trace;
import de.prob.statespace.Transition;
import de.prob.web.AbstractSession;
import de.prob.web.WebUtils;

@Singleton
@PublicSession
public class CurrentAnimations extends AbstractSession implements
		IAnimationChangeListener {

	private final AnimationSelector animations;

	@Inject
	public CurrentAnimations(final AnimationSelector animations) {
		this.animations = animations;
		animations.registerAnimationChangeListener(this);
		incrementalUpdate = false;
	}

	@Override
	public String html(final String clientid,
			final Map<String, String[]> parameterMap) {
		return simpleRender(clientid, "ui/animations/index.html");
	}

	public Object selectTrace(final Map<String, String[]> params) {
		int pos = Integer.parseInt(params.get("pos")[0]);
		Trace trace = animations.getTraces().get(pos);
		animations.changeCurrentAnimation(trace);
		return null;
	}

	public Object removeTrace(final Map<String, String[]> params) {
		int pos = Integer.parseInt(params.get("pos")[0]);
		Trace trace = animations.getTraces().get(pos);
		animations.removeTrace(trace);
		return null;
	}

	public Object protectTrace(final Map<String, String[]> params) {
		int pos = Integer.parseInt(params.get("pos")[0]);
		boolean protect = Boolean.valueOf(params.get("protect")[0]);
		Trace trace = animations.getTraces().get(pos);
		animations.setProtected(trace, protect);
		return null;
	}

	@Override
	public void traceChange(final Trace currentTrace,
			final boolean currentAnimationChanged) {
		List<Trace> traces = animations.getTraces();
		Object[] result = new Object[traces.size()];
		int ctr = 0;
		for (Trace t : traces) {
			AbstractModel model = t.getModel();
			AbstractElement mainComponent = model.getMainComponent();
			String modelName = mainComponent != null ? mainComponent.toString()
					: model.getModelFile().getName();
			Transition op = t.getCurrentTransition();
			String lastOp = op != null ? op.getPrettyRep() : "";

			String steps = t.getTransitionList().size() + "";
			String isCurrent = t.equals(currentTrace) + "";
			boolean isProtected = animations.getProtectedTraces().contains(
					t.getUUID());
			Map<String, String> wrapped = WebUtils.wrap("model", modelName,
					"lastOp", lastOp, "steps", steps, "isCurrent", isCurrent,
					"protected", isProtected);
			result[ctr++] = wrapped;
		}
		Map<String, String> wrap = WebUtils.wrap("cmd",
				"Animations.setContent", "animations", WebUtils.toJson(result));
		submit(wrap);
	}

	@Override
	public void animatorStatus(final boolean busy) {
		// This does not need to be considered for this view
	}

	@Override
	public void reload(final String client, final int lastinfo,
			final AsyncContext context) {
		sendInitMessage(context);
		traceChange(animations.getCurrentTrace(), false);
	}
}
