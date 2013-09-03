/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.animator.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public class LtlCheckingCommand extends AbstractCommand {

	private static final String VARIABLE_NAME_ATOMICS = "A";
	private static final String VARIABLE_NAME_STRUCTURE = "S";
	private static final String VARIABLE_NAME_RESULT = "R";

	public static enum StartMode {
		init, starthere, checkhere
	};

	private PrologTerm formula;
	private final int max;
	private final StartMode mode;

	Logger logger = LoggerFactory.getLogger(LtlCheckingCommand.class);

	public LtlCheckingCommand(PrologTerm formula, int max, StartMode mode) {
		this.formula = formula;
		this.max = max;
		this.mode = mode;
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {


	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("do_ltl_modelcheck");
		formula.toTermOutput(pto);
		pto.printNumber(max);
		pto.printAtom(mode.toString());
		pto.printVariable(VARIABLE_NAME_ATOMICS);
		pto.printVariable(VARIABLE_NAME_STRUCTURE);
		pto.printVariable(VARIABLE_NAME_RESULT);
		pto.closeTerm();
	}
}
