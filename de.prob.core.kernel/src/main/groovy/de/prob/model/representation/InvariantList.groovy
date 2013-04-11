package de.prob.model.representation;

import de.prob.model.eventb.EventBInvariant


class InvariantList extends ArrayList<EventBInvariant> {

	def keys

	def InvariantList(Collection<Invariant> invariants) {
		def map = [:]
		invariants.each {
			if(it instanceof EventBInvariant) {
				add(it)
				map.put(it.getName(),it)
			}
		}
		keys = map
	}

	def getProperty(String prop) {
		return keys[prop];
	}

	def setProperty(def prop, def obj) {
		keys[prop]=obj;
	}
}
