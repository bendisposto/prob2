package de.prob.web.worksheet;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;

public class BindingsSnapshot {

	private final Set<Entry<String, Object>> global;
	private final Set<Entry<String, Object>> local;
	private final Set<String> keys = new HashSet<String>();

	public static final BindingsSnapshot FRESH = new BindingsSnapshot();

	private BindingsSnapshot() {
		global = new HashSet<Map.Entry<String, Object>>();
		local = new HashSet<Map.Entry<String, Object>>();
	}

	public BindingsSnapshot(ScriptEngine groovy) {
		Bindings gbindings = groovy.getBindings(ScriptContext.GLOBAL_SCOPE);
		global = new HashSet<Map.Entry<String, Object>>(gbindings.entrySet());
		Bindings lbindings = groovy.getBindings(ScriptContext.ENGINE_SCOPE);
		local = new HashSet<Map.Entry<String, Object>>(lbindings.entrySet());
		keys.addAll(gbindings.keySet());
		keys.addAll(lbindings.keySet());
	}

	public void restoreBindings(ScriptEngine groovy) {
		Bindings gbindings = groovy.getBindings(ScriptContext.GLOBAL_SCOPE);
		gbindings.clear();
		Bindings lbindings = groovy.getBindings(ScriptContext.ENGINE_SCOPE);
		lbindings.clear();
		for (Entry<String, Object> entry : global) {
			gbindings.put(entry.getKey(), entry.getValue());
		}
		for (Entry<String, Object> entry : local) {
			lbindings.put(entry.getKey(), entry.getValue());
		}
	}

	public Set<String> getKeys() {
		return keys;
	}

	public Set<String> delta(@Nullable BindingsSnapshot old) {
		Set<String> delta = new HashSet<String>(this.keys);
		if (old != null)
			delta.removeAll(old.keys);
		return delta;
	}

}
