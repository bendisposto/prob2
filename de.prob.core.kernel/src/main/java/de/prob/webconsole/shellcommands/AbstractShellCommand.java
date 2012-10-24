package de.prob.webconsole.shellcommands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Joiner;

import de.prob.webconsole.GroovyExecution;

public abstract class AbstractShellCommand {
	public abstract Object perform(List<String> m, GroovyExecution exec) throws IOException;

	public List<String> complete(List<String> m, int pos) {
		String join = Joiner.on(" ").join(m);
		return Collections.singletonList(join);
	}
	
	private String greatestCommonPrefix(String a, String b) {
		int minLength = Math.min(a.length(), b.length());
		for (int i = 0; i < minLength; i++) {
			if (a.charAt(i) != b.charAt(i)) {
				return a.substring(0, i);
			}
		}
		return a.substring(0, minLength);
	}

	protected String findLongestCommonPrefix(ArrayList<String> suggestions) {
		if (suggestions.size() == 1)
			return suggestions.get(0);
		String res = greatestCommonPrefix(suggestions.get(0),
				suggestions.get(1));
		for (int i = 2; i < suggestions.size(); i++) {
			res = greatestCommonPrefix(res, suggestions.get(i));
		}
		return res;
	}
}
