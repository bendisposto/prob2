package de.prob.model.eventb.algorithm.graph;

import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.algorithm.Assertion
import de.prob.model.eventb.algorithm.Assignments;
import de.prob.model.eventb.algorithm.Block
import de.prob.model.eventb.algorithm.Statement



public class AlgorithmGraph2 {

	def NodeNaming naming
	def Set<String> nodes = new HashSet<String>()
	def Map<String, Set<Edge>> outgoingEdges = new HashMap<String, Set<Edge>>()
	def Map<String, Set<Edge>> incomingEdges = new HashMap<String, Set<Edge>>()

	private String pcscope
	private Map<String, Integer> currentPcs = [:]
	private Map<String, Map<String, Integer>> nodesToPc = [:]
	private Map<String, Statement> nameToCurrentStmt = [:]

	def AlgorithmGraph(Block algorithm) {
		naming = new NodeNaming(algorithm)
		pcscope = "pc"
		currentPcs[pcscope] = 0
	}

	def addNode(String node) {
		nodes.add(node)
		if (outgoingEdges[node] == null) {
			outgoingEdges[node] = new HashSet<Edge>()
		}
		if (incomingEdges[node] == null) {
			incomingEdges[node] = new HashSet<Edge>()
		}
		node
	}

	def addEdge(String from, String to, List<EventB> conditions, List<String> statements) {
		Edge e = new Edge(from, to, conditions, statements)
		outgoingEdges[addNode(from)].add(e)
		incomingEdges[addNode(to)].add(e)
		e
	}

	private getPc(String node) {
		if (nodesToPc[node] == null) {
			nodesToPc[node] = [:]
		}
		if (nodesToPc[node][pcscope] == null) {
			if (currentPcs[pcscope] == null) {
				currentPcs[pcscope] = 0
			}
			nodesToPc[node][pcscope] = currentPcs[pcscope]++
		}
		nodesToPc[node][pcscope]
	}

	def addStatement(Assignments a, List<Statement> nextStmts) {
		String name = naming.getName(a)
		addNode(name)
		if (!nextStmts.isEmpty()) {
			Statement s = nextStmts.head()
			def pc = getPc(naming.getName(s))
			List<Assignments> acts = a.addAssignments("$pcscope := $pc")
			if (acts.size() != 1) {
				throw new IllegalArgumentException("To generate this algorithm, do not use variables named $pcscope in your machine")
			}
			nameToCurrentStmt[name] = acts[0]
			EventB condition = a.parsePredicate("$pcscope = $pc")
			addEdge(name, naming.getName(s), [condition], [])
			addStatement(s, nextStmts.tail())
		} else {
			nameToCurrentStmt[name] = a
		}
		name
	}

	@Override
	public String toString() {
		return nodes.toString()
	}

	public int size() {
		return nodes.size()
	}
}
