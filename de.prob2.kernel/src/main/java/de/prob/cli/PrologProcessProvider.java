package de.prob.cli;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.prob.annotations.Home;
import de.prob.cli.ModuleCli.DebuggingKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class PrologProcessProvider implements Provider<ProcessHandle> {
	private static final Logger logger = LoggerFactory.getLogger(PrologProcessProvider.class);
	private static final List<Process> toDestroyOnShutdown = Collections.synchronizedList(new ArrayList<>());

	static {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			synchronized (toDestroyOnShutdown) {
				for (final Process process : toDestroyOnShutdown) {
					process.destroy();
				}
			}
		}, "Prolog Process Destroyer"));
	}

	private final String debuggingKey;
	private final String dir;
	private final OsSpecificInfo osInfo;

	@Override
	public ProcessHandle get() {
		return makeProcess();
	}

	@Inject
	public PrologProcessProvider(final OsSpecificInfo osInfo,
			@DebuggingKey final String debuggingKey, @Home final String path) {
		this.osInfo = osInfo;
		this.debuggingKey = debuggingKey;
		// Create ProB home directory if necessary.
		new File(path).mkdirs();
		dir = path;

	}

	private ProcessHandle makeProcess() {
		final String executable = dir + osInfo.getCliName();
		List<String> command = makeCommand(executable);
		final ProcessBuilder pb = new ProcessBuilder();
		pb.command(command);
		pb.environment().put("PROB_DEBUGGING_KEY", debuggingKey);
		pb.environment().put("TRAILSTKSIZE", "1M");
		pb.environment().put("PROLOGINCSIZE", "50M");
		pb.environment().put("PROB_HOME", dir);
		pb.redirectErrorStream(true);
		final Process prologProcess;
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

		toDestroyOnShutdown.add(prologProcess);
		return new ProcessHandle(prologProcess, debuggingKey);
	}

	private List<String> makeCommand(final String executable) {
		List<String> command = new ArrayList<>();
		if (osInfo.getHelperCmd() != null) {
			command.add(osInfo.getHelperCmd());
		}
		command.add(executable);
		command.add("-sf");
		return command;
	}
}
