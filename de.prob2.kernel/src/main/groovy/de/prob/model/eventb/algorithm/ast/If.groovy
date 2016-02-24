package de.prob.model.eventb.algorithm.ast

import org.eventb.core.ast.extension.IFormulaExtension

import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.ModelGenerationException
import de.prob.model.representation.IllegalModificationException

public class If extends Statement {
	def final EventB condition
	def final EventB elseCondition
	def final Block Then
	def final Block Else

	def If(String condition, Set<IFormulaExtension> typeEnvironment=Collections.emptySet()) throws ModelGenerationException {
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
		assignments.inject(newBlock()) { Block b, String assignment ->
			b.Assign(assignment)
		}
		newIf(assignments.inject(newBlock()) { Block b, String assignment ->
			b.Assign(assignment)
		}, Else)
	}

	def If Then(Closure definition) {
		if(Then != null) {
			throw new IllegalModificationException("The Then block of this If statement has already been defined. Cannot be redefined.")
		}
		newIf(newBlock().make(definition), Else)
	}

	def If Then(Block block) {
		if(Then != null) {
			throw new IllegalModificationException("The Then block of this If statement has already been defined. Cannot be redefined.")
		}
		newIf(block, Else)
	}

	def If Else(String... assignments) {
		if(Else != null) {
			throw new IllegalModificationException("The Then block of this If statement has already been defined. Cannot be redefined.")
		}
		newIf(Then, assignments.inject(newBlock()) { Block b, String assignment ->
			b.Assign(assignment)
		})
	}

	def If Else(Closure definition) {
		if(Else != null) {
			throw new IllegalModificationException("The Then block of this If statement has already been defined. Cannot be redefined.")
		}
		newIf(Then, newBlock().make(definition))
	}

	def If Else(Block block) {
		if(Else != null) {
			throw new IllegalModificationException("The Then block of this If statement has already been defined. Cannot be redefined.")
		}
		newIf(Then, block)
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
}
