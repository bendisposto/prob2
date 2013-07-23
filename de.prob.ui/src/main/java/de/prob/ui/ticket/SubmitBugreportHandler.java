package de.prob.ui.ticket;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class SubmitBugreportHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil
				.getActiveWorkbenchWindowChecked(event);
		MessageDialog
				.openError(
						window.getShell(),
						"Not available",
						"This feature has been disabled because it requires a larger refactoring. For now, please submit your bugreport on http://jira.cobra.cs.uni-duesseldorf.de");
		return null;
	}

}
