package de.prob;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import jline.ConsoleReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import de.prob.annotations.Language;
import de.prob.scripting.Api;

class Shell {

	private final Logger logger = LoggerFactory.getLogger(Shell.class);
	private volatile boolean terminated;
	private final ConsoleReader reader;
	private final ScriptEngine engine;
	private final Api api;
	private final ClassLoader classloader;

	@Inject
	public Shell(@Language final String lang, final Api api,
			final ConsoleReader reader,
			@Named("Shell") final ClassLoader classloader,
			final ScriptEngineManager manager) {

		this.api = api;
		this.reader = reader;
		this.classloader = classloader;
		logger.trace("Get Engine");
		engine = manager.getEngineByName(lang);
		checkEngine(lang, manager);
	}

	boolean isTerminated() {
		return terminated;
	}

	private void runInitScript(final List<String> extensions) {
		InputStream script = getInitScript("init", extensions);
		if (script == null) {
			logger.warn("No init script found.");

		} else {
			try {
				logger.trace("Evaluate Init Script");
				engine.eval(new InputStreamReader(script));
				logger.trace("Evaluated Init Script");
			} catch (ScriptException e) {
				logger.error("Error in init script.", e);
				throw new IllegalStateException(e);
			}
		}
	}

	private InputStream getInitScript(final String prefix,
			final List<String> extensions) {
		for (String ex : extensions) {
			String name = prefix + '.' + ex;
			logger.trace("Check for: {}.", name);
			InputStream stream = classloader.getResourceAsStream(name);
			if (stream != null) {
				logger.trace("Found: {}.", name);
				return stream;
			}
		}
		return null;
	}

	private void checkEngine(final String lang,
			final ScriptEngineManager manager) {
		if (engine == null) {
			logger.error("Unsupported language: {}.", lang);
			if (logger.isDebugEnabled()) {
				printInstalledLanguages(manager);
			}
			throw new UnsupportedOperationException();
		}
	}

	private void prepareBindings() {
		engine.put("api", api);
		engine.put("console_reader", reader);
	}

	Object evaluate(final String line) throws ScriptException {
		if ("EXIT".equals(line.trim())) {
			terminated = true;
		} else
			return engine.eval(line);
		return "";
	}

	private void printInstalledLanguages(final ScriptEngineManager manager) {
		List<ScriptEngineFactory> factories = manager.getEngineFactories();
		for (ScriptEngineFactory f : factories) {
			String name = f.getEngineName();
			List<String> shortnames = f.getNames();
			logger.debug("Available: {} ({})", name,
					Joiner.on(", ").join(shortnames));
		}
	}

	public void run() {
		prepareBindings();
		System.out.println(banner());
		logger.trace("Run Init Script");
		runInitScript(engine.getFactory().getExtensions());
		logger.trace("Start Console");
		repl();
		logger.trace("Shell Terminated.");
	}

	public String banner() {

		String langName = engine.getFactory().getLanguageName();
		return "Welcome to the ProB interactive shell.\n"
				+ "======================================\n"
				+ "The shell uses " + langName + " "
				+ engine.getFactory().getLanguageVersion() + ".\n"
				// + "For specific information on ProB type HELP\n"
				+ "To exit, type: EXIT";
	}

	private void repl() {
		try {
			while (!terminated) {
				String line = reader.readLine(">>> ");
				Object resulting;
				try {
					resulting = evaluate(line);
					String result = (resulting == null) ? "null" : resulting
							.toString();
					System.out.println(result);
				} catch (ScriptException e) {
					System.out.println(e.getLocalizedMessage());
					logger.debug(e.getMessage(), e);
				}
			}
		} catch (IOException e) {
			logger.error("Read-Error", e);
		}
	}

}
