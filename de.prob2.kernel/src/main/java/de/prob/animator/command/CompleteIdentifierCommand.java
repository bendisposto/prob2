package de.prob.animator.command;

import java.util.Collections;
import java.util.List;

import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public final class CompleteIdentifierCommand extends AbstractCommand {
	private static final String PROLOG_COMMAND_NAME = "get_possible_completions";
	
	private static final String COMPLETIONS_VAR = "Completions";
	
	private final String identifier;
	private boolean ignoreCase;
	private boolean includeKeywords;
	private List<String> completions;
	
	public CompleteIdentifierCommand(final String identifier) {
		super();
		
		this.identifier = identifier;
		this.ignoreCase = false;
		this.includeKeywords = false;
		this.completions = null;
	}
	
	public String getIdentifier() {
		return this.identifier;
	}
	
	public boolean isIgnoreCase() {
		return this.ignoreCase;
	}
	
	public void setIgnoreCase(final boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}
	
	public boolean isIncludeKeywords() {
		return this.includeKeywords;
	}
	
	public void setIncludeKeywords(final boolean includeKeywords) {
		this.includeKeywords = includeKeywords;
	}
	
	public List<String> getCompletions() {
		return Collections.unmodifiableList(this.completions);
	}
	
	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME);
		pto.printAtom(this.getIdentifier());
		pto.openList();
		if (this.isIgnoreCase()) {
			pto.printAtom("lower_case");
		}
		if (this.isIncludeKeywords()) {
			pto.printAtom("keywords");
		}
		pto.closeList();
		pto.printVariable(COMPLETIONS_VAR);
		pto.closeTerm();
	}
	
	@Override
	public void processResult(final ISimplifiedROMap<String, PrologTerm> bindings) {
		this.completions = PrologTerm.atomicStrings(BindingGenerator.getList(bindings, COMPLETIONS_VAR));
	}
}
