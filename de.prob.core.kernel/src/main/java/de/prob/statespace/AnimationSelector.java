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

	List<StateSpace> statespaces = new ArrayList<StateSpace>();
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

	public void unregisterModelChangedListener(
			final IModelChangedListener listener) {
		modelListeners.remove(listener);
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
		if (!statespaces.contains(s)) {
			statespaces.add(s);
		}
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
	 */
	public void notifyAnimationChange(final Trace trace) {
		for (final WeakReference<IAnimationChangeListener> listener : traceListeners) {
			IAnimationChangeListener animationChangeListener = listener.get();
			if (animationChangeListener != null) {
				animationChangeListener.traceChange(trace);
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

	public List<StateSpace> getStatespaces() {
		return statespaces;
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
		if (oldTrace.equals(currentTrace)) {
			notifyAnimationChange(newTrace);
		}
		int indexOf = traces.indexOf(oldTrace);
		traces.set(indexOf, newTrace);
		currentTrace = newTrace;

		if (currentTrace != null
				&& currentTrace.getStateSpace() != currentStateSpace) {
			currentStateSpace = currentTrace.getStateSpace();
			notifyModelChanged(currentStateSpace);
		}
	}

	/**
	 * Lets the {@link IAnimationListener} know that it should remove the
	 * {@link Trace} object from its registry.
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
				return;
			}
			if (indexOf == traces.size()) {
				currentTrace = traces.get(indexOf - 1);
				return;
			}
			currentTrace = traces.get(indexOf);
			return;
		}
		traces.remove(trace);
	}

}