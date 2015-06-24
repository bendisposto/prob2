package de.prob.statespace

import de.prob.model.representation.ModelElementList
import de.prob.statespace.SyncedEvent.Event

/**
 * Provides an API to synchronize the animation of different {@link Trace}s.
 * @author joy
 *
 */
class SyncedTraces {
	final List<Trace> traces
	private final ModelElementList<SyncedEvent> syncedEvents

	def SyncedTraces(List<Trace> traces, List<SyncedEvent> events) {
		this.traces = traces
		this.syncedEvents = (events instanceof ModelElementList) ? events : new ModelElementList<SyncedEvent>(events)
	}

	/**
	 * This method has been marked as deprecated because it is only available in the class to
	 * enable groovy magic. Calls the {@link SyncedTraces#execute(String)} method. In a Java environment,
	 * call this directly.
	 * @param name of method
	 * @param args from method
	 */
	@Deprecated
	def invokeMethod(String name, args) {
		execute(name)
	}


	/**
	 * Attempts to execute a synchronized event.
	 * @param name of a user defined {@link SyncedEvent}
	 * @return a new {@link SyncedTraces} object in which all of the events defined by the
	 * specified {@link SyncedEvent} have been executed.
	 * @throws IllegalArgumentException if no synced event with that name has been defined
	 */
	def SyncedTraces execute(String name) {
		if (!syncedEvents[name]) {
			throw new IllegalArgumentException("No syncronized event is named $name")
		}
		SyncedEvent event = syncedEvents[name]
		def newTraces = traces.collect { Trace t ->
			Event e = event.synced[t.getUUID()]
			e ? t.execute(e.name, e.parameters) : t
		}
		return new SyncedTraces(newTraces, syncedEvents)
	}

	/**
	 * If the name and parameters for the specified {@link Trace} are defined by a {@link SyncedEvent},
	 * the {@link SyncedEvent} is fired. Otherwise, the name and parameters combination is executed for
	 * the specified {@link Trace} via the {@link Trace#execute(String,List<String>)} method.
	 * @param index of the {@link Trace} that is of interest
	 * @param name of the event to be executed
	 * @param parameters a list of String predicates which represent a conjunction defining the
	 * parameters that can be defined for this event.
	 * @return a new {@link SyncedTraces} object after the specified event has been executed
	 * @throws IllegalArgumentException if executing the specified event is not successful
	 */
	def SyncedTraces execute(int index, String name, List<String> parameters) {
		List<Trace> newTraces = new ArrayList<Trace>(traces)
		Trace t = traces[index]
		List<SyncedEvent> synced = syncedEvents.findAll { SyncedEvent e ->
			Event e2 = e.synced[t.getUUID()]
			e2 && e2.name == name && e2.parameters == parameters
		}

		if (synced) {
			return synced.inject(this) { result, event -> result.execute(event.name) }
		}

		newTraces[index] = traces[index].execute(name, parameters)
		return new SyncedTraces(newTraces, syncedEvents)
	}

	def String toString() {
		traces.collect { it.getRep() }.iterator().join("\n")
	}
}
