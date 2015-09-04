package de.prob;

import static java.io.File.separator;

import java.io.File;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Stage;

import de.prob.scripting.Downloader;
import de.prob.scripting.FileHandler;

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

	public static int maxCacheSize = 100;
	private final Logger logger = LoggerFactory.getLogger(Main.class);
	private final CommandLineParser parser;
	private final Options options;
	private final Shell shell;

	private static Injector INJECTOR = Guice.createInjector(Stage.PRODUCTION,
			new MainModule());

	/**
	 * Name of file in which the preferences are saved. Currently
	 * "prob2preferences"
	 */
	public static final String PREFERENCE_FILE_NAME = "prob2preferences";

	public static Injector getInjector() {
		return INJECTOR;
	}

	/**
	 * Allows to customize the Injector. Handle with care!
	 *
	 * @param i
	 */
	public static void setInjector(final Injector i) {
		INJECTOR = i;
	}

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

	private final Downloader downloader;

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
			final Shell shell, final Downloader downloader) {
		this.parser = parser;
		this.options = options;
		this.shell = shell;
		this.downloader = downloader;
		logger.debug("Java version: {}", System.getProperty("java.version"));
	}

	private void run(final String[] args) throws Throwable {

		try {
			CommandLine line = parser.parse(options, args);
			if (line.hasOption("upgrade") || line.hasOption("cli")) {
				String version = line.hasOption("upgrade") ? line
						.getOptionValue("upgrade") : line.getOptionValue("cli");
				if (version == null) {
					version = "latest";
				}
				if (version.equals("cspm")) {
					System.out.println(downloader.installCSPM());
				} else {
					System.out.println(downloader.downloadCli(version));
				}
			}

			if (line.hasOption("maxCacheSize")) {
				logger.debug("setting maximum cache size requested");
				String value = line.getOptionValue("maxCacheSize");
				logger.debug("retrieved maxSize");
				Main.maxCacheSize = Integer.valueOf(value);
				logger.debug("Max size set successfully to {}", value);
			}

			if (line.hasOption("script")) {
				logger.debug("Run Script");
				String value = line.getOptionValue("script");
				shell.runScript(new File(value), false);
			}
		} catch (ParseException exp) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("java -jar probcli.jar", options);
			System.exit(-1);
		}
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

	public static Map<String, String> getGlobalPreferences(
			final Map<String, String> localPrefs) {
		String preferenceFileName = Main.getProBDirectory()
				+ PREFERENCE_FILE_NAME;
		FileHandler handler = new FileHandler();
		Map<String, String> prefs = handler.getMapOfStrings(preferenceFileName);
		if (prefs == null) {
			return localPrefs;
		}
		prefs.putAll(localPrefs);
		return prefs;
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
			System.setProperty("PROB_LOG_CONFIG", LOG_CONFIG);

			Main main = getInjector().getInstance(Main.class);

			main.run(args);
		} catch (Throwable e) {
			e.printStackTrace();
			System.exit(-1);
		}
		System.exit(0);
	}

}
