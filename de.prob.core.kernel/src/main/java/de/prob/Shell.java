package de.prob;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

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

	public void runScript(final String dir, final File script) {
		if (script.isDirectory()) {
			File[] files = script.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(final File arg0, final String arg1) {
					return arg1.endsWith(".groovy");
				}
			});
			for (File file : files) {
				runScript(script.getAbsolutePath(), file);
			}
		} else {
			runSingleScript(dir, script);
		}
	}

	private void runSingleScript(final String dir, final File script) {
		System.out.println("Running " + script.getAbsolutePath());
		Binding binding = new Binding();
		binding.setVariable("api", api);
		binding.setVariable("dir", dir);
		GroovyShell s = new GroovyShell(binding);
		try {
			s.evaluate("script = new File('"
					+ script.getAbsolutePath()
					+ "'); try { evaluate(script.getText()) } catch (Throwable t) {t.printStackTrace(); System.exit(-1)} finally {}");
			s.run(script, new ArrayList<String>());
		} catch (CompilationFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void runScript(final File file) {
		runScript(file.getAbsolutePath(), file);
	}
}
