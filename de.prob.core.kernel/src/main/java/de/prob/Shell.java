package de.prob;

import java.io.File;
import java.io.FilenameFilter;

import org.slf4j.Logger;

import com.google.inject.Inject;

import de.prob.exception.ProBLoggerFactory;
import de.prob.webconsole.GroovyExecution;

class Shell {

	private final GroovyExecution executor;
	private final Logger logger = ProBLoggerFactory.getLogger(Shell.class);

	@Inject
	public Shell(final GroovyExecution executor) {
		this.executor = executor;
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

		logger.debug("Runnning script: {}", script.getAbsolutePath());

		executor.getBindings().setVariable("dir", dir);
		executor.runSilentScript(script.getAbsolutePath());

	}

	public void runScript(final File file) {
		runScript(file.getAbsolutePath(), file);
	}
}
