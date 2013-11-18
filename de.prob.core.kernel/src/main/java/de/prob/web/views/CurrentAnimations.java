package de.prob.web.views;

import java.util.List;
import java.util.Map;

import javax.servlet.AsyncContext;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.AbstractModel;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.IAnimationChangeListener;
import de.prob.statespace.Trace;
import de.prob.web.AbstractSession;
import de.prob.web.WebUtils;

@Singleton
public class CurrentAnimations extends AbstractSession implements
		IAnimationChangeListener {

	private final AnimationSelector animations;

	@Inject
	public CurrentAnimations(AnimationSelector animations) {
		this.animations = animations;
		animations.registerAnimationChangeListener(this);
	}

	@Override
	public String html(String clientid, Map<String, String[]> parameterMap) {
		return simpleRender(clientid, "ui/animations/index.html");
	}

	@Override
	public void traceChange(Trace trace) {
		List<Trace> traces = animations.getTraces();
		Object[] result = new Object[traces.size()];
		int ctr = 0;
		for (Trace t : traces) {
			AbstractModel model = t.getModel();
			AbstractElement mainComponent = model.getMainComponent();
			String modelName = mainComponent != null ? mainComponent.toString()
					: model.getModelFile().getName();
			String lastOp = !t.getCurrent().getSrc().getId().equals("root") ? t
					.getCurrent().getOp().toString() : "";

			String steps = t.getCurrent().getOpList().size() + "";
			String isCurrent = t.equals(trace) + "";
			Map<String, String> wrapped = WebUtils.wrap("model", modelName,
					"lastOp", lastOp, "steps", steps, "isCurrent", isCurrent);
			result[ctr++] = wrapped;
		}
		Map<String, String> wrap = WebUtils.wrap("cmd",
				"Animations.setContent", "animations", WebUtils.toJson(result));
		submit(wrap);
	}

	@Override
	public void reload(String client, int lastinfo, AsyncContext context) {
		super.reload(client, lastinfo, context);
		// traceChange(animations.getCurrentTrace());
	}

	public Object selectTrace(Map<String, String[]> params) {
		int pos = Integer.parseInt(params.get("pos")[0]);
		Trace trace = animations.getTraces().get(pos);
		animations.changeCurrentAnimation(trace);
		return null;
	}

	public Object removeTrace(Map<String, String[]> params) {
		int pos = Integer.parseInt(params.get("pos")[0]);
		Trace trace = animations.getTraces().get(pos);
		animations.removeTrace(trace);
		return null;
	}

}
