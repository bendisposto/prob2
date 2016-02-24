package de.prob.animator.command

import de.prob.parser.ISimplifiedROMap
import de.prob.prolog.output.IPrologTermOutput
import de.prob.prolog.term.PrologTerm

class GetDottyForTransitionDiagramCmd extends AbstractCommand {

	private static final String PROLOG_COMMAND_NAME = "write_dotty_transition_diagram"

	String expression
	File tempFile
	String content

	def GetDottyForTransitionDiagramCmd(String e) {
		expression = e
		tempFile = File.createTempFile("dotTD", ".dot")
	}

	@Override
	def void writeCommand(IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME)
		pto.printAtom(expression)
		pto.printAtom(tempFile.getAbsolutePath())
		pto.closeTerm()
	}

	@Override
	def void processResult(ISimplifiedROMap<String, PrologTerm> bindings) {
		content = tempFile.getText();
	}
}
