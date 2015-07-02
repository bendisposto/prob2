package de.prob.web.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.AsyncContext;

import org.apache.commons.lang.StringEscapeUtils;

import com.google.inject.Inject;

import de.prob.animator.domainobjects.AbstractEvalResult;
import de.prob.animator.domainobjects.EnumerationWarning;
import de.prob.animator.domainobjects.EvalResult;
import de.prob.animator.domainobjects.EvaluationErrorResult;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.animator.domainobjects.IdentifierNotInitialised;
import de.prob.annotations.OneToOne;
import de.prob.annotations.PublicSession;
import de.prob.model.eventb.EventBMachine;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.AbstractFormulaElement;
import de.prob.model.representation.ModelElementList;
import de.prob.model.representation.ModelRep;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.FormalismType;
import de.prob.statespace.State;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;
import de.prob.statespace.Transition;
import de.prob.unicode.UnicodeTranslator;
import de.prob.web.AbstractAnimationBasedView;
import de.prob.web.WebUtils;

@PublicSession
@OneToOne
public class StateInspector extends AbstractAnimationBasedView {

	List<IEvalElement> formulasForEvaluating = new ArrayList<IEvalElement>();
	List<String> history = new ArrayList<String>();
	Trace currentTrace;
	StateSpace currentStateSpace;

	@Inject
	public StateInspector(final AnimationSelector animations) {
		super(animations);
		incrementalUpdate = false;
		animations.registerAnimationChangeListener(this);
	}

	@Override
	public void reload(final String client, final int lastinfo,
			final AsyncContext context) {
		sendInitMessage(context);
		if (currentStateSpace != null && currentTrace != null) {

			extractFormulas(currentStateSpace);
			Object values = calculateFormulas(currentTrace);
			submit(WebUtils.wrap("cmd", "StateInspector.setModel",
					"components",
					WebUtils.toJson(ModelRep.translate(currentStateSpace)),
					"values", WebUtils.toJson(values), "history",
					WebUtils.toJson(history)));
		}
	}

	public Object evaluate(final Map<String, String[]> params) {
		String code = params.get("code")[0];
		if (history.size() > 200) {
			history = history.subList(100, history.size());
		}
		history.add(code);
		if (currentStateSpace != null) {
			Object eval = currentTrace.evalCurrent(currentStateSpace.getModel()
					.parseFormula(code));
			return WebUtils.wrap("cmd", "StateInspector.result", "code",
					unicode(code), "result",
					StringEscapeUtils.escapeHtml(eval.toString()));
		}
		return null;

	}

	public Object registerFormula(final Map<String, String[]> params) {
		String[] path = params.get("path[]");
		List<String> listP = Arrays.asList(path);
		if (currentStateSpace != null) {
			AbstractElement abstractElement = currentStateSpace.getModel().get(
					listP);
			if (abstractElement instanceof AbstractFormulaElement) {
				AbstractFormulaElement e = (AbstractFormulaElement) abstractElement;
				if (!e.isSubscribed(currentStateSpace)) {
					e.subscribe(currentStateSpace);
					formulasForEvaluating.add(e.getFormula());
				}
			}
		}
		return WebUtils.wrap("cmd", "StateInspector.updateValues", "values",
				WebUtils.toJson(calculateFormulas(currentTrace)));
	}

	public Object deregisterFormula(final Map<String, String[]> params) {
		String[] path = params.get("path[]");
		List<String> listP = Arrays.asList(path);
		if (currentStateSpace != null) {
			AbstractElement element = currentStateSpace.getModel().get(listP);
			if (element instanceof AbstractFormulaElement) {
				AbstractFormulaElement e = (AbstractFormulaElement) element;
				if (e.isSubscribed(currentStateSpace)) {
					e.unsubscribe(currentStateSpace);
					formulasForEvaluating.remove(e.getFormula());
				}
			}
		}
		return WebUtils.wrap("cmd", "StateInspector.updateValues", "values",
				WebUtils.toJson(calculateFormulas(currentTrace)));
	}

