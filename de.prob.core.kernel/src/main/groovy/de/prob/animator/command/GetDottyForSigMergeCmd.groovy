package de.prob.animator.command

import de.prob.parser.ISimplifiedROMap
import de.prob.prolog.output.IPrologTermOutput
import de.prob.prolog.term.PrologTerm

public class GetDottyForSigMergeCmd extends AbstractCommand {

	def List<String> ignored

	def GetDottyForSigMergeCmd(List<String> ignored) {
		this.ignored = ignored
		tempFile = File.createTempFile("dotSM", ".dot")
	}

	String expression
	File tempFile
	String content

	@Override
	def void writeCommand(IPrologTermOutput pto) {
		pto.openTerm("write_dotty_signature_merge")
		pto.openList()
		ignored.each { pto.printAtom(it) }
		pto.closeList()
		pto.printAtom(tempFile.getAbsolutePath())
		pto.closeTerm()
	}

	@Override
	def void processResult(ISimplifiedROMap<String, PrologTerm> bindings) {
		content = tempFile.getText();
	}
}
