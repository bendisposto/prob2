package de.prob.animator.domainobjects;

import com.google.gson.Gson;

import de.be4.classicalb.core.parser.ClassicalBParser;
import de.be4.ltl.core.parser.LtlParseException;

import de.prob.animator.command.EvaluationCommand;
import de.prob.animator.command.LtlCheckingCommand;
import de.prob.ltl.parser.LtlParser;
import de.prob.model.representation.FormulaUUID;
import de.prob.model.representation.IFormulaUUID;
import de.prob.parserbase.ProBParserBase;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.State;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LTL extends AbstractEvalElement {
	private final FormulaUUID uuid = new FormulaUUID();
	private final PrologTerm generatedTerm;
	
	private static final Logger logger = LoggerFactory.getLogger(LTL.class);

	public LTL(final String code) throws LtlParseException {
		this(code, new ClassicalBParser());
	}
	
	public LTL(final String code, ProBParserBase languageSpecificParser)
			throws LtlParseException {
		super(code, FormulaExpand.EXPAND);
		generatedTerm = new de.be4.ltl.core.parser.LtlParser(languageSpecificParser)
							.generatePrologTerm(code, "root");
	}
	
	public LTL(final String code, ProBParserBase languageSpecificParser, LtlParser parser) {
		super(code, FormulaExpand.EXPAND);
		generatedTerm = parser.generatePrologTerm("root", languageSpecificParser);
	}
		
	@Override
	public void printProlog(final IPrologTermOutput pout) {
		if(generatedTerm == null) {
			logger.error("PrologTerm is null");
			return;
		}
		pout.printTerm(generatedTerm);
	}

	@Override
	public EvalElementType getKind() {
		throw new UnsupportedOperationException("Should never be called on an LTL");
	}

	@Override
	public String serialized() {
		// FIXME, maybe?
		return "#LTL:" + new Gson().toJson(this);
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
