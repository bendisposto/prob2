package de.prob.model.eventb.translate

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import de.prob.model.eventb.Context
import de.prob.model.eventb.Event
import de.prob.model.eventb.EventBMachine
import de.prob.model.eventb.proof.EQL
import de.prob.model.eventb.proof.FIN
import de.prob.model.eventb.proof.FIS
import de.prob.model.eventb.proof.GRD
import de.prob.model.eventb.proof.INV
import de.prob.model.eventb.proof.MRG
import de.prob.model.eventb.proof.NAT
import de.prob.model.eventb.proof.ProofObligation
import de.prob.model.eventb.proof.SIM
import de.prob.model.eventb.proof.THM
import de.prob.model.eventb.proof.VAR
import de.prob.model.eventb.proof.VWD
import de.prob.model.eventb.proof.WD
import de.prob.model.eventb.proof.WFIS
import de.prob.model.eventb.proof.WWD


class ProofFactory {

	class PO {
		String source
		String desc
		Map<String, String> elements
		boolean discharged

		def PO(source, desc, elements, discharged) {
			this.source = source
			this.desc = desc
			this.elements = elements
			this.discharged = discharged
		}
	}

	Logger logger = LoggerFactory.getLogger(ProofFactory.class)

	def List<ProofObligation> addProofsForMachine(EventBMachine m, String baseFileName) {
		def time = System.currentTimeMillis()
		def time1 = System.currentTimeMillis()
		def bpoText = new File(baseFileName+".bpo").getText().replace("org.eventb.core.", "")
		def slurper = new XmlParser().parseText(bpoText)
		def descriptions = [:]
		slurper.poSequent.each {
			descriptions[it.@name] = it.@poDesc
		}
		println "    Cached Proof bpo for "+m.getName()+": "+(System.currentTimeMillis() - time)

		time = System.currentTimeMillis()
		def bpsText = new File(baseFileName+".bps").getText().replace("org.eventb.core.","")
		Node bpsXML = new XmlParser().parseText(bpsText)
		println "    Parsing bps for "+m.getName()+": "+(System.currentTimeMillis() - time)

		time = System.currentTimeMillis()
		Map<String, Node> statuses = [:]
		bpsXML.psStatus.each {
			statuses[it.@name] = it.@confidence == "1000"
		}
		println "    Cached Proof bpr for "+m.getName()+": "+(System.currentTimeMillis() - time)

		time = System.currentTimeMillis()
		def pos = addMachineProofs(m, statuses, descriptions)
		println "    Added All Proofs for ${m.getName()}: ${System.currentTimeMillis() - time}"
		println "  added Proofs To machine: "+m.getName()+": "+(System.currentTimeMillis() - time1)
		return pos
	}

	def List<ProofObligation> addProofsForContext(Context c, String baseFileName) {
		def time = System.currentTimeMillis()
		def time1 = System.currentTimeMillis()
		def bpoText = new File(baseFileName+".bpo").getText().replace("org.eventb.core.", "")
		def slurper = new XmlParser().parseText(bpoText)
		def descriptions = [:]
		slurper.poSequent.each {
			descriptions[it.@name] = it.@poDesc
		}
		println "    Cached Proof bpo for "+c.getName()+": "+(System.currentTimeMillis() - time)

		time = System.currentTimeMillis()
		def bpsText = new File(baseFileName+".bps").getText().replace("org.eventb.core.","")
		Node bpsXML = new XmlParser().parseText(bpsText)
		println "    Parsing bps for "+c.getName()+": "+(System.currentTimeMillis() - time)

		time = System.currentTimeMillis()
		Map<String, Node> statuses = [:]
		bpsXML.psStatus.each {
			statuses[it.@name] = it.@confidence == "1000"
		}
		println "    Cached Proof bpr for "+c.getName()+": "+(System.currentTimeMillis() - time)

		time = System.currentTimeMillis()
		def pos = addContextProofs(c, statuses, descriptions)
		println "    Added All Proofs for ${c.getName()}: ${System.currentTimeMillis() - time}"
		println "  added Proofs To context: "+c.getName()+": "+(System.currentTimeMillis() - time1)
		return pos
	}

