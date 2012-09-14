package de.prob.statespace;

import java.util.ArrayList;
import java.util.List;

public class StaticRegistry {
	public static List<ILoadListener> listeners = new ArrayList<ILoadListener>();
	public static void registerListener(ILoadListener listener) {
		listeners.add(listener);
	}
	
	public static void notifyNewHistory(History h) {
		for (ILoadListener listener : listeners) {
			listener.notifyLoadHistory(h);
		}
	}
}
