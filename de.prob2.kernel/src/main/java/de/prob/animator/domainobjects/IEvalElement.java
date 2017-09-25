package de.prob.animator.domainobjects;

import de.prob.animator.command.EvaluationCommand;
import de.prob.model.representation.IFormulaUUID;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.statespace.State;

/**
 * Objects that implement this interface correctly are automatically recognized
 * as a formula that can be evaluated. The user can easily get the prolog
 * representation of the given formula.
 * 
 * @author joy
 * 
 */
public interface IEvalElement {
	/**
	 * @return String representing the formula
	 */
	public abstract String getCode();

	/**
	 * Writes the formula to {@link IPrologTermOutput} pout
	 * 
	 * @param pout the {@link IPrologTermOutput} to write to
	 */
	public abstract void printProlog(IPrologTermOutput pout);

	// public void typecheck();

	/**
	 * @return The kind of the formula. For B formulas, this needs to be either formula or expression. For other formula types, new kinds need to be defined to recognize the formula.
	 */
	public abstract EvalElementType getKind();

	public String serialized();

	public IFormulaUUID getFormulaId();

	public EvaluationCommand getCommand(State state);

	public FormulaExpand expansion();
}
