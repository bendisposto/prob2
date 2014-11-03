package de.prob.animator.domainobjects;

import de.prob.animator.command.EvaluateAndTranslateCommand;
import de.prob.animator.command.EvaluationCommand;
import de.prob.model.representation.FormulaUUID;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.statespace.StateId;

public class TranslateFormula implements IEvalElement {

	private final IBEvalElement element;

	public TranslateFormula(final IBEvalElement element) {
		this.element = element;
	}

	@Override
	public String getCode() {
		return element.getCode();
	}

	@Override
	public void printProlog(final IPrologTermOutput pout) {
		element.printProlog(pout);
	}

	@Override
	public String getKind() {
		return element.getKind();
	}

	@Override
	public String serialized() {
		return element.serialized();
	}

	@Override
	public FormulaUUID getFormulaId() {
		return element.getFormulaId();
	}

	public IBEvalElement getFormula() {
		return element;
	}

	@Override
	public EvaluationCommand getCommand(final StateId stateid) {
		return new EvaluateAndTranslateCommand(this, stateid.getId());
	}

	@Override
	public boolean equals(final Object that) {
		if (this == that) {
			return true;
		}
		if (that instanceof TranslateFormula) {
			return this.getFormula().equals(
					((TranslateFormula) that).getFormula());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.getFormula().hashCode();
	}

	@Override
	public String toString() {
		return getCode();
	}
}
