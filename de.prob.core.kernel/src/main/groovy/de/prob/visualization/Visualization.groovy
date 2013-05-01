package de.prob.visualization

import de.prob.webconsole.GroovyExecution

class Visualization {

	private final IVisualizationServlet serv;
	private final String sessionId;
	private final GroovyExecution g

	def Visualization(GroovyExecution g, String sessionId, IVisualizationServlet serv) {
		this.g = g
		this.sessionId = sessionId
		this.serv = serv
	}

	def apply(Transformer transformer) {
		println transformer
		serv.addUserDefinitions(sessionId, transformer)
	}

	@Override
	public String toString() {
		return sessionId
	}
}
