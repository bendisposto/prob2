package de.prob.model.eventb

import org.eventb.core.ast.Assignment
import org.eventb.core.ast.Expression
import org.eventb.core.ast.FormulaFactory
import org.eventb.core.ast.FreeIdentifier
import org.eventb.core.ast.Predicate

import de.be4.classicalb.core.parser.node.AAssignSubstitution
import de.be4.classicalb.core.parser.node.ABecomesElementOfSubstitution
import de.be4.classicalb.core.parser.node.ABecomesSuchSubstitution
import de.be4.classicalb.core.parser.node.AConjunctPredicate
import de.be4.classicalb.core.parser.node.AEqualPredicate
import de.be4.classicalb.core.parser.node.AIdentifierExpression
import de.be4.classicalb.core.parser.node.Node
import de.prob.animator.domainobjects.EvalElementType
import de.prob.animator.domainobjects.EvaluationException;
import de.prob.animator.domainobjects.EventB
import de.prob.unicode.UnicodeTranslator

class FormulaUtil {

	def EventB substitute(EventB formula, Map<String, EventB> identifierMapping) {
		if (formula.kind == EvalElementType.ASSIGNMENT) {
			return substituteAssignment(formula, identifierMapping)
		}

		FormulaFactory ff = FormulaFactory.getInstance(formula.getTypes())
		Map<FreeIdentifier, Expression> substitutions = identifierMapping.collectEntries { k, EventB v ->
			if (v.kind != EvalElementType.EXPRESSION) {
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
		assert formula.kind == EvalElementType.ASSIGNMENT
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
			v.kind == EvalElementType.EXPRESSION && v.getRodinParsedResult().getParsedExpression() instanceof FreeIdentifier
		}.collectEntries { String x, EventB v ->
			[
				x+"'",
				new EventB(v.getCode().trim()+"'")
			]
		}
		def newMapping = identifierMapping + primed

		def lhs = split[0].trim().split(",").collect {
			newMapping[it.trim()]
		}.iterator().join(",")

		def sub = lhs+":|"+substitute(new EventB(split[1], formula.getTypes()), newMapping).getCode()
		new EventB(sub)
	}

	def List<EventB> formulasWith(List<EventB> formulas, EventB identifier) {
		FreeIdentifier fi = getIdentifier(identifier)
		formulas.findAll {
			getRodinFormula(it).getFreeIdentifiers().contains(fi)
		}
	}

	def getRodinFormula(EventB formula) {
		switch (formula.kind) {
			case EvalElementType.EXPRESSION:
				return formula.rodinParsedResult.parsedExpression
			
			case EvalElementType.ASSIGNMENT:
				return formula.rodinParsedResult.parsedAssignment
			
			case EvalElementType.PREDICATE:
				return formula.rodinParsedResult.parsedPredicate
			
			default:
				throw new IllegalArgumentException("Unhandled kind: " + formula.kind)
		}
	}

	def FreeIdentifier getIdentifier(EventB formula) {
		assert formula.kind == EvalElementType.EXPRESSION
		assert formula.getRodinParsedResult().getParsedExpression() instanceof FreeIdentifier
		return formula.getRodinParsedResult().getParsedExpression()
	}


	/**
	 * @param assignment for the specified var
	 * @param var variable for which the assignment should be copied
	 * @param newVar new variable which should be set to the same value as the specified var
	 * @return new assigment. After this assignment is executed, newVar = var
	 */
	def EventB copyVarAssignment(EventB assignment, String var, String newVar) {
		if (assignment.getAst() instanceof AAssignSubstitution) {
			def code = assignment.getCode()
			def split = code.split(":=")
			def slhs = split[0].split(",").collect { it.trim() }
			def srhs = split[1].split(",").collect { it.trim() }

			def newVal = [slhs, srhs].transpose().inject(null) { acc, l ->
				l[0] == var ? l[1] : acc
			}
			if (newVal == null) {
				throw new IllegalArgumentException("Could not find value for $var in assignment $assignment")
			}
			return new EventB(split[0].trim()+",$newVar"+" := "+split[1].trim()+",$newVal",assignment.getTypes())
		}
		if (assignment.getAst() instanceof ABecomesSuchSubstitution) {
			def code = assignment.getCode()
			def split = code.split(":\\|")
			def lhs = split[0].trim() + ",$newVar"
			def rhs = split[1] + " & "+"${var}' = ${newVar}'"
			return new EventB(lhs+":|"+rhs, assignment.getTypes())
		}
		if (assignment.getAst() instanceof ABecomesElementOfSubstitution) {
			def code = assignment.getCode()
			def split = code.split("::")
			assert split[0].trim() == var
			def formula = "${var},${newVar} :| ${var}' : ${split[1].trim()} & ${var}' = ${newVar}'"
			return new EventB(formula, assignment.getTypes())
		}
		throw new IllegalArgumentException(assignment+" must be of type assignment");
	}


	/**
	 * Attempts to transform formula into a deterministic assignment.
	 * @param formula (a conjunct of equivalences) to be transformed
	 * @param output identifiers that specify the output variables
	 * @param input identifiers that specify the input variables
	 * @return transformed formula
	 * @throws IllegalArgumentException if the transformation is not successful
	 */
	def List<EventB> conjunctToAssignments(EventB formula, Set<String> input, Set<String> output) {
		if (!(formula.getAst() instanceof AConjunctPredicate || formula.getAst() instanceof AEqualPredicate)) {
			throw new IllegalArgumentException("Expected conjunct predicate.")
		}
		try {
			List<EventB> split = formula.getCode().split("&").collect { new EventB(it.trim(), formula.getTypes()) }
			split.collect { EventB f ->
				if (!(f.getAst() instanceof AEqualPredicate)) {
					throw new IllegalArgumentException("Expected predicate to be conjunct of equivalences.")
				}
				def split2 = f.toUnicode().split(UnicodeTranslator.toUnicode("="))
				assert split2.length == 2
				def lhs = new EventB(split2[0], formula.getTypes()).getAst()
				if (!(lhs instanceof AIdentifierExpression)) {
					throw new IllegalArgumentException("Left hand side must be a single identifier")
				}
				def identifier = lhs.getIdentifier().get(0).getText()
				if (!(output.contains(identifier))) {
					throw new IllegalArgumentException("output ($output) must contain the identifiers ($identifier) that are defined on the left hand side")
				}
				def rf = getRodinFormula(new EventB(split2[1], formula.getTypes()))
				rf.getFreeIdentifiers().each { id ->
					if (!input.contains(id.getName())) {
						throw new IllegalArgumentException("$id is not defined as an input element")
					}
				}
				[identifier, split2[1]]
			}.collect { l ->
				new EventB("${l[0]} := ${l[1]}", formula.getTypes())
			}
		} catch(EvaluationException e) {
			throw new IllegalArgumentException("Transformation was unsuccessful")
		}
	}

	def EventB predicateToBecomeSuchThat(EventB predicate, Set<String> lhsIdentifiers) {
		if (predicate.kind != EvalElementType.PREDICATE) {
			throw new IllegalArgumentException("expected $predicate to be a predicate" )
		}
		Map<String, EventB> subMap = lhsIdentifiers.inject([:]) { acc, String id ->
			acc[id] = new EventB("${id}'", predicate.getTypes())
			acc
		}
		EventB substituted = substitute(predicate, subMap)
		new EventB(lhsIdentifiers.iterator().join(",") + " :| "+substituted.getCode(), predicate.getTypes())
	}

	def List<EventB> predicateToAssignments(EventB predicate, Set<String> input, Set<String> output) {
		List<EventB> assignments = []
		try {
			List<EventB> split = conjunctToAssignments(predicate, input, output)
			assignments.addAll(split)
		} catch(IllegalArgumentException ex) {
			assignments <<  predicateToBecomeSuchThat(predicate, output)
		}
		assignments
	}

	def EventB applyAssignment(EventB predicate, EventB assignment) {
		Predicate p = getRodinFormula(predicate)
		Assignment a = getRodinFormula(assignment)
		new EventB(p.applyAssignment(a).toString(), predicate.getTypes())
	}

	def EventB applyAssignments(EventB predicate, List<EventB> assignments) {
		assignments.inject(predicate) { p, assignment ->
			applyAssignment(p, assignment)
		}
	}
}
