package de.prob.statespace

/**
 * Defined {@link SyncedEvent}s can be used to synchronize animations with the
 * {@link SyncedTraces} object.
 * @author joy
 *
 */
class SyncedEvent {

	final String name
	final Map<UUID,Event> synced

	/**
	 * @param name is a user defined name to specify this particular event.
	 * @param synced is a mapping from the UUID of a Trace object to an {@link Event} object
	 * defining the event that should be executed on the specified trace
	 */
	def SyncedEvent(String name, Map<UUID, Event> synced=[:]) {
		this.name = name
		this.synced = synced
	}

	/**
	 * @param trace for which this event should be synchronized
	 * @param name of the event to be synchronized
	 * @param parameters a list of String predicates defining the parameters
	 * @return a new {@link SyncedEvent} with this configuration added.
	 */
	def SyncedEvent sync(Trace trace, String name, List<String> parameters) {
		def intNames = [:]
		intNames.putAll(synced)
		intNames[trace.getUUID()] = new Event(name, parameters)
		return new SyncedEvent(this.name, intNames)
	}

	class Event {
		String name
		List<String> parameters

		def Event(String name, List<String> parameters) {
			this.name = name
			this.parameters = parameters
		}
	}
}