	def addMachineProofs(EventBMachine m, statuses, descriptions) {
		List<PO> proofs = []
		def switching = 0
		def mysteryBlock3 = 0
		statuses.each {
			def time = System.currentTimeMillis()
			def name = it.getKey()
			def discharged = it.getValue()

			// extract proof description
			def desc = descriptions[name]

			def split = name.split("/")
			def type = split.size() == 1 ? split[0] : (split.size() == 2 ? split[1] : split[2])

			mysteryBlock3 += (System.currentTimeMillis() - time)

			time = System.currentTimeMillis()
			switch(type) {
				case "EQL":
					def variable
					m.getRefines().each {
						if(it.getVariable(split[1]) != null) {
							variable = it.getVariable(split[1])
						}
					}
					EQL eql = new EQL(name, m.getEvent(split[0]), variable, null, discharged, desc, null)
					proofs << eql
					break
				case "FIN":
					FIN fin = new FIN(name, m.getVariant(), null, discharged, desc, null)
					proofs << fin
					break
				case "FIS":
					FIS fis = new FIS(name, m.getEvent(split[0]).getAction(split[1]), null,  discharged, desc, null)
					proofs << fis
					break
				case "GRD":
					def guard
					m.getEvent(split[0]).getRefines().each {
						if(it.getGuard(split[1]) != null) {
							guard = it.getGuard(split[1])
						}
					}
					GRD grd = new GRD(name, m.getEvent(split[0]), guard, null,  discharged, desc, null)
					proofs << grd
					break
				case "INV":
					INV inv = new INV(name, m.getEvent(split[0]), m.getInvariant(split[1]), null,  discharged, desc, null)
					proofs << inv
					break
				case "MRG":
					MRG mrg = new MRG(name, m.getEvent(split[0]), null,  discharged, desc, null)
					proofs << mrg
					break
				case "NAT":
					NAT nat = new NAT(name, m.getEvent(split[0]), m.getVariant(), null,  discharged, desc, null)
					proofs << nat
					break
				case "SIM":
					def action
					m.getEvent(split[0]).getRefines().each {
						if(it.getAction(split[1]) != null) {
							action = it.getAction(split[1])
						}
					}
					SIM sim = new SIM(name, m.getEvent(split[0]), action, null,  discharged, desc, null)
					proofs << sim
					break
				case "THM":
					if(split.size() == 2) {
						THM thm = new THM(name, m.getInvariant(split[0]), null, discharged, desc, null)
						proofs << thm
					} else {
						THM thm = new THM(name, m.getEvent(split[0]).getGuard(split[1]), null,  discharged, desc, null)
						proofs << thm
					}
					break
				case "VAR":
					VAR v = new VAR(name, m.getEvent(split[0]), m.getVariant(), null, discharged, desc, null)
					proofs << v
					break
				case "VWD":
					VWD v = new VWD(name, m.getVariant(), null,  discharged, desc, null)
					proofs << v
					break
				case "WD":
					if(split.size() == 2) {
						WD wd = new WD(name, m.getInvariant(split[0]), null,  discharged, desc, null)
						proofs << wd
					} else {
						Event event = m.events[split[0]]
						if(event.actions[split[1]] != null) {
							WD wd = new WD(name, event.getAction(split[1]), null, discharged, desc, null)
							proofs << wd
						} else {
							// If it is not an action, then it must be a guard
							WD wd = new WD(name, event.getGuard(split[1]), null,  discharged, desc, null)
							proofs << wd
						}
					}
					break
				case "WFIS":
					WFIS wfis= new WFIS(name, m.getEvent(split[0]).getWitness(split[1]), null,  discharged, desc, null)
					proofs << wfis
					break
				case "WWD":
					WWD wwd = new WWD(name, m.getEvent(split[0]).getWitness(split[1]), null, discharged, desc, null)
					proofs << wwd
					break
				default:
					logger.info("Could not resolve proof of type "+type+". Ignoring proof and continuing translation.")
			}
			switching += (System.currentTimeMillis() - time)
		}
		println "      Mystery 3: $mysteryBlock3"
		println "      Switching: $switching"
		return proofs
	}

	def addContextProofs(Context c, statuses, descriptions) {
		List<ProofObligation> proofs = []
		def switching = 0
		def mysteryBlock3 = 0
		statuses.each {
			def time = System.currentTimeMillis()
			def name = it.getKey()
			def discharged = it.getValue()

			// extract proof description
			def desc = descriptions[name]

			def split = name.split("/")
			def type = split[1]

			mysteryBlock3 += (System.currentTimeMillis() - time)

			time = System.currentTimeMillis()
			switch(type) {
				case "THM":
					THM thm = new THM(name, c.getAxiom(split[0]), null,  discharged, desc, null)
					proofs << thm
					break
				case "WD":
					WD wd = new WD(name, c.getAxiom(split[0]), null,  discharged, desc, null)
					proofs << wd
					break
				default:
					logger.info("Could not resolve proof of type "+type+". Ignoring proof and continuing translation.")
			}
			switching += (System.currentTimeMillis() - time)
		}
		println "      Mystery 3: $mysteryBlock3"
		println "      Switching: $switching"
		return proofs
	}

}
