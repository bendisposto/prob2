package de.prob.scripting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.prob.animator.command.AbstractCommand;
import de.prob.animator.command.ComposedCommand;
import de.prob.animator.command.SetPreferenceCommand;
import de.prob.animator.command.StartAnimationCommand;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.AbstractModel;
import de.prob.statespace.StateSpace;

public class StateSpaceProvider {
	private final Provider<StateSpace> ssProvider;

	@Inject
	public StateSpaceProvider(final Provider<StateSpace> ssProvider) {
		this.ssProvider = ssProvider;
	}

	public StateSpace loadFromCommand(final AbstractModel model,
			final AbstractElement mainComponent,
			final Map<String, String> preferences, final AbstractCommand loadCmd) {
		StateSpace s = ssProvider.get();
		s.setModel(model, mainComponent);
		List<AbstractCommand> cmds = new ArrayList<>();

		for (Entry<String, String> pref : preferences.entrySet()) {
			cmds.add(new SetPreferenceCommand(pref.getKey(), pref.getValue()));
		}

		try {
			s.execute(new ComposedCommand(cmds));
			s.execute(loadCmd);
			s.execute(new StartAnimationCommand());
		} catch (Exception e) {
			s.kill();
			throw e;
		}
		return s;
	}
}
