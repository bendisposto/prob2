package de.prob;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.prob.webconsole.GroovyExecution;

class Shell {

	private final GroovyExecution executor;
	private final Logger logger = LoggerFactory.getLogger(Shell.class);

	@Inject
	public Shell(final GroovyExecution executor) {
		this.executor = executor;
	}

	public void runScript(final String dir, final File script)
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

		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(
				script.getAbsolutePath()));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();

		executor.getBindings().setVariable("dir", dir);

		executor.runSilentScript(fileData.toString(), true, true);

	}

	public void runScript(final File file) throws Throwable {
		runScript(file.getAbsolutePath(), file);
	}
}
