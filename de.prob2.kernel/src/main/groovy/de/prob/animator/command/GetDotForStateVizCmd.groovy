package de.prob.animator.command

import de.prob.parser.ISimplifiedROMap
import de.prob.prolog.output.IPrologTermOutput
import de.prob.prolog.term.PrologTerm
import de.prob.statespace.State

public class GetDotForStateVizCmd extends AbstractCommand {

	private static final String PROLOG_COMMAND_NAME = "write_dot_for_state_viz"
	
	
	State id
	File tempFile
	String content

	def GetDotForStateVizCmd(State id) {
		this.id = id
		tempFile = File.createTempFile("dotSM", ".dot")
	}

	@Override
	def void writeCommand(IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME)
		pto.printAtomOrNumber(id.getId())
		pto.printAtom(tempFile.getAbsolutePath())
		pto.closeTerm()
	}

	@Override
	def void processResult(ISimplifiedROMap<String, PrologTerm> bindings) {
		content = tempFile.getText()
	}
}
