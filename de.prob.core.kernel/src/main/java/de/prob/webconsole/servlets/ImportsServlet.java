package de.prob.webconsole.servlets;

import java.io.IOException;
import java.io.PrintWriter;

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
public class ImportsServlet extends HttpServlet {

	private final GroovyExecution executor;

	private String[] defaultImports = { "java.lang.*", "java.util.*",
			"java.net.*", "java.io.*", "java.math.BigInteger",
			"java.math.BigDecimal", "groovy.lang.*", "groovy.util.*" };

	@Inject
	public ImportsServlet(GroovyExecution executor) {
		this.executor = executor;
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		PrintWriter out = res.getWriter();

		String[] imports = executor.getImports();
		String[] r = new String[imports.length + 8];

		System.arraycopy(defaultImports, 0, r, 0, defaultImports.length);
		System.arraycopy(imports, 0, r, defaultImports.length, imports.length);

		Gson g = new Gson();
		String json = g.toJson(r);
		out.println(json);
		out.close();
	}

}