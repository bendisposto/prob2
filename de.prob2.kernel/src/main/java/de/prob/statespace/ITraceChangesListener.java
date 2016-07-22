package de.prob.statespace;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * @deprecated Use IAnimationChangeListener instead (also use AnimationSelector instead of Animations)
 *
 */
@Deprecated
public interface ITraceChangesListener {
	void changed(List<Trace> t);

	void removed(List<UUID> t);

	void animatorStatus(Set<UUID> busy);
}
