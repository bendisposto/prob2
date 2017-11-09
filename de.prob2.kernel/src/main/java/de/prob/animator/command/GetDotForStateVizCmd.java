package de.prob.animator.command;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.State;
import org.codehaus.groovy.runtime.ResourceGroovyMethods;

import java.io.File;
import java.io.IOException;

public class GetDotForStateVizCmd extends AbstractCommand {
	public GetDotForStateVizCmd(State id) throws Exception{
		this.id = id;
		try {
			tempFile = File.createTempFile("dotSM", ".dot");
		} catch (IOException e){
			throw new Exception();
		}
	}

	@Override
	public void writeCommand(IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME);
		pto.printAtomOrNumber(id.getId());
		pto.printAtom(tempFile.getAbsolutePath());
		pto.closeTerm();
	}

	@Override
	public void processResult(ISimplifiedROMap<String, PrologTerm> bindings){
		try {
			content = ResourceGroovyMethods.getText(tempFile);
		} catch (IOException e){
			//throw new Exception();
		}
	}

	public State getId() {
		return id;
	}

	public void setId(State id) {
		this.id = id;
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

	private static final String PROLOG_COMMAND_NAME = "write_dot_for_state_viz";
	private State id;
	private File tempFile;
	private String content;
}
