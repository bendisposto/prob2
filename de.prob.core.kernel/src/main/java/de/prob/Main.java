package de.prob;

import static java.io.File.*;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.google.common.base.Joiner;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import de.prob.animator.IAnimator;
import de.prob.annotations.Logfile;

public class Main {

	static {
		System.setProperty("PROB_LOGFILE", log());
		if (System.getProperty("PROB_LOG_CONFIG") == null) {
			System.setProperty("PROB_LOG_CONFIG", "production.xml");
		}
	}

	private static String log() {
		String userhome = System.getProperty("user.home");
		String[] log = { userhome, ".prob", "logs", "ProB.txt" };
		return Joiner.on(separator).join(log);
	}

	private static Injector INJECTOR;

	public IAnimator getAnimator() {
		return INJECTOR.getInstance(IAnimator.class);
	}

	private final CommandLineParser parser;
	private final Options options;
	private Shell shell;

	@Inject
	public Main(@Logfile final String logfile, final CommandLineParser parser,
			final Options options, final Shell shell) {
		this.parser = parser;
		this.options = options;
		this.shell = shell;
		System.setProperty("PROB_LOGFILE", logfile);
	}

	void run(final String[] args) {

		try {
			CommandLine line = parser.parse(options, args);
			if (line.hasOption("shell")) {
				shell.repl();
			}
		} catch (ParseException exp) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("java -jar probcli-deploy.jar", options);
		}
	}

	public static void main(final String[] args) {
		INJECTOR = Guice.createInjector(new MainModule());
		Main main = INJECTOR.getInstance(Main.class);

		// IStateSpace instance = injector.getInstance(IStateSpace.class);

		main.run(args);
		// System.exit(0);
	}

}