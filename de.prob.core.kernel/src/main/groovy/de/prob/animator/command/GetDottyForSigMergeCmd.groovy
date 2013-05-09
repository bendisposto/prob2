package de.prob.animator.command

import de.prob.parser.ISimplifiedROMap
import de.prob.prolog.output.IPrologTermOutput
import de.prob.prolog.term.PrologTerm

class GetDottyForSigMergeCmd extends AbstractCommand {

	String expression
	File tempFile
	String content

	def GetDottyForSigMergeCmd() {
		tempFile = File.createTempFile("dotSM", ".dot")
	}

	@Override
	def void writeCommand(IPrologTermOutput pto) {
		pto.openTerm("write_dotty_signature_merge")
		pto.printAtom(tempFile.getAbsolutePath())
		pto.closeTerm()
	}

	@Override
	def void processResult(ISimplifiedROMap<String, PrologTerm> bindings) {
		content = tempFile.getText();
	}
}
