package de.prob.model.eventb.algorithm.ast.transform

import de.be4.classicalb.core.parser.node.ABecomesElementOfSubstitution
import de.be4.classicalb.core.parser.node.ABecomesSuchSubstitution
import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.FormulaUtil
import de.prob.model.eventb.algorithm.ast.While
import de.prob.util.Tuple2

public class VariantAssertion {
	final FormulaUtil fuu = new FormulaUtil()
	final List<EventB> conditions
	final EventB variantCondition
	final EventB positive
	final String name
	final While stmt

	def VariantAssertion(String name, While stmt) {
		if (!stmt.variant) {
			throw new IllegalArgumentException("Variant must be defined for while statement $stmt!")
		}
		this.name = name
		this.stmt = stmt
		def var = name + "_variant"
		this.conditions = []
		this.variantCondition = new EventB(stmt.variant.getCode() +" < "+var, stmt.variant.getTypes())
		this.positive = new EventB(var + " > 0", stmt.variant.getTypes())
	}

	def VariantAssertion(String name, While stmt, List<EventB> conditions, EventB variantCondition, EventB positive) {
		this.name = name
		this.stmt = stmt
		this.conditions = conditions
		this.variantCondition = variantCondition
		this.positive = positive
	}

	private VariantAssertion applyAssignment(EventB assignment) {
		if (assignment.getAst() instanceof ABecomesSuchSubstitution ||
		assignment.getAst() instanceof ABecomesElementOfSubstitution) {
			def v = new VariantAssertion(name, stmt)
			return new VariantAssertion(name, stmt, conditions, v.variantCondition, v.positive)
		}
		return new VariantAssertion(name, stmt, conditions, fuu.applyAssignment(variantCondition, assignment), fuu.applyAssignment(positive, assignment))
	}

	private VariantAssertion addCondition(EventB condition) {
		return new VariantAssertion(name, stmt, [condition]+conditions, variantCondition, positive)
	}

	@Override
	public boolean equals(Object that) {
		if (that.is(this)) {
			return true
		}
		if (that instanceof VariantAssertion) {
			if (that.conditions.size() != this.conditions.size()) {
				return false
			}
			[
				that.conditions,
				this.conditions
			].transpose().each { EventB c1, EventB c2 ->
				if (fuu.getRodinFormula(c1) != fuu.getRodinFormula(c2)) {
					return false
				}
			}
			return fuu.getRodinFormula(that.variantCondition) == fuu.getRodinFormula(this.variantCondition) &&
			fuu.getRodinFormula(that.positive) == fuu.getRodinFormula(this.positive)
		}
		return false
	}

	@Override
	public int hashCode() {
		return toString().hashCode()
	}

	@Override
	public String toString() {
		def f = variantCondition.getCode() + " & " + positive.getCode()
		if (!conditions) {
			return f
		}
		conditions.iterator().join(" & ") + " => ("+ f + ")"
	}
}
