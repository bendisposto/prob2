package de.prob.animator.prologast;

import java.util.Collections;
import java.util.Objects;

import com.google.common.base.MoreObjects;

import de.prob.animator.domainobjects.FormulaExpand;
import de.prob.animator.domainobjects.ProBEvalElement;
import de.prob.prolog.term.PrologTerm;

public final class ASTFormula extends PrologASTNode{
	private final PrologTerm formulaTerm;

	public ASTFormula(PrologTerm formulaTerm) {
		super(Collections.emptyList());

		Objects.requireNonNull(formulaTerm, "formulaTerm");

		this.formulaTerm = formulaTerm;
	}

	public PrologTerm getFormulaTerm() {
		return this.formulaTerm;
	}

	public ProBEvalElement getFormula(FormulaExpand expand) {
		final PrologTerm term = this.getFormulaTerm().getArgument(1);
		final String prettyPrint = this.getFormulaTerm().getArgument(2).getFunctor();
		return new ProBEvalElement(term, prettyPrint, expand);
	}

	public ProBEvalElement getFormula() {
		return this.getFormula(FormulaExpand.TRUNCATE);
	}

	@Override
	public String toString(){
		return MoreObjects.toStringHelper(this)
			.add("formulaTerm", this.getFormulaTerm())
			.toString();
	}
}
