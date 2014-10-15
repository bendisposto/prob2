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
import com.google.inject.Singleton;

import de.prob.animator.domainobjects.EvalResult;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.animator.domainobjects.IEvalResult;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.AbstractFormulaElement;
import de.prob.model.representation.AbstractModel;
import de.prob.model.representation.ModelElementList;
import de.prob.model.representation.ModelRep;
import de.prob.scripting.FileHandler;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.FormalismType;
import de.prob.statespace.IAnimationChangeListener;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;
import de.prob.unicode.UnicodeTranslator;
import de.prob.web.AbstractSession;
import de.prob.web.WebUtils;

@Singleton
public class StateInspector extends AbstractSession implements
		IAnimationChangeListener {

	private static final String HISTORY_FILE_NAME = "stateInspectorRepl";
	List<IEvalElement> formulasForEvaluating = new ArrayList<IEvalElement>();
	List<String> history = new ArrayList<String>();
	Trace currentTrace;
	AbstractModel currentModel;
	private final FileHandler fileWriter;

	@Inject
	public StateInspector(final FileHandler fileWriter,
			final AnimationSelector animations) {
		this.incrementalUpdate = false;
		this.fileWriter = fileWriter;
		animations.registerAnimationChangeListener(this);
	}

	@Override
	public void reload(final String client, final int lastinfo,
			final AsyncContext context) {
		sendInitMessage(context);
		if (currentModel != null && currentTrace != null) {

			extractFormulas(currentModel);
			Object values = calculateFormulas(currentTrace);
			submit(WebUtils.wrap("cmd", "StateInspector.setModel",
					"components",
					WebUtils.toJson(ModelRep.translate(currentModel)),
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
		if (currentModel != null) {
			fileWriter.setContent(currentModel.getModelDirPath()
					+ HISTORY_FILE_NAME, history);

			Object eval = currentTrace.evalCurrent(currentModel
					.parseFormula(code));
			return WebUtils.wrap("cmd", "StateInspector.result", "code",
					unicode(code), "result", eval.toString());
		}
		return null;

	}

	public Object registerFormula(final Map<String, String[]> params) {
		String[] path = params.get("path[]");
		List<String> listP = Arrays.asList(path);
		if (currentModel != null) {
			StateSpace s = currentModel.getStateSpace();
			AbstractElement abstractElement = currentModel.get(listP);
			if (abstractElement instanceof AbstractFormulaElement) {
				AbstractFormulaElement e = (AbstractFormulaElement) abstractElement;
				if (!e.isSubscribed(s)) {
					e.subscribe(s);
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
		if (currentModel != null) {
			StateSpace s = currentModel.getStateSpace();
			AbstractElement element = currentModel.get(listP);
			if (element instanceof AbstractFormulaElement) {
				AbstractFormulaElement e = (AbstractFormulaElement) element;
				if (e.isSubscribed(s)) {
					e.unsubscribe(s);
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
	public void traceChange(final Trace trace,
			final boolean currentAnimationChanged) {
		if (currentAnimationChanged) {
			if (trace == null) {
				currentTrace = null;
				currentModel = null;
				submit(WebUtils.wrap("cmd", "StateInspector.clearInput"));
				return;
			}
			currentTrace = trace;
			AbstractModel newModel = trace.getModel();
			if (!newModel.equals(currentModel)) {
				currentModel = newModel;
				extractFormulas(currentModel);

				history = getCurrentHistory(currentModel.getModelDirPath());

				Object calculatedValues = calculateFormulas(currentTrace);
				submit(WebUtils.wrap("cmd", "StateInspector.setModel",
						"components",
						WebUtils.toJson(ModelRep.translate(currentModel)),
						"values", WebUtils.toJson(calculatedValues), "history",
						WebUtils.toJson(history)));
				return;
			}

			Object calculatedValues = calculateFormulas(currentTrace);
			submit(WebUtils.wrap("cmd", "StateInspector.updateValues",
					"values", WebUtils.toJson(calculatedValues)));
		}
	}

	private List<String> getCurrentHistory(final String modelDirPath) {
		String fileName = modelDirPath + HISTORY_FILE_NAME;
		List<String> history = fileWriter.getListOfStrings(fileName);
		if (history == null) {
			history = new ArrayList<String>();
			fileWriter.setContent(fileName, history);
		}
		return history;
	}

	public Object calculateFormulas(final Trace t) {
		List<Object> extracted = new ArrayList<Object>();
		StateSpace s = t.getStateSpace();
		Map<IEvalElement, IEvalResult> current = s
				.valuesAt(t.getCurrentState());
		Map<IEvalElement, IEvalResult> previous = t.getCurrentPos() > -1 ? s
				.valuesAt(t.getCurrentTransition().getSrcId())
				: new HashMap<IEvalElement, IEvalResult>();

		for (IEvalElement e : formulasForEvaluating) {
			String currentVal = current.get(e) instanceof EvalResult ? unicode(((EvalResult) current
					.get(e)).getValue()) : "";
			String previousVal = previous.get(e) instanceof EvalResult ? unicode(((EvalResult) previous
					.get(e)).getValue()) : "";
			extracted.add(WebUtils.wrap("id", e.getFormulaId().uuid, "code",
					unicode(e.getCode()), "current",
					current.get(e) == null ? "" : currentVal, "previous",
					previous.get(e) == null ? "" : previousVal));
		}

		return extracted;
	}

	private String unicode(final String code) {
		return StringEscapeUtils.escapeHtml(UnicodeTranslator.toUnicode(code));
	}

	private void extractFormulas(final AbstractModel m) {
		formulasForEvaluating = new ArrayList<IEvalElement>();
		if (m.getFormalismType().equals(FormalismType.B)) {
			extractFormulas(m, m.getStateSpace());
		}
	}

	private void extractFormulas(final AbstractElement e, final StateSpace s) {
		if (e instanceof AbstractFormulaElement) {
			AbstractFormulaElement formulaElement = (AbstractFormulaElement) e;
			if (formulaElement.isSubscribed(s)) {
				formulasForEvaluating.add(formulaElement.getFormula());
			}
			return;
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
