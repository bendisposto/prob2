package de.prob;

import static java.io.File.separator;

import java.io.File;
import java.util.Set;
import java.util.WeakHashMap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.prob.exception.ProBAppender;
import de.prob.web.views.Log;
import de.prob.webconsole.ServletContextListener;
import de.prob.webconsole.WebConsole;

/**
 * The Main class initializes ProB 2.0. This class should NOT be instantiated
 * but should rather be started from a .jar file, accessed through Guice via
 * {@link ServletContextListener#getInjector()#getInstance()} with Main.class as
 * parameter, or started in a jetty server via {@link WebConsole#run()}.
 * 
 * @author joy
 * 
 */
public class Main {

	private final Logger logger = LoggerFactory.getLogger(Main.class);
	private final CommandLineParser parser;
	private final Options options;
	private final Shell shell;

	/**
	 * String representing the ProB home directory. Calls method
	 * {@link Main#getProBDirectory()}
	 */
	public final static String PROB_HOME = getProBDirectory();

	/**
	 * String representing the log configuration file. This defaults to
	 * "production.xml" if the System property "PROB_LOG_CONFIG" is not defined.
	 * Otherwise, the system property is used.
	 */
	public final static String LOG_CONFIG = System
			.getProperty("PROB_LOG_CONFIG") == null ? "production.xml" : System
			.getProperty("PROB_LOG_CONFIG");

	private final static WeakHashMap<Process, Boolean> processes = new WeakHashMap<Process, Boolean>();

	/**
	 * Parameters are injected by Guice via {@link MainModule}. This class
	 * should NOT be instantiated by hand.
	 * 
	 * @param parser
	 * @param options
	 * @param shell
	 * @param log
	 */
	@Inject
	public Main(final CommandLineParser parser, final Options options,
			final Shell shell, final Log log) {
		this.parser = parser;
		this.options = options;
		this.shell = shell;
		ProBAppender.initialize(log);
		logger.debug("Java version: {}", System.getProperty("java.version"));
	}

	private void run(final String[] args) throws Throwable {
		String url = "";
		try {
			CommandLine line = parser.parse(options, args);
			if (line.hasOption("browser")) {
				logger.debug("Browser");
				url = line.getOptionValue("browser");
				logger.debug("Browser started");
			}
			runServer(url);
			if (line.hasOption("shell")) {
				while (true) {
				}

			}
			if (line.hasOption("test")) {
				logger.debug("Run Script");
				String value = line.getOptionValue("test");
				shell.runScript(new File(value));
			}
		} catch (ParseException exp) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("java -jar probcli.jar", options);
		}
	}

	private void runServer(final String url) {
		logger.debug("Shell");
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					WebConsole.run(url);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}

	/**
	 * Returns the directory in which the binary files and libraries for ProB
	 * are stored.
	 * 
	 * @return if System Property "prob.home" is defined, the path to this
	 *         directory is returned. Otherwise, the directory specified by
	 *         System Property "user.home" is chosen, and the directory ".prob"
	 *         is appended to it.
	 */
	public static String getProBDirectory() {
		String homedir = System.getProperty("prob.home");
		if (homedir != null) {
			return homedir + separator;
		}
		return System.getProperty("user.home") + separator + ".prob"
				+ separator;
	}

	/**
	 * Start the ProB 2.0 shell with argument -s. Run integration tests with
	 * -test /path/to/testDir
	 * 
	 * @param args
	 * @throws Throwable
	 */
	public static void main(final String[] args) {
		try {
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					Set<Process> keySet = Main.processes.keySet();
					for (Process process : keySet) {
						process.destroy();
					}
				}
			});
			System.setProperty("PROB_LOG_CONFIG", LOG_CONFIG);

			Main main = ServletContextListener.INJECTOR.getInstance(Main.class);

			main.run(args);
		} catch (Throwable e) {
			e.printStackTrace();
			System.exit(-1);
		}
		System.exit(0);
	}

	/**
	 * @param process
	 *            - process is registered here so that it can be destroyed upon
	 *            shutdown of the program.
	 */
	public static void registerPrologProcess(final Process process) {
		processes.put(process, Boolean.TRUE);
	}

	/**
	 * Destroy all processes associated with the program. This is called during
	 * shutdown, or if the ServletContext changes.
	 */
	public static void destroyPrologProcesses() {
		Set<Process> keySet = Main.processes.keySet();
		for (Process process : keySet) {
			process.destroy();
		}
	}
}
