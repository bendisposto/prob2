package de.prob;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import javax.script.ScriptException;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Stage;

import de.prob.annotations.Home;
import de.prob.cli.ProBInstanceProvider;
import de.prob.scripting.Api;
import de.prob.scripting.Installer;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.io.File.separator;

/**
 * The Main class initializes ProB 2.0. This class should NOT be instantiated
 * but should rather be started from a .jar file, accessed through Guice via
 * {@code ServletContextListener.getInjector().getInstance()} with Main.class as
 * parameter, or started in a jetty server via {@code WebConsole.run()}.
 *
 * @author joy
 *
 */
public class Main {

	private static int maxCacheSize = 100;
	private static final Logger logger = LoggerFactory.getLogger(Main.class);
	private final CommandLineParser parser;
	private final Options options;
	private final Shell shell;

	private static Injector injector = null;

	private static final String PROB2_BUILD_PROPERTIES_FILE = "/prob2-build.properties";

	private static final Properties buildProperties;
	static {
		buildProperties = new Properties();
		final InputStream is = Main.class.getResourceAsStream(PROB2_BUILD_PROPERTIES_FILE);
		if (is == null) {
			logger.error("Build properties not found, this should never happen!");
		} else {
			try (final Reader r = new InputStreamReader(is, StandardCharsets.UTF_8)) {
				buildProperties.load(r);
			} catch (IOException e) {
				logger.error("IOException while loading build properties, this should never happen!", e);
			}
		}
	}

	public static synchronized Injector getInjector() {
		if (injector == null) {
			injector = Guice.createInjector(Stage.PRODUCTION, new MainModule());
		}
		return injector;
	}

	/**
	 * Allows to customize the Injector. Handle with care!
	 *
	 * @param i
	 *            the new injector to use
	 */
	public static synchronized  void setInjector(final Injector i) {
		injector = i;
	}

	/**
	 * String representing the ProB home directory. Calls method
	 * {@link Main#getProBDirectory()}
	 */
	public static final String PROB_HOME = getProBDirectory();

	/**
	 * Parameters are injected by Guice via {@link MainModule}. This class
	 * should NOT be instantiated by hand.
	 *
	 * @param parser
	 *            command-line parser
	 * @param options
	 *            command-line options
	 * @param shell
	 *            ProB shell
	 * @param probdir
	 *            the ProB home directory
	 */
	@Inject
	public Main(final CommandLineParser parser, final Options options, final Shell shell, @Home String probdir) {
		this.parser = parser;
		this.options = options;
		this.shell = shell;
		System.setProperty("prob.stdlib", probdir + File.separator + "stdlib");
		logger.debug("Java version: {}", System.getProperty("java.version"));
	}

	private void run(final String[] args) throws IOException, ScriptException {

		try {
			CommandLine line = parser.parse(options, args);
			if (line.hasOption("maxCacheSize")) {
				logger.debug("setting maximum cache size requested");
				String value = line.getOptionValue("maxCacheSize");
				logger.debug("retrieved maxSize");
				maxCacheSize = Integer.valueOf(value);
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
		return Installer.DEFAULT_HOME;
	}

	public static String getVersion() {
		return buildProperties.getProperty("version");
	}

	public static String getGitSha() {
		return buildProperties.getProperty("git");
	}

	public static int getMaxCacheSize() {
		return maxCacheSize;
	}

	/**
	 * Start the ProB 2.0 shell with argument -s. Run integration tests with
	 * -test /path/to/testDir
	 *
	 * @param args
	 *            command-line arguments
	 */
	public static void main(final String[] args) {

		try {
			Main main = getInjector().getInstance(Main.class);
			Api api = getInjector().getInstance(Api.class);
		    logger.info("probcli version: {}",api.getVersion().toString());

			main.run(args);
		} catch (Throwable e) {
			getInjector().getInstance(ProBInstanceProvider.class).shutdownAll();
			logger.error("Unhandled exception", e);
			System.exit(-1);
		}
		System.exit(0);
	}

}
