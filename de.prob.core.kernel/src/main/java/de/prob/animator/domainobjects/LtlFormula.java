package de.prob.animator.domainobjects;

import com.google.gson.Gson;

import de.prob.model.representation.FormulaUUID;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;


public class LtlFormula extends AbstractEvalElement {

	private final PrologTerm generatedTerm;
	private final FormulaUUID uuid = new FormulaUUID();

	public LtlFormula(PrologTerm term){
		generatedTerm = term;
	}

	@Override
	public void printProlog(IPrologTermOutput pout) {
		pout.printTerm(generatedTerm);
	}

	@Override
	public String getKind() {
		return "LTL";
	}

	@Override
	public String serialized() {
		// FIXME, maybe?
		Gson g = new Gson();
		return "#LTL:" + g.toJson(this);
	}

	@Override
	public FormulaUUID getFormulaId() {
		return uuid;
	}

}
