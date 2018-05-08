package de.prob.animator.command;

import de.prob.animator.command.AbstractCommand;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GetRightClickOptionsForStateVisualizationCommand extends AbstractCommand {

	private static final String PROLOG_COMMAND_NAME = "get_react_to_item_right_click_options_for_state";
	private static final String OPTIONS = "Options";
	private final String stateId;
	private final int row;
	private final int column;

	private List<String> options = new ArrayList<>();

	public GetRightClickOptionsForStateVisualizationCommand(String stateId, int row, int column) {
		this.stateId = stateId;
		this.row = row-1;
		this.column = column+1;
	}

	@Override
	public void writeCommand(IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME);
		pto.printAtomOrNumber(stateId);
		pto.printNumber(row);
		pto.printNumber(column);
		pto.printVariable(OPTIONS);
		pto.closeTerm();
	}

	@Override
	public void processResult(ISimplifiedROMap<String, PrologTerm> bindings) {
		ListPrologTerm optionTerms = BindingGenerator.getList(bindings.get(OPTIONS));
		options.addAll(optionTerms.stream().map(PrologTerm::getFunctor).collect(Collectors.toList()));
	}

	public List<String> getOptions() {
		return Collections.unmodifiableList(options);
	}

}
