package de.prob.statespace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import com.google.inject.Singleton;

import de.prob.model.representation.AbstractElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @deprecated Use AnimationSelector instead
 */
@Deprecated
@Singleton
public class Animations {

	private static final Logger logger = LoggerFactory.getLogger(Animations.class);

	private final List<ITraceChangesListener> traceListeners = new CopyOnWriteArrayList<>();
	private final Map<UUID, Trace> traces = new LinkedHashMap<>();

	/**
	 * An {@link IAnimationChangeListener} can register itself via this method
	 * when it wants to receive updates about any changes in the current state.
	 * 
	 * @param listener the listener to register
	 */
	public void registerAnimationChangeListener(
			final ITraceChangesListener listener) {
		traceListeners.add(listener);
		listener.changed(getTraces());
	}

	public void deregisterAnimationChangeListener(
			final ITraceChangesListener listener) {
		traceListeners.remove(listener);
	}

	/**
	 * Adds the specified {@link Trace} trace to the registry, sets the current
	 * trace to trace, and notifies an animation change (
	 * {@link Animations#notifyTraceChange(Trace)}). If a trace with the same
	 * UUID is already being tracked, a {@link Trace#copy()} is made and this is
	 * animated.
	 * 
	 * @param trace
	 *            to be added
	 */
	public void addNewAnimation(final Trace trace) {
		Trace t = trace;
		if (traces.containsKey(trace.getUUID())) {
			t = trace.copy();
		}
		traces.put(t.getUUID(), t);
		notifyTraceChange(t);
	}

	/**
	 * Let all {@link IAnimationChangeListener}s know that the current animation
	 * has changed
	 * 
	 * @param trace
	 *            {@link Trace} representing the current animation
	 */
	private void notifyTraceChange(final Trace trace) {
		for (ITraceChangesListener listener : traceListeners) {
			try {
				listener.changed(Collections.singletonList(trace));
			} catch (RuntimeException e) {
				logger.error("An exception was thrown while executing IAnimationChangeListener of class {}", listener.getClass(), e);
			}
		}
	}

	private void notifyTraceRemove(final Trace trace) {
		for (ITraceChangesListener listener : traceListeners) {
			try {
				listener.removed(Collections.singletonList(trace.getUUID()));
			} catch (RuntimeException e) {
				logger.error("An exception was thrown while executing IAnimationChangeListener of class {}", listener.getClass(), e);
			}
		}
	}

	public void notifyBusy() {
		Set<UUID> busy = traces.values().stream()
			.filter(t -> t.getStateSpace().isBusy())
			.map(Trace::getUUID)
			.collect(Collectors.toSet());
		
		for (ITraceChangesListener listener : traceListeners) {
			try {
				listener.animatorStatus(busy);
			} catch (RuntimeException e) {
				logger.error("An exception was thrown while executing IAnimationChangeListener of class {}", listener.getClass(), e);
			}
		}
	}

	/**
	 * @return the list of {@link Trace} objects in the registry.
	 */
	public List<Trace> getTraces() {
		return Collections.unmodifiableList(new ArrayList<>(traces.values()));
	}

	public Trace getTrace(final UUID uuid) {
		return traces.get(uuid);
	}

	/**
	 * Get the {@link AbstractElement} model that corresponds to the given {@link Trace}.
	 * 
	 * @param trace the trace for which to get the model
	 * @return the trace's model
	 */
	public AbstractElement getModel(final Trace trace) {
		return trace.getModel();
	}

	@Override
	public String toString() {
		return "Animations Registry. (" + traces.size() + " traces)";
	}

	/**
	 * If the {@link Trace} object is already being tracked, the {@link Trace}
	 * in the registry will be updated. If not, the {@link Trace} is tracked.
	 * Listeners are fired according to the changes that have been made.
	 * 
	 * @param trace
	 *            Trace object containing the changes.
	 */
	public void traceChange(final Trace trace) {
		traces.put(trace.getUUID(), trace);
		notifyTraceChange(trace);
	}

	/**
	 * Removes the {@link Trace} from the list of animations. If there are other
	 * {@link Trace}s available to animate, one of these is selected as the new
	 * current trace. Otherwise, the current {@link Trace} is set to null.
	 * 
	 * @param trace the trace to remove
	 */
	public void removeTrace(final Trace trace) {
		traces.remove(trace.getUUID());
		notifyTraceRemove(trace);
	}

}
