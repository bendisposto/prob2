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
import de.prob.model.eventb.proof.SimpleProofNode
import de.prob.model.eventb.proof.THM
import de.prob.model.eventb.proof.VAR
import de.prob.model.eventb.proof.VWD
import de.prob.model.eventb.proof.WD
import de.prob.model.eventb.proof.WFIS
import de.prob.model.eventb.proof.WWD


class ProofFactory {

	private Logger logger = LoggerFactory.getLogger(ProofFactory.class)

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
		xml.prProof.each {
			cache[it.@name] = it
		}
		return cache
	}

	def addProofsForMachine(EventBMachine m, String baseFileName, typeEnv) {
		this.typeEnv = typeEnv
		def bpoXML = getXML(new File("${baseFileName}.bpo"))
		def predSetsXML = cachePredSetXML(bpoXML)
		def sequentsXML = cacheSequentXML(bpoXML)

		def bprXML = getXML(new File("${baseFileName}.bpr"))
		def proofXML = cacheProofXML(bprXML)

		return addMachineProofs(m, predSetsXML, sequentsXML, proofXML)
	}

	def addProofsForContext(Context c, String baseFileName, typeEnv) {
		this.typeEnv = typeEnv
		def bpoXML = getXML(new File("${baseFileName}.bpo"))
		def predSetsXML = cachePredSetXML(bpoXML)
		def sequentsXML = cacheSequentXML(bpoXML)

		def bprXML = getXML(new File("${baseFileName}.bpr"))
		def proofXML = cacheProofXML(bprXML)

		return addContextProofs(c, predSetsXML, sequentsXML, proofXML)
	}

	def addMachineProofs(EventBMachine m, predSetsXML, sequentsXML, proofXML) {
		List<ProofObligation> proofs = []
		sequentsXML.each {
			def name = it.getKey()
			def seq = it.getValue()
			def pr = proofXML[name]

			// extract proof goal
			def goal = new EventB(seq.poPredicate.@predicate[0], typeEnv)

			// extract hypotheses. At the beginning of a proof, this is the predicate set specified in the sequent
			def predSetName = seq.poPredicateSet.@parentSet[0]
			predSetName = predSetName.substring(predSetName.lastIndexOf('#')+1, predSetName.size());
			predSetName = predSetName.replace("\\","")
			def hyps = extractPredicateSet(predSetName, predSetsXML)

			// extract discharged
			def discharged = pr.@confidence == "1000"

			// extract proof description
			def desc = seq.@poDesc

			// extract first proof child
			def cachedPreds = [:]
			pr.prPred.each {
				cachedPreds[it.@name] = new EventB(it.@predicate,typeEnv)
			}
			def tree = pr.prRule.isEmpty() ? []: [
				extractProof(hyps, goal, cachedPreds, pr.prRule[0])
			]

			def split = name.split("/")
			def type = split.size() == 1 ? split[0] : (split.size() == 2 ? split[1] : split[2])

			switch(type) {
				case "EQL":
					def variable
					m.getRefines().each {
						if(it.getVariable(split[1]) != null) {
							variable = it.getVariable(split[1])
						}
					}
					EQL eql = new EQL(name, m.getEvent(split[0]), variable, goal, hyps, discharged, desc)
					eql.addChildrenNodes(tree)
					proofs << eql
					break
				case "FIN":
					FIN fin = new FIN(name, m.getVariant(), goal, hyps, discharged, desc)
					fin.addChildrenNodes(tree)
					proofs << fin
					break
				case "FIS":
					FIS fis = new FIS(name, m.getEvent(split[0]).getAction(split[1]), goal, hyps, discharged, desc)
					fis.addChildrenNodes(tree)
					proofs << fis
					break
				case "GRD":
					def guard
					m.getEvent(split[0]).getRefines().each {
						if(it.getGuard(split[1]) != null) {
							guard = it.getGuard(split[1])
						}
					}
					GRD grd = new GRD(name, m.getEvent(split[0]), guard, goal, hyps, discharged, desc)
					grd.addChildrenNodes(tree)
					proofs << grd
					break
				case "INV":
					INV inv = new INV(name, m.getEvent(split[0]), m.getInvariant(split[1]), goal, hyps, discharged, desc)
					inv.addChildrenNodes(tree)
					proofs << inv
					break
				case "MRG":
					MRG mrg = new MRG(name, m.getEvent(split[0]), goal, hyps, discharged, desc)
					mrg.addChildrenNodes(tree)
					proofs << mrg
					break
				case "NAT":
					NAT nat = new NAT(name, m.getEvent(split[0]), m.getVariant(), goal, hyps, discharged, desc)
					nat.addChildrenNodes(tree)
					proofs << nat
					break
				case "SIM":
					def action
					m.getEvent(split[0]).getRefines().each {
						if(it.getAction(split[1]) != null) {
							action = it.getAction(split[1])
						}
					}
					SIM sim = new SIM(name, m.getEvent(split[0]), action, goal, hyps, discharged, desc)
					sim.addChildrenNodes(tree)
					proofs << sim
					break
				case "THM":
					if(split.size() == 2) {
						THM thm = new THM(name, m.getInvariant(split[0]), goal, hyps, discharged, desc)
						thm.addChildrenNodes(tree)
						proofs << thm
					} else {
						THM thm = new THM(name, m.getEvent(split[0]).getGuard(split[1]), goal, hyps, discharged, desc)
						thm.addChildrenNodes(tree)
						proofs << thm
					}
					break
				case "VAR":
					VAR v = new VAR(name, m.getEvent(split[0]), m.getVariant(), goal, hyps, discharged, desc)
					v.addChildrenNodes(tree)
					proofs << v
					break
				case "VWD":
					VWD v = new VWD(name, m.getVariant(), goal, hyps, discharged, desc)
					v.addChildrenNodes(tree)
					proofs << v
					break
				case "WD":
					if(split.size() == 2) {
						WD wd = new WD(name, m.getInvariant(split[1]), goal, hyps, discharged, desc)
						wd.addChildrenNodes(tree)
						proofs << wd
					} else {
						Event event = m.events[split[0]]
						if(event.actions[split[1]] != null) {
							WD wd = new WD(name, event.getAction(split[1]), goal, hyps, discharged, desc)
							wd.addChildrenNodes(tree)
							proofs << wd
						} else {
							// If it is not an action, then it must be a guard
							WD wd = new WD(name, event.getGuard(split[1]), goal, hyps, discharged, desc)
							wd.addChildrenNodes(tree)
							proofs << wd
						}
					}
					break
				case "WFIS":
					WFIS wfis= new WFIS(name, m.getEvent(split[0]).getWitness(split[1]), goal, hyps, discharged, desc)
					wfis.addChildrenNodes(tree)
					proofs << wfis
					break
				case "WWD":
					WWD wwd = new WWD(name, m.getEvent(split[0]).getWitness(split[1]), goal, hyps, discharged, desc)
					wwd.addChildrenNodes(tree)
					proofs << wwd
					break
				default:
					logger.info("Could not resolve proof of type "+type+". Ignoring proof and continuing translation.")
			}
		}
		return proofs
	}

	def addContextProofs(Context c, predSetsXML, sequentsXML, proofXML) {
		List<ProofObligation> proofs = []
		sequentsXML.each {
			def name = it.getKey()
			def seq = it.getValue()
			def pr = proofXML[name]

			// extract proof goal
			def goal = new EventB(seq.poPredicate.@predicate[0], typeEnv)

			// extract hypotheses. At the beginning of a proof, this is the predicate set specified in the sequent
			def predSetName = seq.poPredicateSet.@parentSet[0]
			predSetName = predSetName.substring(predSetName.lastIndexOf('#')+1, predSetName.size());
			predSetName = predSetName.replace("\\","")
			def hyps = extractPredicateSet(predSetName, predSetsXML)

			// extract discharged
			def discharged = pr.@confidence == "1000"

			// extract proof description
			def desc = seq.@poDesc

			// extract first proof child
			def cachedPreds = [:]
			pr.prPred.each {
				cachedPreds[it.@name] = new EventB(it.@predicate,typeEnv)
			}
			def tree = pr.prRule.isEmpty() ? []: [
				extractProof(hyps, goal, cachedPreds, pr.prRule[0])
			]

			def split = name.split("/")
			def type = split[1]

			switch(type) {
				case "THM":
					THM thm = new THM(name, c.getAxiom(split[0]), goal, hyps, discharged, desc)
					thm.addChildrenNodes(tree)
					proofs << thm
					break
				case "WD":
					WD wd = new WD(name, c.getAxiom(split[0]), goal, hyps, discharged, desc)
					wd.addChildrenNodes(tree)
					proofs << wd
					break
				default:
					logger.info("Could not resolve proof of type "+type+". Ignoring proof and continuing translation.")
			}
		}
		return proofs
	}

	def extractPredicateSet(name, cachedSetXML) {
		def xml = cachedSetXML[name]
		Set<EventB> preds = new HashSet<EventB>()
		xml.poPredicate.@predicate.each {
			preds << new EventB(it,typeEnv)
		}
		if(xml.@parentSet == null) {
			return preds
		}
		def parentSet = xml.@parentSet
		parentSet = parentSet.substring(parentSet.lastIndexOf('#')+1, parentSet.size())
		parentSet = parentSet.replace("\\", "")
		preds.addAll(extractPredicateSet(parentSet, cachedSetXML))
		return preds
	}

	def extractProof(hyps, goal, cachedPreds, xml) {
		def discharged = xml.@confidence == "1000"
		def desc = xml.@prDisplay

		def SimpleProofNode proof = new SimpleProofNode(goal, hyps, discharged, desc)

		def kids = []
		if(xml.prAnte.isEmpty()) {
			return proof
		}
		xml.prAnte.each {
			def g = it.@prGoal == null ? goal : cachedPreds[it.@prGoal]
			def h = new HashSet<EventB>()
			it.prHypAction.each {
				if(it.@prInfHyps != null) {
					def ids = it.@prInfHyps.split(",")
					ids.each { h << cachedPreds[it] }
				}
			}
			if(it.@prHyps != null) {
				def ids = it.@prHyps.split(",")
				ids.each { h << cachedPreds[it] }
			}
			h.addAll(hyps)

			if(!it.prRule.isEmpty()) {
				kids << extractProof(h,g,cachedPreds,it.prRule[0])
			} else {
				// if the Ante has no children, is is not discharged and it doesn't have a description.
				// But hypotheses and goal may have changed
				kids << new SimpleProofNode(g, h, false, "")
			}
		}
		proof.addChildrenNodes(kids)
		return proof
	}

}
