package de.prob.model.classicalb

import org.jgrapht.graph.DirectedMultigraph

import de.be4.classicalb.core.parser.BParser
import de.be4.classicalb.core.parser.analysis.prolog.RecursiveMachineLoader
import de.be4.classicalb.core.parser.node.Start
import de.prob.model.classicalb.RefType.ERefType
import spock.lang.Specification


class ClassicalBModelTest extends Specification {

	def File model
	def ClassicalBModel c
	def BParser bparser
	def Start ast
	def DependencyWalker dw
	def DirectedMultigraph<ClassicalBMachine,RefType> graph

	def setup() {
		model = new File(System.getProperties().get("user.dir")+"/groovyTests/machines/references/Foo.mch")
		c = new ClassicalBModel(null)
		bparser = new BParser();

		ast = bparser.parseFile(model,false)

		def RecursiveMachineLoader rml = new RecursiveMachineLoader(model.getParent(),bparser.getContentProvider())
		rml.loadAllMachines(model, ast, null, bparser.getDefinitions(),
				bparser.getPragmas());


		c.initialize(ast, rml)

		graph = c.graph
	}

	def "all the machine names are now saved in the graph"() {
		setup:
		def machine = new ClassicalBMachine(null)

		when:
		machine.setName(a)

		then:
		graph.vertexSet().contains(machine) == b

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

	def "the user can access machines from the string name"() {
		setup:
		def machine = new ClassicalBMachine(null)

		when:
		machine.setName(a)

		then:
		c.getVertex(a) == machine

		where:
		a <<[
			"A",
			"E",
			"A",
			"Foo",
			"B",
			"C",
			"Bar"
		]
	}

	def "when a machine is not in the graph, null is returned"() {
		expect:
		c.getVertex(b) == null

		where:
		b <<[
			"I",
			"am",
			"not",
			"in",
			"the",
			"graph"
		]
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
		"Blah"| "A"
		"A"	| 	"Blah"
	}

	def "getVertex and getMachine are the same"() {
		expect:
		c.getVertex(a) == c.getMachine(a)

		where:
		a <<[
			"Foo",
			"Bar",
			"A",
			"Z",
			"NOT"
		]
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
		"Blah"|	"A"
		"A"	|	"Blah"
	}
}
