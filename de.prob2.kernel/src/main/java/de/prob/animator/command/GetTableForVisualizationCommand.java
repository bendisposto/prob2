package de.prob.animator.command;

import java.util.ArrayList;
import java.util.List;

import de.prob.animator.domainobjects.DynamicCommandItem;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.animator.domainobjects.TableData;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.State;

public class GetTableForVisualizationCommand extends AbstractCommand {
	private static final String TABLE_VAR = "TABLE";

	private static final String PROLOG_COMMAND_NAME = "call_table_command_in_state"; // StateID,Command,Formulas,TableResult

	private final List<IEvalElement> formulas;
	private final DynamicCommandItem item;
	private final State id;
	private TableData table;

	public GetTableForVisualizationCommand(final State id, DynamicCommandItem item, List<IEvalElement> formulas) {
		this.id = id;
		this.item = item;
		this.formulas = new ArrayList<>(formulas);
	}

	@Override
	public void writeCommand(IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME);
		pto.printAtomOrNumber(id.getId());
		pto.printAtom(item.getCommand());
		pto.openList();
		for (IEvalElement formula : formulas) {
			formula.printProlog(pto);
		}
		pto.closeList();
		pto.printVariable(TABLE_VAR);
		pto.closeTerm();
	}

	public TableData getTable() {
		return table;
	}

	@Override
	public void processResult(ISimplifiedROMap<String, PrologTerm> bindings) {
		this.table = TableData.fromProlog(bindings.get(TABLE_VAR));
	}
}
