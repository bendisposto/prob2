package de.prob.model.eventb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import de.be4.classicalb.core.parser.node.AAssignSubstitution;
import de.be4.classicalb.core.parser.node.ABecomesElementOfSubstitution;
import de.be4.classicalb.core.parser.node.ABecomesSuchSubstitution;
import de.be4.classicalb.core.parser.node.AConjunctPredicate;
import de.be4.classicalb.core.parser.node.AEqualPredicate;
import de.be4.classicalb.core.parser.node.AIdentifierExpression;
import de.be4.classicalb.core.parser.node.Node;

import de.prob.animator.domainobjects.EvalElementType;
import de.prob.animator.domainobjects.EvaluationException;
import de.prob.animator.domainobjects.EventB;
import de.prob.unicode.UnicodeTranslator;

import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;

public class FormulaUtil {
	public EventB substitute(final EventB formula, final Map<String, EventB> identifierMapping) {
		if (formula.getKind().equals(EvalElementType.ASSIGNMENT)) {
			return substituteAssignment(formula, identifierMapping);
		}

		final FormulaFactory ff = FormulaFactory.getInstance(formula.getTypes());
		final Map<FreeIdentifier, Expression> substitutions = new HashMap<>();
		identifierMapping.forEach((k, v) -> {
			if (!v.getKind().equals(EvalElementType.EXPRESSION)) {
				throw new IllegalArgumentException(v + " is not an expression. substitutions only work with expressions");
			}
			final FreeIdentifier id = ff.makeFreeIdentifier(k, null);
			final Expression expr = v.getRodinParsedResult().getParsedExpression();
			substitutions.put(id, expr);
		});
		return new EventB(getRodinFormula(formula).substituteFreeIdents(substitutions).toString());
	}

	public EventB substituteAssignment(final EventB formula, final Map<String, EventB> identifierMapping) {
		assert formula.getKind().equals(EvalElementType.ASSIGNMENT);
		final Node ast = formula.getAst();
		if (ast instanceof AAssignSubstitution) {
			return substituteDeterministicAssignment(formula, identifierMapping);
		}
		if (ast instanceof ABecomesSuchSubstitution) {
			return substituteBecomeSuchThat(formula, identifierMapping);
		}
		if (ast instanceof ABecomesElementOfSubstitution) {
			return substituteBecomeElementOf(formula, identifierMapping);
		}
		throw new AssertionError("shouldn't be possible");
	}

	public EventB substituteDeterministicAssignment(final EventB formula, final Map<String, EventB> identifierMapping) {
		final String code = formula.getCode();
		assert code.contains(":=");
		final String[] split = code.split(":=");
		assert split.length == 2;

		final String lhs = Arrays.stream(split[0].split(","))
			.map(it -> substitute(new EventB(it, formula.getTypes()), identifierMapping).getCode())
			.collect(Collectors.joining(","));
		final String rhs = Arrays.stream(split[1].split(","))
			.map(it -> substitute(new EventB(it, formula.getTypes()), identifierMapping).getCode())
			.collect(Collectors.joining(","));
		return new EventB(lhs + ":=" + rhs);
	}

	public EventB substituteBecomeElementOf(final EventB formula, final Map<String, EventB> identifierMapping) {
		final String code = formula.getCode();
		assert code.contains("::");
		final String[] split = code.split("::");
		assert split.length == 2;

		final String sub = substitute(new EventB(split[0], formula.getTypes()), identifierMapping).getCode() + "::" + substitute(new EventB(split[1], formula.getTypes()), identifierMapping).getCode();
		return new EventB(sub);
	}

	public EventB substituteBecomeSuchThat(final EventB formula, final Map<String, EventB> identifierMapping) {
		final String code = formula.getCode();
		assert code.contains(":|");
		final String[] split = code.split(":\\|");
		assert split.length == 2;
		
		final Map<String, EventB> newMapping = new HashMap<>(identifierMapping);
		identifierMapping.forEach((x, v) -> {
			if (v.getKind().equals(EvalElementType.EXPRESSION) && v.getRodinParsedResult().getParsedExpression() instanceof FreeIdentifier) {
				newMapping.put(x + '\'', new EventB(v.getCode().trim() + '\''));
			}
		});

		final String lhs = Arrays.stream(split[0].trim().split(","))
			.map(it -> newMapping.get(it.trim()).toString())
			.collect(Collectors.joining(","));
		return new EventB(lhs + ":|" + substitute(new EventB(split[1], formula.getTypes()), newMapping).getCode());
	}

	public List<EventB> formulasWith(final List<EventB> formulas, final EventB identifier) {
		final FreeIdentifier fi = getIdentifier(identifier);
		return formulas.stream()
			.filter(it -> Arrays.asList(getRodinFormula(it).getFreeIdentifiers()).contains(fi))
			.collect(Collectors.toList());
	}
	
	public Formula<? extends Formula<?>> getRodinFormula(final EventB formula) {
		switch (formula.getKind()) {
			case EXPRESSION:
				return formula.getRodinParsedResult().getParsedExpression();
			
			case ASSIGNMENT:
				return formula.getRodinParsedResult().getParsedAssignment();
			
			case PREDICATE:
				return formula.getRodinParsedResult().getParsedPredicate();
			
			default:
				throw new IllegalArgumentException("Unhandled kind: " + formula.getKind());
		}
	}

	public FreeIdentifier getIdentifier(final EventB formula) {
		assert formula.getKind().equals(EvalElementType.EXPRESSION);
		assert formula.getRodinParsedResult().getParsedExpression() instanceof FreeIdentifier;
		return (FreeIdentifier)formula.getRodinParsedResult().getParsedExpression();
	}


