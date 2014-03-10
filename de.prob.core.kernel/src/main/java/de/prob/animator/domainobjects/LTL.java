package de.prob.animator.domainobjects;

import com.google.gson.Gson;

import de.be4.classicalb.core.parser.ClassicalBParser;
import de.be4.ltl.core.parser.LtlParseException;
import de.be4.ltl.core.parser.LtlParser;
import de.prob.animator.command.EvaluationCommand;
import de.prob.animator.command.LtlCheckingCommand;
import de.prob.model.representation.FormulaUUID;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.StateId;

public class LTL extends AbstractEvalElement {
	/* TODO: worry about the language specific parser */
	private final static LtlParser ltlParser = new LtlParser(
			new ClassicalBParser());

	private final FormulaUUID uuid = new FormulaUUID();
	private final PrologTerm generatedTerm;

	public LTL(final String code) throws LtlParseException {
		this.code = code;
		generatedTerm = ltlParser.generatePrologTerm(code, "root");
	}

	@Override
	public void printProlog(final IPrologTermOutput pout) {
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

	@Override
	public EvaluationCommand getCommand(final StateId stateid) {
		return new LtlCheckingCommand(this, 500);
	}

}
