package de.prob;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import de.prob.annotations.Home;
import de.prob.annotations.Version;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;

public class MainConfiguration extends AbstractModule {
	public MainConfiguration() {}

	@Override
	protected void configure() {
		bind(CommandLineParser.class).to(DefaultParser.class);
		bind(String.class).annotatedWith(Version.class).toInstance(Main.getVersion());
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
