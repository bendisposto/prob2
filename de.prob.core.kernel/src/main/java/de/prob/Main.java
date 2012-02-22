package de.prob;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.codehaus.groovy.tools.shell.util.HelpFormatter;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

import de.prob.animator.IAnimator;
import de.prob.annotations.Logfile;

public class Main {

	static {
		if (System.getProperty("PROB_LOG_CONFIG") == null) {
			System.setProperty("PROB_LOG_CONFIG", "production.xml");
		}
		// System.setProperty("PROB_LOG_CONFIG", "fulltrace.xml");
	}

	public final static Injector INJECTOR = Guice
			.createInjector(new MainModule());

	public IAnimator getAnimator() {
		return INJECTOR.getInstance(IAnimator.class);
	}

	private final CommandLineParser parser;
	private final Options options;
	private final Provider<Shell> shellProvider;

	@Inject
	public Main(@Logfile final String logfile, final CommandLineParser parser,
			final Options options, final Provider<Shell> shellProvider) {
		this.parser = parser;
		this.options = options;
		this.shellProvider = shellProvider;
		System.setProperty("PROB_LOGFILE", logfile);
	}

	void run(final String[] args) {
		try {
			CommandLine line = parser.parse(options, args);
			if (line.hasOption("shell")) {
				shellProvider.get().repl();
			}
		} catch (ParseException exp) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("java -jar probcli-deploy.jar", options);
		}
	}

	public static void main(final String[] args) {
		if (System.getProperty("PROB_LOG_CONFIG") == null) {
			System.setProperty("PROB_LOG_CONFIG", "production.xml");
		}
		System.setProperty("PROB_LOG_CONFIG", "fulltrace.xml");
		Injector injector = Guice.createInjector(new MainModule());
		Main main = injector.getInstance(Main.class);

		// IStateSpace instance = injector.getInstance(IStateSpace.class);

		main.run(args);
		// System.exit(0);
	}

}