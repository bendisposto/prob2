package de.prob.animator.command;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.prob.animator.domainobjects.DotCommandItem;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.State;

public class GetSvgForVisualizationCommand extends AbstractCommand {
		
	private static final String PROLOG_COMMAND_NAME = "call_dot_command_and_dot";
	
	private File file;
	
	private List<IEvalElement> formulas;
	
	private DotCommandItem item;
	
	private final State id;
	
	public GetSvgForVisualizationCommand(final State id, DotCommandItem item, File file, List<IEvalElement> formulas) {
		this.id = id;
		this.item = item;
		this.file = file;
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
		pto.printAtom("svg");
		pto.printAtom(file.getAbsolutePath().toString());
		pto.closeTerm();
	}

	@Override
	public void processResult(ISimplifiedROMap<String, PrologTerm> bindings) {
		
	}
	
}
