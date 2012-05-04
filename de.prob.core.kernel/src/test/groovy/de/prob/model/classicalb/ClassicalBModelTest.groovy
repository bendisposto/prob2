package de.prob.model.classicalb

import de.be4.classicalb.core.parser.BParser
import de.be4.classicalb.core.parser.analysis.prolog.RecursiveMachineLoader
import de.be4.classicalb.core.parser.node.Start
import de.prob.model.classicalb.RefType.ERefType
import edu.uci.ics.jung.graph.DirectedSparseMultigraph
import spock.lang.Ignore;
import spock.lang.Specification


@Ignore
class ClassicalBModelTest extends Specification {

	def File model
	def ClassicalBModel c
	def BParser bparser
	def Start ast
	def DependencyWalker dw
	def DirectedSparseMultigraph<ClassicalBMachine,RefType> graph

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
		graph.getVertices().contains(machine) == b

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
		setup:
		def machine1 = new ClassicalBMachine(null)
		def machine2 = new ClassicalBMachine(null)

		expect:

		when:
		machine1.setName(a)
		machine2.setName(b)

		then:
		graph.findEdge(machine1,machine2).relationship == etype

		where:
		a	|	b	| 	etype
		"Foo"| "Bar"|	ERefType.REFINES
		"Foo"| "A"	|	ERefType.SEES
		"Foo"| "B"	|	ERefType.USES
		"Foo"| "C"	|	ERefType.INCLUDES
		"Foo"| "D"	|	ERefType.INCLUDES
		"Foo"| "E"	|	ERefType.IMPORTS
	}
}
