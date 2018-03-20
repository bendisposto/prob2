package de.prob.animator.command;

import java.util.ArrayList;
import java.util.List;

import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class GetMachineIdentifiersCommand extends AbstractCommand {

	public enum Category {
		MACHINES("machines"), VARIABLES("variables"), CONSTANTS("constants");

		String prologAtom;

		Category(String atom) {
			this.prologAtom = atom;
		}

		public String getPrologAtom() {
			return this.prologAtom;
		}
	}

	private static final String PROLOG_COMMAND_NAME = "get_machine_identifiers";
	private static final String RESULT_VARIABLE = "Identifiers";
	private final Category category;
	private final List<String> identifiers = new ArrayList<>();

	public GetMachineIdentifiersCommand(Category category) {
		this.category = category;
	}

	@Override
	public void writeCommand(IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME).printAtom(category.getPrologAtom()).printVariable(RESULT_VARIABLE)
				.closeTerm();
	}

	@Override
	public void processResult(ISimplifiedROMap<String, PrologTerm> bindings) {
		ListPrologTerm list = BindingGenerator.getList(bindings, RESULT_VARIABLE);
		for (PrologTerm prologTerm : list) {
			identifiers.add(prologTerm.getFunctor());
		}
	}

	public List<String> getIdentifiers() {
		return this.identifiers;
	}

}
