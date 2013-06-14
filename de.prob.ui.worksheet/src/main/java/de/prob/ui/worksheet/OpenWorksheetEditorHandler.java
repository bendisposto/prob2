package de.prob.ui.worksheet;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class OpenWorksheetEditorHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		// IWorkspace workspace = ResourcesPlugin.getWorkspace();
		// One attempt to create a temporary file would be to use the ProB
		// Plugins State Location;

		// IFolder folder =
		// workspace.getRoot().getFolder(Platform.getPlugin("de.prob.ui").getStateLocation());
		// TODO Select the "active" Project
		// IFile tempFile=folder.getFile("tempWorksheet.wsh");

		IEditorInput input = new WorksheetEditorInput();

		try {
			page.openEditor(input, "de.prob.ui.worksheetEditor");
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
