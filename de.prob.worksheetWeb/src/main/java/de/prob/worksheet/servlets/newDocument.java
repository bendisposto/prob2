package de.prob.worksheet.servlets;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.worksheet.WorksheetDocument;
import de.prob.worksheet.block.JavascriptBlock;

@Singleton
public class newDocument extends HttpServlet {
	private static final long	serialVersionUID	= -8455020946701964097L;

	@Inject
	public newDocument() {
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		resp.setStatus(HttpServletResponse.SC_ACCEPTED);
		final String wsid = req.getParameter("worksheetSessionId");
		HashMap<String, Object> sessionAttributes = (HashMap<String, Object>) req.getSession().getAttribute(wsid);
		if(sessionAttributes==null)
			sessionAttributes=new HashMap<String, Object>();
		WorksheetDocument doc = (WorksheetDocument) sessionAttributes.get("document");

		if (req.getSession().isNew()) {
			req.getSession().setMaxInactiveInterval(-1);
		}
		if (doc == null) {
			doc = new WorksheetDocument();
			doc.setId("ui-id-1");

			sessionAttributes = new HashMap<String, Object>();
			sessionAttributes.put("document", doc);

			final JavascriptBlock block1 = new JavascriptBlock();
			doc.insertBlock(0, block1);
		}
		
		req.getSession().setAttribute(wsid, sessionAttributes);

		final ObjectMapper mapper = new ObjectMapper();
		resp.getWriter().print(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(doc));
	}
}
