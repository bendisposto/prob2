package de.prob.model.eventb.algorithm.graph

import static org.junit.Assert.*
import spock.lang.Specification
import de.prob.model.eventb.algorithm.Assertion
import de.prob.model.eventb.algorithm.Assignments
import de.prob.model.eventb.algorithm.Block

public class GraphConstructionTest extends Specification {

	def AlgorithmGraph graph(Closure cls) {
		Block b = new Block().make(cls)
		AlgorithmToGraph g = new AlgorithmToGraph(b)
		return new AlgorithmGraph(g.getNode())
	}

	def node(AlgorithmGraph graph, int index, Class clazz) {
		assert graph.nodes[index].getClass() == clazz
		graph.nodes[index]
	}

	def edge(AlgorithmGraph graph, int from, int to, String rep) {
		return graph.edges[from].contains(new Edge(from, to, rep))
	}

	def "empty has one node"() {
		when:
		def graph = graph({})

		then:
		node(graph, 0, Node).getStatements().isEmpty()
		node(graph, 1, Nil)
		edge(graph, 0, 1, "-->")
	}

	def "one assignment block has one node"() {
		when:
		def graph = graph({ Assign("x := 1", "y := 1") })

		then:
		node(graph, 0, Node).getStatements() == [
			new Assignments(["x := 1", "y := 1"])
		]
		node(graph, 1, Node).getStatements().isEmpty()
		node(graph, 2, Nil)
		edge(graph, 0,1,"-->")
		edge(graph, 1,2,"-->")
	}

	def "one assert block has one node"() {
		when:
		def graph = graph({ Assert("x = 1") })

		then:
		node(graph, 0, Node).getStatements() == [
			new Assertion("x = 1")
		]
		node(graph, 1, Node).getStatements() == []
		node(graph, 2, Nil)
		edge(graph, 0, 1,"-->")
		edge(graph, 1, 2,"-->")
	}

	def "an empty if has several nodes"() {
		when:
		def graph = graph({
			If("x < 4") {
				Then {}
				Else {}
			}
		})

		then:
		node(graph, 0, Branch).condition == "x < 4"
		node(graph, 1, Node).getStatements().isEmpty()
		node(graph, 2, Graft)
		node(graph, 3, Node).getStatements().isEmpty()
		node(graph, 4, Nil)
		node(graph, 5, Node).getStatements().isEmpty()

		edge(graph, 0, 1, "-- x < 4 -->")
		edge(graph, 0, 5, "-- not(x < 4) -->")
		edge(graph, 1, 2, "-->")
		edge(graph, 5, 2, "-->")
		edge(graph, 2, 3, "-->")
		edge(graph, 3, 4, "-->")
	}

	def "an empty while also has several nodes"() {
		when:
		def graph = graph({
			While("x < 4") {
			}
		})

		then:
		node(graph, 0, Branch).condition == "x < 4"
		node(graph, 1, Node).getStatements().isEmpty()
		node(graph, 2, Node).getStatements().isEmpty()
		node(graph, 3, Nil)

		edge(graph, 0, 1, "-- x < 4 -->")
		edge(graph, 1, 0, "-->")
		edge(graph, 0, 2, "-- not(x < 4) -->")
		edge(graph, 2, 3, "-->")
	}

	def "euclid"() {
		when:
		def graph = graph({
			While("u /= 0") {
				If ("u < v") { Then("u := v", "v := u") }
				Assign("u := u - v")
			}
			Assert("u|->m|->n : IsGCD")
		})

		then:
		node(graph, 0, Branch).condition == "u /= 0"
		node(graph, 1, Branch).condition == "u < v"
		node(graph, 2, Node).getStatements() == [
			new Assignments(["u := v", "v := u"])
		]
		node(graph, 3, Node).getStatements().isEmpty()
		node(graph, 4, Graft)
		node(graph, 5, Node).getStatements() == [
			new Assignments(["u := u - v"])
		]
		node(graph, 6, Node).getStatements().isEmpty()
		node(graph, 7, Node).getStatements().isEmpty()
		node(graph, 8, Node).getStatements() == [
			new Assertion("u|->m|->n : IsGCD")
		]
		node(graph, 9, Node).getStatements().isEmpty()
		node(graph, 10, Nil)

		edge(graph, 0, 1, "-- u /= 0 -->")
		edge(graph, 0, 8, "-- not(u /= 0) -->")
		edge(graph, 1, 2, "-- u < v -->")
		edge(graph, 1, 7, "-- not(u < v) -->")
		edge(graph, 2, 3, "-->")
		edge(graph, 3, 4, "-->")
		edge(graph, 4, 5, "-->")
		edge(graph, 5, 6, "-->")
		edge(graph, 6, 0, "-->")
		edge(graph, 7, 4, "-->")
		edge(graph, 8, 9, "-->")
		edge(graph, 9, 10, "-->")
	}

	def "optimized euclid"() {
		when:
		def b = new Block().make {
			While("u /= 0") {
				If ("u < v") { Then("u := v", "v := u") }
				Assign("u := u - v")
			}
			Assert("u|->m|->n : IsGCD")
		}
		def n = new GraphOptimizer(new AlgorithmToGraph(b).getNode())
		def graph = new AlgorithmGraph(n.getAlgorithm())

		then:
		print graph
		node(graph, 0, CombinedBranch).branches.collect { it.getConditions() } == [
			["u /= 0", "u < v"],
			["u /= 0", "not(u < v)"],
			["not(u /= 0)"]
		]
		node(graph, 1, Node).getStatements() == [
			new Assignments(["u := v", "v := u"])
		]
		node(graph, 2, Node).getStatements() == [
			new Assignments(["u := u - v"])
		]
		node(graph, 3, Node).getStatements() == [
			new Assertion("u|->m|->n : IsGCD")
		]
		node(graph, 4, Nil)

		edge(graph, 0, 1, "--[u /= 0, u < v]-->")
		edge(graph, 0, 2, "--[u /= 0, not(u < v)]-->")
		edge(graph, 2, 0, "-->")
		edge(graph, 0, 3, "--[not(u /= 0)]-->")
		edge(graph, 3, 4, "-->")
	}
}
