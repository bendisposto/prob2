package de.prob.model.eventb.algorithm.graph;

import com.github.krukow.clj_lang.PersistentVector;

import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.algorithm.ast.IAssignment
import de.prob.model.eventb.algorithm.ast.If;
import de.prob.model.eventb.algorithm.ast.Statement;
import de.prob.model.eventb.algorithm.ast.While;
import de.prob.util.Tuple2;


public class Edge {
	def Statement from
	def Statement to
	def PersistentVector<Tuple2<Statement, EventB>> conditions
	def IAssignment assignment

	def Edge(Statement from, Statement to, PersistentVector<Tuple2<Statement, EventB>> conditions) {
		this.from = from
		this.to = to
		this.conditions = conditions
		if (to instanceof IAssignment) {
			this.assignment = assignment
		}
	}

	@Override
	public boolean equals(Object that) {
		if (that instanceof Edge) {
			return from.equals(that.getFrom()) &&
					to.equals(that.getTo()) &&
					conditions.equals(that.getConditions())
		}
		return false
	}

	@Override
	public int hashCode() {
		return from.hashCode() * 7 + to.hashCode() * 13 + conditions.hashCode() * 17;
	}

	@Override
	public String toString() {
		return conditions ? "--${conditions.collect { it.getSecond() }}-->" : "-->"
	}

	public String getName(NodeNaming n) {
		def names = conditions.collect { getEventName(n, it.getFirst(), it.getSecond()) }
		if (assignment) {
			names << n.getName(assignment)
		}
		names.iterator().join("_")
	}

	private String getEventName(NodeNaming n, While s, EventB condition) {
		def name = n.getName(s)
		if (condition == s.condition) {
			return "enter_$name"
		}
		if (condition == s.notCondition) {
			return "exit_$name"
		}
		return "unknown_branch_$name"
	}

	private String getEventName(NodeNaming n, If s, EventB condition) {
		def name = n.getName(s)
		if (condition == s.condition) {
			return "${name}_then"
		}
		if (condition == s.elseCondition) {
			return "${name}_else"
		}
		return "unknown_branch_$name"
	}
}
