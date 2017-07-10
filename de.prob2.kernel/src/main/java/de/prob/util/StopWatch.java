package de.prob.util;

import java.util.HashMap;

public class StopWatch {

	private static final HashMap<String, Long> startTime = new HashMap<>();
	private static final HashMap<String, Long> runTime = new HashMap<>();

	private StopWatch() {
	}

	public static void start(String watch) {
		startTime.put(watch, System.currentTimeMillis());
		runTime.remove(watch);
	}

	public static long stop(String id) {
		long time = System.currentTimeMillis() - startTime.remove(id);
		runTime.put(id, time);
		return time;
	}

	public static long getRunTime(String id) {
		if (runTime.containsKey(id)) {
			return runTime.get(id);
		} else if (startTime.containsKey(id)) {
			return stop(id);
		}
		throw new RuntimeException("Unkown stop watch: " + id);
	}

	public static String getRunTimeAsString(String id) {
		long l = getRunTime(id);
		return "RUNTIME " + id + ": " + l + " ms";
	}

}