package de.prob.worksheet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@SuppressWarnings("serial")
@Singleton
public class WorksheetServlet extends HttpServlet {

	private int session_counter = 0;

	private final Map<String, WorkSheet> worksheets = new HashMap<String, WorkSheet>();

	private Provider<WorkSheet> wsp;

	@Inject
	public WorksheetServlet(Provider<WorkSheet> wsp) {
		this.wsp = wsp;
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String session = request.getParameter("session");

		if (session == null || session.equals("null") || session.isEmpty()
				|| session.equals("undefined")) {
			session = createSession();
		}

		WorkSheet ws = getSheet(session);
		ws.doGet(session, request, response);

	}

	private String createSession() {
		String session;
		int c = session_counter++;
		session = String.valueOf(c);
		return session;
	}

	private WorkSheet getSheet(String session) {
		WorkSheet ws = worksheets.get(session);
		if (ws == null) {
			ws = wsp.get();
			worksheets.put(session, ws);
		}
		return ws;
	}

}
