package de.prob.statespace

import de.prob.model.representation.ModelElementList
import de.prob.statespace.SyncedEvent.Event

class SyncedTraces {
	final List<Trace> traces
	final ModelElementList<SyncedEvent> syncedEvents

	def SyncedTraces(List<Trace> traces, List<SyncedEvent> events) {
		this.traces = traces
		this.syncedEvents = (events instanceof ModelElementList) ? events : new ModelElementList<SyncedEvent>(events)
	}

	def invokeMethod(String name, args) {
		execute(name)
	}

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
