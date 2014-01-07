package de.prob.statespace;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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
public class AnimationSelector {

	List<WeakReference<IAnimationChangeListener>> traceListeners = new CopyOnWriteArrayList<WeakReference<IAnimationChangeListener>>();
	List<WeakReference<IModelChangedListener>> modelListeners = new CopyOnWriteArrayList<WeakReference<IModelChangedListener>>();

	List<Trace> traces = new ArrayList<Trace>();

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

		traceListeners
				.add(new WeakReference<IAnimationChangeListener>(listener));
		if (currentTrace != null) {
			notifyAnimationChange(currentTrace);
		}
	}

	public void registerModelChangedListener(
			final IModelChangedListener listener) {
		modelListeners.add(new WeakReference<IModelChangedListener>(listener));
		if (currentStateSpace != null) {
			notifyModelChanged(currentStateSpace);
		}
	}

	/**
	 * Changes the current trace to the specified {@link Trace} and notifies an
	 * animation change ({@link AnimationSelector#notifyAnimationChange(Trace)})
	 * 
	 * @param trace
	 */
	public void changeCurrentAnimation(final Trace trace) {
		currentTrace = trace;
		notifyAnimationChange(trace);

		if (currentTrace != null
				&& currentTrace.getStateSpace() != currentStateSpace) {
			currentStateSpace = currentTrace.getStateSpace();
			notifyModelChanged(currentStateSpace);
		}
	}

	/**
	 * Adds the specified {@link Trace} trace to the registry, sets the current
	 * trace to trace, and notifies an animation change (
	 * {@link AnimationSelector#notifyAnimationChange(Trace)})
	 * 
	 * @param trace
	 */
	public void addNewAnimation(final Trace trace) {
		traces.add(trace);
		currentTrace = trace;
		notifyAnimationChange(trace);

		StateSpace s = trace.getStateSpace();
		if (s != null && !s.equals(currentStateSpace)) {
			currentStateSpace = s;
			notifyModelChanged(s);
		}
	}

	/**
	 * Let all {@link IAnimationChangeListener}s know that the current animation
	 * has changed
	 * 
	 * @param trace
	 *            {@link Trace} representing the current animation
	 */
	private void notifyAnimationChange(final Trace trace) {
		for (final WeakReference<IAnimationChangeListener> listener : traceListeners) {
			IAnimationChangeListener animationChangeListener = listener.get();
			if (animationChangeListener != null) {
				animationChangeListener.traceChange(trace);
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
		for (final WeakReference<IAnimationChangeListener> listener : traceListeners) {
			IAnimationChangeListener animationChangeListener = listener.get();
			if (animationChangeListener != null) {
				animationChangeListener.animatorStatus(busy);
			}
		}
	}

	private void notifyModelChanged(final StateSpace s) {
		for (WeakReference<IModelChangedListener> listener : modelListeners) {
			IModelChangedListener modelChangedListener = listener.get();
			if (modelChangedListener != null) {
				modelChangedListener.modelChanged(s);
			}
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
		return traces;
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
	 * {@link AnimationSelector#notifyAnimationChange(Trace)}
	 */
	public void refresh() {
		notifyAnimationChange(currentTrace);
		notifyModelChanged(currentStateSpace);
	}

	/**
	 * Lets the AnimationSelector know that the {@link Trace} object with
	 * reference oldTrace has been changed to newTrace so that the
	 * AnimationSelector can update its registry.
	 * 
	 * @param oldTrace
	 * @param newTrace
	 */
	public void replaceTrace(final Trace oldTrace, final Trace newTrace) {
		int indexOf = traces.indexOf(oldTrace);
		traces.set(indexOf, newTrace);
		if (oldTrace.equals(currentTrace)) {
			notifyAnimationChange(newTrace);
			currentTrace = newTrace;
		}

		if (currentTrace != null
				&& currentTrace.getStateSpace() != currentStateSpace) {
			currentStateSpace = currentTrace.getStateSpace();
			notifyModelChanged(currentStateSpace);
		}
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
		if (!traces.contains(trace)) {
			return;
		}
		if (currentTrace == trace) {
			int indexOf = traces.indexOf(trace);
			traces.remove(trace);
			if (traces.isEmpty()) {
				currentTrace = null;
				currentStateSpace = null;
				return;
			}
			if (indexOf == traces.size()) {
				currentTrace = traces.get(indexOf - 1);
				currentStateSpace = currentTrace.getStateSpace();
				return;
			}
			currentTrace = traces.get(indexOf);
			currentStateSpace = currentTrace.getStateSpace();
			return;
		}
		traces.remove(trace);
	}

	public void notifyAnimatorStatus(final String animatorId, final boolean busy) {
		if (currentTrace != null
				& currentTrace.getStateSpace().getId().equals(animatorId)) {
			notifyStatusChange(busy);
		}
	}

}