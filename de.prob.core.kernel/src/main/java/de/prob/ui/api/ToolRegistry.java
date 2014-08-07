package de.prob.ui.api;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Singleton;

@Singleton
public class ToolRegistry {

	private final Map<String, ITool> tools = new HashMap<String, ITool>();
	List<WeakReference<IToolListener>> listeners = new ArrayList<WeakReference<IToolListener>>();

	public void registerListener(final IToolListener listener) {
		listeners.add(new WeakReference<IToolListener>(listener));
	}

	public void register(final String name, final ITool stateprovider) {
		tools.put(name, stateprovider);
		notifyToolChange(stateprovider);
	}

	public void unregister(final String name) {
		tools.remove(name);
	}

	public void notifyToolChange(final ITool tool) {
		for (WeakReference<IToolListener> listener : listeners) {
			listener.get().animationChange(tool);
		}
	}

	public ITool getTool(String id) {
		return tools.get(id);
	}
	
}
