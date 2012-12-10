package de.prob.ui.junitview;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import de.prob.testing.TestRegistry;
import de.prob.webconsole.ServletContextListener;

public class SpecifyTestDirectoryHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		Shell shell = HandlerUtil.getActiveShell(event);

		InputDialog inputDialog = new InputDialog(
				shell,
				"Open Test Directory",
				"Please specify the directory where you have saved your groovy tests:",
				null, new IInputValidator() {
					String errormsg = "That is not a valid directory";

					@Override
					public String isValid(final String newText) {
						return null;
					}
				});
		inputDialog.open();
		String answer = inputDialog.getValue();
		if (answer != null) {
			ServletContextListener.INJECTOR.getInstance(TestRegistry.class)
					.loadTests(answer);
			return null;
		} else {
			throw new IllegalArgumentException();
		}
	}

}
