package de.prob.model.classicalb

import java.nio.file.Paths

import de.be4.classicalb.core.parser.BParser
import de.be4.classicalb.core.parser.analysis.prolog.RecursiveMachineLoader
import de.prob.model.representation.DependencyGraph.ERefType

import spock.lang.Specification

class ClassicalBModelTest extends Specification {
	private ClassicalBModel model

	def setup() {
		final modelFile = Paths.get("groovyTests", "machines", "references", "Foo.mch").toFile()
		model = new ClassicalBModel(null)
		final bparser = new BParser()
		final ast = bparser.parseFile(modelFile,false)
		final rml = new RecursiveMachineLoader(modelFile.parent, bparser.contentProvider)
		rml.loadAllMachines(modelFile, ast, bparser.definitions)
		model = model.create(ast, rml, modelFile, bparser)
	}

	def "all the machine names are now saved in the graph"() {
		expect:
		(a in model.graph.vertices) == b

		where:
		a     | b
		"A"   | true
		"E"   | true
		"A"   | true
		"Foo" | true
		"B"   | true
		"C"   | true
		"Bar" | true
		"Baz" | false
	}

	def "the correct RefType connects the different machines"() {
		expect:
		model.getEdge(a, b) == etype

		where:
		a     | b     | etype
		"Foo" | "Bar" | ERefType.REFINES
		"Foo" | "A"   | ERefType.SEES
		"Foo" | "B"   | ERefType.USES
		"Foo" | "C"   | ERefType.INCLUDES
		"Foo" | "D"   | ERefType.INCLUDES
		"Foo" | "E"   | ERefType.IMPORTS
	}

	def "If an edge is not in the graph, null is returned"() {
		expect:
		model.getEdge(a, b) == null

		where:
		a   | b
		"A" | "B"
		"B" | "C"
	}

	def "If a vertex is not in the graph, an IllegalArgumentException is thrown"() {
		when:
		model.getEdge(a, b)

		then:
		final IllegalArgumentException e = thrown()
		e.message.contains("is not in graph")

		where:
		a      | b
		"Blah" | "A"
		"A"    | "Blah"
	}

	def "getRelationship and getEdge are the same"() {
		expect:
		model.getRelationship(a, b) == model.getEdge(a, b)

		where:
		a     | b
		"Foo" | "Bar"
		"Foo" | "A"
		"Foo" | "B"
		"Foo" | "C"
		"Foo" | "D"
		"Foo" | "E"
		"A"   | "B"
		"B"   | "C"
	}
}
