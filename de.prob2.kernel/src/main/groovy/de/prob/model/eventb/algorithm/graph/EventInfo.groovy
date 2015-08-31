package de.prob.model.eventb.algorithm.graph

import de.prob.model.eventb.algorithm.Statement

class EventInfo {
	Map<Integer, BranchCondition> conditions = new HashMap<Integer, BranchCondition>()
	List<Statement> actions = new ArrayList<Statement>()

	def addEdge(int pc, BranchCondition cond) {
		if (conditions[pc]) {
			BranchCondition old = conditions[pc]
			def oldc = conditions[pc].conditions.iterator().join(" & ")
			def newc = cond.conditions.iterator().join(" & ")
			def newcondition = "($oldc) or ($newc)"
			def stmts = []
			stmts.addAll(old.statements)
			stmts.addAll(cond.statements)
			conditions.put(pc, new BranchCondition([newcondition], stmts, cond.getOutNode()))
		} else {
			conditions.put(pc, cond)
		}
	}

	def addActions(List<Statement> stmts) {
		actions.addAll(stmts)
	}

	@Override
	public String toString() {
		return "when ${conditions.toString()} then ${actions.toString()}"
	}
}
