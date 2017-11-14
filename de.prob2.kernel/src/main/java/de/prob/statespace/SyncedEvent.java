package de.prob.statespace;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Defined {@link SyncedEvent}s can be used to synchronize animations with the
 * {@link SyncedTraces} object.
 *
 * @author joy
 */
public class SyncedEvent {
	/**
	 * @param name   is a user defined name to specify this particular event.
	 * @param synced is a mapping from the UUID of a Trace object to an {@link Event} object
	 *               defining the event that should be executed on the specified trace
	 */
	public SyncedEvent(String name, Map<UUID, Event> synced) {
		this.name = name;
		this.synced = synced;
	}

	/**
	 * @param name   is a user defined name to specify this particular event.
	 */
	public SyncedEvent(String name) {
		this(name, new LinkedHashMap());
	}

	/**
	 * @param trace      for which this event should be synchronized
	 * @param name       of the event to be synchronized
	 * @param parameters a list of String predicates defining the parameters
	 * @return a new {@link SyncedEvent} with this configuration added.
	 */
	public SyncedEvent sync(Trace trace, String name, List<String> parameters) {
		LinkedHashMap intNames = new LinkedHashMap();
		intNames.putAll(synced);
		intNames.put(trace.getUUID(), new Event(name, parameters));
		return new SyncedEvent(this.name, intNames);
	}

	public final String getName() {
		return name;
	}

	public final Map<UUID, Event> getSynced() {
		return synced;
	}

	private final String name;
	private final Map<UUID, Event> synced;

	public class Event {
		public Event(String name, List<String> parameters) {
			this.name = name;
			this.parameters = parameters;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public List<String> getParameters() {
			return parameters;
		}

		public void setParameters(List<String> parameters) {
			this.parameters = parameters;
		}

		private String name;
		private List<String> parameters;
	}
}
