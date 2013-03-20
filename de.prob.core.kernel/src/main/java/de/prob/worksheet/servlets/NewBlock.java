package de.prob.worksheet.servlets;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Key;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

import de.prob.webconsole.ServletContextListener;
import de.prob.worksheet.WorksheetObjectMapper;
import de.prob.worksheet.block.impl.DefaultBlock;
import de.prob.worksheet.document.impl.WorksheetDocument;

@Singleton
public class NewBlock extends HttpServlet {
	public static Logger logger = LoggerFactory.getLogger(NewBlock.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		NewBlock.logger.trace("in:");
		doPost(req, resp);
		NewBlock.logger.trace("out:");
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		NewBlock.logger.trace("in:");
		logParameters(req);

		resp.setCharacterEncoding("UTF-8");
		// initialize session and document
		setSessionProperties(req.getSession());

		HashMap<String, Object> sessionAttributes = getSessionAttributes(
				req.getSession(), req.getParameter("worksheetSessionId"));
		WorksheetDocument doc = (WorksheetDocument) sessionAttributes
				.get("document");

		int index = doc.getBlockIndexById(req.getParameter("blockId"));

		DefaultBlock newBlock = null;
		if (!Boolean.parseBoolean(req.getParameter("before")))
			index++;

		newBlock = ServletContextListener.INJECTOR.getInstance(Key.get(
				DefaultBlock.class, Names.named(req.getParameter("type"))));
		doc.insertBlock(index, newBlock);

		// TODO add immediateEvaluation Handling

		setSessionAttributes(req.getSession(),
				req.getParameter("worksheetSessionId"), sessionAttributes);

		final WorksheetObjectMapper mapper = new WorksheetObjectMapper();
		resp.getWriter().print(
				mapper.writerWithDefaultPrettyPrinter().writeValueAsString(
						new DefaultBlock[] { newBlock }));

		NewBlock.logger.trace("out:");
		return;
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, Object> getSessionAttributes(HttpSession session,
			String wsid) {
		NewBlock.logger.trace("{} {}", session, wsid);
		HashMap<String, Object> attributes = (HashMap<String, Object>) session
				.getAttribute(wsid);
		if (attributes == null) {
			attributes = new HashMap<String, Object>();
			NewBlock.logger.debug("New 'Sub'session initialized with id  :"
					+ wsid);
		}
		NewBlock.logger.trace("{}", attributes);
		return attributes;
	}

	private void setSessionAttributes(HttpSession session, String wsid,
			HashMap<String, Object> attributes) {
		NewBlock.logger.trace("{}" + session);
		NewBlock.logger.trace("{}" + wsid);
		NewBlock.logger.trace("{}" + attributes);
		session.setAttribute(wsid, attributes);
	}

	private void setSessionProperties(HttpSession session) {
		NewBlock.logger.trace("{}", session);
		if (session.isNew()) {
			NewBlock.logger.debug("New Session initialized");
			session.setMaxInactiveInterval(-1);
		}
	}

	private void logParameters(HttpServletRequest req) {
		String[] params = { "worksheetSessionId", "id" };
		String msg = "{ ";
		for (int x = 0; x < params.length; x++) {
			if (x != 0)
				msg += " , ";
			msg += params[x] + " : " + req.getParameter(params[x]);
		}
		msg += " }";
		NewBlock.logger.debug(msg);
	}
}
