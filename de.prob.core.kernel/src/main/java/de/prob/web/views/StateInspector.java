package de.prob.web.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.AsyncContext;

import org.apache.commons.lang.StringEscapeUtils;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.EvalResult;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.animator.domainobjects.IEvalResult;
import de.prob.model.eventb.Context;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.AbstractModel;
import de.prob.model.representation.Axiom;
import de.prob.model.representation.BSet;
import de.prob.model.representation.Constant;
import de.prob.model.representation.IEval;
import de.prob.model.representation.Invariant;
import de.prob.model.representation.Machine;
import de.prob.model.representation.Variable;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.IAnimationChangeListener;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;
import de.prob.unicode.UnicodeTranslator;
import de.prob.web.AbstractSession;
import de.prob.web.WebUtils;

@Singleton
public class StateInspector extends AbstractSession implements
		IAnimationChangeListener {

	List<IEvalElement> formulasForEvaluating = new ArrayList<IEvalElement>();
	Trace currentTrace;
	AbstractModel currentModel;

	@Inject
	public StateInspector(final AnimationSelector animations) {
		animations.registerAnimationChangeListener(this);
	}

	@Override
	public void reload(final String client, final int lastinfo,
			final AsyncContext context) {
		super.reload(client, lastinfo, context);

		if (currentModel != null && currentTrace != null) {
			Map<String, Object> extracted = extractModel(currentModel);
			Object values = calculateFormulas(currentTrace);
			submit(WebUtils.wrap("cmd", "StateInspector.setModel",
					"components", WebUtils.toJson(extracted), "values",
					WebUtils.toJson(values)));
		}
	}

	public Object evaluate(final Map<String, String[]> params) {
		String code = params.get("code")[0];
		Object eval = currentTrace.getCurrentState().eval(new ClassicalB(code));
		return WebUtils.wrap("cmd", "StateInspector.result", "code", code,
				"result", eval.toString());
	}

	@Override
	public String html(final String clientid,
			final Map<String, String[]> parameterMap) {
		return simpleRender(clientid, "ui/stateInspector/index.html");
	}

	@Override
	public void traceChange(final Trace trace) {
		if (trace == null) {
			currentTrace = null;
			deregisterFormulas(currentModel);
			currentModel = null;
			submit(WebUtils.wrap("cmd", "StateInspector.clearInput"));
			return;
		}
		currentTrace = trace;
		AbstractModel newModel = trace.getModel();
		if (!newModel.equals(currentModel)) {
			if (currentModel != null) {
				deregisterFormulas(currentModel);
			}
			currentModel = newModel;
			Map<String, Object> extracted = extractModel(currentModel);
			registerFormulas(currentModel);

			Object calculatedValues = calculateFormulas(currentTrace);
			submit(WebUtils.wrap("cmd", "StateInspector.setModel",
					"components", WebUtils.toJson(extracted), "values",
					WebUtils.toJson(calculatedValues)));
			return;
		}

		Object calculatedValues = calculateFormulas(currentTrace);
		submit(WebUtils.wrap("cmd", "StateInspector.updateValues", "values",
				WebUtils.toJson(calculatedValues)));
	}

	public Object calculateFormulas(final Trace t) {
		List<Object> extracted = new ArrayList<Object>();
		StateSpace s = t.getStateSpace();
		Map<IEvalElement, IEvalResult> current = s
				.valuesAt(t.getCurrentState());
		Map<IEvalElement, IEvalResult> previous = s.valuesAt(t.getCurrent()
				.getSrc());

		for (IEvalElement e : formulasForEvaluating) {
			String currentVal = current.get(e) instanceof EvalResult ? unicode(((EvalResult) current
					.get(e)).getValue()) : "";
			String previousVal = previous.get(e) instanceof EvalResult ? unicode(((EvalResult) previous
					.get(e)).getValue()) : "";
			extracted.add(WebUtils.wrap("id", e.getFormulaId().uuid, "code",
					unicode(e.getCode()), "current",
					current.get(e) == null ? "" : unicode(currentVal),
					"previous", previous.get(e) == null ? ""
							: unicode(previousVal)));
		}

		return extracted;
	}

	private String unicode(final String code) {
		return StringEscapeUtils.escapeHtml(UnicodeTranslator.toUnicode(code));
	}

	private void registerFormulas(final AbstractModel model) {
		StateSpace s = model.getStatespace();
		for (IEvalElement e : formulasForEvaluating) {
			s.subscribe(this, e);
		}
	}

	private void deregisterFormulas(final AbstractModel model) {
		StateSpace s = model.getStatespace();
		for (IEvalElement iEvalElement : formulasForEvaluating) {
			s.unsubscribe(this, iEvalElement);
		}
	}

	private Map<String, Object> extractModel(final AbstractModel m) {
		formulasForEvaluating = new ArrayList<IEvalElement>();
		Map<String, Object> extracted = new HashMap<String, Object>();
		List<Object> components = new ArrayList<Object>();
		Map<String, AbstractElement> modelComponents = m.getComponents();
		if (modelComponents != null) {
			for (Entry<String, AbstractElement> e : modelComponents.entrySet()) {
				components.add(extractComponent(e.getKey(), e.getValue()));
			}
		}
		extracted.put("components", components);
		return extracted;
	}

	private Object extractComponent(final String name, final AbstractElement e) {
		Map<String, Object> extracted = new HashMap<String, Object>();
		List<Object> kids = new ArrayList<Object>();
		if (e instanceof Context) {
			kids.add(extractElement(e, BSet.class));
			kids.add(extractElement(e, Constant.class));
			kids.add(extractElement(e, Axiom.class));
		}
		if (e instanceof Machine) {
			kids.add(extractElement(e, Variable.class));
			kids.add(extractElement(e, Invariant.class));
		}
		extracted.put("label", name);
		extracted.put("children", kids);
		return extracted;
	}

	private Object extractElement(final AbstractElement parent,
			final Class<? extends AbstractElement> c) {
		Map<String, Object> extracted = new HashMap<String, Object>();
		List<Object> kids = new ArrayList<Object>();
		List<? extends AbstractElement> children = parent.getChildrenOfType(c);
		for (AbstractElement abstractElement : children) {
			if (abstractElement instanceof IEval) {
				IEvalElement formula = ((IEval) abstractElement).getEvaluate();
				Map<String, String> wrap = WebUtils.wrap("code",
						unicode(formula.getCode()), "id",
						formula.getFormulaId().uuid);
				kids.add(wrap);
				formulasForEvaluating.add(formula);
			}
		}
		String label = extractLabel(c);
		extracted.put("label", label);
		extracted.put("children", kids);
		return extracted;
	}

	private String extractLabel(final Class<? extends AbstractElement> c) {
		String simpleName = c.getSimpleName();
		if ("BSet".equals(simpleName)) {
			return "Sets";
		}
		return simpleName + "s";
	}

}
