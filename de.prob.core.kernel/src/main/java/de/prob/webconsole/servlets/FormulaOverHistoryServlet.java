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

@SuppressWarnings("serial")
@Singleton
public class FormulaOverHistoryServlet extends HttpServlet {

	private AnimationSelector animations;

	@Inject
	public FormulaOverHistoryServlet(AnimationSelector animations) {
		this.animations = animations;
	}

	@SuppressWarnings("unused")
	private static class Element {
		public final String stateid;
		public final Integer value;
		public final Integer t;

		public Element(String string, int t, Object value) {
			this.stateid = string;
			this.t = t;
			this.value = Integer.parseInt((String) value);
		}
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		List<Object> result = new ArrayList<Object>();
		PrintWriter out = res.getWriter();

		String formula = req.getParameter("formula");

		History history = animations.getCurrentHistory().first();

		int c = 0;

		while (history.canGoForward()) {
			String id = history.getCurrentState().getId();
			if (id.equals("root")) {
				// skip root state
			} else {
				EvaluationResult e = history.eval(formula);
				result.add(new Element(id, c++, e.value));
			}
			history = history.forward();
		}

		Gson g = new Gson();
		String json = g.toJson(result);
		out.println(json);
		out.close();

	}

}
