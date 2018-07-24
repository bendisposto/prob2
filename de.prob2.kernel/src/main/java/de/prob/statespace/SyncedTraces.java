package de.prob.statespace;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.prob.model.representation.ModelElementList;

import groovy.lang.GroovyObjectSupport;

/**
 * Provides an API to synchronize the animation of different {@link Trace}s.
 * @author joy
 */
public class SyncedTraces extends GroovyObjectSupport {
	private final List<Trace> traces;
	private final ModelElementList<SyncedEvent> syncedEvents;

	public SyncedTraces(List<Trace> traces, List<SyncedEvent> events) {
		this.traces = traces;
		this.syncedEvents = (events instanceof ModelElementList) ? (ModelElementList<SyncedEvent>)events : new ModelElementList<>(events);
	}

	public final List<Trace> getTraces() {
		return this.traces;
	}

	/**
	 * This method has been marked as deprecated because it is only available in the class to
	 * enable groovy magic. Calls the {@link SyncedTraces#execute(String)} method. In a Java environment,
	 * call this directly.
	 * @param name of method
	 * @param args from method
	 */
	@Deprecated
	@Override
	public SyncedTraces invokeMethod(String name, Object args) {
		return execute(name);
	}

	/**
	 * Attempts to execute a synchronized event.
	 * @param name of a user defined {@link SyncedEvent}
	 * @return a new {@link SyncedTraces} object in which all of the events defined by the
	 * specified {@link SyncedEvent} have been executed.
	 * @throws IllegalArgumentException if no synced event with that name has been defined
	 */
	public SyncedTraces execute(String name) {
		if (syncedEvents.getAt(name) == null) {
			throw new IllegalArgumentException("No syncronized event is named " + name);
		}
		final SyncedEvent event = (SyncedEvent)syncedEvents.getAt(name);
		final List<Trace> newTraces = traces.stream().map(t -> {
			SyncedEvent.Event e = event.getSynced().get(t.getUUID());
			return e != null ? t.execute(e.getName(), e.getParameters()) : t;
		}).collect(Collectors.toList());
		return new SyncedTraces(newTraces, syncedEvents);
	}

	/**
	 * If the name and parameters for the specified {@link Trace} are defined by a {@link SyncedEvent},
	 * the {@link SyncedEvent} is fired. Otherwise, the name and parameters combination is executed for
	 * the specified {@link Trace} via the {@link Trace#execute(String, List)} method.
	 * @param index of the {@link Trace} that is of interest
	 * @param name of the event to be executed
	 * @param parameters a list of String predicates which represent a conjunction defining the
	 * parameters that can be defined for this event.
	 * @return a new {@link SyncedTraces} object after the specified event has been executed
	 * @throws IllegalArgumentException if executing the specified event is not successful
	 */
	public SyncedTraces execute(int index, String name, List<String> parameters) {
		final List<Trace> newTraces = new ArrayList<>(traces);
		final Trace t = traces.get(index);
		final List<SyncedEvent> synced = syncedEvents.stream().filter(e -> {
			SyncedEvent.Event e2 = e.getSynced().get(t.getUUID());
			return e2 != null && e2.getName().equals(name) && e2.getParameters().equals(parameters);
		}).collect(Collectors.toList());

		if (!synced.isEmpty()) {
			SyncedTraces result = this;
			for (SyncedEvent event : synced) {
				result = result.execute(event.getName());
			}
			return result;
		}

		newTraces.set(index, traces.get(index).execute(name, parameters));
		return new SyncedTraces(newTraces, syncedEvents);
	}

	@Override
	public String toString() {
		return traces.stream().map(Trace::getRep).collect(Collectors.joining("\n"));
	}
}
