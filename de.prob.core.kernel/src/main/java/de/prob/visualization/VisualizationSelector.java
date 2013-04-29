package de.prob.visualization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServlet;

import com.google.common.base.Joiner;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.webconsole.GroovyExecution;

@Singleton
public class VisualizationSelector {

	private static int ctr = 0;

	Map<IVisualizationServlet, String> servlets = new HashMap<IVisualizationServlet, String>();
	Map<IVisualizationServlet, List<String>> servletToId = new HashMap<IVisualizationServlet, List<String>>();
	Map<String, IVisualizationServlet> idToServlet = new HashMap<String, IVisualizationServlet>();
	private final GroovyExecution e;

	@Inject
	public VisualizationSelector(final GroovyExecution e) {
		this.e = e;
		e.getBindings().setVariable("viz", this);
	}

	public void registerServlet(final IVisualizationServlet servlet,
			final String name) {
		servlets.put(servlet, name);
		servletToId.put(servlet, new ArrayList<String>());
	}

	public void registerSession(final String sessionId,
			final IVisualizationServlet servlet) {
		e.getBindings().setVariable("viz" + ctr++,
				new Visualization(e, sessionId, servlet));
		idToServlet.put(sessionId, servlet);
		servletToId.get(servlet).add(sessionId);
	}

	public IVisualizationServlet getServletForSessionId(final String sessId) {
		return idToServlet.get(sessId);
	}

	public List<String> getSessionIdsForServlet(final HttpServlet s) {
		return servletToId.get(s);
	}

	@Override
	public String toString() {
		StringBuffer b = new StringBuffer();
		Set<Entry<IVisualizationServlet, List<String>>> entries = servletToId
				.entrySet();
		for (Entry<IVisualizationServlet, List<String>> entry : entries) {
			b.append("Open Sessions for " + servlets.get(entry.getKey())
					+ ":\n");
			List<String> ids = entry.getValue();
			if (ids.isEmpty()) {
				b.append("NONE\n");
			} else {
				b.append(Joiner.on(",").join(ids) + "\n");
			}
			b.append("\n");
		}

		return b.toString();
	}

	public Transformer selectAll(final String selection) {
		return new Transformer(selection);
	}

	public void addToSession(final String session, final Transformer selection) {
		idToServlet.get(session).addUserDefinitions(session, selection);
	}

}
