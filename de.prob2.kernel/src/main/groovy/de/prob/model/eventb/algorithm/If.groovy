package de.prob.model.eventb.algorithm

import org.eventb.core.ast.extension.IFormulaExtension

import de.prob.animator.domainobjects.EventB
import de.prob.model.representation.IllegalModificationException

public class If extends Statement {
	def final EventB condition
	def final EventB elseCondition
	def final Block Then
	def final Block Else

	def If(String condition, Set<IFormulaExtension> typeEnvironment=Collections.emptySet()) {
		super(typeEnvironment)
		this.condition = parsePredicate(condition)
		this.elseCondition = parsePredicate("not($condition)")
		this.Then = null
		this.Else = null
	}

	private If(EventB condition, EventB elseCondition, Block Then, Block Else, Set<IFormulaExtension> typeEnvironment=Collections.emptySet()) {
		super(typeEnvironment)
		this.condition = condition
		this.elseCondition = elseCondition
		this.Then = Then
		this.Else = Else
	}

	def If newIf(Block Then, Block Else) {
		return new If(condition, elseCondition, Then, Else, typeEnvironment)
	}

	def If Then(String... assignments) {
		if(Then != null) {
			throw new IllegalModificationException("The Then block of this If statement has already been defined. Cannot be redefined.")
		}
		newIf(newBlock([
			new Assignments(assignments as List, typeEnvironment)
		]), Else)
	}

	def If Then(Closure definition) {
		if(Then != null) {
			throw new IllegalModificationException("The Then block of this If statement has already been defined. Cannot be redefined.")
		}
		newIf(newBlock().make(definition), Else)
	}

	def If Else(String... assignments) {
		if(Else != null) {
			throw new IllegalModificationException("The Then block of this If statement has already been defined. Cannot be redefined.")
		}
		newIf(Then, newBlock([
			new Assignments(assignments as List, typeEnvironment)
		]))
	}

	def If Else(Closure definition) {
		if(Else != null) {
			throw new IllegalModificationException("The Then block of this If statement has already been defined. Cannot be redefined.")
		}
		newIf(Then, newBlock().make(definition))
	}

	def If finish() {
		// makes sure that Then and Else blocks are not null!
		newIf(Then ?: newBlock(), Else ?: newBlock())
	}

	def If make(Closure definition) {
		runClosure(definition).finish()
	}

	def String toString() {
		"if (${condition.toUnicode()}):"
	}

	@Override
	public boolean equals(Object that) {
		if (that instanceof If) {
			return this.condition.getCode().equals(that.getCondition().getCode()) &&
					this.Then.equals(that.getThen()) &&
					this.Else.equals(that.getElse())
		}
		return false
	}

	@Override
	public int hashCode() {
		return this.condition.hashCode() * 7 + this.Then.hashCode() * 13 + this.Else.hashCode() * 17
	}
}
