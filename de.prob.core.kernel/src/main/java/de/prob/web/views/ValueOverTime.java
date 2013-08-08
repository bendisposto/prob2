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
		super.outOfDateCall(client, lastinfo, context);
		traceChange(currentTrace);
	}

	@Override
	public void traceChange(final Trace trace) {
		if (trace != null
				&& trace.getStateSpace().equals(model.getStatespace())) {
			currentTrace = trace;
			List<Object> result = new ArrayList<Object>();
			List<EvaluationResult> timeRes = new ArrayList<EvaluationResult>();
			IEvalElement time = null;
			if (testedFormulas.containsKey("time")) {
				time = formulas.get("time");
				if (time != null) {
					timeRes = currentTrace.eval(time);
				}
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
							points.add(wrapPoints(
									it.getStateId(),
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

			Map<String, String> wrap = WebUtils
					.wrap("cmd",
							"ValueOverTime.draw",
							"data",
							WebUtils.toJson(result),
							"xLabel",
							time == null ? "Number of Animation Steps" : time
									.getCode(), "mode", mode);
			submit(wrap);
		}
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
		mode = params.get("mode")[0];
		return null;
	}

	public Object addFormula(final Map<String, String[]> params) {
		ArrayList<String> problemIds = new ArrayList<String>();
		boolean hasNoParsingErrors = true;

		for (java.util.Map.Entry<String, IEvalElement> pair : formulas
				.entrySet()) {
			if (pair.getValue() != null) {
				try {
					EvaluationResult res = currentTrace.evalCurrent(pair
							.getValue());
					if (res != null && res.hasError()) {
						problemIds.add(pair.getKey());
					} else {
						testedFormulas.put(pair.getKey(), pair.getValue());
					}
				} catch (Exception e) {
					problemIds.add(pair.getKey());
				}
			} else {
				hasNoParsingErrors = false;
			}
		}

		if (problemIds.isEmpty() && hasNoParsingErrors) {
			traceChange(currentTrace);
			return WebUtils.wrap("cmd", "ValueOverTime.formulasAdded");
		}
		return WebUtils.wrap("cmd", "ValueOverTime.hasFormulaErrors", "ids",
				WebUtils.toJson(problemIds));
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
}
