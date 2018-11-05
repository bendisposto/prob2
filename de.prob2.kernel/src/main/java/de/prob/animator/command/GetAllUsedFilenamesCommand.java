package de.prob.animator.command;

import java.util.ArrayList;
import java.util.List;

import de.prob.animator.command.AbstractCommand;
import de.prob.animator.domainobjects.MachineFileInformation;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class GetAllUsedFilenamesCommand extends AbstractCommand {
	
	private static final String PROLOG_COMMAND_NAME = "get_machine_files";

	private static final String FILES = "Files";
	
	private List<MachineFileInformation> files = new ArrayList<>();

	@Override
	public void writeCommand(IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME);
		pto.printVariable(FILES);
		pto.closeTerm();
	}

	@Override
	public void processResult(ISimplifiedROMap<String, PrologTerm> bindings) {
		ListPrologTerm res = (ListPrologTerm) bindings.get(FILES);
		for (PrologTerm prologTerm : res) {
			final String name = PrologTerm.atomicString(prologTerm.getArgument(1));
			final String extension = PrologTerm.atomicString(prologTerm.getArgument(2));
			final String path = PrologTerm.atomicString(prologTerm.getArgument(3));
			files.add(new MachineFileInformation(name, extension, path));
		}
	}

	public List<MachineFileInformation> getFiles() {
		return files;
	}

}