package de.prob.ui.services;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

public class AbstractSimpleBooleanProvider extends AbstractSourceProvider {

	public static final String ENABLED = "enabled";
	public static final String DISABLED = "disabled";
	private boolean enabled = false;
	private final String SERVICE;

	public AbstractSimpleBooleanProvider(String SERVICE) {
		this.SERVICE = SERVICE;
	}

	@Override
	public void dispose() {
	}

	@Override
	public Map<String, String> getCurrentState() {
		Map<String, String> currentState = new HashMap<String, String>(1);
		String current = enabled ? ENABLED : DISABLED;
		currentState.put(SERVICE, current);
		return currentState;
	}

	@Override
	public String[] getProvidedSourceNames() {
		return new String[] { SERVICE };
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(final boolean enabled) {
		if (nochange(enabled))
			return;
		this.enabled = enabled;
		fireSourceChanged(ISources.WORKBENCH, getCurrentState());
	}

	private boolean nochange(final boolean enabled) {
		return this.enabled == enabled;
	}

}
