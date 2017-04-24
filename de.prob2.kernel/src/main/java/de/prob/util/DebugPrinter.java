package de.prob.util;

public class DebugPrinter {

	private static boolean enabled = true;

	public static void enableDebugPrinter() {
		enabled = true;
	}

	public static void disableDebugPrinter() {
		enabled = false;
	}

	public static void debugPrint(String message) {
		if (enabled) {
			System.out.println("*** DEBUG: " + message);
		}
	}

	public static boolean isDebugMode() {
		return enabled;
	}
}
