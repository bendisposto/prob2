/**
 * 
 */
package de.prob.worksheet.servlets.eval;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.worksheet.WorksheetObjectMapper;
import de.prob.worksheet.block.IBlockData;
import de.prob.worksheet.document.impl.WorksheetDocument;
import de.prob.worksheet.evaluator.DocumentEvaluator;
import de.prob.worksheet.servlets.SubsessionServletHelper;

/**
 * @author Rene
 * 
 */
@WebServlet(urlPatterns = { "/evaluate" })
public class Evaluate extends HttpServlet {
	private static final long serialVersionUID = -6247147601671174584L;
	Logger logger = LoggerFactory.getLogger(Evaluate.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(final HttpServletRequest req,
			final HttpServletResponse resp) throws ServletException,
			IOException {
		this.doPost(req, resp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(final HttpServletRequest req,
			final HttpServletResponse resp) throws ServletException,
			IOException {
		SubsessionServletHelper.logParameters(req);

		// get session attributes
		final String subSessionId = req.getParameter("worksheetSessionId");
		HashMap<String, Object> attributes = null;
		attributes = SubsessionServletHelper.getSessionAttributes(
				req.getSession(), subSessionId);
		if (attributes == null) {
			resp.setStatus(HttpServletResponse.SC_GONE);
			resp.getWriter()
					.write("The Session doesn't exist any more! <br/> Refresh the Browser or open a new Editor.");
			return;
		}

		// get the document
		WorksheetDocument doc = SubsessionServletHelper.getDocument(attributes);

		logger.debug("in ContextHistory={}", doc.history);

		final int index = doc.getBlockIndexById(req.getParameter("id"));

		DocumentEvaluator docEvaluator = new DocumentEvaluator();

		docEvaluator.evaluateFrom(doc, index);

		SubsessionServletHelper.setSessionAttributes(req.getSession(),
				subSessionId, attributes);
		logger.debug("in ContextHistory={}", doc.history);

		final WorksheetObjectMapper mapper = new WorksheetObjectMapper();

		IBlockData[] blocks = doc.getBlocksFrom(index);
		resp.setCharacterEncoding("UTF-8");
		resp.setStatus(HttpServletResponse.SC_ACCEPTED);
		resp.getWriter().print(
				mapper.writerWithDefaultPrettyPrinter().writeValueAsString(
						blocks));
		return;

	}
}
