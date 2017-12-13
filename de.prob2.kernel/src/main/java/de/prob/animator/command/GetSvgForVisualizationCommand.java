package de.prob.animator.command;

import java.io.File;
import java.util.ArrayList;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public class GetSvgForVisualizationCommand extends AbstractCommand {
	
	public enum Option {
		DFA_MERGE("dfa_merge"), 
		STATE_AS_GRAPH("state_as_graph"), 
		INVARIANT("invariant"), 
		PROPERTIES("properties"), 
		ASSERTIONS("assertions"), 
		DEADLOCK("deadlock"), 
		GOAL("goal"), 
		LAST_ERROR("last_error"), 
		ENABLE_GRAPH("enable_graph"), 
		DEPENDENCE_GRAPH("dependence_graph"),
		DEFINITIONS("definitions"),
		EXPR_AS_GRAPH("expr_as_graph"), 
		FORMULA_TREE("formula_tree"), 
		TRANSITION_DIAGRAM("transition_diagram"), 
		PREDICATE_DEPENDENCY("predicate_dependency"),
		STATE_SPACE("state_space");
		
		private String option;
		
		private Option(String option) {
			this.option = option;
		}
		
		public String getOption() {
			return option;
		}
	}
	
	private static final String PROLOG_COMMAND_NAME = "call_dot_command_and_dot";
	
	private Option option;

	private File file;
	
	private ArrayList<String> formulas;
	
	public GetSvgForVisualizationCommand(Option option, File file, ArrayList<String> formulas) {
		this.option = option;
		this.file = file;
		this.formulas = formulas;
	}
	
	@Override
	public void writeCommand(IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME);
		pto.printAtom(option.getOption());
		pto.openList();
		for(String formula : formulas) {
			pto.printAtom(formula);
		}
		pto.closeList();
		pto.printAtom("svg");
		pto.printAtom(file.getAbsolutePath().toString());
		pto.closeTerm();
	}

	@Override
	public void processResult(ISimplifiedROMap<String, PrologTerm> bindings) {
				
	}

}
