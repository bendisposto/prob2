package de.prob.scripting;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import jline.Completor;

import org.codehaus.groovy.tools.shell.CommandSupport;
import org.codehaus.groovy.tools.shell.PShell;
import org.codehaus.groovy.tools.shell.Shell;

import de.be4.classicalb.core.parser.exceptions.BException;

public class BLoadCommand extends CommandSupport {

	protected BLoadCommand(final Shell shell) {
		super(shell, "b_load", "\\b");
	}

	@Override
	protected List<Completor> createCompletors() {
		return Arrays.asList((Completor) new FileCompletor());
	}

	@Override
	public Object execute(@SuppressWarnings("rawtypes") final List args) {
		Api api = ((PShell) shell).getApi();
		try {
			return api.b_load((String) args.get(0));
		} catch (IOException e) {
			shell.getIo().out.println("I/O exception - "
					+ e.getLocalizedMessage());
			return null;
		} catch (BException e) {
			shell.getIo().out.println("Parser exception - "
					+ e.getLocalizedMessage());
			return null;
		}
	}

}
