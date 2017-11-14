package de.prob;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;

import de.prob.annotations.Home;
import de.prob.annotations.Logfile;
import de.prob.annotations.Version;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

import static java.io.File.separator;

public class MainConfiguration extends AbstractModule {
	public MainConfiguration() {}

	@Override
	protected void configure() {
		bind(CommandLineParser.class).to(PosixParser.class);
		bind(String.class).annotatedWith(Version.class).toInstance(Main.getVersion());
		bind(ClassLoader.class).annotatedWith(Names.named("Classloader")).toInstance(Main.class.getClassLoader());

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
	@Provides
	public final Options getCommandlineOptions() {
		Options options = new Options();
		options.addOption(null, "maxCacheSize", true, "set the cache size for the states in the StateSpace");

		OptionGroup mode = new OptionGroup();
		mode.setRequired(true);
		// TODO: add modelchecking option
		// mode.addOption(new Option("mc", "modelcheck", false, "start ProB model checking"));
		mode.addOption(new Option(null, "script", true, "run a Groovy script or all .groovy files from a directory"));
		options.addOptionGroup(mode);
		
		return options;
	}
}
