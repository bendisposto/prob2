package de.prob.model.represenation

import spock.lang.Specification
import de.prob.model.representation.DependencyGraph
import de.prob.model.representation.DependencyGraph.ERefType

class DependencyGraphTest extends Specification {
	private DependencyGraph g

	def setup() {
		g = new DependencyGraph()
			.addEdge("M0", "ctx0", ERefType.SEES)
			.addEdge("ctx1", "ctx0", ERefType.EXTENDS)
			.addEdge("ctx2", "ctx1", ERefType.EXTENDS)
			.addEdge("M1", "ctx1", ERefType.SEES)
			.addEdge("M1", "ctx2", ERefType.SEES)
			.addEdge("M1", "M0", ERefType.REFINES)
			.addEdge("M2", "M0", ERefType.REFINES)
			.addEdge("M2", "ctx2", ERefType.REFINES)
	}

	def "right number of edges in the graph"() {
		expect:
		g.edges.size() == 8
	}

	def "right number of vertices in the graph"() {
		expect:
		g.vertices.size() == 6
	}

	def "right outgoing edges from M1"() {
		when:
		final edges = g.getOutEdges("M1")

		then:
		edges.every {e -> e.from.elementName == "M1"}
		edges.collect {e -> e.to.elementName}.sort() == ["M0", "ctx1", "ctx2"]
		edges.findAll {e -> e.relationship == ERefType.REFINES}.collect {e -> e.to.elementName} == ["M0"]
		edges.findAll {e -> e.relationship == ERefType.SEES}.collect {e -> e.to.elementName}.sort() == ["ctx1", "ctx2"]
	}

	def "right incoming edges for M0"() {
		when:
		final edges = g.getIncomingEdges("M0")

		then:
		edges.size() == 2
		edges.every {e -> e.relationship == ERefType.REFINES}
		edges.every {e -> e.to.elementName == "M0"}
		edges.collect {e -> e.from.elementName}.sort() == ["M1", "M2"]
	}
}
