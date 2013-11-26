package de.prob;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;

import javax.script.ScriptEngine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.prob.scripting.ScriptEngineProvider;

class Shell {

	private final ScriptEngineProvider sep;
	private final Logger logger = LoggerFactory.getLogger(Shell.class);

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

	public void runScript(final File file) throws Throwable {
		runScript(file.getAbsolutePath(), file);
	}
}
