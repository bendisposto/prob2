package de.prob.statespace;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.inject.Singleton;

import de.prob.model.representation.AbstractElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class AnimationSelector {

	// private Animations animations;
	//
	// @Inject
	// public AnimationSelector(Animations animations) {
	// this.animations = animations;
	// }

	Logger logger = LoggerFactory.getLogger(AnimationSelector.class);

	List<IAnimationChangeListener> traceListeners = new CopyOnWriteArrayList<IAnimationChangeListener>();
	List<IModelChangedListener> modelListeners = new CopyOnWriteArrayList<IModelChangedListener>();

	Map<UUID, Trace> traces = new LinkedHashMap<UUID, Trace>();
	Set<UUID> protectedTraces = new HashSet<UUID>();

	Trace currentTrace = null;
	StateSpace currentStateSpace = null;

	/**
	 * An {@link IAnimationChangeListener} can register itself via this method
	 * when it wants to receive updates about any changes in the current state.
	 * 
	 * @param listener
	 */
	public void registerAnimationChangeListener(
			final IAnimationChangeListener listener) {

		traceListeners.add(listener);
		if (currentTrace != null) {
			listener.traceChange(currentTrace, true);
			listener.animatorStatus(currentTrace.getStateSpace().isBusy());
		}
	}

	public void deregisterAnimationChangeListener(
			final IAnimationChangeListener listener) {
		traceListeners.remove(listener);
	}

	public void registerModelChangedListener(
			final IModelChangedListener listener) {
		modelListeners.add(listener);
		if (currentStateSpace != null) {
			listener.modelChanged(currentStateSpace);
		}
	}

	public void deregisterModelChangedListeners(
			final IModelChangedListener listener) {
		modelListeners.remove(listener);
	}

	/**
	 * Changes the current trace to the specified {@link Trace} and notifies an
	 * animation change ({@link AnimationSelector#notifyModelChanged(StateSpace)})
	 * 
	 * @param trace
	 */
	public void changeCurrentAnimation(final Trace trace) {
		currentTrace = trace;
		notifyAnimationChange(trace, true);

		if (currentTrace != null
				&& currentTrace.getStateSpace() != currentStateSpace) {
			currentStateSpace = currentTrace.getStateSpace();
			notifyModelChanged(currentStateSpace);
		}
	}

	public void addNewAnimation(final Trace trace) {
		addNewAnimation(trace, true);
	}

	/**
	 * Adds the specified {@link Trace} trace to the registry, sets the current
	 * trace to trace, and notifies an animation change (
	 * {@link AnimationSelector#notifyModelChanged(StateSpace)}). If a trace with
	 * the same UUID is already being tracked, a {@link Trace#copy()} is made
	 * and this is animated.
	 * 
	 * @param trace
	 *            to be added
	 * @param protect
	 *            if protected, the trace will not be deleted when
	 *            {@link AnimationSelector#clearUnprotected()} is called.
	 */
	public void addNewAnimation(final Trace trace, final boolean protect) {
		Trace t = trace;
		if (traces.containsKey(trace.getUUID())) {
			t = trace.copy();
		}
		traces.put(t.getUUID(), t);
		if (protect) {
			protectedTraces.add(t.getUUID());
		}
		currentTrace = t;
		notifyAnimationChange(t, true);

		StateSpace s = t.getStateSpace();
		if (s != null && !s.equals(currentStateSpace)) {
			currentStateSpace = s;
			notifyModelChanged(s);
		}
	}

	public void setProtected(final Trace trace, final boolean isProtected) {
		if (isProtected) {
			protectedTraces.add(trace.getUUID());
		} else {
			protectedTraces.remove(trace.getUUID());
		}
	}

	public void clearUnprotected() {
		ArrayList<Trace> list = new ArrayList<Trace>(traces.values());
		boolean currentChanged = false;
		for (Trace trace : list) {
			if (!protectedTraces.contains(trace.getUUID())) {
				if (currentTrace != null
						&& trace.getUUID().equals(currentTrace.getUUID())) {
					currentTrace = null;
					currentStateSpace = null;
					currentChanged = true;
				}
				traces.remove(trace.getUUID());
			}
		}
		if (currentChanged && !traces.isEmpty()) {
			currentTrace = traces.values().iterator().next();
			currentStateSpace = currentTrace.getStateSpace();
		}
		refresh();
	}

	/**
	 * Let all {@link IAnimationChangeListener}s know that the current animation
	 * has changed
	 * 
	 * @param trace
	 *            {@link Trace} representing the current animation
	 */
	private void notifyAnimationChange(final Trace trace,
			final boolean currentAnimationChanged) {

		// Trace may be null, or not busy
		if (trace == null || trace != null && !trace.getStateSpace().isBusy()) {

			for (IAnimationChangeListener animationChangeListener : traceListeners) {
				try {
					animationChangeListener.traceChange(trace,
							currentAnimationChanged);
				} catch (Exception e) {
					logger.error("An exception of type "
							+ e.getClass()
							+ " was thrown while executing IAnimationChangeListener of class "
							+ animationChangeListener.getClass()
							+ " with message " + e.getMessage());
				}
			}
		}
	}

	/**
	 * Informs all {@link IAnimationChangeListener}s of the current status of
	 * the animator in the current animation. Calls method
	 * {@link IAnimationChangeListener#animatorStatus(boolean)}.
	 * 
	 * @param busy
	 *            boolean value of the status of the animator for the current
	 *            animation
	 */
	private void notifyStatusChange(final boolean busy) {
		for (IAnimationChangeListener animationChangeListener : traceListeners) {
			animationChangeListener.animatorStatus(busy);
		}
	}

	private void notifyModelChanged(final StateSpace s) {
		for (IModelChangedListener modelChangedListener : modelListeners) {
			modelChangedListener.modelChanged(s);
		}
	}

	/**
	 * @return the current {@link Trace}
	 */
	public Trace getCurrentTrace() {
		return currentTrace;
	}

	/**
	 * @return the list of {@link Trace} objects in the registry.
	 */
	public List<Trace> getTraces() {
		return new ArrayList<Trace>(traces.values());
	}

	public Trace getTrace(final UUID uuid) {
		return traces.get(uuid);
	}

	public Set<UUID> getProtectedTraces() {
		return protectedTraces;
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
		return "Animations Registry";
	}

	/**
	 * notify all of the listeners using the current trace
	 * {@link AnimationSelector#notifyModelChanged(StateSpace)}
	 */
	public void refresh() {
		notifyAnimationChange(currentTrace, true);
		notifyModelChanged(currentStateSpace);
		if (currentStateSpace != null) {
			notifyStatusChange(currentStateSpace.isBusy());
		}
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
		if (!traces.containsKey(uuid)) {
			traces.put(uuid, trace);
			notifyAnimationChange(currentTrace, false);
		} else {
			// Trace oldT = traces.get(uuid);
			traces.put(uuid, trace);
			if (trace.getUUID().equals(currentTrace.getUUID())) {
				notifyAnimationChange(trace, true);
				Trace oldT = currentTrace;
				currentTrace = trace;

				if (oldT.getStateSpace().isBusy() != currentTrace
						.getStateSpace().isBusy()) {
					notifyStatusChange(currentTrace.getStateSpace().isBusy());
				}
			} else {
				notifyAnimationChange(currentTrace, false);
			}
		}

		if (currentTrace != null
				&& currentTrace.getStateSpace() != currentStateSpace) {
			currentStateSpace = currentTrace.getStateSpace();
			notifyModelChanged(currentStateSpace);
		}
	}

	/**
	 * Lets the AnimationSelector know that the {@link Trace} object with
	 * reference oldTrace has been changed to newTrace so that the
	 * AnimationSelector can update its registry.
	 * 
	 * This is deprecated. The Traces are now identified via UUID. Use
	 * {@link AnimationSelector#traceChange(Trace)} with the new trace as a
	 * parameter.
	 * 
	 * @param oldTrace
	 *            reference to old {@link Trace} object
	 * @param newTrace
	 *            reference to new {@link Trace} object
	 */
	@Deprecated
	public void replaceTrace(final Trace oldTrace, final Trace newTrace) {
		traceChange(newTrace);
	}

	/**
	 * Removes the {@link Trace} from the list of animations. If there are other
	 * {@link Trace}s available to animate, one of these is selected as the new
	 * current trace. Otherwise, the current {@link Trace} is set to null.
	 * 
	 * @param trace
	 */
	public void removeTrace(final Trace trace) {
		remove(trace);
		refresh();
	}

	private void remove(final Trace trace) {
		if (!traces.containsKey(trace.getUUID())) {
			return;
		}
		if (currentTrace == trace) {
			traces.remove(trace.getUUID());
			if (traces.isEmpty()) {
				currentTrace = null;
				currentStateSpace = null;
				return;
			}
			currentTrace = traces.values().iterator().next();
			currentStateSpace = currentTrace.getStateSpace();
			return;
		}
		traces.remove(trace.getUUID());
		protectedTraces.remove(trace.getUUID());
	}

	public void notifyAnimatorStatus(final String animatorId, final boolean busy) {
		if (currentTrace != null
				&& currentTrace.getStateSpace().getId().equals(animatorId)) {
			notifyStatusChange(busy);
		}
	}
}
