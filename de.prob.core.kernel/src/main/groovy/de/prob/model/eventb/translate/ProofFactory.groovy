package de.prob.model.eventb.translate

import org.eventb.core.ast.extension.IFormulaExtension

import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.proof.INV
import de.prob.model.eventb.proof.SimpleProofNode
import de.prob.model.eventb.proof.THM
import de.prob.model.eventb.proof.WD

class ProofFactory {

	def elements = [:]
	def List<? extends SimpleProofNode> proofs = []
	def Set<IFormulaExtension> typeEnv
	def predicateSets = [:]
	def sequentsXML = [:]
	def proofsXML = [:]

	def ProofFactory(String baseFileName, typeEnv) {
		this.typeEnv = typeEnv
		extractBPSFile(getXML(new File("${baseFileName}.bps")))
		extractBPOFile(getXML(new File("${baseFileName}.bpo")))
		extractBPRFile(getXML(new File("${baseFileName}.bpr")))
	}

	def extractBPRFile(xml) {
		xml.prProof.each {
			proofsXML[it.@name] = it
		}
	}

	def extractBPOFile(xml) {
		def cache = [:]
		xml.poPredicateSet.each {
			cache[it.@name] = it
		}
		xml.poPredicateSet.each {
			extractPredicateSet(cache, it);
		}
		xml.poSequent.each {
			sequentsXML[it.@name] = it
		}
	}

	def extractPredicateSet(cache, xml) {
		def name = xml.@name
		if(predicateSets.containsKey(name)) {
			return predicateSets[name]
		}
		Set<EventB> preds = new HashSet<EventB>()
		xml.poPredicate.@predicate.each { preds << new EventB(it,typeEnv)}
		if(xml.@parentSet == null) {
			return preds
		}
		def parentSet = xml.@parentSet[0]
		parentSet = parentSet.substring(parentSet.lastIndexOf('#')+1, parentSet.size())
		preds.addAll(extractPredicateSet(cache,cache[parentSet]))
		predicateSets[name] = preds
		return preds
	}

	def getXML(file) {
		def text = file.text.replaceAll("org.eventb.core.","")
		return new XmlParser().parseText(text)
	}

	def extractBPSFile(xml) {
		xml.psStatus.each {
			def infos = [:]
			def split = it.'@name'.split("/")
			if(split.size() == 2) {
				infos["type"] = split[1]
				infos["discharged"] = (it.'@psBroken' != "true") && (it.'@confidence' == "1000")
				infos["name"] = it.'@name'
				elements[split[0]] = infos
			} else {
				if(!elements.containsKey(split[0])) {
					elements[split[0]] = [:]
				}
				def eventInfos = [:]
				eventInfos["type"] = split[2]
				eventInfos["discharged"] = (it.'@psBroken' != "true") && (it.'@confidence' == "1000")
				eventInfos["name"] = it.'@name'
				elements[split[0]][split[1]] = eventInfos
			}
		}
	}

	def addProof(label, element) {
		if(!elements.containsKey(label)) {
			return
		}
		def proof = elements[label]
		def p = createProof(proof.type, proof.name, element, proof.discharged)
		proofs << p
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
			def p = createProof(proof.type, proof.name, element, proof.discharged)
			if(p) {
				discharged << p
			}
		} else {
			def p = createProof(proof.type, proof.name, element, proof.discharged)
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
						proofs << new INV(proof.name, event, inv, proof.discharged)
					}
				}
			}
		}
	}

	def createProof(type, name, element, discharged) {
		def proofState = extractProofState(name)
		def seq = sequentsXML[name]
		def goal = new EventB(seq.poPredicate.@predicate[0], typeEnv)
		def desc = seq.@poDesc
		def predSetName = seq.poPredicateSet.@parentSet[0]
		predSetName = predSetName.substring(predSetName.lastIndexOf('#')+1, predSetName.size());
		def hyps = predicateSets[predSetName]

		def SimpleProofNode proof = new SimpleProofNode(goal, hyps, discharged, desc)
		def cachedPreds = extractCachedPreds(proof)

		if(type == "THM") {
			proof = new THM(name, element, discharged, proofState, desc)
		}
		if(type == "WD") {
			proof = new WD(name, element, discharged, proofState, desc)
		}
		if(proof != null) {
			extractChildren(sequentsXML[name], proofsXML[name])
		}
		return proof
	}

	def extractProof(hyps, goal, cachedPreds, xml) {
		def discharged = xml.@confidence == "1000"
		def desc = xml.@prDisplay

		def SimpleProofNode proof = new SimpleProofNode(goal, hyps, discharged, desc)

		def kids = []
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
				kids << extractProofs(h,g,cachedPreds,it.prRule[0])
			}
		}
		proof.addChildrenNodes(kids)
		return proof
	}

	def extractDescription(name) {
		return sequentsXML[name].@poDesc
	}

	def extractProofState(name) {
		def seq = sequentsXML[name]
		def proof = proofsXML[name]

		def goal = new EventB(seq.poPredicate.@predicate[0], typeEnv)
		def desc = seq.@poDesc
		def predSetName = seq.poPredicateSet.@parentSet[0]
		predSetName = predSetName.substring(predSetName.lastIndexOf('#')+1, predSetName.size());
		def hypos = predicateSets[predSetName]

		def status = proof.prProof.confidence == "1000"
	}

	def extractCachedPreds(proof) {
		def cache = [:]
		proof.prPred.each {
			cache[it.@name] = new EventB(it.@predicate,typeEnv)
		}
	}
}
