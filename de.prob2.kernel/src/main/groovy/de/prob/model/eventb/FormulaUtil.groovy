package de.prob.model.eventb

import org.eventb.core.ast.Assignment
import org.eventb.core.ast.Expression
import org.eventb.core.ast.FormulaFactory
import org.eventb.core.ast.FreeIdentifier

import de.be4.classicalb.core.parser.node.AAssignSubstitution
import de.be4.classicalb.core.parser.node.ABecomesElementOfSubstitution
import de.be4.classicalb.core.parser.node.ABecomesSuchSubstitution
import de.prob.animator.domainobjects.EvalElementType
import de.prob.animator.domainobjects.EventB
import de.be4.classicalb.core.parser.node.Node

class FormulaUtil {

	def EventB substitute(EventB formula, Map<String, EventB> identifierMapping) {
		if (formula.getKind() == EvalElementType.ASSIGNMENT.toString()) {
			return substituteAssignment(formula, identifierMapping)
		}

		FormulaFactory ff = FormulaFactory.getInstance(formula.getTypes())
		Map<FreeIdentifier, Expression> substitutions = identifierMapping.collectEntries { k, EventB v ->
			if (v.getKind() != EvalElementType.EXPRESSION.toString()) {
				throw new IllegalArgumentException("$v is not an expression. substitutions only work with expressions")
			}
			def id = ff.makeFreeIdentifier(k, null)
			def expr = v.getRodinParsedResult().getParsedExpression()
			[id, expr]
		}
		def f = getRodinFormula(formula)
		def s = f.substituteFreeIdents(substitutions).toString()
		new EventB(s)
	}

	def EventB substituteAssignment(EventB formula, Map<String, EventB> identifierMapping) {
		assert formula.getKind() == EvalElementType.ASSIGNMENT.toString()
		Node ast = formula.getAst()
		if (ast instanceof AAssignSubstitution) {
			return substituteDeterministicAssignment(formula, identifierMapping)
		}
		if (ast instanceof ABecomesSuchSubstitution) {
			return substituteBecomeSuchThat(formula, identifierMapping)
		}
		if (ast instanceof ABecomesElementOfSubstitution) {
			return substituteBecomeElementOf(formula, identifierMapping)
		}
		// shouldn't be possible
	}

	def EventB substituteDeterministicAssignment(EventB formula, Map<String, EventB> identifierMapping) {
		String code = formula.getCode()
		assert code.contains(":=")
		String[] split = code.split(":=")
		assert split.length == 2

		def lhs = split[0].split(",").collect {
			substitute(new EventB(it, formula.getTypes()), identifierMapping).getCode()
		}.iterator().join(",")
		def rhs = split[1].split(",").collect {
			substitute(new EventB(it, formula.getTypes()), identifierMapping).getCode()
		}.iterator().join(",")
		new EventB(lhs+":="+rhs)
	}

	def EventB substituteBecomeElementOf(EventB formula, Map<String, EventB> identifierMapping) {
		String code = formula.getCode()
		assert code.contains("::")
		String[] split = code.split("::")
		assert split.length == 2

		def sub = substitute(new EventB(split[0], formula.getTypes()), identifierMapping).getCode()+"::"+substitute(new EventB(split[1], formula.getTypes()), identifierMapping).getCode()
		new EventB(sub)
	}

	def EventB substituteBecomeSuchThat(EventB formula, Map<String, EventB> identifierMapping) {
		String code = formula.getCode()
		assert code.contains(":|")
		String[] split = code.split(":\\|")
		assert split.length == 2

		def primed = identifierMapping.findAll { String x, EventB v ->
			v.getKind() == EvalElementType.EXPRESSION.toString() && v.getRodinParsedResult().getParsedExpression() instanceof FreeIdentifier
		}.collectEntries { String x, EventB v ->
			[
				x+"'",
				new EventB(v.getCode().trim()+"'")
			]
		}
		def newMapping = identifierMapping + primed

		def sub = substitute(new EventB(split[0], formula.getTypes()), newMapping).getCode()+":|"+substitute(new EventB(split[1], formula.getTypes()), newMapping).getCode()
		new EventB(sub)
	}

	def List<EventB> formulasWith(List<EventB> formulas, EventB identifier) {
		FreeIdentifier fi = getIdentifier(identifier)
		formulas.findAll { getRodinFormula(it).getFreeIdentifiers().contains(fi) }
	}

	def getRodinFormula(EventB formula) {
		if (formula.getKind() == EvalElementType.EXPRESSION.toString()) {
			return formula.getRodinParsedResult().getParsedExpression()
		}
		if (formula.getKind() == EvalElementType.ASSIGNMENT.toString()) {
			return formula.getRodinParsedResult().getParsedAssignment()
		}
		if (formula.getKind() == EvalElementType.PREDICATE.toString()) {
			return formula.getRodinParsedResult().getParsedPredicate()
		}
		// shouldn't be possible
	}

	def FreeIdentifier getIdentifier(EventB formula) {
		assert formula.getKind() == EvalElementType.EXPRESSION.toString()
		assert formula.getRodinParsedResult().getParsedExpression() instanceof FreeIdentifier
		return formula.getRodinParsedResult().getParsedExpression()
	}
}