	@Override
	public String html(final String clientid,
			final Map<String, String[]> parameterMap) {
		return simpleRender(clientid, "ui/stateInspector/index.html");
	}

	@Override
	public void performTraceChange(final Trace trace) {
		if (trace == null) {
			currentTrace = null;
			currentStateSpace = null;
			submit(WebUtils.wrap("cmd", "StateInspector.clearInput"));
			return;
		}
		currentTrace = trace;
		StateSpace s = trace.getStateSpace();
		if (!s.equals(currentStateSpace)) {
			currentStateSpace = s;
			extractFormulas(currentStateSpace);

			history = new ArrayList<String>();

			Object calculatedValues = calculateFormulas(currentTrace);
			submit(WebUtils.wrap("cmd", "StateInspector.setModel",
					"components",
					WebUtils.toJson(ModelRep.translate(currentStateSpace)),
					"values", WebUtils.toJson(calculatedValues), "history",
					WebUtils.toJson(history)));
			return;
		}

		Object calculatedValues = calculateFormulas(currentTrace);
		submit(WebUtils.wrap("cmd", "StateInspector.updateValues", "values",
				WebUtils.toJson(calculatedValues)));
	}

	public Object calculateFormulas(final Trace t) {
		List<Object> extracted = new ArrayList<Object>();
		StateSpace s = t.getStateSpace();
		Transition currentTransition = t.getCurrentTransition();
		State currentS = currentTransition == null ? t.getCurrentState()
				: currentTransition.getDestination();
		Map<IEvalElement, AbstractEvalResult> current = s.valuesAt(currentS);
		State prevS = currentTransition == null ? null : currentTransition
				.getSource();
		Map<IEvalElement, AbstractEvalResult> previous = prevS == null ? new HashMap<IEvalElement, AbstractEvalResult>()
				: s.valuesAt(prevS);

		for (IEvalElement e : formulasForEvaluating) {
			String currentVal = stringRep(current.get(e));
			String previousVal = stringRep(previous.get(e));
			extracted.add(WebUtils.wrap("id", e.getFormulaId().getUUID(),
					"code", unicode(e.getCode()), "current", currentVal,
					"previous", previousVal));
		}

		return extracted;
	}

	private String stringRep(final AbstractEvalResult res) {
		if (res instanceof EvalResult) {
			return unicode(((EvalResult) res).getValue());
		}
		if (res instanceof EvaluationErrorResult
				&& !(res instanceof IdentifierNotInitialised)) {
			return ((EvaluationErrorResult) res).getResult();
		}
		if (res instanceof EnumerationWarning) {
			return unicode("?(\u221E)");
		}
		return "";
	}

	private String unicode(final String code) {
		return StringEscapeUtils.escapeHtml(UnicodeTranslator.toUnicode(code));
	}

	private void extractFormulas(final StateSpace s) {
		formulasForEvaluating = new ArrayList<IEvalElement>();
		if (s.getModel().getFormalismType().equals(FormalismType.B)) {
			extractFormulas(s.getModel(), s);
		}
	}

	private void extractFormulas(final AbstractElement e, final StateSpace s) {
		if (e instanceof AbstractFormulaElement) {
			AbstractFormulaElement formulaElement = (AbstractFormulaElement) e;
			if (formulaElement.isSubscribed(s)) {
				formulasForEvaluating.add(formulaElement.getFormula());
			}
			return;
		} else if (e instanceof EventBMachine) {

		}
		Map<Class<? extends AbstractElement>, ModelElementList<? extends AbstractElement>> children = e
				.getChildren();
		Collection<ModelElementList<? extends AbstractElement>> values = children
				.values();
		for (ModelElementList<? extends AbstractElement> modelElementList : values) {
			for (AbstractElement e2 : modelElementList) {
				extractFormulas(e2, s);
			}
		}
	}

	@Override
	public void animatorStatus(final boolean busy) {
		if (busy) {
			submit(WebUtils.wrap("cmd", "StateInspector.disable"));
		} else {
			submit(WebUtils.wrap("cmd", "StateInspector.enable"));
		}
	}
}
