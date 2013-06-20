package de.prob.ui;

import java.util.UUID;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import de.prob.ui.console.GroovyConsole;

public class OpenGroovyHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		IWorkbenchPage activePage = window.getActivePage();

		try {
			String id = GroovyConsole.ID;
			String secId = UUID.randomUUID().toString();
			activePage.showView(id, secId, IWorkbenchPage.VIEW_VISIBLE);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		return null;

	}

}
