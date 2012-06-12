package de.prob.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import de.prob.annotations.Home;
import de.prob.exception.CliError;

@Singleton
public final class ProBInstanceProvider implements Provider<ProBInstance> {

	private final static List<WeakReference<ProBInstance>> refs = new ArrayList<WeakReference<ProBInstance>>();
	private final Logger logger = LoggerFactory
			.getLogger(ProBInstanceProvider.class);

	private final PrologProcessProvider processProvider;
	private final String home;
	private final OsSpecificInfo osInfo;

	@Override
	public ProBInstance get() {
		return create();
	}

	@Inject
	public ProBInstanceProvider(final PrologProcessProvider processProvider,
			@Home String home, OsSpecificInfo osInfo) {
		this.processProvider = processProvider;
		this.home = home;
		this.osInfo = osInfo;
	}

	public ProBInstance create() {
		ProBInstance cli = startProlog();
		final WeakReference<ProBInstance> ref = new WeakReference<ProBInstance>(
				cli);
		refs.add(ref);
		return cli;
	}

	private ProBInstance startProlog() {
		ProcessHandle processTuple = processProvider.get();
		Process process = processTuple.getProcess();
		String key = processTuple.getKey();
		final BufferedReader stream = new BufferedReader(new InputStreamReader(
				process.getInputStream(), Charset.defaultCharset()));

		Map<Class<? extends AbstractCliPattern<?>>, AbstractCliPattern<?>> cliInformation = extractCliInformation(stream);

		Integer port = ((PortPattern) cliInformation.get(PortPattern.class))
				.getValue();
		Long userInterruptReference = ((InterruptRefPattern) cliInformation
				.get(InterruptRefPattern.class)).getValue();

		ProBConnection connection = new ProBConnection(key, port);

		try {
			connection.connect();
			ProBInstance cli = new ProBInstance(process, stream,
					userInterruptReference, connection, home, osInfo);
			return cli;
		} catch (IOException e) {
			logger.error("Error connecting to Prolog binary.", e);
			return null;
		}

	}

	Map<Class<? extends AbstractCliPattern<?>>, AbstractCliPattern<?>> extractCliInformation(
			final BufferedReader input) {
		final PortPattern portPattern = new PortPattern();
		final InterruptRefPattern intPattern = new InterruptRefPattern();

		Map<Class<? extends AbstractCliPattern<?>>, AbstractCliPattern<?>> pattern = new HashMap<Class<? extends AbstractCliPattern<?>>, AbstractCliPattern<?>>();
		pattern.put(PortPattern.class, portPattern);
		pattern.put(InterruptRefPattern.class, intPattern);
		Collection<AbstractCliPattern<?>> values = pattern.values();
		analyseStdout(input, values);
		return pattern;
	}

	private void analyseStdout(final BufferedReader input,
			Collection<? extends AbstractCliPattern<?>> patterns) {
		patterns = new ArrayList<AbstractCliPattern<?>>(patterns);
		try {
			String line;
			boolean endReached = false;
			while (!endReached && (line = input.readLine()) != null) { // NOPMD
				logger.debug("Apply cli detection patterns to {}", line);
				applyPatterns(patterns, line);
				endReached = patterns.isEmpty()
						|| line.contains("starting command loop");
			}
		} catch (IOException e) {
			final String message = "Problem while starting ProB. Cannot read from input stream.";
			logger.error(message);
			logger.debug(message, e);
			throw new CliError(message, e);
		}
		for (AbstractCliPattern<?> p : patterns) {
			p.notifyNotFound();
			if (p.notFoundIsFatal())
				throw new CliError("Missing info from CLI "
						+ p.getClass().getSimpleName());
		}
	}

	private void applyPatterns(
			final Collection<? extends AbstractCliPattern<?>> patterns,
			final String line) {
		for (Iterator<? extends AbstractCliPattern<?>> it = patterns.iterator(); it
				.hasNext();) {
			final AbstractCliPattern<?> p = it.next();
			if (p.matchesLine(line)) {
				it.remove();
			}
		}
	}

	public static List<WeakReference<ProBInstance>> getClis() {
		return refs;
	}

}
