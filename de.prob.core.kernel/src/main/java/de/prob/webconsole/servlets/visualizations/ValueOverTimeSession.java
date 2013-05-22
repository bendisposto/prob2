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
import com.google.gson.JsonParser;

import de.prob.animator.domainobjects.CSP;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.EvalElementFactory;
import de.prob.animator.domainobjects.EvaluationResult;
import de.prob.animator.domainobjects.EventB;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.scripting.CSPModel;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.History;
import de.prob.statespace.IHistoryChangeListener;
import de.prob.statespace.StateSpace;
import de.prob.visualization.AnimationProperties;
import de.prob.visualization.Transformer;

public class ValueOverTimeSession implements ISessionServlet,
		IHistoryChangeListener, IVisualizationServlet {

	private final String vizType;
	private final StateSpace stateSpace;
	private final List<IEvalElement> formulas = new ArrayList<IEvalElement>();
	private int count = 0;
	private History currentHistory;
	private List<Object> datasets = new ArrayList<Object>();
	private final List<Transformer> styling = new ArrayList<Transformer>();
	private final AnimationProperties properties;
	private final String saveFile;
	private final String sessionId;

	public ValueOverTimeSession(final String sessionId,
			final IEvalElement formula, final AnimationSelector animations,
			final AnimationProperties properties) {
		this.sessionId = sessionId;
		this.properties = properties;
		currentHistory = animations.getCurrentHistory();
		animations.registerHistoryChangeListener(this);
		stateSpace = currentHistory.getStatespace();
		formulas.add(formula);
		vizType = formula.getClass().getSimpleName();
		datasets = calculate();
		saveFile = properties.getPropFileFromModelFile(stateSpace.getModel()
				.getModelFile().getAbsolutePath());
		properties.setProperty(saveFile, sessionId, serialize());
	}

	public ValueOverTimeSession(final String sessionId, final String json,
			final AnimationSelector animations,
			final AnimationProperties properties,
			final EvalElementFactory deserializer) {
		this.sessionId = sessionId;
		this.properties = properties;
		currentHistory = animations.getCurrentHistory();
		animations.registerHistoryChangeListener(this);
		stateSpace = currentHistory.getStatespace();
		JsonParser parser = new JsonParser();
		JsonElement parsed = parser.parse(json);
		if (parsed != null) {
			JsonArray asJsonArray = parsed.getAsJsonArray();
			for (JsonElement jsonElement : asJsonArray) {
				String string = jsonElement.getAsString();
				if (string != null) {
					formulas.add(deserializer.deserialize(string));
				}
			}
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
		String formula = req.getParameter("param");
		if (formula != null) {
			if (vizType.equals("ClassicalB")) {
				formulas.add(new ClassicalB(formula));
			} else if (vizType.equals("EventB")) {
				formulas.add(new EventB(formula));
			} else if (vizType.equals("CSP")) {
				formulas.add(new CSP(formula, (CSPModel) stateSpace.getModel()));
			}
		}
		datasets = calculate();
		properties.setProperty(saveFile, sessionId, serialize());
	}

	public Map<String, Object> doNormalResponse(final HttpServletRequest req) {
		Map<String, Object> response = new HashMap<String, Object>();

		Boolean getFormula = Boolean.valueOf(req.getParameter("getFormula"));

		if (getFormula) {
			response.put("data", datasets);
		}
		response.put("count", count);
		response.put("styling", styling);

		return response;
	}

	public List<Object> calculate() {
		List<Object> result = new ArrayList<Object>();
		if (currentHistory != null && currentHistory.getS() == stateSpace) {
			for (IEvalElement formula : formulas) {

				List<EvaluationResult> results = currentHistory.eval(formula);
				List<Object> points = new ArrayList<Object>();

				int c = 0;
				for (EvaluationResult it : results) {
					points.add(new Element(it.getStateId(), c, it.getValue()));
					points.add(new Element(it.getStateId(), c + 1, it
							.getValue()));
					c++;
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

		public Element(final String string, final int t, final Object value) {
			stateid = string;
			this.t = t;
			if (value.equals("TRUE")) {
				this.value = 1;
			} else if (value.equals("FALSE")) {
				this.value = 0;
			} else {
				this.value = Integer.parseInt((String) value);
			}

		}
	}

	@Override
	public void historyChange(final History history) {
		currentHistory = history;

		datasets = calculate();
	}

	@Override
	public void apply(final Transformer styling) {
		this.styling.add(styling);
		count++;
	}

	public String serialize() {
		List<String> serialized = new ArrayList<String>();
		for (IEvalElement e : formulas) {
			serialized.add(e.serialized());
		}

		Gson g = new Gson();

		return g.toJson(serialized);
	}

}
