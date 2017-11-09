package de.prob.animator.command;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;
import groovy.lang.Closure;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.ResourceGroovyMethods;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class GetDottyForSigMergeCmd extends AbstractCommand {
	public GetDottyForSigMergeCmd(List<String> ignored) {
		this.ignored = ignored;
		try {
			tempFile = File.createTempFile("dotSM", ".dot");
		} catch (IOException e){
			e.printStackTrace();
		}
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME);
		pto.openList();
		DefaultGroovyMethods.each(ignored, new Closure<IPrologTermOutput>(this, this) {
			public IPrologTermOutput doCall(String it) {
				return pto.printAtom(it);
			}

			public IPrologTermOutput doCall() {
				return doCall(null);
			}

		});
		pto.closeList();
		pto.printAtom(tempFile.getAbsolutePath());
		pto.closeTerm();
	}

	@Override
	public void processResult(ISimplifiedROMap<String, PrologTerm> bindings) {
		try {
			content = ResourceGroovyMethods.getText(tempFile);
		} catch (IOException e){
			e.printStackTrace();
		}
	}

	public List<String> getIgnored() {
		return ignored;
	}

	public void setIgnored(List<String> ignored) {
		this.ignored = ignored;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public File getTempFile() {
		return tempFile;
	}

	public void setTempFile(File tempFile) {
		this.tempFile = tempFile;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	private static final String PROLOG_COMMAND_NAME = "write_dotty_signature_merge";
	private List<String> ignored;
	private String expression;
	private File tempFile;
	private String content;
}
