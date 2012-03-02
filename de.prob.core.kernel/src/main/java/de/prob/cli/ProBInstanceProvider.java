package de.prob.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import de.prob.ProBException;

@Singleton
public final class ProBInstanceProvider implements Provider<ProBInstance> {

	private final Logger logger = LoggerFactory
			.getLogger(ProBInstanceProvider.class);

	private final PrologProcessProvider processProvider;

	@Override
	public ProBInstance get() {
		try {
			return create();
		} catch (ProBException e) {
			return null;
		}
	}

	@Inject
	public ProBInstanceProvider(final PrologProcessProvider processProvider) {
		this.processProvider = processProvider;
	}

	public ProBInstanceImpl create() throws ProBException {
		ProBInstanceImpl cli = startProlog();
		return cli;
	}

	private ProBInstanceImpl startProlog() throws ProBException {
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

		ProBInstanceImpl cli = new ProBInstanceImpl(process, stream,
				userInterruptReference, connection);
		return cli;
	}

	Map<Class<? extends AbstractCliPattern<?>>, AbstractCliPattern<?>> extractCliInformation(
			final BufferedReader input) throws ProBException {
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
			Collection<? extends AbstractCliPattern<?>> patterns)
			throws ProBException {
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
			throw new ProBException();
		}
		for (AbstractCliPattern<?> p : patterns) {
			p.notifyNotFound();
			if (p.notFoundIsFatal())
				throw new ProBException();
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

}
