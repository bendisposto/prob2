package de.prob.model.eventb.algorithm

import static de.prob.unicode.UnicodeTranslator.toUnicode
import de.prob.model.representation.IllegalModificationException

class If extends Statement {
	def final String condition
	def final Block Then
	def final Block Else

	def If(String condition) {
		this.condition = condition
	}

	def If(String condition, Block Then, Block Else) {
		this.condition = condition
		this.Then = Then
		this.Else = Else
	}

	def If Then(String... assignments) {
		if(Then != null) {
			throw new IllegalModificationException("The Then block of this If statement has already been defined. Cannot be redefined.")
		}
		new If(condition, new Block([
			new Assignments(assignments as List)
		]), Else)
	}

	def If Then(Closure definition) {
		if(Then != null) {
			throw new IllegalModificationException("The Then block of this If statement has already been defined. Cannot be redefined.")
		}
		new If(condition, new Block().make(definition), Else)
	}

	def If Else(String... assignments) {
		if(Else != null) {
			throw new IllegalModificationException("The Then block of this If statement has already been defined. Cannot be redefined.")
		}
		new If(condition, Then, new Block([
			new Assignments(assignments as List)
		]))
	}

	def If Else(Closure definition) {
		if(Else != null) {
			throw new IllegalModificationException("The Then block of this If statement has already been defined. Cannot be redefined.")
		}
		new If(condition, Then, new Block().make(definition))
	}

	def If make(Closure definition) {
		If i = runClosure(definition)
		i = i.Then ? i : new If(i.condition, new Block(), i.Else)
		i.Else ? i : new If(i.condition, i.Then, new Block())
	}

	def String toString() {
		"if (${toUnicode(condition)}):"
	}
}
