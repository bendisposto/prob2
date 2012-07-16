package de.prob.webconsole.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.webconsole.GroovyExecution;

@SuppressWarnings("serial")
@Singleton
public class CompletionServlet extends HttpServlet {

	private final GroovyExecution executor;

	@Inject
	public CompletionServlet(GroovyExecution executor) {
		this.executor = executor;
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		String input = req.getParameter("input");

		ArrayList<String> completions = computeCompletions(input);

		Gson g = new Gson();
		String json = g.toJson(completions);
		out.println(json);
		out.close();
	}

	private ArrayList<String> computeCompletions(String input) {
		ArrayList<String> result = new ArrayList<String>();
		result.add(input + " ");
		return result;
	}

}