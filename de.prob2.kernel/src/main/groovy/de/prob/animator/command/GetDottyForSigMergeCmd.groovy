package de.prob.animator.command

import de.prob.parser.ISimplifiedROMap
import de.prob.prolog.output.IPrologTermOutput
import de.prob.prolog.term.PrologTerm

public class GetDottyForSigMergeCmd extends AbstractCommand {

	private static final String PROLOG_COMMAND_NAME = "write_dotty_signature_merge"

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
		pto.openTerm(PROLOG_COMMAND_NAME)
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
