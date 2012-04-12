package de.prob;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.File;
import java.io.IOException;

import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.tools.shell.IO;
import org.codehaus.groovy.tools.shell.PShell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.prob.annotations.Version;
import de.prob.scripting.Api;

class Shell {

	private final Logger logger = LoggerFactory.getLogger(Shell.class);
	private final Api api;
	private final String version;

	@Inject
	public Shell(final Api api, @Version final String version) {

		this.api = api;
		this.version = version;
	}

	public void repl() {
		logger.trace("Starting REPL");
		IO io = new IO();
		// io.setVerbosity(Verbosity.QUIET);
		Binding binding = new Binding();
		binding.setVariable("api", api);
		PShell shell = new PShell(this.getClass().getClassLoader(), binding,
				io, version);
		shell.run("");
	}

	public void runScript(final File script) throws CompilationFailedException,
			IOException {
		logger.trace("Running " + script.getAbsolutePath());
		Binding binding = new Binding();
		binding.setVariable("api", api);
		GroovyShell s = new GroovyShell(binding);
		s.evaluate(script);
	}
}
