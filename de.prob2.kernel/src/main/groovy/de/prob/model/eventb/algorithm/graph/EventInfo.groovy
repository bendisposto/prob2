package de.prob.model.eventb.algorithm.graph

import org.eventb.core.ast.extension.IFormulaExtension

import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.algorithm.Statement

class EventInfo {
	Map<Integer, BranchCondition> conditions = new HashMap<Integer, BranchCondition>()
	List<Statement> actions = new ArrayList<Statement>()

	def addEdge(int pc, BranchCondition cond) {
		if (conditions[pc]) {
			BranchCondition old = conditions[pc]
			def oldc = conditions[pc].conditions
			def newc = cond.conditions
			def newconditions = oldc ? (newc ? [merge(oldc, newc)]: oldc) : newc
			def stmts = []
			stmts.addAll(old.statements)
			stmts.addAll(cond.statements)
			conditions.put(pc, new BranchCondition(newconditions, stmts, cond.getOutNode()))
		} else {
			conditions.put(pc, cond)
		}
	}

	def EventB merge(List<EventB> oldc, List<EventB> newc) {
		Set<IFormulaExtension> typeEnv = new HashSet<IFormulaExtension>()
		String oc = oldc.collect { EventB e ->
			if (!e.getTypes().isEmpty()) {
				typeEnv.addAll(e.getTypes())
			}
			e.getCode()
		}.iterator().join(" & ")
		String nc = newc.collect { EventB e ->
			if (!e.getTypes().isEmpty()) {
				typeEnv.addAll(e.getTypes())
			}
			e.getCode()
		}.iterator().join(" & ")

		return new EventB("($oc) or ($nc)", typeEnv)
	}

	def addActions(List<Statement> stmts) {
		actions.addAll(stmts)
	}

	@Override
	public String toString() {
		return "when ${conditions.toString()} then ${actions.toString()}"
	}
}
