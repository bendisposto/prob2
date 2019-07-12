package de.prob.animator.command;

import de.prob.animator.command.AbstractCommand;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

import java.util.HashMap;
import java.util.Map;

/**
 * Fetches a map of id to filename from ProB for the visualization.
 */
public class GetImagesForMachineCommand extends AbstractCommand {

	private static final String PROLOG_COMMAND_NAME = "get_animation_image_list";
	private static final String VARIABLE_NAME = "Images";

	@Override
	public void writeCommand(IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME);
		pto.printVariable(VARIABLE_NAME);
		pto.closeTerm();
	}

	Map<Integer, String> images = new HashMap<>();

	@Override
	public void processResult(ISimplifiedROMap<String, PrologTerm> bindings) {
		ListPrologTerm entries = BindingGenerator.getList(bindings.get(VARIABLE_NAME));
		for (PrologTerm t : entries) {
			int id = BindingGenerator.getInteger(t.getArgument(1)).getValue().intValue();
			String name = t.getArgument(2).getFunctor();
			images.put(id, name);
		}
	}

	public Map<Integer, String> getImages() {
		return images;
	}

}
