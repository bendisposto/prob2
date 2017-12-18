package de.prob.animator.prologast;

import java.util.Collections;
import java.util.Objects;

import com.google.common.base.MoreObjects;

import de.prob.animator.domainobjects.FormulaExpand;
import de.prob.animator.domainobjects.ProBEvalElement;
import de.prob.prolog.term.PrologTerm;

public final class ASTFormula extends PrologASTNode{
	private final PrologTerm term;
	private final String prettyPrint;

	public ASTFormula(PrologTerm term, String prettyPrint) {
		super(Collections.emptyList());

		Objects.requireNonNull(term, "term");
		Objects.requireNonNull(prettyPrint, "prettyPrint");

		this.term = term;
		this.prettyPrint = prettyPrint;
	}

	public PrologTerm getTerm() {
		return this.term;
	}

	public String getPrettyPrint() {
		return this.prettyPrint;
	}

	public ProBEvalElement getFormula(FormulaExpand expand) {
		return new ProBEvalElement(this.getTerm(), this.getPrettyPrint(), expand);
	}

	public ProBEvalElement getFormula() {
		return this.getFormula(FormulaExpand.TRUNCATE);
	}

	@Override
	public String toString(){
		return MoreObjects.toStringHelper(this)
			.add("term", this.getTerm())
			.add("prettyPrint", this.getPrettyPrint())
			.toString();
	}
}
