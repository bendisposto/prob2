package de.prob.animator.domainobjects;

import static de.prob.animator.domainobjects.EvalElementType.EXPRESSION;
import static de.prob.animator.domainobjects.EvalElementType.PREDICATE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

import de.be4.classicalb.core.parser.analysis.prolog.ASTProlog;
import de.be4.classicalb.core.parser.node.Node;
import de.prob.formula.TranslationVisitor;
import de.prob.model.representation.FormulaUUID;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.unicode.UnicodeTranslator;

/**
 * Representation of an Event-B formula
 * 
 * @author joy
 * 
 */
public class EventB extends AbstractEvalElement {

	Logger logger = LoggerFactory.getLogger(EventB.class);
	public FormulaUUID uuid = new FormulaUUID();

	private String kind;
	private Node ast = null;

	private final Set<IFormulaExtension> types;

	/**
	 * @param code
	 *            - The String which is a representation of the desired Event-B
	 *            formula
	 */
	public EventB(final String code) {
		this.code = UnicodeTranslator.toAscii(code);
		types = Collections.emptySet();
		ensureParsed();
	}

	public EventB(final String code, final Set<IFormulaExtension> types) {
		this.code = UnicodeTranslator.toAscii(code);
		this.types = types;
		ensureParsed();
	}

	private void ensureParsed() {
		final String unicode = UnicodeTranslator.toUnicode(code);
		kind = PREDICATE.toString();
		IParseResult parseResult = FormulaFactory.getInstance(types)
				.parsePredicate(unicode, LanguageVersion.LATEST, null);

		if (!parseResult.hasProblem()) {
			ast = preparePredicateAst(parseResult);

		} else {
			kind = EXPRESSION.toString();
			parseResult = FormulaFactory.getInstance(types).parseExpression(
					unicode, LanguageVersion.LATEST, null);
			if (!parseResult.hasProblem()) {
				ast = prepareExpressionAst(parseResult);
			}
		}
		if (parseResult.hasProblem()) {
			throwException(code, parseResult);
		}
	}

	private Node prepareExpressionAst(final IParseResult parseResult) {
		final Expression expr = parseResult.getParsedExpression();
		final TranslationVisitor visitor = new TranslationVisitor();
		try {
			expr.accept(visitor);
		} catch (Exception e) {
			logger.error("Creation of ast failed for predicate " + code, e);
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

	private void throwException(final String code,
			final IParseResult parseResult) {
		final List<ASTProblem> problems = parseResult.getProblems();
		final ArrayList<String> msgs = new ArrayList<String>();
		for (final ASTProblem astProblem : problems) {
			msgs.add(astProblem.getMessage().toString());
		}
		final String error = Joiner.on(", \n").join(msgs);
		throw new EvaluationException("Cannot parse " + code + ":\n " + error);
	}

	@Override
	public void printProlog(final IPrologTermOutput pout) {
		if (ast == null) {
			ensureParsed();
		}

		assert ast != null;
		final ASTProlog prolog = new ASTProlog(pout, null);
		ast.apply(prolog);
	}

	@Override
	public String getKind() {
		return kind;
	}

	@Override
	public String toString() {
		return getCode();
	}

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
	public FormulaUUID getFormulaId() {
		return uuid;
	}
}
