package de.prob.ui.worksheet;

import java.awt.Desktop;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.activities.WorkbenchActivityHelper;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.ide.FileStoreEditorInputFactory;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.FileEditorInputFactory;

public class OpenWorksheetEditorHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		IWorkbenchPage page=PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		//One attempt to create a temporary file would be to use the ProB Plugins State Location;
		
		IFolder folder = workspace.getRoot().getFolder(Platform.getPlugin("de.prob.ui").getStateLocation());
		//TODO Select the "active" Project
		IFile tempFile=folder.getFile("tempWorksheet.wsh");
		try {
			page.openEditor(new FileEditorInput(tempFile), "de.prob.ui.worksheet");
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}



}