	/**
	 * @param assignment for the specified var
	 * @param var variable for which the assignment should be copied
	 * @param newVar new variable which should be set to the same value as the specified var
	 * @return new assigment. After this assignment is executed, newVar = var
	 */
	public EventB copyVarAssignment(final EventB assignment, final String var, final String newVar) {
		if (assignment.getAst() instanceof AAssignSubstitution) {
			final String code = assignment.getCode();
			final String[] split = code.split(":=");
			final List<String> slhs = Arrays.stream(split[0].split(","))
				.map(String::trim)
				.collect(Collectors.toList());
			final List<String> srhs = Arrays.stream(split[1].split(","))
				.map(String::trim)
				.collect(Collectors.toList());

			assert slhs.size() == srhs.size();
			String newVal = null;
			for (int i = 0; i < slhs.size(); i++) {
				if (slhs.get(i).equals(var)) {
					newVal = srhs.get(i);
				}
			}
			if (newVal == null) {
				throw new IllegalArgumentException("Could not find value for " + var + " in assignment " + assignment);
			}
			return new EventB(split[0].trim() + ',' + newVar + " := " + split[1].trim() + ',' + newVal, assignment.getTypes());
		}
		if (assignment.getAst() instanceof ABecomesSuchSubstitution) {
			final String code = assignment.getCode();
			final String[] split = code.split(":\\|");
			final String lhs = split[0].trim() + ',' + newVar;
			final String rhs = split[1] + " & " + var + "\' = " + newVar + '\'';
			return new EventB(lhs + ":|" + rhs, assignment.getTypes());
		}
		if (assignment.getAst() instanceof ABecomesElementOfSubstitution) {
			final String code = assignment.getCode();
			final String[] split = code.split("::");
			assert split[0].trim().equals(var);
			final String formula = var + ',' + newVar + " :| " + var + "\' : " + split[1].trim() + " & " + var + "\' = " + newVar + '\'';
			return new EventB(formula, assignment.getTypes());
		}
		throw new IllegalArgumentException(assignment + " must be of type assignment");
	}


	/**
	 * Attempts to transform formula into a deterministic assignment.
	 * @param formula (a conjunct of equivalences) to be transformed
	 * @param output identifiers that specify the output variables
	 * @param input identifiers that specify the input variables
	 * @return transformed formula
	 * @throws IllegalArgumentException if the transformation is not successful
	 */
	public List<EventB> conjunctToAssignments(final EventB formula, final Set<String> input, final Set<String> output) {
		if (!(formula.getAst() instanceof AConjunctPredicate || formula.getAst() instanceof AEqualPredicate)) {
			throw new IllegalArgumentException("Expected conjunct predicate.");
		}
		try {
			return Arrays.stream(formula.getCode().split("&")).map(code -> {
				final EventB f = new EventB(code.trim(), formula.getTypes());
				if (!(f.getAst() instanceof AEqualPredicate)) {
					throw new IllegalArgumentException("Expected predicate to be conjunct of equivalences.");
				}
				final String[] split2 = f.toUnicode().split(UnicodeTranslator.toUnicode("="));
				assert split2.length == 2;
				final Node lhs = new EventB(split2[0], formula.getTypes()).getAst();
				if (!(lhs instanceof AIdentifierExpression)) {
					throw new IllegalArgumentException("Left hand side must be a single identifier");
				}
				final String identifier = ((AIdentifierExpression)lhs).getIdentifier().get(0).getText();
				if (!output.contains(identifier)) {
					throw new IllegalArgumentException("output (" + output + ") must contain the identifiers (" + identifier + ") that are defined on the left hand side");
				}
				for (FreeIdentifier id : getRodinFormula(new EventB(split2[1], formula.getTypes())).getFreeIdentifiers()) {
					if (!input.contains(id.getName())) {
						throw new IllegalArgumentException(id + " is not defined as an input element");
					}
				}
				return new EventB(identifier + " := " + split2[1], formula.getTypes());
			}).collect(Collectors.toList());
		} catch (EvaluationException e) {
			throw new IllegalArgumentException("Transformation was unsuccessful", e);
		}
	}

	public EventB predicateToBecomeSuchThat(final EventB predicate, final Set<String> lhsIdentifiers) {
		if (!predicate.getKind().equals(EvalElementType.PREDICATE)) {
			throw new IllegalArgumentException("expected " + predicate + " to be a predicate");
		}
		final Map<String, EventB> subMap = new LinkedHashMap<>();
		for (String id : lhsIdentifiers) {
			subMap.put(id, new EventB(id + '\'', predicate.getTypes()));
		}
		final EventB substituted = substitute(predicate, subMap);
		return new EventB(String.join(",", lhsIdentifiers) + " :| " + substituted.getCode(), predicate.getTypes());
	}

	public List<EventB> predicateToAssignments(final EventB predicate, final Set<String> input, final Set<String> output) {
		final List<EventB> assignments = new ArrayList<>();
		try {
			assignments.addAll(conjunctToAssignments(predicate, input, output));
		} catch (IllegalArgumentException ex) {
			assignments.add(predicateToBecomeSuchThat(predicate, output));
		}
		return assignments;
	}

	public EventB applyAssignment(final EventB predicate, final EventB assignment) {
		final Predicate p = (Predicate)getRodinFormula(predicate);
		final BecomesEqualTo a = (BecomesEqualTo)getRodinFormula(assignment);
		return new EventB(p.applyAssignment(a).toString(), predicate.getTypes());
	}

	public EventB applyAssignments(final EventB predicate, final List<EventB> assignments) {
		EventB p = predicate;
		for (final EventB assignment : assignments) {
			p = applyAssignment(p, assignment);
		}
		return p;
	}
}
