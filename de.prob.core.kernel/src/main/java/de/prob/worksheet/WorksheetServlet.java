package de.prob.worksheet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.webconsole.GroovyExecution;
import de.prob.webconsole.OutputBuffer;

@SuppressWarnings("serial")
@Singleton
public class WorksheetServlet extends HttpServlet {

	private GroovyExecution executor;

	@Inject
	public WorksheetServlet(GroovyExecution executor, OutputBuffer sideeffects) {
		this.executor = executor;
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
			// resp.put("result", e.getCause());
			// resp.put("error", true);
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
