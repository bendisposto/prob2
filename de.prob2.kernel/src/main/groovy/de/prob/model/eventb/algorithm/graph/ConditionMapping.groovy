package de.prob.model.eventb.algorithm.graph

import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.algorithm.Assertion
import de.prob.model.eventb.algorithm.Assignments
import de.prob.model.eventb.algorithm.Block
import de.prob.model.eventb.algorithm.If
import de.prob.model.eventb.algorithm.While

class ConditionMapping {
	Map<EventB, String> mapping = [:]
	NodeNaming naming

	def ConditionMapping(NodeNaming naming, Block algorithm) {
		this.naming = naming
		mapConditions(algorithm)
	}

	def mapConditions(Block b) {
		b.each { mapConditions(it) }
	}

	def mapConditions(While w) {
		mapping[w.condition] = naming.getName(w)
		mapping[w.notCondition] = naming.getName(w)
		mapConditions(w.block)
	}

	def mapConditions(If i) {
		mapping[i.condition] = naming.getName(i)
		mapping[i.elseCondition] = naming.getName(i)
		mapConditions(i.Then)
		mapConditions(i.Else)
	}

	def mapConditions(Assertion a) {}

	def mapConditions(Assignments a) {}

	def String getMapping(EventB condition) {
		mapping[condition]
	}
}
