package de.prob.statespace;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

import de.prob.model.representation.AbstractElement;

/**
 * This class provides a registry of all currently running animations. It
 * provides the user to communicate between the UI and the console, and provides
 * a listener framework so that the user can animate machines using
 * {@link Trace} objects to represent the different animations. It also
 * maintains a pointer to one {@link Trace} object which is the current
 * animation.
 * 
 * @author joy
 * 
 */
@Singleton
public class Animations {

	Logger logger = LoggerFactory.getLogger(Animations.class);

	List<ITraceChangesListener> traceListeners = new CopyOnWriteArrayList<ITraceChangesListener>();

	Map<UUID, Trace> traces = new LinkedHashMap<UUID, Trace>();

	/**
	 * An {@link IAnimationChangeListener} can register itself via this method
	 * when it wants to receive updates about any changes in the current state.
	 * 
	 * @param listener
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
				listener.changed(java.util.Collections.singletonList(trace));
			} catch (Exception e) {
				logger.error("An exception of type "
						+ e.getClass()
						+ " was thrown while executing IAnimationChangeListener of class "
						+ listener.getClass() + " with message "
						+ e.getMessage());
			}
		}
	}

	private void notifyTraceRemove(final Trace trace) {
		for (ITraceChangesListener listener : traceListeners) {
			try {
				listener.removed(java.util.Collections.singletonList(trace
						.getUUID()));
			} catch (Exception e) {
				logger.error("An exception of type "
						+ e.getClass()
						+ " was thrown while executing IAnimationChangeListener of class "
						+ listener.getClass() + " with message "
						+ e.getMessage());
			}
		}
	}

	public void notifyBusy() {
		HashSet<UUID> busy = new HashSet<UUID>();
		for (Trace t : traces.values()) {
			if (t.getStateSpace().isBusy())
				busy.add(t.getUUID());
		}

		for (ITraceChangesListener listener : traceListeners) {
			try {
				listener.animatorStatus(busy);
			} catch (Exception e) {
				logger.error("An exception of type "
						+ e.getClass()
						+ " was thrown while executing IAnimationChangeListener of class "
						+ listener.getClass() + " with message "
						+ e.getMessage());
			}
		}
	}

	/**
	 * @return the list of {@link Trace} objects in the registry.
	 */
	public List<Trace> getTraces() {
		return java.util.Collections.unmodifiableList(new ArrayList<Trace>(
				traces.values()));
	}

	public Trace getTrace(final UUID uuid) {
		return traces.get(uuid);
	}

	/**
	 * @param trace
	 * @return the {@link AbstractElement} model that corresponds to the given
	 *         {@link Trace}
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
		UUID uuid = trace.getUUID();
		traces.put(uuid, trace);
		notifyTraceChange(trace);
	}

	/**
	 * Removes the {@link Trace} from the list of animations. If there are other
	 * {@link Trace}s available to animate, one of these is selected as the new
	 * current trace. Otherwise, the current {@link Trace} is set to null.
	 * 
	 * @param trace
	 */
	public void removeTrace(final Trace trace) {
		traces.remove(trace.getUUID());
		notifyTraceRemove(trace);
	}

}