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
import de.be4.classicalb.core.parser.exceptions.BCompoundException;
import de.be4.classicalb.core.parser.node.AExpressionParseUnit;
import de.be4.classicalb.core.parser.node.APredicateParseUnit;
import de.be4.classicalb.core.parser.node.EOF;
import de.be4.classicalb.core.parser.node.Node;
import de.be4.classicalb.core.parser.node.Start;
import de.prob.animator.command.EvaluateFormulaCommand;
import de.prob.animator.command.EvaluationCommand;
import de.prob.model.classicalb.PrettyPrinter;
import de.prob.model.representation.FormulaUUID;
import de.prob.model.representation.IFormulaUUID;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.State;
import de.prob.translator.TranslatingVisitor;
import de.prob.translator.types.BObject;

/**
 * Representation of a ClassicalB formula.
 * 
 * @author joy
 */
public class ProBEvalElement extends AbstractEvalElement {

	private final FormulaUUID uuid = new FormulaUUID();
	private PrologTerm ast;

	/**
	 * Convenience Constructor that defaults to truncationg the result values
	 * for Parameters, see the other constructor
	 */
	public ProBEvalElement(final PrologTerm ast, final String code) {
		this(ast, code, FormulaExpand.truncate);
	}

	/**
	 * @param ast
	 *            is the Prolog AST retrieved using GetMachineStructureCommand
	 * @param code
	 *            is the prettyprint of the formula as created by probcli (this
	 *            may not be parsabel!)
	 * @param expansion
	 *            is used to determine if we want to get expanded or shortened
	 *            values (e.g. for large sets)
	 * @throws EvaluationException
	 */
	public ProBEvalElement(final PrologTerm ast, final String code, final FormulaExpand expansion) {
		this.ast = ast;
		this.code = code;
		this.expansion = expansion;
	}

	/**
	 * @see de.prob.animator.domainobjects.IEvalElement#getKind()
	 * 
	 * @return kind {@link EvalElementType#toString()}. Either '#EXPRESSION' or
	 *         '#PREDICATE'
	 */
	@Override
	public String getKind() {
		if ("pred".equals(ast.getArgument(1).getArgument(2).toString())) {
			return PREDICATE.toString();
		}
		return EXPRESSION.toString();
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
		pout.printTerm(ast);

	}

	@Override
	public String serialized() {
		return "#ProB:" + ast;
	}

	@Override
	public IFormulaUUID getFormulaId() {
		return uuid;
	}

	@Override
	public EvaluationCommand getCommand(final State stateId) {
		return new EvaluateFormulaCommand(this, stateId.getId());
	}

}
