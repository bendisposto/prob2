package de.prob.statespace

class SyncedEvent {

	final String name
	final Map<UUID,Event> synced

	def SyncedEvent(String name, Map<UUID, Event> synced=[:]) {
		this.name = name
		this.synced = synced
	}

	def SyncedEvent sync(Trace t, String name, List<String> parameters) {
		def intNames = [:]
		intNames.putAll(synced)
		intNames[t.getUUID()] = new Event(name, parameters)
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
