/**
 * 
 */
package de.prob.cli;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Base class for doing pattern matching on the standard output on startup of
 * the ProB command line executable.
 * 
 * @author plagge
 */
abstract class AbstractCliPattern<T> {
	private final Pattern pattern;
	private Matcher matcher;

	protected AbstractCliPattern(String regex) {
		this.pattern = Pattern.compile(regex);
	}

	/**
	 * Is called for each line of the standard output until this object matches
	 * a line or until the command loop starts.
	 * 
	 * @param line
	 *            the standard output line as string
	 * @return if the line matches
	 */
	public boolean matchesLine(String line) {
		if (line == null)
			return false;
		matcher = pattern.matcher(line);
		final boolean hit = matcher.find();
		if (hit) {
			setValue(matcher);
		}
		return hit;
	}

	/**
	 * If the current line matches the pattern, this method is called with the
	 * resulting {@link Matcher} object. An implementation of this method should
	 * find a value that can be accessed via {@link #getValue()}.
	 * 
	 * @param matcher
	 */
	protected abstract void setValue(Matcher matcher);

	/**
	 * Returns the resulting value determined by the input line.
	 * 
	 * @return
	 */
	public abstract T getValue();

	/**
	 * This method is called if no line matched on this pattern and the start of
	 * the command loop is reached.
	 * 
	 * @throws CliError
	 */
	public abstract void notifyNotFound();

	public abstract boolean notFoundIsFatal();
}
