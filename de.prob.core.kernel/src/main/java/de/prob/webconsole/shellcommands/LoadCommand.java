package de.prob.webconsole.shellcommands;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Joiner;

import jline.FileNameCompletor;

public class LoadCommand extends AbstractShellCommand {

	@Override
	public String perform(List<String> m) {
		return "loading foo";
	}

	@Override
	public List<String> complete(List<String> args, int pos) {
		ArrayList<String> suggestions = new ArrayList<String>();
		FileNameCompletor completor = new FileNameCompletor();
		completor.complete(Joiner.on(" ").join(args), pos, suggestions);
		ArrayList<String> s = new ArrayList<String>();
		for (String string : suggestions) {
			s.add("load" + string);
		}
		return s;
	}
}
