/*
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 */

package de.prob.animator.domainobjects;

import de.be4.classicalb.core.parser.BParser;
import de.be4.classicalb.core.parser.analysis.prolog.ASTProlog;
import de.be4.classicalb.core.parser.analysis.prolog.NodeIdAssignment;
import de.be4.classicalb.core.parser.analysis.prolog.OffsetPositionPrinter;
import de.be4.classicalb.core.parser.exceptions.BCompoundException;
import de.be4.classicalb.core.parser.node.*;
import de.be4.classicalb.core.parser.util.PrettyPrinter;
import de.prob.animator.command.EvaluateFormulaCommand;
import de.prob.animator.command.EvaluationCommand;
import de.prob.model.representation.FormulaUUID;
import de.prob.model.representation.IFormulaUUID;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.statespace.State;
import de.prob.translator.TranslatingVisitor;
import de.prob.translator.types.BObject;

import static de.prob.animator.domainobjects.EvalElementType.ASSIGNMENT;
import static de.prob.animator.domainobjects.EvalElementType.EXPRESSION;
import static de.prob.animator.domainobjects.EvalElementType.PREDICATE;

/**
 * Representation of a ClassicalB formula.
 * 
 * @author joy
 */
public class ClassicalB extends AbstractEvalElement implements IBEvalElement {

	private final FormulaUUID uuid = new FormulaUUID();

	private final Start ast;

	/**
	 * @param code
	 *            will be parsed and the resulting {@link Start} ast saved
	 * @throws EvaluationException
	 *             if the code could not be parsed
	 */
	public ClassicalB(final String code) {
		this(code, FormulaExpand.truncate);
	}

	public ClassicalB(final String code, final FormulaExpand expansion) {
		Start ast;
		try {
			ast = BParser.parse(BParser.FORMULA_PREFIX + " " + code);
		} catch (BCompoundException e) {
			try {
				ast = BParser.parse(BParser.SUBSTITUTION_PREFIX + " " + code);
			} catch (BCompoundException f) {
				throw new EvaluationException(f.getMessage(), f);
			}
		}
		this.ast = ast;
		this.code = prettyprint(ast);
		this.expansion = expansion;
	}

	/**
	 * @param ast
	 *            is saved and the string representation determined from the ast
	 *            and saved
	 */
	public ClassicalB(final Start ast) {
		this(ast, FormulaExpand.truncate);
	}

	public ClassicalB(final Start ast, final FormulaExpand expansion, String code) {
		this.ast = ast;
		this.expansion = expansion;
		this.code = code;
	}

	public ClassicalB(final Start ast, final FormulaExpand expansion) {
		this(ast, expansion, prettyprint(ast));
	}

	/**
	 * @see de.prob.animator.domainobjects.IEvalElement#getKind()
	 * 
	 * @return kind {@link EvalElementType#toString()}. Either '#EXPRESSION' or
	 *         '#PREDICATE'
	 */
	@Override
	public String getKind() {
		if (ast.getPParseUnit() instanceof AExpressionParseUnit) {
			return EXPRESSION.toString();
		} else if (ast.getPParseUnit() instanceof APredicateParseUnit) {
			return PREDICATE.toString();
		} else {
			return ASSIGNMENT.toString();
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
	public String toString() {
		return code;
	}

	@Override
	public void printProlog(final IPrologTermOutput pout) {
		if (getKind().equals(ASSIGNMENT.toString())) {
			throw new EvaluationException("Substitutions are currently unsupported for evaluation");
		}
		if (ast.getEOF() == null) {
			//ast.setEOF(new EOF());
		}
		// TODO use ASTProlog.printFormula(pout) when new parser is released
		NodeIdAssignment na = new NodeIdAssignment();
		ast.apply(na);
		OffsetPositionPrinter pprinter = new OffsetPositionPrinter(na, -1, 0);
		final ASTProlog prolog = new ASTProlog(pout, pprinter);

		ast.apply(prolog);

	}

	private static String prettyprint(final Node predicate) {
		final PrettyPrinter prettyPrinter = new PrettyPrinter();
		predicate.apply(prettyPrinter);
		return prettyPrinter.getPrettyPrint();
	}

	@Override
	public String serialized() {
		return "#ClassicalB:" + code;
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
	public BObject translate() {
		if (!getKind().equals(EXPRESSION.toString())) {
			throw new IllegalArgumentException();
		}
		TranslatingVisitor v = new TranslatingVisitor();
		getAst().apply(v);
		return v.getResult();
	}
}
