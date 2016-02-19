package de.prob;

import static java.io.File.separator;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;

import de.prob.annotations.Home;
import de.prob.annotations.Logfile;
import de.prob.annotations.Version;

public class MainConfiguration extends AbstractModule {
	private final Properties buildConstants;

	private final Logger logger = LoggerFactory
			.getLogger(MainConfiguration.class);

	public MainConfiguration() {
		buildConstants = loadBuildConstants();
	}

	@Override
	protected void configure() {
		bind(CommandLineParser.class).to(PosixParser.class);
		bind(String.class).annotatedWith(Version.class).toInstance(
				buildConstants.getProperty("version", "0.0.0"));
		bind(ClassLoader.class).annotatedWith(Names.named("Classloader"))
				.toInstance(Main.class.getClassLoader());

		// TODO: Should this property be set here? Should it be set at all?
		System.setProperty("PROB_LOGFILE", getProBLogfile());
	}

	/**
	 * Calls {@link Main#getProBDirectory()} to find the absolute path
	 * associated with the ProB directory. Binds this path to the {@link Home}
	 * annotation in order to be able to inject it.
	 *
	 * @return the absolute path to ProB directory.
	 */
	@Provides
	@Home
	public final String getProBDirectory() {
		return Main.getProBDirectory();
	}

	/**
	 * Returns the path to the log file associated with ProB 2.0. This is
	 * currently {@link Main#getProBDirectory()}logs/ProB.txt, but this is
	 * subject to change. Binds this path to the {@link Logfile} annotation in
	 * order to be able to inject it.
	 *
	 * @return the path to the fog file for ProB 2.0
	 */
	@Provides
	@Logfile
	public final String getProBLogfile() {
		return getProBDirectory() + "logs" + separator + "ProB.txt";
	}

	/**
	 * @return an {@link Option} object containing the available command line
	 *         options for ProB 2.0
	 */
	@SuppressWarnings("static-access")
	@Provides
	public final Options getCommandlineOptions() {
		Options options = new Options();

		Option maxCacheSize = OptionBuilder
				.withArgName("maxSize")
				.hasArg()
				.withDescription(
						"set the cache size for the states in the StateSpace")
						.create("maxCacheSize");

		Option script = OptionBuilder
				.withArgName("script/dir")
				.hasArg()
				.withDescription(
						"run a Groovy script or all .groovy files from a directory")
						.create("script");

		// TODO: add modelchecking option
		// Option modelcheck = new Option("mc", "modelcheck", false,
		// "start ProB model checking");
		OptionGroup mode = new OptionGroup();
		mode.setRequired(true);
		// mode.addOption(modelcheck);
		mode.addOption(script);
		options.addOptionGroup(mode);
		options.addOption(maxCacheSize);
		return options;
	}

	private Properties loadBuildConstants() {
		ClassLoader classLoader = MainModule.class.getClassLoader();
		InputStream stream = classLoader
				.getResourceAsStream("build.properties");
		Properties properties = new Properties();
		try {
			properties.load(stream);
		} catch (IOException e) {
			logger.debug("Could not load build.properties.", e);
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				logger.debug("Could not close stream.", e);
			}
		}
		return properties;
	}
}
