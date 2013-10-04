package de.prob.model.eventb.translate

import de.prob.model.eventb.Context
import de.prob.model.eventb.Event
import de.prob.model.eventb.EventBMachine
import de.prob.model.eventb.proof.Tuple
import de.prob.model.eventb.proof.UncalculatedPO
import de.prob.model.representation.ModelElementList

class ProofTranslator {

	XmlParser parser = new XmlParser()

	def List<UncalculatedPO> translateProofsForMachine(EventBMachine m, String baseFileName) {
		def bpoText = new File(baseFileName+".bpo").getText().replace("org.eventb.core.", "")
		def parsed = parser.parseText(bpoText)
		def descriptions = [:]
		parsed.poSequent.each {
			descriptions[it.@name] = it.@poDesc
		}

		def bpsText = new File(baseFileName+".bps").getText().replace("org.eventb.core.","")
		Node bpsXML = parser.parseText(bpsText)

		Map<String, Node> statuses = [:]
		bpsXML.psStatus.each {
			statuses[it.@name] = it.@confidence == "1000"
		}

		def pos = addMachineProofs(m, statuses, descriptions)
		return pos
	}

	def List<UncalculatedPO> translateProofsForContext(Context c, String baseFileName) {
		def bpoText = new File(baseFileName+".bpo").getText().replace("org.eventb.core.", "")
		def parsed = parser.parseText(bpoText)
		def descriptions = [:]
		parsed.poSequent.each {
			descriptions[it.@name] = it.@poDesc
		}

		def bpsText = new File(baseFileName+".bps").getText().replace("org.eventb.core.","")
		Node bpsXML = parser.parseText(bpsText)

		Map<String, Node> statuses = [:]
		bpsXML.psStatus.each {
			statuses[it.@name] = it.@confidence == "1000"
		}

		def pos = addContextProofs(c, statuses, descriptions)
		return pos
	}

	def addMachineProofs(EventBMachine m, statuses, descriptions) {
		List<UncalculatedPO> proofs = new ModelElementList<UncalculatedPO>()
		statuses.each {
			def name = it.getKey()
			def discharged = it.getValue()

			// extract proof description
			def desc = descriptions[name]

			def split = name.split("/")
			def type = split.size() == 1 ? split[0] : (split.size() == 2 ? split[1] : split[2])

			def source = m.getName()

			switch(type) {
				case "GRD":
					def elements = []
					Event concreteEvent = m.getEvent(split[0])
					def guard
					concreteEvent.getRefines().each {
						if(it.getGuard(split[1]) != null) {
							guard = it.getGuard(split[1])
							elements << new Tuple("event", guard.getParentEvent().getName())
							elements << new Tuple("guard", guard.getName())
						}
					}
					elements << new Tuple("event", concreteEvent.getName())
					proofs << new UncalculatedPO(source, name, desc, elements, discharged)
					break
				case "INV":
					def elements = [
						new Tuple("event", split[0]),
						new Tuple("invariant", split[1])
					]
					proofs << new UncalculatedPO(source, name,desc, elements, discharged)
					break
				case "THM":
					if(split.size() == 2) {
						def elements = [
							new Tuple("invariant", split[0])
						]
						proofs << new UncalculatedPO(source,name, desc, elements, discharged)
					} else {
						def elements = [
							new Tuple("guard",split[1]),
							new Tuple("event",split[0])
						]
						proofs << new UncalculatedPO(source,name, desc, elements, discharged)
					}
					break
				case "WD":
					if(split.size() == 2) {
						def elements = [
							new Tuple("invariant",split[0])
						]
						proofs << new UncalculatedPO(source,name, desc, elements, discharged)
					} else {
						Event event = m.getEvent(split[0])
						if(event.actions[split[1]] != null) {
							def elements = [
								new Tuple("event",event.getName()),
								new Tuple("action", split[1])
							]
							proofs << new UncalculatedPO(source,name, desc, elements, discharged)
						} else {
							// If it is not an action, then it must be a guard
							def elements = [
								new Tuple("event", event.getName()),
								new Tuple("guard",event.getName())
							]
							proofs << new UncalculatedPO(source,name, desc, elements, discharged)
						}
					}
					break
				default:
					proofs << new UncalculatedPO(source,name, desc, [], discharged)
			}
		}
		return proofs
	}

	def addContextProofs(Context c, statuses, descriptions) {
		List<UncalculatedPO> proofs = new ModelElementList<UncalculatedPO>()
		statuses.each {
			def name = it.getKey()
			def discharged = it.getValue()

			// extract proof description
			def desc = descriptions[name]

			def split = name.split("/")
			def type = split[1]

			def source = c.getName()

			switch(type) {
				case "THM":
					def elements = [
						new Tuple("axiom", split[0])
					]
					proofs << new UncalculatedPO(source, name, desc, elements, discharged)
					break
				case "WD":
					def elements = [
						new Tuple("axiom", split[0])
					]
					proofs << new UncalculatedPO(source, name, desc, elements, discharged)
					break
				default:
					proofs << new UncalculatedPO(source, name, desc, [], discharged)
			}
		}
		return proofs
	}

}
