package de.bmotionstudio.core.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IEditorLauncher;

import de.bmotionstudio.core.util.BMotionUtil;


public class BMotionStudioLauncher implements IEditorLauncher {
	
	public void open(IFile visualizationFile) {
		BMotionUtil.openVisualization(visualizationFile);
	}

	@Override
	public void open(IPath path) {
		open(ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(path));
	}

}
