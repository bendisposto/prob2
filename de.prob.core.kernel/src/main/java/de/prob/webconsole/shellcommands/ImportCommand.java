package de.prob.webconsole.shellcommands;

import groovy.lang.Binding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.codehaus.groovy.tools.shell.Interpreter;


import com.google.common.base.Joiner;

import de.prob.webconsole.GroovyExecution;

public class ImportCommand extends AbstractShellCommand {
	
	private Interpreter try_interpreter;
	
	public ImportCommand() {
		this.try_interpreter = new Interpreter(
				this.getClass().getClassLoader(), new Binding());
	}
	
	private void collectImports(String input, GroovyExecution exec) {
		String[] split = input.split(";|\n");
		for (String string : split) {
			if (string.trim().startsWith("import ")) {
				try {
					try_interpreter.evaluate(Collections.singletonList(string));
//					imports.add(string + ";"); // if try_interpreter does not
												// throw an exception, it was a
												// valid import statement
					exec.addImport(string + ";");
				} catch (Exception e) {
					this.try_interpreter = new Interpreter(this.getClass()
							.getClassLoader(), new Binding());
				}
			}
		}
	}
	
	@Override
	public Object perform(List<String> m, GroovyExecution exec)
			throws IOException {
		String line = Joiner.on(' ').join(m);
		///exec.evaluate(line);
		collectImports(line, exec);
		return null;
	}
	
	@Override
	public List<String> complete(List<String> m, int pos) {
		ArrayList<String> suggestions = new ArrayList<String>();
		Package[] packages = Package.getPackages();
		for (int i = 0; i < packages.length; i++) {
			String name = packages[i].getName();
			
			if (m.isEmpty() || name.startsWith(m.get(0))) {
				suggestions.add(name);
			}
		}
		String pre = "";
		if (!suggestions.isEmpty())
			pre = findLongestCommonPrefix(suggestions);
		if (!(pre.isEmpty() || pre.equals(m.get(0)))) {
			return Collections.singletonList("import " + pre);
		}
		if (suggestions.size() == 1)
			return Collections.singletonList("import " + suggestions.get(0));
		return suggestions;
	}

}
