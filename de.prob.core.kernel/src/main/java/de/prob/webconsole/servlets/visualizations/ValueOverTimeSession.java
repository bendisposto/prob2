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

import de.prob.animator.domainobjects.EvaluationResult;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.History;
import de.prob.statespace.IHistoryChangeListener;
import de.prob.statespace.StateSpace;
import de.prob.visualization.Transformer;

public class ValueOverTimeSession implements ISessionServlet,
		IHistoryChangeListener, IVisualizationServlet {
	private final StateSpace stateSpace;
	private final IEvalElement formula;
	private int count = 0;
	private History currentHistory;
	private List<Object> result;
	private final List<Transformer> styling = new ArrayList<Transformer>();

	public ValueOverTimeSession(final IEvalElement formula,
			final AnimationSelector animations) {
		currentHistory = animations.getCurrentHistory();
		animations.registerHistoryChangeListener(this);
		stateSpace = currentHistory.getStatespace();
		this.formula = formula;
		result = calculate();
	}

	@Override
	public void doGet(final HttpServletRequest req,
			final HttpServletResponse resp) throws IOException {
		PrintWriter out = resp.getWriter();
		Map<String, Object> response = new HashMap<String, Object>();

		Boolean getFormula = Boolean.valueOf(req.getParameter("getFormula"));

		if (getFormula) {
			response.put("data", result);
		}
		response.put("count", count);
		response.put("styling", styling);

		Gson g = new Gson();

		String json = g.toJson(response);
		out.println(json);
		out.close();
	}

	public List<Object> calculate() {
		List<Object> result = new ArrayList<Object>();
		if (currentHistory != null && currentHistory.getS() == stateSpace) {
			List<EvaluationResult> results = currentHistory.eval(formula);

			int c = 0;
			for (EvaluationResult it : results) {
				result.add(new Element(it.getStateId(), c, it.getValue()));
				result.add(new Element(it.getStateId(), c + 1, it.getValue()));
				c++;
			}

			count++;
		}
		return result;
	}

	private class Element {
		public final String stateid;
		public final Integer value;
		public final Integer t;

		public Element(final String string, final int t, final Object value) {
			stateid = string;
			this.t = t;
			this.value = Integer.parseInt((String) value);
		}
	}

	@Override
	public void historyChange(final History history) {
		currentHistory = history;

		result = calculate();
	}

	@Override
	public void apply(final Transformer styling) {
		this.styling.add(styling);
		count++;
	}

}
