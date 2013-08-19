package de.prob.model.representation

import de.prob.model.eventb.proof.INV
import de.prob.model.eventb.proof.IProof
import de.prob.model.eventb.proof.THM
import de.prob.model.eventb.proof.WD

class ProofFactory {

	def elements = [:]
	def List<IProof> discharged = []
	def List<IProof> unproven = []

	def ProofFactory(xml) {
		xml.psStatus.each {
			def infos = [:]
			def split = it.'@name'.split("/")
			if(split.size() == 2) {
				infos["type"] = split[1]
				infos["discharged"] = (it.'@psBroken' != "true") && (it.'@confidence' == "1000")
				elements[split[0]] = infos
			} else {
				if(!elements.containsKey(split[0])) {
					elements[split[0]] = [:]
				}
				def eventInfos = [:]
				eventInfos["type"] = split[2]
				eventInfos["discharged"] = (it.'@psBroken' != "true") && (it.'@confidence' == "1000")
				elements[split[0]][split[1]] = eventInfos
			}
		}
	}

	def addProof(label, element) {
		if(!elements.containsKey(label)) {
			return
		}
		def proof = elements[label]
		if(proof.discharged) {
			def p = createProof(proof.type, element)
			if(p) {
				discharged << p
			}
		} else {
			def p = createProof(proof.type, element)
			if(p) {
				unproven << p
			}
		}
	}

	def addProof(eventName, label, element) {
		if(!elements.containsKey(eventName)) {
			return
		}
		def eventProofs = elements[eventName]
		if(!eventProofs.containsKey(label)) {
			return
		}

		def proof = eventProofs[label]
		if(proof.discharged) {
			def p = createProof(proof.type, element)
			if(p) {
				discharged << p
			}
		} else {
			def p = createProof(proof.type, element)
			if(p) {
				unproven << p
			}
		}
	}

	def addInvariantProofs(invariants, events) {
		events.each { event ->
			def name = event.getName()
			if(elements.containsKey(name)) {
				def eventInfo = elements[name]
				invariants.each { inv ->
					def invName = inv.getName()
					if(eventInfo.containsKey(invName)) {
						def proof = eventInfo[invName]
						if(proof.discharged) {
							discharged << new INV(event, inv)
						} else {
							unproven << new INV(event, inv)
						}
					}
				}
			}
		}
	}

	def createProof(type, element) {
		if(type == "THM") {
			return new THM(element)
		}
		if(type == "WD") {
			return new WD(element)
		}
		return null
	}
}
