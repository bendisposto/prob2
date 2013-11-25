package de.prob;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;

import javax.script.ScriptEngine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.prob.scripting.ScriptEngineProvider;

/**
 * A class that provides the user to execute a .groovy script or a directory
 * containing .groovy scripts in the context specified by the given directory.
 * 
 * @author joy
 * 
 */
class Shell {

	private final ScriptEngineProvider sep;
	private final Logger logger = LoggerFactory.getLogger(Shell.class);

	/**
	 * An instance of this class should be either injected into another class,
	 * or fetched from the Guice injector framework so that the
	 * {@link ScriptEngineProvider} executor can be configured correctly.
	 * 
	 * @param executor
	 *            is a {@link ScriptEngineProvider} initialized by Guice
	 */
	@Inject
	public Shell(final ScriptEngineProvider executor) {
		sep = executor;
	}

	private void runScript(final String dir, final File script)
			throws Throwable {
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

	private void runSingleScript(final String dir, final File script)
			throws Throwable {
		logger.debug("Runnning script: {}", script.getAbsolutePath());
		ScriptEngine executor = sep.get();
		executor.put("dir", dir);

		FileReader fr = new FileReader(script);
		executor.eval(fr);
	}

	/**
	 * This method executes either a .groovy script or a directory. If a
	 * directory is specified, all .groovy files within the directory are
	 * executed.
	 * 
	 * @param file
	 *            is either a .groovy file or a directory
	 * @throws Throwable
	 *             if the execution of the script/scripts fails
	 */
	public void runScript(final File file) throws Throwable {
		runScript(file.getAbsolutePath(), file);
	}
}
