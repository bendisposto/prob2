package de.prob.web.views;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.servlet.AsyncContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.prob.animator.command.ExpandFormulaCommand;
import de.prob.animator.command.InsertFormulaForVisualizationCommand;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.EventB;
import de.prob.animator.domainobjects.ExpandedFormula;
import de.prob.animator.domainobjects.FormulaId;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.IAnimationChangeListener;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;
import de.prob.visualization.AnimationNotLoadedException;
import de.prob.web.AbstractSession;
import de.prob.web.WebUtils;

public class FormulaView extends AbstractSession implements
		IAnimationChangeListener {

	Logger logger = LoggerFactory.getLogger(FormulaView.class);
	private Trace currentTrace;
	private IEvalElement formula;
	private FormulaId setFormula;
	private final StateSpace currentStateSpace;
	private final Set<String> collapsedNodes = new CopyOnWriteArraySet<String>();

	@Inject
	public FormulaView(final AnimationSelector animations) {
		currentTrace = animations.getCurrentTrace();
		if (currentTrace == null) {
			throw new AnimationNotLoadedException(
					"Please load model before opening Value over Time visualization");
		}
		currentStateSpace = currentTrace.getStateSpace();
		animations.registerAnimationChangeListener(this);
	}

	@Override
	public String html(final String clientid,
			final Map<String, String[]> parameterMap) {
		return simpleRender(clientid, "ui/formulaView/index.html");
	}

	@Override
	public void reload(final String client, final int lastinfo,
			final AsyncContext context) {
		super.reload(client, lastinfo, context);
		if (formula != null) {
			Object data = calculateData();
			Map<String, String> wrap = WebUtils.wrap("cmd",
					"FormulaView.formulaSet", "formula", formula.getCode(),
					"data", WebUtils.toJson(data));
			submit(wrap);
		}

	}

	@Override
	public void traceChange(final Trace trace) {
		currentTrace = trace;
		if (currentTrace != null
				&& currentTrace.getStateSpace().equals(currentStateSpace)) {
			Object data = calculateData();
			Map<String, String> wrap = WebUtils.wrap("cmd", "FormulaView.draw",
					"data", WebUtils.toJson(data));
			submit(wrap);
		}
	}

	public Object calculateData() {
		if (setFormula != null) {
			ExpandFormulaCommand cmd = new ExpandFormulaCommand(setFormula,
					currentTrace.getCurrentState().getId());
			currentStateSpace.execute(cmd);
			ExpandedFormula result = cmd.getResult();
			result.collapseNodes(new HashSet<String>(collapsedNodes));
			return result;
		}
		return null;
	}

	public Object setFormula(final Map<String, String[]> params) {
		if (formula == null) {
			return sendError(
					"Whoops!",
					"Could not add formula because it is invalid for this model",
					"alert-danger");
		}
		if (currentTrace == null) {
			return sendError(
					"Sorry!",
					"Could not assert the validity of the formula because an animation is not loaded",
					"");
		}
		if (!(formula instanceof EventB || formula instanceof ClassicalB)) {
			return sendError("Sorry!",
					"This visualization requires B-type formulas",
					"alert-danger");
		}

		try {
			InsertFormulaForVisualizationCommand cmd = new InsertFormulaForVisualizationCommand(
					formula);
			currentStateSpace.execute(cmd);
			setFormula = cmd.getFormulaId();

			Object data = calculateData();
			return WebUtils.wrap("cmd", "FormulaView.formulaSet", "formula",
					formula.getCode(), "data", WebUtils.toJson(data));
		} catch (Exception e) {
			return sendError(
					"Whoops!",
					"Could not add formula because evaluation of the formula threw an exception of type "
							+ e.getClass().getSimpleName(), "alert-danger");
		}

	}

	private Map<String, String> sendError(final String emphasized,
			final String msg, final String level) {
		return WebUtils.wrap("cmd", "FormulaView.error", "msg", msg, "strong",
				emphasized, "alertLevel", level);
	}

	public Object parse(final Map<String, String[]> params) {
		String f = params.get("formula")[0];

		try {
			IEvalElement e = currentStateSpace.getModel().parseFormula(f);
			formula = e;
			return WebUtils.wrap("cmd", "FormulaView.parseOk");
		} catch (Exception e) {
			formula = null;
			return WebUtils.wrap("cmd", "FormulaView.parseError");
		}
	}

	public Object removeFormula(final Map<String, String[]> params) {
		formula = null;
		return WebUtils.wrap("cmd", "FormulaView.formulaRemoved");
	}

	public Object collapseNode(final Map<String, String[]> params) {
		String id = params.get("formulaId")[0];

		collapsedNodes.add(id);
		return null;
	}

	public Object expandNode(final Map<String, String[]> params) {
		String id = params.get("formulaId")[0];

		collapsedNodes.remove(id);
		return null;
	}

}
