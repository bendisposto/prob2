package de.prob;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import com.google.inject.Inject;

import de.prob.cli.ProBInstanceProvider;
import de.prob.scripting.ScriptEngineProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Shell {

	private final ScriptEngineProvider sep;
	private final Logger logger = LoggerFactory.getLogger(Shell.class);
	private ProBInstanceProvider ProBs;

	@Inject
	public Shell(final ScriptEngineProvider executor, ProBInstanceProvider ProBs) {
		sep = executor;
		this.ProBs = ProBs;
	}

	public void runScript(final File script, final boolean silent) throws IOException, ScriptException {
		if (script.isDirectory()) {
			long time = System.currentTimeMillis();
			File[] files = script.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(final File arg0, final String arg1) {
					return arg1.endsWith(".groovy");
				}
			});
			if (files != null) {
				for (File file : files) {
					runScript(file, silent);
				}
				if (!silent) {
					System.out.println("TOTAL TIME: " + (System.currentTimeMillis() - time));
				}
			}
		} else {
			runSingleScript(script.getAbsoluteFile().getParent(), script, silent);
		}
	}

	private void runSingleScript(final String dir, final File scriptFile, final boolean silent) throws IOException, ScriptException {
		long time = System.currentTimeMillis();
		logger.debug("Runnning script: {}", scriptFile.getAbsolutePath());
		ScriptEngine executor = sep.get();
		executor.put("dir", dir);
		executor.put("inConsole", false);

		if (!silent) {
			System.out.print(scriptFile.getName());
		}
		Object res = null;
		try (FileReader fr = new FileReader(scriptFile)) {
			res = executor.eval(fr);
		} catch (IOException e) {
			System.err.printf("Could not read script %s: %s%n", scriptFile, e);
			logger.error("Could not read script", e);
			throw e;
		} catch (ScriptException e) {
			System.err.printf("Exception thrown by script %s: %s%n", scriptFile, e);
			logger.error("Exception thrown by script", e);
			throw e;
		}
		ProBs.shutdownAll();
		if (!silent) {
			double seconds = (System.currentTimeMillis() - time) / 1000.0;
			System.out.println(" - " + res.toString() + " (" + String.format("%.4g", seconds) + " s)");
		}
	}
}
