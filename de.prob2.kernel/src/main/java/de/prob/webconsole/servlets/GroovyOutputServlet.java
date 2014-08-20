package de.prob.webconsole.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.webconsole.OutputBuffer;

/**
 * This servlet takes a line from the web interface and evaluates it using
 * Groovy. The Groovy interpreter does not remember import statements, i.e., the
 * input 'import foo.Bar; x = new Bar' will work, but spliting it into two
 * separate lines won't. We thus collect any import statement and prefix every
 * command with all the imports.
 * 
 * @author bendisposto
 * 
 */
@SuppressWarnings("serial")
@Singleton
public class GroovyOutputServlet extends HttpServlet {

	private final Logger logger = LoggerFactory
			.getLogger(GroovyOutputServlet.class);
	private final OutputBuffer sideeffects;

	@Inject
	public GroovyOutputServlet(final OutputBuffer sideeffects) {
		this.sideeffects = sideeffects;
	}

	@Override
	public void doGet(final HttpServletRequest req,
			final HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		String input = req.getParameter("since");
		int pos = 0;
		try {
			pos = Integer.parseInt(input);
		} catch (NumberFormatException e) {
			String msg = "Illegal parameter '" + input
					+ "'. Was expecting an integer but received " + input;
			logger.error(msg);
		}

		String json = sideeffects.getTextAsJSon(pos);
		out.println(json);
		out.close();
	}
}