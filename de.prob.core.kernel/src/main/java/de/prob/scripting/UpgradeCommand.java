package de.prob.scripting;

import java.lang.ref.WeakReference;
import java.util.List;

import org.codehaus.groovy.tools.shell.CommandSupport;
import org.codehaus.groovy.tools.shell.PShell;
import org.codehaus.groovy.tools.shell.Shell;

import de.prob.cli.ProBInstance;
import de.prob.cli.ProBInstanceProvider;

public class UpgradeCommand extends CommandSupport {

	protected UpgradeCommand(final Shell shell) {
		super(shell, "upgrade", "\\u");
	}

	@Override
	public Object execute(@SuppressWarnings("rawtypes") final List args) {
		Api api = ((PShell) shell).getApi();
		List<WeakReference<ProBInstance>> clis = ProBInstanceProvider.getClis();
		for (WeakReference<ProBInstance> weakReference : clis) {
			final ProBInstance p = weakReference.get();
			if (p != null)
				api.shutdown(p);
		}
		System.out.println(api.upgrade());
		return null;
	}
}
