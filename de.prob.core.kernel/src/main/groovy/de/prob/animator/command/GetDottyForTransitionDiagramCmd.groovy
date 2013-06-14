package de.prob.animator.command

import de.prob.parser.ISimplifiedROMap
import de.prob.prolog.output.IPrologTermOutput
import de.prob.prolog.term.PrologTerm

class GetDottyForTransitionDiagramCmd extends AbstractCommand {

	String expression
	File tempFile
	String content

	def GetDottyForTransitionDiagramCmd(String e) {
		expression = e
		tempFile = File.createTempFile("dotTD", ".dot")
	}

	@Override
	def void writeCommand(IPrologTermOutput pto) {
		pto.openTerm("write_dotty_transition_diagram")
		pto.printAtom(expression)
		pto.printAtom(tempFile.getAbsolutePath())
		pto.closeTerm()
	}

	@Override
	def void processResult(ISimplifiedROMap<String, PrologTerm> bindings) {
		content = tempFile.getText();
	}
}
