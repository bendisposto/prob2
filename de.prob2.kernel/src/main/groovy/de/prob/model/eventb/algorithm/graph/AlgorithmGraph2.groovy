package de.prob.model.eventb.algorithm.graph;

import com.google.inject.Scopes;

import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.algorithm.Assertion
import de.prob.model.eventb.algorithm.Assignments;
import de.prob.model.eventb.algorithm.Block
import de.prob.model.eventb.algorithm.Statement
import de.prob.model.eventb.algorithm.While



public class AlgorithmGraph2 {

	def NodeNaming naming
	def Set<String> nodes = new HashSet<String>()
	def Map<String, Set<Edge>> outgoingEdges = new HashMap<String, Set<Edge>>()
	def Map<String, Set<Edge>> incomingEdges = new HashMap<String, Set<Edge>>()

	private currentscope
	private List<String> scopes = []
	private Map<String, Integer> currentPcs = [:]
	private Map<String, Map<String, Integer>> nodesToPc = [:]
	private Map<String, Statement> nameToCurrentStmt = [:]

	private Map<String, Set<Assertion>> assertions

	def AlgorithmGraph(Block algorithm) {
		AssertionEliminator e = new AssertionEliminator(algorithm)
		naming = new NodeNaming(e.algorithm)
		assertions = e.getAssertions().collectEntries { Statement stmt, Set<Assertion> a ->
			[naming.getName(stmt), a]
		}
		newScope("pc")
	}

	def newScope(String scopeName) {
		currentscope = scopeName
		currentPcs[currentscope] = 0
		scopes << currentscope
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

	def addEdge(String from, String to, List<EventB> conditions, List<String> statements, Map<String, Integer> pcInformation) {
		Edge e = new Edge(from, to, conditions, statements, pcInformation)
		outgoingEdges[addNode(from)].add(e)
		incomingEdges[addNode(to)].add(e)
		e
	}

	private Map<String, Integer> getPc(String node) {
		if (nodesToPc[node] == null) {
			Map<String, Integer> pcs = [:]
			scopes.collect { String scope ->
				if (scope == currentscope) {
					pcs[scope] = currentPcs[scope]++
				} else {
					pcs[scope] = currentPcs[scope]
				}
			}
			nodesToPc[node] = pcs
		}
		nodesToPc[node]
	}

	def addStatement(Assignments a, List<Statement> nextStmts) {
		String name = naming.getName(a)
		addNode(name)
		if (!nextStmts.isEmpty()) {
			Statement s = nextStmts.head()
			Map<String, Integer> pcs = getPc(naming.getName(s))
			def pc = pcs[currentscope]
			List<Assignments> acts = a.addAssignments("$currentscope := $pc")
			if (acts.size() != 1) {
				throw new IllegalArgumentException("To generate this algorithm, do not use variables named $pcscope in your machine")
			}
			nameToCurrentStmt[name] = acts[0]
			addEdge(name, naming.getName(s), [], [], pcs)
			addStatement(s, nextStmts.tail())
		} else {
			nameToCurrentStmt[name] = a
		}
		name
	}

	def addStatement(While w, List<Statement> nextStmts) {
		String name = naming.getName(w)
		String oldscope = currentscope
		newScope(name)

		// do something

		scopes.remove(name)
		currentscope = oldscope
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
