package de.prob.animator.command;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public class GetDottyForSigMergeCmd extends AbstractCommand {
	private static final String PROLOG_COMMAND_NAME = "write_dotty_signature_merge";

	private final List<String> ignored;
	private final File tempFile;
	private String content;

	public GetDottyForSigMergeCmd(List<String> ignored) {
		this.ignored = ignored;
		try {
			tempFile = File.createTempFile("dotSM", ".dot");
		} catch (IOException e){
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME);
		pto.openList();
		ignored.forEach(pto::printAtom);
		pto.closeList();
		pto.printAtom(tempFile.getAbsolutePath());
		pto.closeTerm();
	}

	@Override
	public void processResult(ISimplifiedROMap<String, PrologTerm> bindings) {
		try (final BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(tempFile)))) {
			content = r.lines().collect(Collectors.joining("\n"));
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public String getContent() {
		return content;
	}
}
