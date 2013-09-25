package de.prob.model.eventb.translate

import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.proof.SimpleProofNode


/**
 * @author joy
 * This class was created to allow the lazy expansion of Proof trees. When the {@link ProofObligation} is
 * generated, it receives a {@link ProofTreeCreator} as a parameter. If someone wants to view the proof tree
 * associated with the given {@link ProofObligation}, they can retrieve it by lazily activating the
 * {@link ProofTreeCreator}. Only then will the XML be parsed and the proof tree created. This is for
 * performance reasons.
 */
class ProofTreeCreator {

	def static xmlCache = [:]
	def static predCache = [:]

	def static getXML(file, name) {
		if(xmlCache.containsKey(file.absolutePath)) {
			return xmlCache[file.absolutePath][name]
		}
		def text = file.text.replaceAll("org.eventb.core.","")
		def xml = new XmlParser().parseText(text)
		def cached = [:]
		xml.prProof.each {
			cached[it.@name] = it
		}
		xmlCache[file.absolutePath, cached]
		return cached[name]
	}

	boolean created
	def goal
	def Set<EventB> hyps
	def file
	def xml
	def proofName
	def predSetName
	def predSetXML

	public ProofTreeCreator(goal, predSetXML, predSetName, proofName, file) {
		created = false
		this.goal = goal
		this.file = file
		this.proofName = proofName
		this.predSetXML = predSetXML
		this.predSetName = predSetName
	}

	def Set<EventB> getHyps() {
		if(hyps != null) {
			return hyps
		}
		hyps = extractPredicateSet(predSetName, predSetXML)
		return hyps
	}

	def List<SimpleProofNode> create() {
		def cachedPreds = [:]
		xml = getXML(file, proofName)
		xml.prPred.each {
			cachedPreds[it.@name] = new EventB(it.@predicate,typeEnv)
		}
		def tree = xml.prRule.isEmpty() ? []: [
			extractProof(getHyps(), goal, cachedPreds, xml.prRules[0])
		]
		created = true
		return tree
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

	def extractPredicateSet(name, cachedSetXML) {
		if(predCache[file.absolutePath] != null && predCache[file.absolutePath].containsKey(name)) {
			return predCache[file.absolutePath][name]
		}
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
		if(predCache[file.absolutePath] == null) {
			predCache[file.absolutePath] = [:]
		}
		predCache[file.absolutePath][name] = preds
		return preds
	}
}
