package de.prob;

import java.io.File;
import java.io.FilenameFilter;

import com.google.inject.Inject;

import de.prob.webconsole.GroovyExecution;

class Shell {

	private GroovyExecution executor;

	@Inject
	public Shell(GroovyExecution executor) {
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
		System.out.println("Running " + script.getAbsolutePath());

		executor.getBindings().setVariable("dir", dir);
		executor.runScript(script.getAbsolutePath());

	}

	public void runScript(final File file) {
		runScript(file.getAbsolutePath(), file);
	}
}
