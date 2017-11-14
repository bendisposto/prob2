package de.prob.animator.command;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;
import org.codehaus.groovy.runtime.ResourceGroovyMethods;

import java.io.File;
import java.io.IOException;

public class GetDottyForTransitionDiagramCmd extends AbstractCommand {
	public GetDottyForTransitionDiagramCmd(String e) {
		expression = e;
		try {
			tempFile = File.createTempFile("dotTD", ".dot");
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

	@Override
	public void writeCommand(IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME);
		pto.printAtom(expression);
		pto.printAtom(tempFile.getAbsolutePath());
		pto.closeTerm();
	}

	@Override
	public void processResult(ISimplifiedROMap<String, PrologTerm> bindings) {
		try {
			content = ResourceGroovyMethods.getText(tempFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

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

	private static final String PROLOG_COMMAND_NAME = "write_dotty_transition_diagram";
	private String expression;
	private File tempFile;
	private String content;
}
