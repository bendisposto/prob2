/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.animator.domainobjects;

import java.util.Objects;

import de.prob.animator.command.EvaluateFormulaCommand;
import de.prob.animator.command.EvaluationCommand;
import de.prob.model.representation.FormulaUUID;
import de.prob.model.representation.IFormulaUUID;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.State;

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

	@Override
	public boolean equals(final Object obj) {
		if (obj == null || this.getClass() != obj.getClass()) {
			return false;
		}
		final ProBEvalElement other = (ProBEvalElement)obj;
		return Objects.equals(this.ast, other.ast)
			&& Objects.equals(this.getCode(), other.getCode())
			&& Objects.equals(this.expansion(), other.expansion());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.ast, this.code, this.expansion);
	}
	
	@Override
	public String getKind() {
		throw new UnsupportedOperationException("Should never be called on a ProBEvalElement");
	}

	@Override
	public String toString() {
		return code;
	}

	@Override
	public void printProlog(final IPrologTermOutput pout) {
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
