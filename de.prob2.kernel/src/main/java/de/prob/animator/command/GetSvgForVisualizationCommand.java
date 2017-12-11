package de.prob.animator.command;

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
	
	public GetSvgForVisualizationCommand(Option option) {
		this.option = option;
	}
	
	@Override
	public void writeCommand(IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME);
		pto.printAtom(option.getOption());
		pto.emptyList();
		pto.printAtom("svg");
		pto.printAtom("./out.svg");
		pto.closeTerm();
	}

	@Override
	public void processResult(ISimplifiedROMap<String, PrologTerm> bindings) {
		
	}

}
