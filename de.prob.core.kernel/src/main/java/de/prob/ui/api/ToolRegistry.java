package de.prob.ui.api;

import java.util.HashMap;
import java.util.Map;

public class ToolRegistry {

	private final Map<String, ITool> tools = new HashMap<String, ITool>();

	public void register(String name, ITool stateprovider) {
		tools.put(name, stateprovider);
	}

	public void unregister(String name) {
		tools.remove(name);
	}

	public void stateChange(String statereference) {

	}

}
