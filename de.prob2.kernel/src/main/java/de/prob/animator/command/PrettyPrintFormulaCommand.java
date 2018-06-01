package de.prob.animator.command;

import de.prob.animator.domainobjects.IEvalElement;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public final class PrettyPrintFormulaCommand extends AbstractCommand {
	public enum Mode {
		ASCII, UNICODE, LATEX
	}
	
	private static final String PROLOG_COMMAND_NAME = "pretty_print_predicate";
	
	private static final String PP_STRING = "PPString";
	
	private final IEvalElement formula;
	private final Mode mode;
	private boolean optimize;
	private String prettyPrint;
	
	public PrettyPrintFormulaCommand(final IEvalElement formula, final Mode mode) {
		this.formula = formula;
		this.mode = mode;
		this.optimize = true;
		this.prettyPrint = null;
	}
	
	public boolean isOptimize() {
		return this.optimize;
	}
	
	public void setOptimize(final boolean optimize) {
		this.optimize = optimize;
	}
	
	public String getPrettyPrint() {
		return this.prettyPrint;
	}
	
	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME);
		this.formula.printProlog(pto);
		
		pto.openList();
		switch (this.mode) {
			case ASCII:
				break;
			
			case UNICODE:
				pto.printAtom("unicode");
				break;
			
			case LATEX:
				pto.printAtom("latex");
				break;
			
			default:
				throw new AssertionError("Mode not implemented: " + this.mode);
		}
		if (!this.isOptimize()) {
			pto.printAtom("nopt");
		}
		pto.closeList();
		
		pto.printVariable(PP_STRING);
		pto.closeTerm();
	}
	
	@Override
	public void processResult(final ISimplifiedROMap<String, PrologTerm> bindings) {
		this.prettyPrint = PrologTerm.atomicString(bindings.get(PP_STRING));
	}
}
