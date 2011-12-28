package de.prob.cli;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.prob.annotations.Home;
import de.prob.cli.ModuleCli.DebuggingKey;

class PrologProcessProvider implements Provider<ProcessHandle> {

	private final Logger logger = LoggerFactory
			.getLogger(PrologProcessProvider.class);
	private final String debuggingKey;
	private final String dir;
	private final OsSpecificInfo osInfo;

	public ProcessHandle get() {
		return makeProcess();
	}

	@Inject
	public PrologProcessProvider(final OsSpecificInfo osInfo,
			@DebuggingKey final String debuggingKey, @Home final String path) {
		this.osInfo = osInfo;
		this.debuggingKey = debuggingKey;
		this.dir = checkCliPath(path);

	}

	private ProcessHandle makeProcess() {
		final String executable = dir + osInfo.cliName;
		List<String> command = makeCommand(executable);
		Process prologProcess = null;
		final ProcessBuilder pb = new ProcessBuilder();
		pb.command(command);
		pb.environment().put("PROB_DEBUGGING_KEY", debuggingKey);
		pb.environment().put("TRAILSTKSIZE", "1M");
		pb.environment().put("PROLOGINCSIZE", "50M");
		pb.environment().put("PROB_HOME", dir);
		pb.redirectErrorStream(true);
		try {
			logger.info("Starting ProB's Prolog Core. Path is {}", executable);
			prologProcess = pb.start();
			logger.debug("Cli started");
		} catch (IOException e) {
			String msg = "Problem while starting up ProB CLI. Tried to execute: "
					+ executable;
			logger.error(msg, e);
			return null;
		}

		if (prologProcess == null) {
			logger.error("CLI Process is null. Cannot start Prolog part of ProB.");
			return null;
		}
		setShutdownHook(prologProcess);
		return new ProcessHandle(prologProcess, debuggingKey);
	}

	private void setShutdownHook(final Process p) {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				p.destroy();
			}
		}));
	}

	private List<String> makeCommand(final String executable) {
		List<String> command = new ArrayList<String>();
		if (osInfo.helperCmd != null) {
			command.add(osInfo.helperCmd);
		}
		command.add(executable);
		command.add("-sf");
		return command;
	}

	/**
	 * Checks if the directory exists and creates it if it does not exist.
	 * 
	 * @return Name of the ProB Directory
	 * @throws SecurityException
	 *             if the directory does not exist end cannot be created.
	 */
	private String checkCliPath(final String dirname) {
		File dir = new File(dirname);
		if (!dir.exists()) {
			try {
				dir.mkdirs();
			} catch (SecurityException e) {
				throw e;
			}
		}
		return dirname;
	}

}
