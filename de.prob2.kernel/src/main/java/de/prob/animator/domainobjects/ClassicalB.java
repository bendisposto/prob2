/*
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 */

package de.prob.animator.domainobjects;

import de.be4.classicalb.core.parser.BParser;
import de.be4.classicalb.core.parser.analysis.prolog.ASTProlog;
import de.be4.classicalb.core.parser.exceptions.BCompoundException;
import de.be4.classicalb.core.parser.node.*;
import de.be4.classicalb.core.parser.util.PrettyPrinter;

import de.hhu.stups.prob.translator.BValue;
import de.hhu.stups.prob.translator.TranslatingVisitor;
import de.prob.animator.command.EvaluateFormulaCommand;
import de.prob.animator.command.EvaluationCommand;
import de.prob.model.representation.FormulaUUID;
import de.prob.model.representation.IFormulaUUID;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.statespace.State;

/**
 * Representation of a ClassicalB formula.
 * 
 * @author joy
 */
public class ClassicalB extends AbstractEvalElement implements IBEvalElement {

	private final FormulaUUID uuid = new FormulaUUID();

	private final Start ast;

	public ClassicalB(final Start ast, final FormulaExpand expansion, final String code) {
		super(code, expansion);

		this.ast = ast;
	}

	public ClassicalB(final Start ast, final FormulaExpand expansion) {
		this(ast, expansion, prettyprint(ast));
	}

	/**
	 * @param ast
	 *            is saved and the string representation determined from the ast
	 *            and saved
	 * @deprecated Use {@link #ClassicalB(Start, FormulaExpand)} with an explicit {@link FormulaExpand} argument instead
	 */
	@Deprecated
	public ClassicalB(final Start ast) {
		this(ast, FormulaExpand.TRUNCATE);
	}

	public ClassicalB(final String formula, final FormulaExpand expansion) {
		this(parse(formula), expansion);
	}

	/**
	 * @param code
	 *            will be parsed and the resulting {@link Start} ast saved
	 * @throws EvaluationException
	 *             if the code could not be parsed
	 * @deprecated Use {@link #ClassicalB(String, FormulaExpand)} with an explicit {@link FormulaExpand} argument instead
	 */
	@Deprecated
	public ClassicalB(final String code) {
		this(code, FormulaExpand.EXPAND);
	}

	private static Start parse(final String formula) {
		final BParser bParser = new BParser();
		try {
			return bParser.parseFormula(formula);
		} catch (BCompoundException e) {
			try {
				return bParser.parseSubstitution(formula);
			} catch (BCompoundException f) {
				throw new EvaluationException(f.getMessage(), f);
			}
		}
	}

	private static String prettyprint(final Node predicate) {
		final PrettyPrinter prettyPrinter = new PrettyPrinter();
		predicate.apply(prettyPrinter);
		return prettyPrinter.getPrettyPrint();
	}

	@Override
	public EvalElementType getKind() {
		if (ast.getPParseUnit() instanceof AExpressionParseUnit) {
			return EvalElementType.EXPRESSION;
		} else if (ast.getPParseUnit() instanceof APredicateParseUnit) {
			return EvalElementType.PREDICATE;
		} else {
			return EvalElementType.ASSIGNMENT;
		}
	}

	/**
	 * @return {@link Start} ast corresponding to the formula
	 */
	@Override
	public Start getAst() {
		return ast;
	}

	@Override
	public void printProlog(final IPrologTermOutput pout) {
		if (EvalElementType.ASSIGNMENT.equals(getKind())) {
			throw new EvaluationException("Substitutions are currently unsupported for evaluation");
		}
		if (ast.getEOF() == null) {
			ast.setEOF(new EOF());
		}
		ASTProlog.printFormula(ast, pout);
	}

	@Override
	public String serialized() {
		return "#ClassicalB:" + this.getCode();
	}

	@Override
	public IFormulaUUID getFormulaId() {
		return uuid;
	}

	@Override
	public EvaluationCommand getCommand(final State state) {
		return new EvaluateFormulaCommand(this, state.getId());
	}

	@Override
	public BValue translate() {
		if (!EvalElementType.EXPRESSION.equals(getKind())) {
			throw new IllegalArgumentException();
		}
		TranslatingVisitor v = new TranslatingVisitor();
		getAst().apply(v);
		return v.getResult();
	}
}
