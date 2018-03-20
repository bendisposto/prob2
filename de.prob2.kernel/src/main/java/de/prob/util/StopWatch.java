package de.prob.util;

import java.util.HashMap;

public class StopWatch<E extends Object> {

	private final HashMap<E, Long> startTime = new HashMap<>();
	private final HashMap<E, Long> runTime = new HashMap<>();

	public void start(E watch) {
		startTime.put(watch, System.currentTimeMillis());
		runTime.remove(watch);
	}

	public long stop(E id) {
		long time = System.currentTimeMillis() - startTime.remove(id);
		runTime.put(id, time);
		return time;
	}

	public long getRunTime(E id) {
		if (runTime.containsKey(id)) {
			return runTime.get(id);
		} else if (startTime.containsKey(id)) {
			return stop(id);
		}
		throw new IllegalArgumentException("Unkown stop watch: " + id);
	}

	public String getRunTimeAsString(E id) {
		long l = getRunTime(id);
		return "RUNTIME " + id + ": " + l + " ms";
	}
	
	public void printTime(E id) {
		System.out.println(getRunTimeAsString(id));
	}

}
