package de.bmotionstudio.core.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IEditorLauncher;

import de.bmotionstudio.core.util.BMotionUtil;


public class BMotionStudioLauncher implements IEditorLauncher {
	
	public void open(IFile visualizationFile) {
		
		// TODO reimplement me!!!
		// Check if a simulation is already open
//		Simulation currentSimulation = BMotionStudio.getCurrentSimulation();
		// Check if the simulation is dirty and ask the user for saving it
//		if (currentSimulation != null && currentSimulation.isDirty()) {
//			switch (BMotionUtil.openSaveDialog()) {
//			case 0:
//				// yes - save the visualization and perspective
//				SaveSimulationAction saveSimulationAction = new SaveSimulationAction(
//						currentSimulation,
//						BMotionStudio.getCurrentProjectFile());
//				saveSimulationAction.run();
//				break;
//			case 1:
//				// no - do nothing
//				break;
//			case 2:
//				// cancel - return
//				return;
//			}
//		}
		BMotionUtil.closeCurrentVisualization();
		// TODO Currently we have only EventB. Make this more
		// generic, i.e. support ClassicalB
		BMotionUtil.openVisualization(visualizationFile, "EventB");
	}

	@Override
	public void open(IPath path) {
		open(ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(path));
	}

}
