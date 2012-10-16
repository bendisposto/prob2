package de.prob.animator.domainobjects;

import static de.prob.animator.domainobjects.EvalElementType.EXPRESSION;
import static de.prob.animator.domainobjects.EvalElementType.PREDICATE;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.Predicate;

import com.google.common.base.Joiner;

import de.be4.classicalb.core.parser.analysis.prolog.ASTProlog;
import de.be4.classicalb.core.parser.node.Node;
import de.prob.formula.eventb.ExpressionVisitor;
import de.prob.formula.eventb.PredicateVisitor;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.unicode.UnicodeTranslator;

public class EventB implements IEvalElement {

	private final String code;
	private String kind;
	private final Node ast;

	public EventB(String code) {
		this.code = code;
		String unicode = UnicodeTranslator.toUnicode(code);
		kind = PREDICATE.toString();
		IParseResult parseResult = FormulaFactory.getDefault().parsePredicate(
				unicode, LanguageVersion.LATEST, null);

		if (!parseResult.hasProblem()) {
			ast = preparePredicateAst(parseResult);

		} else {
			kind = EXPRESSION.toString();
			parseResult = FormulaFactory.getDefault().parseExpression(unicode,
					LanguageVersion.LATEST, null);
			ast = prepareExpressionAst(parseResult);
		}
		if (parseResult.hasProblem()) {
			throwException(code, parseResult);
		}

	}

	private Node prepareExpressionAst(IParseResult parseResult) {
		Expression expr = parseResult.getParsedExpression();
		ExpressionVisitor visitor = new ExpressionVisitor(
				new LinkedList<String>());
		expr.accept(visitor);
		Node expression = visitor.getExpression();
		return expression;
	}

	private Node preparePredicateAst(IParseResult parseResult) {
		Predicate parsedPredicate = parseResult.getParsedPredicate();
		PredicateVisitor visitor = new PredicateVisitor();
		parsedPredicate.accept(visitor);
		return visitor.getPredicate();
	}

	private void throwException(String code, IParseResult parseResult) {
		List<ASTProblem> problems = parseResult.getProblems();
		ArrayList<String> msgs = new ArrayList<String>();
		for (ASTProblem astProblem : problems) {
			msgs.add(astProblem.getMessage().toString());
		}
		String error = Joiner.on(", \n").join(msgs);
		throw new RuntimeException("Cannot parse " + code + ":\n " + error); // FIXME
	}

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public void printProlog(IPrologTermOutput pout) {
		final ASTProlog prolog = new ASTProlog(pout, null);
		ast.apply(prolog);
	}

	@Override
	public String getKind() {
		return kind;
	}

}
