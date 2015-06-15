package de.prob.web.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.AsyncContext;

import org.parboiled.common.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.prob.animator.domainobjects.AbstractEvalResult;
import de.prob.animator.domainobjects.ComputationNotCompletedResult;
import de.prob.animator.domainobjects.EvalResult;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.annotations.OneToOne;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;
import de.prob.web.AbstractAnimationBasedView;
import de.prob.web.WebUtils;

@OneToOne
public class ValueOverTime extends AbstractAnimationBasedView {

	private class FormulaElement {
		public final String id;
		public IEvalElement formula;

		public FormulaElement(final String id, final IEvalElement formula) {
			this.id = id;
			this.formula = formula;
		}
	}

	List<FormulaElement> testedFormulas = new CopyOnWriteArrayList<ValueOverTime.FormulaElement>();
	IEvalElement time = null;

	Map<String, IEvalElement> formulas = new ConcurrentHashMap<String, IEvalElement>();

	Logger logger = LoggerFactory.getLogger(ValueOverTime.class);
	private Trace currentTrace;
	private final StateSpace stateSpace;
	private String mode = "over";

	@Inject
	public ValueOverTime(final AnimationSelector animations) {
		super(animations);
		incrementalUpdate = false;
		currentTrace = animations.getCurrentTrace();
		if (currentTrace == null) {
			stateSpace = null;
		} else {
			stateSpace = currentTrace.getStateSpace();
			animations.registerAnimationChangeListener(this);
		}
	}

	@Override
	public String html(final String clientid,
			final Map<String, String[]> parameterMap) {
		if (stateSpace == null) {
			return "<html><head><title>Value Over Time</title></head></html>";
		}
		Object scope = WebUtils.wrap("clientid", clientid, "id", UUID
				.randomUUID().toString());
		return WebUtils.render("ui/valueOverTime/index.html", scope);
	}

	@Override
	public void reload(final String client, final int lastinfo,
			final AsyncContext context) {
		sendInitMessage(context);
		if (stateSpace != null) {
			List<Object> result = new ArrayList<Object>();
			for (FormulaElement formula : testedFormulas) {
				result.add(WebUtils.wrap("id", formula.id, "formula",
						formula.formula.getCode()));
			}

			List<Object> data = calculateData();
			IEvalElement time = formulas.get("time");

			Map<String, String> wrap = WebUtils
					.wrap("cmd",
							"ValueOverTime.restorePage",
							"formulas",
							WebUtils.toJson(result),
							"time",
							time == null ? "" : time.getCode(),
							"data",
							WebUtils.toJson(data),
							"xLabel",
							time == null ? "Number of Animation Steps" : time
									.getCode(), "drawMode", mode);

			submit(wrap);
		}

	}

