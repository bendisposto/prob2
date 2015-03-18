package de.prob.animator.domainobjects;

import de.prob.animator.command.EvaluateAndTranslateCommand;
import de.prob.animator.command.EvaluationCommand;
import de.prob.model.representation.IFormulaUUID;
import de.prob.model.representation.TranslateUUID;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.statespace.State;

public class TranslateFormula implements IEvalElement {

	private final IBEvalElement element;
	private final IFormulaUUID uuid;

	public TranslateFormula(final IBEvalElement element) {
		this.element = element;
		this.uuid = new TranslateUUID(element.getFormulaId());
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
	public IFormulaUUID getFormulaId() {
		return uuid;
	}

	public IBEvalElement getFormula() {
		return element;
	}

	@Override
	public EvaluationCommand getCommand(final State stateid) {
		return new EvaluateAndTranslateCommand(this, stateid.getId());
	}

	@Override
	public String toString() {
		return getCode();
	}

	@Override
	public FormulaExpand expansion() {
		return FormulaExpand.truncate;
	}
}
