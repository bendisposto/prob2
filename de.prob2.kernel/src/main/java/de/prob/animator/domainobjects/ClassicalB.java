/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.animator.domainobjects;

import static de.prob.animator.domainobjects.EvalElementType.ASSIGNMENT;
import static de.prob.animator.domainobjects.EvalElementType.EXPRESSION;
import static de.prob.animator.domainobjects.EvalElementType.PREDICATE;
import de.be4.classicalb.core.parser.BParser;
import de.be4.classicalb.core.parser.analysis.prolog.ASTProlog;
import de.be4.classicalb.core.parser.exceptions.BException;
import de.be4.classicalb.core.parser.node.AExpressionParseUnit;
import de.be4.classicalb.core.parser.node.APredicateParseUnit;
import de.be4.classicalb.core.parser.node.EOF;
import de.be4.classicalb.core.parser.node.Node;
import de.be4.classicalb.core.parser.node.Start;
import de.prob.animator.command.EvaluateFormulaCommand;
import de.prob.animator.command.EvaluationCommand;
import de.prob.model.classicalb.PrettyPrinter;
import de.prob.model.representation.FormulaUUID;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.statespace.StateId;

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
	 */
	public ClassicalB(final String code) {
		//this.code = code;
		Start ast;
		try {
			ast = BParser.parse(BParser.FORMULA_PREFIX + " " + code);
			this.code = prettyprint(ast);
		} catch (BException e) {
			try {
				ast = BParser.parse(BParser.SUBSTITUTION_PREFIX + " " + code);
				this.code = prettyprint(ast);
			} catch (BException f) {
				throw new EvaluationException(f.getMessage(), f);
			}
		}
		this.ast = ast;
	}

	/**
	 * @param ast
	 *            is saved and the string representation determined from the ast
	 *            and saved
	 */
	public ClassicalB(final Start ast) {
		this.ast = ast;
		code = prettyprint(ast);
	}

	/**
	 * @see de.prob.animator.domainobjects.IEvalElement#getKind()
	 * 
	 * @return kind {@link EvalElementType#toString()}. Either '#EXPRESSION' or
	 *         '#PREDICATE'
	 */
	@Override
	public String getKind() {
		return ast.getPParseUnit() instanceof AExpressionParseUnit ? EXPRESSION
				.toString()
				: (ast.getPParseUnit() instanceof APredicateParseUnit ? PREDICATE
						.toString() : ASSIGNMENT.toString());
	}

	/**
	 * @return {@link Start} ast corresponding to the formula
	 */
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
			throw new EvaluationException(
					"Subsitutions are currently unsupported for evaluation");
		}
		final ASTProlog prolog = new ASTProlog(pout, null);
		if (ast.getEOF() == null) {
			ast.setEOF(new EOF());
		}
		ast.apply(prolog);

	}

	private String prettyprint(final Node predicate) {
		final PrettyPrinter prettyPrinter = new PrettyPrinter();
		predicate.apply(prettyPrinter);
		return prettyPrinter.getPrettyPrint();
	}

	@Override
	public String serialized() {
		return "#ClassicalB:" + code;
	}

	@Override
	public FormulaUUID getFormulaId() {
		return uuid;
	}

	@Override
	public EvaluationCommand getCommand(final StateId stateId) {
		return new EvaluateFormulaCommand(this, stateId.getId());
	}
}
