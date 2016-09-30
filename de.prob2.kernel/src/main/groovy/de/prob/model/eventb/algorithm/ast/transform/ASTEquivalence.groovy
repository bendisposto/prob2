package de.prob.model.eventb.algorithm.ast.transform

import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.algorithm.ast.Assertion
import de.prob.model.eventb.algorithm.ast.Assignment
import de.prob.model.eventb.algorithm.ast.Block
import de.prob.model.eventb.algorithm.ast.If
import de.prob.model.eventb.algorithm.ast.Statement
import de.prob.model.eventb.algorithm.ast.While

class ASTEquivalence {
	def assertEqual(EventB input, EventB expected) {
		assert input.getCode() == expected.getCode()
	}

	def assertStmtEqual(While input, While output) {
		assertEqual(input.condition, output.condition)
		assertEqual(input.block, output.block)
	}

	def assertStmtEqual(If input, If output) {
		assertEqual(input.condition, output.condition)
		assertEqual(input.Then, output.Then)
		assertEqual(input.Else, output.Else)
	}

	def assertStmtEqual(Assignment input, Assignment output) {
		assertEqual(input.assignment, output.assignment)
	}

	def assertStmtEqual(Assertion input, Assertion output) {
		assertEqual(input.assertion, output.assertion)
	}

	def assertEqual(Statement input, Statement expected) {
		assert input.getClass() == expected.getClass()
		assertStmtEqual(input, expected)
	}

	def assertEqual(Block input, Block expected) {
		assert input.statements.size() == expected.statements.size()
		[
			input.statements,
			expected.statements
		].transpose().each { l ->
			assertEqual(l[0], l[1])
		}
	}

	def isEqual(Block input, Block expected) {
		try {
			assertEqual(input, expected)
		} catch(Exception e) {
			return false
		}
		return true
	}
}
