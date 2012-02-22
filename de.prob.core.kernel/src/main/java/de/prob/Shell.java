package de.prob;

import groovy.lang.Binding;

import org.codehaus.groovy.tools.shell.Groovysh;
import org.codehaus.groovy.tools.shell.IO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.prob.scripting.Api;

class Shell {

	private final Logger logger = LoggerFactory.getLogger(Shell.class);
	private final Api api;

	@Inject
	public Shell(final Api api) {

		this.api = api;
	}

	public void repl() {
		logger.trace("Starting REPL");
		IO io = new IO();
		// io.setVerbosity(Verbosity.QUIET);
		Binding binding = new Binding();
		binding.setVariable("api", api);
		Groovysh shell = new Groovysh(this.getClass().getClassLoader(),
				binding, io);
		shell.run();
	}

}
