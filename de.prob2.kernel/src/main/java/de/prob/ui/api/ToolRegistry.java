package de.prob.ui.api;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Singleton;

@Singleton
public class ToolRegistry {

	private final Map<String, ITool> tools = new HashMap<>();
	private final List<WeakReference<IToolListener>> listeners = new ArrayList<>();

	public void registerListener(final IToolListener listener) {
		listeners.add(new WeakReference<>(listener));
	}

	public void register(final String name, final ITool stateprovider) {
		tools.put(name, stateprovider);
	}

	public void unregister(final String name) {
		tools.remove(name);
	}

	public void notifyToolChange(String trigger, final ITool tool) {
		for (WeakReference<IToolListener> wr : listeners) {
			IToolListener listener = wr.get();
			if (listener != null) {
				listener.animationChange(trigger, tool);
			}
		}
	}

	public ITool getTool(String id) {
		return tools.get(id);
	}

}
