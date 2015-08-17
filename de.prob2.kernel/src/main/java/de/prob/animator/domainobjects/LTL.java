package de.prob.animator.domainobjects;

import com.google.gson.Gson;

import de.be4.classicalb.core.parser.ClassicalBParser;
import de.be4.ltl.core.parser.LtlParseException;
import de.be4.ltl.core.parser.LtlParser;
import de.prob.animator.command.EvaluationCommand;
import de.prob.animator.command.LtlCheckingCommand;
import de.prob.model.representation.FormulaUUID;
import de.prob.model.representation.IFormulaUUID;
import de.prob.parserbase.ProBParserBase;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.State;

public class LTL extends AbstractEvalElement {
	private final FormulaUUID uuid = new FormulaUUID();
	private final PrologTerm generatedTerm;

	public LTL(final String code) throws LtlParseException {
		this(code, new ClassicalBParser());
	}

	public LTL(final String code, ProBParserBase languageSpecificParser)
			throws LtlParseException {
		this.code = code;
		generatedTerm = new LtlParser(languageSpecificParser)
		.generatePrologTerm(code, "root");
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
	public IFormulaUUID getFormulaId() {
		return uuid;
	}

	@Override
	public EvaluationCommand getCommand(final State stateid) {
		return new LtlCheckingCommand(stateid.getStateSpace(), this, 500);
	}

	public static LTL parseEventB(String formula) throws LtlParseException {
		return new LTL(formula, new EventBParserBase());
	}

	public static LTL parseClassicalB(String formula) throws LtlParseException {
		return new LTL(formula, new ClassicalBParser());
	}
}
