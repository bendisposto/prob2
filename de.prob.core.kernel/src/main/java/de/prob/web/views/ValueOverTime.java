package de.prob.web.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.AsyncContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.prob.animator.domainobjects.EvaluationResult;
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

	Map<String, IEvalElement> formulas = new HashMap<String, IEvalElement>();
	Map<String, IEvalElement> testedFormulas = new HashMap<String, IEvalElement>();
	Logger logger = LoggerFactory.getLogger(ValueOverTime.class);
	private Trace currentTrace;
	private final AbstractModel model;
	private String mode = "over";

	@Inject
	public ValueOverTime(final AnimationSelector animations) {
		currentTrace = animations.getCurrentTrace();
		if (currentTrace == null) {
			throw new AnimationNotLoadedException(
					"Please load model before opening Value over Time visualization");
		}
		model = currentTrace.getModel();
		animations.registerAnimationChangeListener(this);
	}

	@Override
	public String html(final String clientid,
			final Map<String, String[]> parameterMap) {
		return simpleRender(clientid, "ui/valueOverTime/index.html");
	}

	@Override
	public void outOfDateCall(final String client, final int lastinfo,
			final AsyncContext context) {
		/*
		 * super.outOfDateCall(client, lastinfo, context);
		 * 
		 * List<Object> result = new ArrayList<Object>(); for (Entry<String,
		 * IEvalElement> formula : testedFormulas .entrySet()) {
		 * result.add(WebUtils.wrap("id", formula.getKey(), "formula",
		 * formula.getValue().getCode())); } List<Object> data =
		 * calculateData(); IEvalElement time = formulas.get("time");
		 * 
		 * Map<String, String> wrap = WebUtils .wrap("cmd",
		 * "ValueOverTime.restorePage", "formulas", WebUtils.toJson(result),
		 * "data", WebUtils.toJson(data), "xLabel", time == null ?
		 * "Number of Animation Steps" : time .getCode(), "drawMode", mode);
		 * 
		 * submit(wrap);
		 */

		super.outOfDateCall(client, lastinfo, context);
		traceChange(currentTrace);

	}

	@Override
	public void traceChange(final Trace trace) {
		if (trace != null
				&& trace.getStateSpace().equals(model.getStatespace())) {
			currentTrace = trace;
			List<Object> result = calculateData();
			IEvalElement time = formulas.get("time");

			Map<String, String> wrap = WebUtils
					.wrap("cmd",
							"ValueOverTime.draw",
							"data",
							WebUtils.toJson(result),
							"xLabel",
							time == null ? "Number of Animation Steps" : time
									.getCode(), "drawMode", mode);
			submit(wrap);
		}
	}

	private List<Object> calculateData() {
		List<Object> result = new ArrayList<Object>();
		List<EvaluationResult> timeRes = new ArrayList<EvaluationResult>();
		IEvalElement time = formulas.get("time");
		if (time != null) {
			timeRes = currentTrace.eval(time);
		}

		for (IEvalElement formula : testedFormulas.values()) {
			if (!formula.equals(time)) {
				List<EvaluationResult> results = currentTrace.eval(formula);
				List<Object> points = new ArrayList<Object>();

				if (timeRes.isEmpty()) {
					int c = 0;
					for (EvaluationResult it : results) {
						points.add(wrapPoints(it.getStateId(),
								extractValue(it.getValue()), c,
								extractType(it.getValue())));
						points.add(wrapPoints(it.getStateId(),
								extractValue(it.getValue()), c + 1,
								extractType(it.getValue())));
						c++;
					}
				} else if (timeRes.size() == results.size()) {
					for (EvaluationResult it : results) {
						int index = results.indexOf(it);
						points.add(wrapPoints(it.getStateId(),
								extractValue(it.getValue()),
								extractValue(timeRes.get(index).getValue()),
								extractType(it.getValue())));
						if (index < results.size() - 1) {
							points.add(wrapPoints(it.getStateId(),
									extractValue(it.getValue()),
									extractValue(timeRes.get(index + 1)
											.getValue()), extractType(it
											.getValue())));
						}

					}
				}

				Map<String, Object> datum = new HashMap<String, Object>();
				datum.put("name", formula.getCode());
				datum.put("dataset", points);
				result.add(datum);
			}
		}
		return result;
	}

	private Map<String, Object> wrapPoints(final String stateid,
			final Integer value, final Integer t, final String type) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("stateid", stateid);
		map.put("value", value);
		map.put("t", t);
		map.put("type", type);
		return map;
	}

	private String extractType(final String value) {
		if (value.equals("TRUE") || value.equals("FALSE")) {
			return "BOOL";
		}
		return "INT";
	}

	private Integer extractValue(final String value) {
		if (value.equals("TRUE")) {
			return 1;
		}
		if (value.equals("FALSE")) {
			return 0;
		}
		return Integer.parseInt(value);
	}

	public Object changeMode(final Map<String, String[]> params) {
		mode = params.get("varMode")[0];
		return null;
	}

	public Object addFormula(final Map<String, String[]> params) {
		String id = params.get("id")[0];
		Boolean newFormula = Boolean.valueOf(params.get("newFormula")[0]);
		IEvalElement formula = formulas.get(id);
		if (formula == null) {
			return sendError(
					id,
					"Whoops!",
					"Could not add formula because it is invalid for this model",
					"alert-danger");
		}

		try {
			EvaluationResult res = currentTrace.evalCurrent(formula);
			if (res == null) {
				return sendError(
						id,
						"Warning!",
						"Could not add formula because it is not possible to assert the validity of the formula at this state in the animation",
						"");
			}
			if (res.hasError()) {
				return sendError(
						id,
						"Sorry!",
						"The specified formula cannot be evaluated for this model!",
						"alert-danger");
			} else {
				testedFormulas.put(id, formula);
			}
		} catch (Exception e) {
			return sendError(
					id,
					"Whoops!",
					"Could not add formula because evaluation of the formula threw an exception of type "
							+ e.getClass().getSimpleName(), "alert-danger");
		}

		List<Object> data = calculateData();
		IEvalElement time = testedFormulas.get("time");
		if (newFormula) {
			return WebUtils
					.wrap("cmd",
							"ValueOverTime.formulaAdded",
							"id",
							id,
							"formula",
							formula.getCode(),
							"data",
							WebUtils.toJson(data),
							"xLabel",
							time == null ? "Number of Animation Steps" : time
									.getCode(), "drawMode", mode);
		}
		return WebUtils.wrap("cmd", "ValueOverTime.formulaRestored", "id", id,
				"formula", formula.getCode(), "data", WebUtils.toJson(data),
				"xLabel",
				time == null ? "Number of Animation Steps" : time.getCode(),
				"drawMode", mode);
	}

	private Map<String, String> sendError(final String id,
			final String emphasized, final String msg, final String level) {
		return WebUtils.wrap("cmd", "ValueOverTime.error", "id", id, "msg",
				msg, "strong", emphasized, "alertLevel", level);
	}

	public Object parse(final Map<String, String[]> params) {
		String f = params.get("formula")[0];
		String id = params.get("id")[0];

		if ("time".equals(id) && "".equals(f)) {
			formulas.remove(id);
			return WebUtils.wrap("cmd", "ValueOverTime.parseOk", "id", id);
		}

		try {
			IEvalElement e = model.parseFormula(f);
			formulas.put(id, e);
			return WebUtils.wrap("cmd", "ValueOverTime.parseOk", "id", id);
		} catch (Exception e) {
			formulas.put(id, null);
			return WebUtils.wrap("cmd", "ValueOverTime.parseError", "id", id);
		}
	}

	public Object removeFormula(final Map<String, String[]> params) {
		String id = params.get("id")[0];
		testedFormulas.remove(id);
		formulas.remove(id);
		List<Object> data = calculateData();
		IEvalElement time = testedFormulas.get("time");
		return WebUtils.wrap("cmd", "ValueOverTime.formulaRemoved", "id", id,
				"data", WebUtils.toJson(data), "xLabel",
				time == null ? "Number of Animation Steps" : time.getCode(),
				"mode", mode);
	}
}
