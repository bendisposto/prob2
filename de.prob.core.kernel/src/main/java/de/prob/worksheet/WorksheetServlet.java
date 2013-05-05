package de.prob.worksheet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.EvaluationException;
import de.prob.animator.domainobjects.EvaluationResult;
import de.prob.statespace.AnimationSelector;
import de.prob.webconsole.GroovyExecution;

@SuppressWarnings("serial")
@Singleton
public class WorksheetServlet extends HttpServlet {

	private GroovyExecution executor;
	private AnimationSelector animations;

	@Inject
	public WorksheetServlet(GroovyExecution executor,
			AnimationSelector animations) {
		this.executor = executor;
		this.animations = animations;
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		System.out.println("got it");
		PrintWriter out = response.getWriter();

		String language = request.getParameter("lang");
		String command = request.getParameter("input");

		Map<String, Object> resp = new HashMap<String, Object>();
		if ("groovy".equals(language)) {
			String result = executor.evaluate(command);
			resp.put("result", result);
		}

		if ("b".equals(language)) {
			try {
				ClassicalB formula = new ClassicalB(command);
				Object eval = animations.getCurrentHistory().getCurrentState()
						.eval(formula);
				resp.put("result", "<p>"+command + " is </p><p> " + eval+"</p>");
			} catch (EvaluationException e) {
				resp.put("result", "Exception while processing '" + command
						+ "'. " + e.getMessage());
				resp.put("error", true);
			}
		}

		if (resp.isEmpty()) {
			resp.put("result", "empty result");
		}

		Gson g = new Gson();

		String json = g.toJson(resp);
		out.println(json);
		out.close();
		;
	}

}
