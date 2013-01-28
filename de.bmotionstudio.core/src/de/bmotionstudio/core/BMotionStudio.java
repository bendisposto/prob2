package de.bmotionstudio.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IPerspectiveDescriptor;

import de.bmotionstudio.core.model.Simulation;

public class BMotionStudio {

	private static String imagePath;
	
	private static Simulation currentSimulation;
	
	private static IFile currentProjectFile;
	
	private static IPerspectiveDescriptor currentPerspective;
	
	public static void setImagePath(String path) {
		imagePath = path;
	}
	
	public static String getImagePath() {
		return imagePath;
	}

	public static Simulation getCurrentSimulation() {
		return currentSimulation;
	}

	public static void setCurrentSimulation(Simulation currentSimulation) {
		BMotionStudio.currentSimulation = currentSimulation;
	}

	public static IPerspectiveDescriptor getCurrentPerspective() {
		return currentPerspective;
	}

	public static void setCurrentPerspective(IPerspectiveDescriptor currentPerspective) {
		BMotionStudio.currentPerspective = currentPerspective;
	}

	public static IFile getCurrentProjectFile() {
		return currentProjectFile;
	}

	public static void setCurrentProjectFile(IFile currentProjectFile) {
		BMotionStudio.currentProjectFile = currentProjectFile;
	}
	
}
