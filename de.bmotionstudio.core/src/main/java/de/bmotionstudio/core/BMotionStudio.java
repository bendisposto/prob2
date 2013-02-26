package de.bmotionstudio.core;

import java.io.File;

import org.eclipse.ui.IPerspectiveDescriptor;

public class BMotionStudio {

	private static String imagePath;

	private static IPerspectiveDescriptor currentPerspective;

	private static File currentModelFile;

	public static void setImagePath(String path) {
		imagePath = path;
	}

	public static String getImagePath() {
		return imagePath;
	}

	public static void reset() {
		BMotionStudio.setImagePath(null);
	}

	public static IPerspectiveDescriptor getCurrentPerspective() {
		return currentPerspective;
	}

	public static void setCurrentPerspective(IPerspectiveDescriptor perspective) {
		currentPerspective = perspective;
	}

	public static File getCurrentModelFile() {
		return currentModelFile;
	}

	public static void setCurrentModelFile(File modelFile) {
		currentModelFile = modelFile;
	}
	
}
