package de.prob.webconsole.servlets.visualizations;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.prob.animator.domainobjects.CSP;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.EvalElementFactory;
import de.prob.animator.domainobjects.EvaluationResult;
import de.prob.animator.domainobjects.EventB;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.scripting.CSPModel;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.Trace;
import de.prob.statespace.IAnimationChangeListener;
import de.prob.statespace.StateSpace;
import de.prob.visualization.AnimationProperties;
import de.prob.visualization.Transformer;

public class ValueOverTimeSession implements ISessionServlet,
		IAnimationChangeListener, IVisualizationServlet {

	private final String vizType;
	private final StateSpace stateSpace;
	private final List<IEvalElement> formulas = new ArrayList<IEvalElement>();
	private int count = 0;
	private Trace currentTrace;
	private List<Object> datasets = new ArrayList<Object>();
	private final List<Transformer> styling = new ArrayList<Transformer>();
	private final AnimationProperties properties;
	private final String saveFile;
	private final String sessionId;
	private final IEvalElement time;
	private String mode;

	public ValueOverTimeSession(final String sessionId,
			final IEvalElement formula, final IEvalElement time,
			final AnimationSelector animations,
			final AnimationProperties properties) {
		this.sessionId = sessionId;
		this.time = time;
		this.properties = properties;
		currentTrace = animations.getCurrentTrace();
		animations.registerAnimationChangeListener(this);
		stateSpace = currentTrace.getStateSpace();
		formulas.add(formula);
		vizType = formula.getClass().getSimpleName();
		datasets = calculate();
		saveFile = properties.getPropFileFromModelFile(stateSpace.getModel()
				.getModelFile().getAbsolutePath());
		mode = "over";
		properties.setProperty(saveFile, sessionId, serialize());
	}

	public ValueOverTimeSession(final String sessionId, final String json,
			final AnimationSelector animations,
			final AnimationProperties properties,
			final EvalElementFactory deserializer) {
		this.sessionId = sessionId;
		this.properties = properties;
		currentTrace = animations.getCurrentTrace();
		animations.registerAnimationChangeListener(this);
		stateSpace = currentTrace.getStateSpace();
		JsonParser parser = new JsonParser();
		JsonElement parsed = parser.parse(json);
		if (parsed != null) {
			JsonObject asJson = parsed.getAsJsonObject();
			JsonElement timeString = asJson.get("time");
			if (timeString != null) {
				time = deserializer.deserialize(timeString.getAsString());
			} else {
				time = null;
			}

			JsonElement f = asJson.get("formula");
			if (f != null) {
				JsonArray asJsonArray = f.getAsJsonArray();
				for (JsonElement jsonElement : asJsonArray) {
					String string = jsonElement.getAsString();
					if (string != null) {
						formulas.add(deserializer.deserialize(string));
					}
				}
			}

			JsonElement m = asJson.get("mode");
			if (m != null) {
				mode = m.getAsString();
			}
		} else {
			time = null;
		}
		vizType = formulas.get(0).getClass().getSimpleName();
		datasets = calculate();
		saveFile = properties.getPropFileFromModelFile(stateSpace.getModel()
				.getModelFile().getAbsolutePath());
		properties.setProperty(saveFile, sessionId, serialize());

	}

	@Override
	public void doGet(final HttpServletRequest req,
			final HttpServletResponse resp) throws IOException {
		PrintWriter out = resp.getWriter();
		Map<String, Object> response;

		if (req.getParameter("cmd") != null) {
			doCommand(req);
			response = new HashMap<String, Object>();
		} else {
			response = doNormalResponse(req);
		}

		Gson g = new Gson();

		String json = g.toJson(response);
		out.println(json);
		out.close();
	}

	public void doCommand(final HttpServletRequest req) {
		String command = req.getParameter("cmd");
		String param = req.getParameter("param");

		if (command.equals("add_formula")) {
			if (param != null) {
				if (vizType.equals("ClassicalB")) {
					formulas.add(new ClassicalB(param));
				} else if (vizType.equals("EventB")) {
					formulas.add(new EventB(param));
				} else if (vizType.equals("CSP")) {
					formulas.add(new CSP(param, (CSPModel) stateSpace
							.getModel()));
				}
			}
			datasets = calculate();
			properties.setProperty(saveFile, sessionId, serialize());
		} else if (command.equals("mode_change")) {
			if (param != null) {
				mode = param;
				properties.setProperty(saveFile, sessionId, serialize());
			}
		}

	}

	public Map<String, Object> doNormalResponse(final HttpServletRequest req) {
		Map<String, Object> response = new HashMap<String, Object>();

		Boolean getFormula = Boolean.valueOf(req.getParameter("getFormula"));

		if (getFormula) {
			response.put("data", datasets);
			response.put("xLabel", time == null ? "Number of Animation Steps"
					: time.getCode());
		}
		response.put("count", count);
		response.put("styling", styling);
		response.put("mode", mode);

		return response;
	}

	public List<Object> calculate() {
		List<Object> result = new ArrayList<Object>();
		if (currentTrace != null && currentTrace.getStateSpace() == stateSpace) {
			List<EvaluationResult> timeRes = new ArrayList<EvaluationResult>();
			if (time != null) {
				timeRes = currentTrace.eval(time);
			}

			for (IEvalElement formula : formulas) {

				List<EvaluationResult> results = currentTrace.eval(formula);
				List<Object> points = new ArrayList<Object>();

				if (timeRes.isEmpty()) {
					int c = 0;
					for (EvaluationResult it : results) {
						points.add(new Element(it.getStateId(), c + "", it
								.getValue()));
						points.add(new Element(it.getStateId(), (c + 1) + "",
								it.getValue()));
						c++;
					}
				} else if (timeRes.size() == results.size()) {
					for (EvaluationResult it : results) {
						int index = results.indexOf(it);
						points.add(new Element(it.getStateId(), timeRes.get(
								index).getValue(), it.getValue()));
						if (index < results.size() - 1) {
							points.add(new Element(it.getStateId(), timeRes
									.get(index + 1).getValue(), it.getValue()));
						}

					}
				}

				Map<String, Object> datum = new HashMap<String, Object>();
				datum.put("name", formula.getCode());
				datum.put("dataset", points);
				result.add(datum);
			}
		}
		count++;
		return result;
	}

	private class Element {
		public final String stateid;
		public final Integer value;
		public final Integer t;
		public final String type;

		public Element(final String string, final String t, final Object value) {
			stateid = string;
			this.t = Integer.parseInt(t);
			if (value.equals("TRUE")) {
				this.value = 1;
				type = "BOOL";
			} else if (value.equals("FALSE")) {
				this.value = 0;
				type = "BOOL";
			} else {
				this.value = Integer.parseInt((String) value);
				type = "INT";
			}

		}
	}

	@Override
	public void traceChange(final Trace trace) {
		currentTrace = trace;

		datasets = calculate();
	}

	@Override
	public void apply(final Transformer styling) {
		this.styling.add(styling);
		count++;
	}

	public String serialize() {
		Map<String, Object> serialized = new HashMap<String, Object>();
		if (time != null) {
			serialized.put("time", time.serialized());
		}

		List<String> f = new ArrayList<String>();
		for (IEvalElement e : formulas) {
			f.add(e.serialized());
		}
		serialized.put("formula", f);
		serialized.put("mode", mode);

		Gson g = new Gson();

		return g.toJson(serialized);
	}

}
