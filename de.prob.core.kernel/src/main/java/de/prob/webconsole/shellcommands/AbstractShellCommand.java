package de.prob.webconsole.shellcommands;

import java.io.IOException;
import java.util.List;

import de.prob.webconsole.GroovyExecution;

/**
 * @author joy
 * 
 *         Provides a basic class for Groovy commands that need to include code
 *         completion. Classes that extend this class need to implement the
 *         {@link #perform(List, GroovyExecution)} and
 *         {@link #complete(List, int)} methods in order for code completion to
 *         work.
 */
public abstract class AbstractShellCommand {
	/**
	 * Performs the specified command given the parameter list within the
	 * specified scope ({@code GroovyExecution}) and returns the result.
	 * 
	 * @param m
	 *            a list of parameters for the given command
	 * @param exec
	 *            the scope in which the command should be performed
	 * @return {@link Object} created by performing the command
	 * @throws IOException
	 */
	public abstract Object perform(List<String> m, GroovyExecution exec)
			throws IOException;

	/**
	 * Performs code completion for the given {@link AbstractShellCommand}.
	 * 
	 * @param m
	 *            List of arguments for a command
	 * @param pos
	 *            position of cursor
	 * @return list of possible completions for given command
	 */
	public abstract List<String> complete(final List<String> m, final int pos);

	/**
	 * Find the greatest common prefix between two strings.
	 * 
	 * @param a
	 * @param b
	 * @return greatest common prefix between a and b
	 */
	private String greatestCommonPrefix(final String a, final String b) {
		int minLength = Math.min(a.length(), b.length());
		for (int i = 0; i < minLength; i++) {
			if (a.charAt(i) != b.charAt(i)) {
				return a.substring(0, i);
			}
		}
		return a.substring(0, minLength);
	}

	/**
	 * Takes a {@link List} of suggestions and finds the longest common prefix
	 * among all of them.
	 * 
	 * @param suggestions
	 * @return longest common prefix among all of the suggestions
	 */
	protected String findLongestCommonPrefix(final List<String> suggestions) {
		if (suggestions.size() == 1) {
			return suggestions.get(0);
		}
		String res = greatestCommonPrefix(suggestions.get(0),
				suggestions.get(1));
		for (int i = 2; i < suggestions.size(); i++) {
			res = greatestCommonPrefix(res, suggestions.get(i));
		}
		return res;
	}
}
