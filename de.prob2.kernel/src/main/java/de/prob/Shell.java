package de.prob;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;

import javax.script.ScriptEngine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.prob.cli.ProBInstanceProvider;
import de.prob.scripting.ScriptEngineProvider;

class Shell {

	private final ScriptEngineProvider sep;
	private final Logger logger = LoggerFactory.getLogger(Shell.class);
	private ProBInstanceProvider ProBs;

	@Inject
	public Shell(final ScriptEngineProvider executor, ProBInstanceProvider ProBs) {
		sep = executor;
		this.ProBs = ProBs;
	}

	private void runScript(final String dir, final File script,
			final boolean silent) throws Throwable {
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
					runScript(script.getAbsolutePath(), file, silent);
				}
				if (!silent) {
					System.out.println("TOTAL TIME: " + (System.currentTimeMillis() - time));
				}
			}
		} else {
			runSingleScript(script.getParent(), script, silent);
		}
	}

	private void runSingleScript(final String dir, final File script, final boolean silent) throws Throwable {
		long time = System.currentTimeMillis();
		logger.debug("Runnning script: {}", script.getAbsolutePath());
		ScriptEngine executor = sep.get();
		executor.put("dir", dir);
		executor.put("inConsole", false);

		if (!silent) {
			System.out.print(script.getName());
		}
		FileReader fr = new FileReader(script);
		Object res = executor.eval(fr);
		ProBs.shutdownAll();
		if (!silent) {
			double seconds = (System.currentTimeMillis() - time) / 1000.0;
			System.out.println(" - " + res.toString() + " ("
					+ String.format("%.4g", seconds) + " s)");
		}
	}

	public void runScript(final File file) throws Throwable {
		runScript(file, true);
	}

	public void runScript(final File file, final boolean silent)
			throws Throwable {
		runScript(file.getAbsolutePath(), file, silent);
	}
}
