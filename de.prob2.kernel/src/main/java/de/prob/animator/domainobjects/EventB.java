package de.prob.animator.domainobjects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import de.be4.classicalb.core.parser.analysis.prolog.ASTProlog;
import de.be4.classicalb.core.parser.node.*;

import de.prob.animator.command.EvaluateFormulaCommand;
import de.prob.animator.command.EvaluationCommand;
import de.prob.formula.TranslationVisitor;
import de.prob.model.representation.FormulaUUID;
import de.prob.model.representation.IFormulaUUID;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.statespace.State;
import de.prob.translator.TranslatingVisitor;
import de.prob.translator.types.BObject;
import de.prob.unicode.UnicodeTranslator;

import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extension.IFormulaExtension;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Representation of an Event-B formula
 *
 * @author joy
 *
 */
public class EventB extends AbstractEvalElement implements IBEvalElement {

	Logger logger = LoggerFactory.getLogger(EventB.class);
	private final FormulaUUID uuid = new FormulaUUID();

	private EvalElementType kind;
	private Node ast = null;

	private final Set<IFormulaExtension> types;

	/**
	 * @param code
	 *            - The String which is a representation of the desired Event-B
	 *            formula
	 */
	public EventB(final String code) {
		this(code, Collections.<IFormulaExtension> emptySet());
	}

	public EventB(final String code, final Set<IFormulaExtension> types) {
		this(code, types, FormulaExpand.TRUNCATE);
	}

	public EventB(final String code, final Set<IFormulaExtension> types,
			final FormulaExpand expansion) {
		this.code = UnicodeTranslator.toAscii(code);
		this.types = types;
		this.expansion = expansion;
	}

	public void ensureParsed() {
		final String unicode = UnicodeTranslator.toUnicode(code);
		kind = EvalElementType.PREDICATE;
		IParseResult parseResult = FormulaFactory.getInstance(types)
				.parsePredicate(unicode, null);
		List<String> errors = new ArrayList<String>();

		if (!parseResult.hasProblem()) {
			ast = preparePredicateAst(parseResult);
		} else {
			errors.add("Parsing predicate failed because: "
					+ parseResult.toString());
			kind = EvalElementType.EXPRESSION;
			parseResult = FormulaFactory.getInstance(types).parseExpression(
					unicode, null);
			if (!parseResult.hasProblem()) {
				ast = prepareExpressionAst(parseResult);
			} else {
				errors.add("Parsing expression failed because: "
						+ parseResult.toString());
				kind = EvalElementType.ASSIGNMENT;
				parseResult = FormulaFactory.getInstance(types)
						.parseAssignment(unicode, null);
				if (!parseResult.hasProblem()) {
					ast = prepareAssignmentAst(parseResult);
				} else {
					errors.add("Parsing assignment failed because: "
							+ parseResult.toString());
				}
			}
		}
		if (parseResult.hasProblem()) {
			for (String string : errors) {
				logger.error(string);
			}
			logger.error("Parsing of code failed. Ascii is: " + code);
			logger.error("Parsing of code failed. Unicode is: " + unicode);
			throw new EvaluationException("Was not able to parse code: " + code
					+ " See log for details.");
		}
	}

	private Node prepareAssignmentAst(final IParseResult parseResult) {
		final Assignment assign = parseResult.getParsedAssignment();
		final TranslationVisitor visitor = new TranslationVisitor();
		try {
			assign.accept(visitor);
		} catch (Exception e) {
			logger.error("Creation of ast failed for assignment " + code, e);
			throw new EvaluationException(
					"Could not create AST for assignment " + assign.toString());
		}
		return visitor.getSubstitution();
	}

	private Node prepareExpressionAst(final IParseResult parseResult) {
		final Expression expr = parseResult.getParsedExpression();
		final TranslationVisitor visitor = new TranslationVisitor();
		try {
			expr.accept(visitor);
		} catch (Exception e) {
			logger.error("Creation of ast failed for expression " + code, e);
			throw new EvaluationException(
					"Could not create AST for expression " + expr.toString());
		}
		final Node expression = visitor.getExpression();
		return expression;
	}

	private Node preparePredicateAst(final IParseResult parseResult) {
		final Predicate parsedPredicate = parseResult.getParsedPredicate();
		final TranslationVisitor visitor = new TranslationVisitor();
		try {
			parsedPredicate.accept(visitor);
		} catch (Exception e) {
			logger.error("Creation of ast failed for expression " + code, e);
			throw new EvaluationException("Could not create AST for predicate "
					+ parsedPredicate.toString());
		}
		return visitor.getPredicate();
	}

	@Override
	public void printProlog(final IPrologTermOutput pout) {
		if (ast == null) {
			ensureParsed();
		}
		if (EvalElementType.ASSIGNMENT.equals(getKind())) {
			throw new EvaluationException(
					"Assignments are currently unsupported for evaluation");
		}

		assert ast != null;
		final ASTProlog prolog = new ASTProlog(pout, null);
		ast.apply(prolog);
	}

	@Override
	public EvalElementType getKind() {
		if (kind == null) {
			ensureParsed();
		}
		return kind;
	}

	@Override
	public String toString() {
		return getCode();
	}

	@Override
	public Node getAst() {
		if (ast == null) {
			ensureParsed();
		}

		assert ast != null;

		return ast;
	}

	@Override
	public String serialized() {
		return "#EventB:" + code;
	}

	@Override
	public IFormulaUUID getFormulaId() {
		return uuid;
	}

	@Override
	public EvaluationCommand getCommand(final State stateId) {
		return new EvaluateFormulaCommand(this, stateId.getId());
	}

	public String toUnicode() {
		return UnicodeTranslator.toUnicode(code);
	}

	public IParseResult getRodinParsedResult() {
		if (kind == null) {
			ensureParsed();
		}
		switch (kind) {
			case PREDICATE:
				return FormulaFactory.getInstance(types).parsePredicate(toUnicode(), null);
			
			case EXPRESSION:
				return FormulaFactory.getInstance(types).parseExpression(toUnicode(), null);
			
			case ASSIGNMENT:
				return FormulaFactory.getInstance(types).parseAssignment(toUnicode(), null);
			
			default:
				throw new IllegalStateException("Unhandled kind: " + kind);
		}
	}

	@Override
	public BObject translate() {
		if (!EvalElementType.EXPRESSION.equals(getKind())) {
			throw new IllegalArgumentException();
		}
		TranslatingVisitor v = new TranslatingVisitor();
		getAst().apply(v);
		return v.getResult();
	}

	public Set<IFormulaExtension> getTypes() {
		return types;
	}
}
