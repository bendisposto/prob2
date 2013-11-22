package de.prob.ui.bunitview;

import java.util.concurrent.Executors;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import de.prob.testing.TestRegistry;
import de.prob.webconsole.ServletContextListener;

public class SpecifyTestDirectoryHandler extends AbstractHandler implements
		IHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		Shell shell = HandlerUtil.getActiveShell(event);

		FileDialog dialog = new FileDialog(shell);
		dialog.open();
		final String answer = dialog.getFilterPath();

		if (answer != null && !answer.isEmpty()) {
			Runnable runTestLoader = new Runnable() {
				@Override
				public void run() {
					ServletContextListener.INJECTOR.getInstance(
							TestRegistry.class).loadTests(answer);
				}
			};
			Executors.newSingleThreadExecutor().execute(runTestLoader);
			return null;
		} else {
			throw new IllegalArgumentException();
		}
	}

}
