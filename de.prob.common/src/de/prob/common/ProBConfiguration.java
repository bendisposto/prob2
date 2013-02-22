package de.prob.common;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IPerspectiveDescriptor;

public class ProBConfiguration {

	private static IPerspectiveDescriptor currentPerspective;
	
	private static IFile currentModelFile;
	
	public static IPerspectiveDescriptor getCurrentPerspective() {
		return currentPerspective;
	}

	public static void setCurrentPerspective(IPerspectiveDescriptor currentPerspective) {
		ProBConfiguration.currentPerspective = currentPerspective;
	}

	public static IFile getCurrentModelFile() {
		return currentModelFile;
	}

	public static void setCurrentModelFile(IFile currentModelFile) {
		ProBConfiguration.currentModelFile = currentModelFile;
	}
	
}
