package de.prob.model.classicalb

import static org.mockito.Mockito.*
import spock.lang.Specification
import de.be4.classicalb.core.parser.BParser
import de.be4.classicalb.core.parser.analysis.prolog.RecursiveMachineLoader
import de.be4.classicalb.core.parser.node.Start
import de.prob.model.representation.DependencyGraph
import de.prob.model.representation.DependencyGraph.ERefType

class ClassicalBModelTest extends Specification {

	def File model
	def ClassicalBModel c
	def BParser bparser
	def Start ast
	def DependencyWalker dw
	def DependencyGraph graph

	def setup() {
		model = new File(System.getProperties().get("user.dir")+"/groovyTests/machines/references/Foo.mch")
		c = new ClassicalBModel(null);
		bparser = new BParser();
		ast = bparser.parseFile(model,false)
		def RecursiveMachineLoader rml = new RecursiveMachineLoader(model.getParent(),bparser.getContentProvider())
		rml.loadAllMachines(model, ast, null, bparser.getDefinitions(),
				);
		c = c.create(ast, rml, model, bparser)
		graph = c.getGraph()
	}

	def "all the machine names are now saved in the graph"() {
		expect:
		graph.getVertices().contains(a) == b
		where:
		a  	| b
		"A" | true
		"E" | true
		"A"	| true
		"Foo"|true
		"B"	| true
		"C"	| true
		"Bar"|true
		"Baz"|false
	}

	def "the correct RefType connects the different machines"() {
		expect:
		c.getEdge(a, b) == etype
		where:
		a	|	b	| 	etype
		"Foo"| "Bar"|	ERefType.REFINES
		"Foo"| "A"	|	ERefType.SEES
		"Foo"| "B"	|	ERefType.USES
		"Foo"| "C"	|	ERefType.INCLUDES
		"Foo"| "D"	|	ERefType.INCLUDES
		"Foo"| "E"	|	ERefType.IMPORTS
	}

	def "If an edge is not in the graph, null is returned"() {
		expect:
		c.getEdge(a, b) == null
		where:
		a	|	b
		"A"	|	"B"
		"B"	|	"C"
	}

	def "If a vertex is not in the graph, an IllegalArgumentException is thrown"() {
		when:
		c.getEdge(a, b)
		then:
		IllegalArgumentException e = thrown()
		e.getMessage().contains("is not in graph")
		where:
		a 		|	b
		"Blah"	|	"A"
		"A"		| 	"Blah"
	}

	def "getRelationship and getEdge are the same"() {
		expect:
		c.getRelationship(a,b) == c.getEdge(a,b)
		where:
		a	|	b
		"Foo"| "Bar"
		"Foo"| "A"
		"Foo"| "B"
		"Foo"| "C"
		"Foo"| "D"
		"Foo"| "E"
		"A"	|	"B"
		"B"	| 	"C"
	}
}
