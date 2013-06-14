package de.bmotionstudio.core.editor;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IEditorLauncher;

import de.bmotionstudio.core.util.BMotionUtil;


public class BMotionStudioLauncher implements IEditorLauncher {

	public void open(File visualizationFile) {
		BMotionUtil.openVisualization(visualizationFile);
	}

	@Override
	public void open(IPath path) {
		IFile fileForLocation = ResourcesPlugin.getWorkspace().getRoot()
				.getFileForLocation(path);
		if (fileForLocation != null) {
			File file = fileForLocation.getRawLocation().makeAbsolute()
					.toFile();
			open(file);
		} else {
			// TODO: return some error message?
		}
	}

}
