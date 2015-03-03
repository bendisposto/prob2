package de.prob.statespace;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ITraceChangesListener {
	void changed(List<Trace> t);

	void remove(List<UUID> t);

	void animatorStatus(Set<UUID> busy);
}
