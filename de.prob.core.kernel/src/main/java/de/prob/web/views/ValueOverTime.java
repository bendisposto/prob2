package de.prob.web.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.prob.animator.domainobjects.EvaluationException;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.representation.AbstractModel;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.IAnimationChangeListener;
import de.prob.statespace.Trace;
import de.prob.visualization.AnimationNotLoadedException;
import de.prob.web.AbstractSession;
import de.prob.web.WebUtils;

public class ValueOverTime extends AbstractSession implements
		IAnimationChangeListener {

	Logger logger = LoggerFactory.getLogger(ValueOverTime.class);
	IEvalElement time;
	List<IEvalElement> formulas = new ArrayList<IEvalElement>();
	private final Trace currentTrace;
	private final AbstractModel model;

	@Inject
	public ValueOverTime(final AnimationSelector animations) {
		currentTrace = animations.getCurrentTrace();
		if (currentTrace == null) {
			throw new AnimationNotLoadedException(
					"Please load model before opening Value over Time visualization");
		}
		model = currentTrace.getModel();
	}

	@Override
	public String html(final String clientid,
			final Map<String, String[]> parameterMap) {
		return simpleRender(clientid, "ui/valueOverTime/index.html");
	}

	@Override
	public void traceChange(final Trace trace) {
		// TODO Auto-generated method stub

	}

	public Object addFormula(final Map<String, String[]> params) {
		String time = params.get("time")[0];
		String formula = params.get("formula")[0];
		try {
			if (!time.equals("")) {
				this.time = model.parseFormula(time);
			}
			if (!formula.equals("")) {
				IEvalElement f = model.parseFormula(formula);
				formulas.add(f);
			}
		} catch (EvaluationException e) {
			logger.error("Could not parse formula " + formula
					+ " for formalism specified by "
					+ model.getClass().getSimpleName());
		}
		return null;
	}

	public Object parse(final Map<String, String[]> params) {
		String f = params.get("formula")[0];
		String id = params.get("id")[0];
		try {
			model.parseFormula(f);
			return WebUtils.wrap("cmd", "ValueOverTime.parseOk", "id", id);
		} catch (Exception e) {
			return WebUtils.wrap("cmd", "ValueOverTime.parseError", "id", id);
		}
	}
}