	@Override
	public void performTraceChange(final Trace trace) {
		if (stateSpace != null && trace != null
				&& trace.getStateSpace().equals(stateSpace)) {
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
		List<Tuple2<String, AbstractEvalResult>> timeRes = new ArrayList<Tuple2<String, AbstractEvalResult>>();
		if (time != null) {
			timeRes = currentTrace.eval(time);
		}

		for (FormulaElement pair : testedFormulas) {
			String id = pair.id;
			IEvalElement formula = pair.formula;
			if (!id.equals("time")) {
				List<Tuple2<String, AbstractEvalResult>> results = currentTrace
						.eval(formula);
				List<Object> points = new ArrayList<Object>();

				if (timeRes.isEmpty()) {
					int c = 0;
					for (Tuple2<String, AbstractEvalResult> it : results) {
						if (it.b instanceof EvalResult) {
							String val = ((EvalResult) it.b).getValue();
							points.add(wrapPoints(it.a, extractValue(val), c,
									extractType(val)));
							points.add(wrapPoints(it.a, extractValue(val),
									c + 1, extractType(val)));
						}
						c++;
					}
				} else if (timeRes.size() == results.size()) {
					for (Tuple2<String, AbstractEvalResult> it : results) {
						int index = results.indexOf(it);
						if (it.b instanceof EvalResult) {
							String val = ((EvalResult) it.b).getValue();
							String time = ((EvalResult) timeRes.get(index).b)
									.getValue();
							String timePlus = ((EvalResult) timeRes.get(index).b)
									.getValue();
							points.add(wrapPoints(it.a, extractValue(val),
									extractValue(time), extractType(val)));
							if (index < results.size() - 1) {
								points.add(wrapPoints(it.a, extractValue(val),
										extractValue(timePlus),
										extractType(val)));
							}
						}

					}
				}

				Map<String, Object> datum = new HashMap<String, Object>();
				datum.put("name", formula.getCode());
				datum.put("dataset", points);
				datum.put("id", pair.id);
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
			if ("time".equals(id)) {
				time = null;
				List<Object> data = calculateData();
				return WebUtils.wrap("cmd", "ValueOverTime.timeSet", "formula",
						"", "data", WebUtils.toJson(data), "xLabel",
						"Number of Animation Steps", "drawMode", mode);
			}

			return sendError(
					id,
					"Whoops!",
					"Could not add formula because it is invalid for this model",
					"alert-danger");
		}

		try {
			AbstractEvalResult res = currentTrace.evalCurrent(formula);
			if (res == null) {
				return sendError(
						id,
						"Warning!",
						"Could not add formula because it is not possible to assert the validity of the formula at this state in the animation",
						"");
			}
			if (res instanceof ComputationNotCompletedResult) {
				return sendError(
						id,
						"Sorry!",
						"The specified formula cannot be evaluated for this model!",
						"alert-danger");
			}
			if (!correctType(id, res)) {
				return sendError(
						id,
						"Sorry!",
						"The specified formula must be of the correct type (Integer for time expression, Integer or boolean for other formula)",
						"alert-danger");
			}

			if ("time".equals(id)) {
				time = formula;
				List<Object> data = calculateData();
				return WebUtils.wrap("cmd", "ValueOverTime.timeSet", "formula",
						time.getCode(), "data", WebUtils.toJson(data),
						"xLabel", time.getCode(), "drawMode", mode);
			}
			if (newFormula) {
				testedFormulas.add(new FormulaElement(id, formula));
				List<Object> data = calculateData();
				return WebUtils.wrap(
						"cmd",
						"ValueOverTime.formulaAdded",
						"id",
						id,
						"formula",
						formula.getCode(),
						"nextId",
						UUID.randomUUID().toString(),
						"data",
						WebUtils.toJson(data),
						"xLabel",
						time == null ? "Number of Animation Steps" : time
								.getCode(), "drawMode", mode);
			}
			for (FormulaElement f : testedFormulas) {
				if (f.id.equals(id)) {
					f.formula = formula;
				}
			}
			List<Object> data = calculateData();
			return WebUtils
					.wrap("cmd",
							"ValueOverTime.formulaRestored",
							"id",
							id,
							"formula",
							formula.getCode(),
							"data",
							WebUtils.toJson(data),
							"xLabel",
							time == null ? "Number of Animation Steps" : time
									.getCode(), "drawMode", mode);

		} catch (Exception e) {
			return sendError(
					id,
					"Whoops!",
					"Could not add formula because evaluation of the formula threw an exception of type "
							+ e.getClass().getSimpleName(), "alert-danger");
		}

	}

	private boolean correctType(final String id, final AbstractEvalResult res) {
		String value = ((EvalResult) res).getValue();
		if ((value.equals("TRUE") || value.equals("FALSE"))
				&& !"time".equals(id)) {
			return true;
		}
		try {
			Integer.parseInt(value);
			return true;
		} catch (Exception e) {
			return false;
		}
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
			IEvalElement e = stateSpace.getModel().parseFormula(f);
			formulas.put(id, e);
			return WebUtils.wrap("cmd", "ValueOverTime.parseOk", "id", id);
		} catch (Exception e) {
			formulas.put(id, null);
			return WebUtils.wrap("cmd", "ValueOverTime.parseError", "id", id);
		}
	}

	public Object removeFormula(final Map<String, String[]> params) {
		String id = params.get("id")[0];
		formulas.remove(id);
		if ("time".equals(id)) {
			time = null;
		} else {
			for (FormulaElement element : testedFormulas) {
				if (element.id.equals(id)) {
					testedFormulas.remove(element);
				}
			}
		}
		List<Object> data = calculateData();
		return WebUtils.wrap("cmd", "ValueOverTime.formulaRemoved", "id", id,
				"data", WebUtils.toJson(data), "xLabel",
				time == null ? "Number of Animation Steps" : time.getCode(),
				"drawMode", mode);
	}

	@Override
	public void animatorStatus(final boolean busy) {
		if (busy) {
			submit(WebUtils.wrap("cmd", "ValueOverTime.disable"));
		} else {
			submit(WebUtils.wrap("cmd", "ValueOverTime.enable"));
		}
	}
}
