package de.bmotionstudio.core;

public class BMotionStudio {

	private static String imagePath;
	
	public static void setImagePath(String path) {
		imagePath = path;
	}
	
	public static String getImagePath() {
		return imagePath;
	}

	public static void reset() {
		BMotionStudio.setImagePath(null);
	}
	
}
