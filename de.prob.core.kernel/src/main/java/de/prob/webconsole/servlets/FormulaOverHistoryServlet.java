package de.prob.webconsole.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.animator.domainobjects.EvaluationResult;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.History;
import de.prob.statespace.StateId;

@SuppressWarnings("serial")
@Singleton
public class FormulaOverHistoryServlet extends HttpServlet {

	private final AnimationSelector animations;

	@Inject
	public FormulaOverHistoryServlet(final AnimationSelector animations) {
		this.animations = animations;
	}

	@SuppressWarnings("unused")
	private static class Element {
		public final String stateid;
		public final Integer value;
		public final Integer t;

		public Element(final String string, final int t, final Object value) {
			this.stateid = string;
			this.t = t;
			this.value = Integer.parseInt((String) value);
		}
	}

	@Override
	public void doGet(final HttpServletRequest req,
			final HttpServletResponse res) throws ServletException, IOException {
		List<Object> result = new ArrayList<Object>();
		PrintWriter out = res.getWriter();

		String formula = req.getParameter("formula");

		int c = 0;
		
		History history = animations.getCurrentHistory();
		if(history != null) {
			List<EvaluationResult> calc = history.eval(formula);
			for (EvaluationResult evaluationResult : calc) {
				result.add(new Element(evaluationResult.getStateId(),c++,evaluationResult.value));
			}			
		}

		Gson g = new Gson();
		String json = g.toJson(result);
		out.println(json);
		out.close();

	}

}
