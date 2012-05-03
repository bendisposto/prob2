package de.prob.model.classicalb

import de.be4.classicalb.core.parser.BParser
import de.be4.classicalb.core.parser.node.Start
import de.prob.model.classicalb.RefType.ERefType
import edu.uci.ics.jung.graph.DirectedSparseMultigraph
import spock.lang.Specification

class ClassicalBModelTest extends Specification {
	def String model = '''REFINEMENT Foo 
							REFINES Bar 
    						SEES A 
    						USES B 
							INCLUDES C 
							EXTENDS D 
							IMPORTS E END'''


	def ClassicalBModel c
	def BParser bparser
	def Start ast
	def DirectedSparseMultigraph<String, RefType> graph
	def DependencyWalker dw

	def setup() {

		c = new ClassicalBModel(null)
		bparser = new BParser();
		ast = bparser.parse(model,false)
		graph = new DirectedSparseMultigraph<String, RefType>();
		dw = new DependencyWalker("Foo",  graph)
		ast.apply(dw)
	}

	def "all the machine names are now saved in the graph"() {
		expect:
		graph.getVertices().containsAll([
			"D",
			"E",
			"A",
			"Foo",
			"B",
			"C",
			"Bar"
		])
	}

	def "the correct RefType connects the different machines"() {
		setup:
		def c = [
			graph.findEdge("Foo", "Bar").relationship,
			graph.findEdge("Foo", "A").relationship,
			graph.findEdge("Foo", "B").relationship,
			graph.findEdge("Foo", "C").relationship,
			graph.findEdge("Foo", "D").relationship,
			graph.findEdge("Foo", "E").relationship
		]

		expect:
		c == [
			ERefType.REFINES,
			ERefType.SEES,
			ERefType.USES,
			ERefType.INCLUDES,
			ERefType.INCLUDES,
			ERefType.IMPORTS
		]
	}
}
