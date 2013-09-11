package de.prob.model.eventb.translate

import org.eventb.core.ast.extension.IFormulaExtension

import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.proof.FIS
import de.prob.model.eventb.proof.INV
import de.prob.model.eventb.proof.SimpleProofNode
import de.prob.model.eventb.proof.THM
import de.prob.model.eventb.proof.WD

class ProofFactory {


	def List<? extends SimpleProofNode> proofs = []
	def Set<IFormulaExtension> typeEnv



	def addProofs(String baseFileName, typeEnv) {
		this.typeEnv = typeEnv
		def bpoXML = getXML(new File("${baseFileName}.bpo"))
		def predSetsXML = cachePredSetXML(bpoXML)
		def sequentsXML = cacheSequentXML(bpoXML)

		def bprXML = getXML(new File("${baseFileName}.bpr"))
		def proofXML = cacheProofXML(bprXML)

		createProofClosures(predSets, sequentsXML, proofXML)
	}

	def extractBPRFile(xml) {
		xml.prProof.each {
			proofsXML[it.@name] = it
		}
	}

	def createProofClosures(predSetsXML, sequentsXML, proofXML) {
		assert sequentsXML.size() == proofXML.size()
		sequentsXML.each {
			def name = it.getKey()
			def seq = it.getValue()
			def pr = proofXML[name]

			// extract proof goal
			def goal = new EventB(seq.poPredicate.@predicate[0], typeEnv)

			// extract hypotheses. At the beginning of a proof, this is the predicate set specified in the sequent
			def predSetName = seq.poPredicateSet.@parentSet[0]
			predSetName = predSetName.substring(predSetName.lastIndexOf('#')+1, predSetName.size());
			def hyps = extractPredicateSet(predSetName, predSetsXML)

			// extract discharged
			def discharged = pr.@confidence == "1000"

			// extract proof description
			def desc = seq.@poDesc
		}
	}


	def cachePredSetXML(xml) {
		def cache = [:]
		xml.poPredicateSet.each {
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

	def extractPredicateSets(xml) {
		def cache = [:]
		xml.poPredicateSet.each {
			cache[it.@name] = name
		}
		def sets = [:]
		xml.poPredicateSet.each  {
			addPredicateSet(cache, xml, sets)
		}
	}

	def extractPredicateSet(cache, xml, sets) {
		def name = xml.@name
		if(sets.containsKey(name)) {
			return sets[name]
		}
		Set<EventB> preds = new HashSet<EventB>()
		xml.poPredicate.@predicate.each { preds << new EventB(it,typeEnv)}
		if(xml.@parentSet == null) {
			sets[name] = preds
			return preds
		}
		def parentSet = xml.@parentSet
		parentSet = parentSet.substring(parentSet.lastIndexOf('#')+1, parentSet.size())
		preds.addAll(extractPredicateSet(cache,cache[parentSet]))
		predicateSets[name] = preds
		return preds
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
		preds.addAll(extractPredicateSet(parentSet, cachedSetXML))
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
						proofs << createInvariantProof(proof.name, event, inv, proof.discharged)
					}
				}
			}
		}
	}

	def addEventProof(concreteEvent, abstractEvent) {
		def eventInfo = elements[concreteEvent.getName()]
		if(eventInfo != null) {
			if(eventInfo.type == "GRD") {
			}
		}
	}

	def createInvariantProof(name, event, inv, discharged) {
		def seq = sequentsXML[name]
		def goal = new EventB(seq.poPredicate.@predicate[0], typeEnv)
		def desc = seq.@poDesc
		def predSetName = seq.poPredicateSet.@parentSet[0]
		predSetName = predSetName.substring(predSetName.lastIndexOf('#')+1, predSetName.size());
		def hyps = predicateSets[predSetName]

		def proof = new INV(name, event, inv, goal, hyps, discharged, desc)
		def kid = extractTopChild(name, proof.discharged, hyps, goal)
		if(kid != null) {
			proof.addChildrenNodes([kid])
		}
		return proof
	}

	def createProof(type, name, element, discharged) {
		def SimpleProofNode proof
		def seq = sequentsXML[name]
		def goal = new EventB(seq.poPredicate.@predicate[0], typeEnv)
		def desc = seq.@poDesc
		def predSetName = seq.poPredicateSet.@parentSet[0]
		predSetName = predSetName.substring(predSetName.lastIndexOf('#')+1, predSetName.size());
		def hyps = predicateSets[predSetName]

		if(type == "THM") {
			proof = new THM(name, element, goal, hyps, discharged, desc)
		}
		if(type == "WD") {
			proof = new WD(name, element, goal, hyps, discharged, desc)
		}
		if(type == "FIS") {
			proof = new FIS(name, element, goal, hyps, discharged, desc)
		}
		if(proof != null) {
			def kid = extractTopChild(name, discharged, hyps, goal)
			if(kid != null) {
				def children  = [kid]
				proof.addChildrenNodes(children)
			}
		}
		return proof
	}

	def extractTopChild(name, discharged, hyps, goal) {

		def proofXML = proofsXML[name]
		def cachedPreds = extractCachedPreds(proofXML)

		if(proofXML != null && !proofXML.prRule.isEmpty()) {
			return extractProof(hyps, goal, cachedPreds, proofXML.prRule[0])
		}
		return null
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

	def extractCachedPreds(proof) {
		def cache = [:]
		proof.prPred.each {
			cache[it.@name] = new EventB(it.@predicate,typeEnv)
		}
		return cache
	}
}
