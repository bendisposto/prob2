package de.prob.model.eventb.translate

import org.eventb.core.ast.extension.IFormulaExtension
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import de.prob.animator.domainobjects.EventB
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

	private Logger logger = LoggerFactory.getLogger(ProofFactory.class)
	private predCache = [:]

	def Set<IFormulaExtension> typeEnv

	def getXML(file) {
		def text = file.text.replaceAll("org.eventb.core.","")
		return new XmlParser().parseText(text)
	}

	def cachePredSetXML(xml) {
		def cache = [:]
		xml.poPredicateSet.each {
			def name = it.@name
			name = name.replace("\\","")
			cache[it.@name] = it
		}
		return cache
	}

	def cacheSequentXML(xml) {
		def cache = [:]
		xml.poSequent.each {
			cache[it.@name] = it
		}
		return cache
	}

	def cacheProofXML(xml) {
		def cache = [:]
		xml.psStatus.each {
			cache[it.@name] = it
		}
		return cache
	}

	def addProofsForMachine(EventBMachine m, String baseFileName, typeEnv) {
		this.typeEnv = typeEnv
		def time = System.currentTimeMillis()
		def bpoXML = getXML(new File("${baseFileName}.bpo"))
		def predSetsXML = cachePredSetXML(bpoXML)
		def sequentsXML = cacheSequentXML(bpoXML)
		println "Cached Proof bpo for "+m.getName()+": "+(System.currentTimeMillis() - time)

		time = System.currentTimeMillis()
		def bpsXML = getXML(new File("${baseFileName}.bps"))
		println "Parsing bps for "+m.getName()+": "+(System.currentTimeMillis() - time)
		time = System.currentTimeMillis()
		def proofXML = cacheProofXML(bpsXML)
		println "Cached Proof bpr for "+m.getName()+": "+(System.currentTimeMillis() - time)

		time = System.currentTimeMillis()
		def pos = addMachineProofs(m, predSetsXML, sequentsXML, proofXML, new File("${baseFileName}.pbr"))
		println "Added All Proofs for ${m.getName()}: ${System.currentTimeMillis() - time}"
		return pos
	}

	def addProofsForContext(Context c, String baseFileName, typeEnv) {
		this.typeEnv = typeEnv
		def time = System.currentTimeMillis()
		def bpoXML = getXML(new File("${baseFileName}.bpo"))
		def predSetsXML = cachePredSetXML(bpoXML)
		def sequentsXML = cacheSequentXML(bpoXML)
		println "Cached Proof bpo for "+c.getName()+": "+(System.currentTimeMillis() - time)

		time = System.currentTimeMillis()
		def bpsXML = getXML(new File("${baseFileName}.bps"))
		println "Parsing bps for "+c.getName()+": "+(System.currentTimeMillis() - time)
		time = System.currentTimeMillis()
		def proofXML = cacheProofXML(bpsXML)
		println "Cached Proof bpr for "+c.getName()+": "+(System.currentTimeMillis() - time)

		time = System.currentTimeMillis()
		def pos = addContextProofs(c, predSetsXML, sequentsXML, proofXML, new File("${baseFileName}.bpr"))
		println "Added All Proofs for ${c.getName()}: ${System.currentTimeMillis() - time}"
		return pos
	}

	def addMachineProofs(EventBMachine m, predSetsXML, sequentsXML, proofXML, bprFile) {
		List<ProofObligation> proofs = []
		def switching = 0
		def extractingHyps = 0
		def mysteryBlock1 = 0
		def mysteryBlock2 = 0
		def mysteryBlock3 = 0
		sequentsXML.each {
			def time = System.currentTimeMillis()
			def name = it.getKey()
			def seq = it.getValue()
			def ps = proofXML[name]

			// extract proof goal
			def goal = new EventB(seq.poPredicate.@predicate[0], typeEnv)
			mysteryBlock1 += (System.currentTimeMillis() - time)

			time = System.currentTimeMillis()
			// extract hypotheses. At the beginning of a proof, this is the predicate set specified in the sequent
			def predSetName = seq.poPredicateSet.@parentSet[0]
			predSetName = predSetName.substring(predSetName.lastIndexOf('#')+1, predSetName.size());
			predSetName = predSetName.replace("\\","")
			extractingHyps += (System.currentTimeMillis() - time)

			time = System.currentTimeMillis()
			// extract discharged
			def discharged = ps.@confidence == "1000"

			// extract proof description
			def desc = seq.@poDesc
			mysteryBlock2 += (System.currentTimeMillis() - time)

			time = System.currentTimeMillis()
			def split = name.split("/")
			def type = split.size() == 1 ? split[0] : (split.size() == 2 ? split[1] : split[2])
			def creator = new ProofTreeCreator(goal, predSetsXML, predSetName, name, bprFile)
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
					EQL eql = new EQL(name, m.getEvent(split[0]), variable, goal, discharged, desc, creator)
					proofs << eql
					break
				case "FIN":
					FIN fin = new FIN(name, m.getVariant(), goal, discharged, desc, creator)
					proofs << fin
					break
				case "FIS":
					FIS fis = new FIS(name, m.getEvent(split[0]).getAction(split[1]), goal,  discharged, desc, creator)
					proofs << fis
					break
				case "GRD":
					def guard
					m.getEvent(split[0]).getRefines().each {
						if(it.getGuard(split[1]) != null) {
							guard = it.getGuard(split[1])
						}
					}
					GRD grd = new GRD(name, m.getEvent(split[0]), guard, goal,  discharged, desc, creator)
					proofs << grd
					break
				case "INV":
					INV inv = new INV(name, m.getEvent(split[0]), m.getInvariant(split[1]), goal,  discharged, desc, creator)
					proofs << inv
					break
				case "MRG":
					MRG mrg = new MRG(name, m.getEvent(split[0]), goal,  discharged, desc, creator)
					proofs << mrg
					break
				case "NAT":
					NAT nat = new NAT(name, m.getEvent(split[0]), m.getVariant(), goal,  discharged, desc, creator)
					proofs << nat
					break
				case "SIM":
					def action
					m.getEvent(split[0]).getRefines().each {
						if(it.getAction(split[1]) != null) {
							action = it.getAction(split[1])
						}
					}
					SIM sim = new SIM(name, m.getEvent(split[0]), action, goal,  discharged, desc, creator)
					proofs << sim
					break
				case "THM":
					if(split.size() == 2) {
						THM thm = new THM(name, m.getInvariant(split[0]), goal, discharged, desc, creator)
						proofs << thm
					} else {
						THM thm = new THM(name, m.getEvent(split[0]).getGuard(split[1]), goal,  discharged, desc, creator)
						proofs << thm
					}
					break
				case "VAR":
					VAR v = new VAR(name, m.getEvent(split[0]), m.getVariant(), goal, discharged, desc, creator)
					proofs << v
					break
				case "VWD":
					VWD v = new VWD(name, m.getVariant(), goal,  discharged, desc, creator)
					proofs << v
					break
				case "WD":
					if(split.size() == 2) {
						WD wd = new WD(name, m.getInvariant(split[0]), goal,  discharged, desc, creator)
						proofs << wd
					} else {
						Event event = m.events[split[0]]
						if(event.actions[split[1]] != null) {
							WD wd = new WD(name, event.getAction(split[1]), goal, discharged, desc, creator)
							proofs << wd
						} else {
							// If it is not an action, then it must be a guard
							WD wd = new WD(name, event.getGuard(split[1]), goal,  discharged, desc, creator)
							proofs << wd
						}
					}
					break
				case "WFIS":
					WFIS wfis= new WFIS(name, m.getEvent(split[0]).getWitness(split[1]), goal,  discharged, desc, creator)
					proofs << wfis
					break
				case "WWD":
					WWD wwd = new WWD(name, m.getEvent(split[0]).getWitness(split[1]), goal, discharged, desc, creator)
					proofs << wwd
					break
				default:
					logger.info("Could not resolve proof of type "+type+". Ignoring proof and continuing translation.")
			}
			switching += (System.currentTimeMillis() - time)
		}
		println "  Mystery 1: $mysteryBlock1"
		println "  Extracting Hypotheses: $extractingHyps"
		println "  Mystery 2: $mysteryBlock2"
		println "  Mystery 3: $mysteryBlock3"
		println "  Switching: $switching"
		return proofs
	}

	def addContextProofs(Context c, predSetsXML, sequentsXML, proofXML, bprFile) {
		List<ProofObligation> proofs = []
		def switching = 0
		def extractingHyps = 0
		def mysteryBlock1 = 0
		def mysteryBlock2 = 0
		def mysteryBlock3 = 0
		sequentsXML.each {
			def time = System.currentTimeMillis()
			def name = it.getKey()
			def seq = it.getValue()
			def ps = proofXML[name]

			// extract proof goal
			def goal = new EventB(seq.poPredicate.@predicate[0], typeEnv)
			mysteryBlock1 += (System.currentTimeMillis() - time)

			time = System.currentTimeMillis()
			// extract hypotheses. At the beginning of a proof, this is the predicate set specified in the sequent
			def predSetName = seq.poPredicateSet.@parentSet[0]
			predSetName = predSetName.substring(predSetName.lastIndexOf('#')+1, predSetName.size());
			predSetName = predSetName.replace("\\","")
			extractingHyps += (System.currentTimeMillis() - time)

			time = System.currentTimeMillis()
			// extract discharged
			def discharged = ps.@confidence == "1000"

			// extract proof description
			def desc = seq.@poDesc
			mysteryBlock2 += (System.currentTimeMillis() - time)

			time = System.currentTimeMillis()
			def split = name.split("/")
			def type = split[1]
			def creator = new ProofTreeCreator(goal, predSetsXML, predSetName, name, bprFile)
			mysteryBlock3 += (System.currentTimeMillis() - time)

			time = System.currentTimeMillis()
			switch(type) {
				case "THM":
					THM thm = new THM(name, c.getAxiom(split[0]), goal,  discharged, desc, creator)
					proofs << thm
					break
				case "WD":
					WD wd = new WD(name, c.getAxiom(split[0]), goal,  discharged, desc, creator)
					proofs << wd
					break
				default:
					logger.info("Could not resolve proof of type "+type+". Ignoring proof and continuing translation.")
			}
			switching += (System.currentTimeMillis() - time)
		}
		println "  Mystery 1: $mysteryBlock1"
		println "  Extracting Hypotheses: $extractingHyps"
		println "  Mystery 2: $mysteryBlock2"
		println "  Mystery 3: $mysteryBlock3"
		println "  Switching: $switching"
		return proofs
	}

}
