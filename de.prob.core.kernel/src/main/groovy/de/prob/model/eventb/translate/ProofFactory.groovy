package de.prob.model.eventb.translate

import org.eventb.core.ast.extension.IFormulaExtension

import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.Context
import de.prob.model.eventb.Event
import de.prob.model.eventb.EventBMachine
import de.prob.model.eventb.EventBVariable
import de.prob.model.eventb.proof.EQL
import de.prob.model.eventb.proof.FIN
import de.prob.model.eventb.proof.FIS
import de.prob.model.eventb.proof.GRD
import de.prob.model.eventb.proof.INV
import de.prob.model.eventb.proof.MRG
import de.prob.model.eventb.proof.NAT
import de.prob.model.eventb.proof.SIM
import de.prob.model.eventb.proof.SimpleProofNode
import de.prob.model.eventb.proof.THM
import de.prob.model.eventb.proof.VAR
import de.prob.model.eventb.proof.VWD
import de.prob.model.eventb.proof.WD
import de.prob.model.eventb.proof.WFIS
import de.prob.model.eventb.proof.WWD

class ProofFactory {

	def List<? extends SimpleProofNode> proofs = []
	def Set<IFormulaExtension> typeEnv
	def global = [:]
	def eventBased = [:]

	def addProofs(String baseFileName, typeEnv) {
		this.typeEnv = typeEnv
		def bpoXML = getXML(new File("${baseFileName}.bpo"))
		def predSetsXML = cachePredSetXML(bpoXML)
		def sequentsXML = cacheSequentXML(bpoXML)

		def bprXML = getXML(new File("${baseFileName}.bpr"))
		def proofXML = cacheProofXML(bprXML)

		createProofClosures(predSetsXML, sequentsXML, proofXML)
	}

	def getXML(file) {
		def text = file.text.replaceAll("org.eventb.core.","")
		return new XmlParser().parseText(text)
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

	def createProofClosures(predSetsXML, sequentsXML, proofXML) {
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

			// extract first proof child
			def cachedPreds = [:]
			pr.prPred.each {
				cachedPreds[it.@name] = new EventB(it.@predicate,typeEnv)
			}
			def kids = pr.prRule.isEmpty() ? []: [
				extractProof(hyps, goal, cachedPreds, pr.prRule[0])
			]

			def split = name.split("/")
			if(split.size() == 1 || (split.size() == 2 && (split[1] == "THM" || split[1] == "WD"))) {
				def label = split.size() == 1 ? 'variant' : split[0]
				def type = split.size() == 1 ? split[0] : split[1]
				createGlobalProof(label, type, name, goal, hyps, discharged, desc, kids)
			} else {
				def eventLabel = split[0]
				def elementLabel = split.size() == 2 ? (split[1] == "MRG" ? 'event' : 'variant') : split[1]
				def type = split.size() == 2 ? split[1] : split[2]
				createEventBasedProof(eventLabel, elementLabel, type, name, goal, hyps, discharged, desc, kids)
			}
		}
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

	def createGlobalProof(label, type, name, goal, hyps, discharged, desc, kids) {
		if(!global.containsKey(label)) {
			global[label] = []
		}
		if(type == "VWD") {
			global['variant'] << createVWD(name, goal, hyps, discharged, desc, kids)
		}
		if(type == "FIN") {
			global['variant'] << createFIN(name, goal, hyps, discharged, desc, kids)
		}
		if(type == "WD") {
			global[label] << createWD(name, goal, hyps, discharged, desc, kids)
		}
		if(type == "THM") {
			global[label] << createTHM(name, goal, hyps, discharged, desc, kids)
		}
	}

	def createEventBasedProof(eventLabel, elementLabel, type, name, goal, hyps, discharged, desc, kids) {
		if(!eventBased.containsKey(eventLabel)) {
			eventBased[eventLabel] = [:]
		}
		// Special case for EQL, WWD, and WFIS: the elementLabel is the code, so it is possible to create an element now that is identical to what you would be able to fish out of the machine later
		if(!(type == "EQL" || type == "WWD" || type == "WFIS") && !eventBased[eventLabel].containsKey(elementLabel)) {
			eventBased[eventLabel][elementLabel] = []
		}
		if(type == "EQL" && !eventBased[eventLabel].containsKey('event')) {
			eventBased[eventLabel]['event'] = []
		}
		if((type == "WWD" || type == "WFIS") && !eventBased[eventLabel].containsKey('witness')) {
			eventBased[eventLabel]['witness'] = []
		}
		if(type == "WD") {
			eventBased[eventLabel][elementLabel] << createWD(name, goal, hyps, discharged, desc, kids)
		}
		if(type == "FIS") {
			eventBased[eventLabel][elementLabel] << createFIS(name, goal, hyps, discharged, desc, kids)
		}
		if(type == "INV") {
			eventBased[eventLabel][elementLabel] << createINV(name, goal, hyps, discharged, desc, kids)
		}
		if(type == "GRD") {
			eventBased[eventLabel][elementLabel] << createGRD(name, goal, hyps, discharged, desc, kids)
		}
		if(type == "MRG") {
			eventBased[eventLabel][elementLabel] << createMRG(name, goal, hyps, discharged, desc, kids)
		}
		if(type == "SIM") {
			eventBased[eventLabel][elementLabel] << createSIM(name, goal, hyps, discharged, desc, kids)
		}
		if(type == "EQL") {
			def var = new EventBVariable(elementLabel)
			eventBased[eventLabel]['event'] << createEQL(var, name, goal, hyps, discharged, desc, kids)
		}
		if(type == "NAT") {
			def var = new EventBVariable()
			eventBased[eventLabel][elementLabel] << createNAT(name, goal, hyps, discharged, desc, kids)
		}
		if(type == "VAR") {
			eventBased[eventLabel][elementLabel] << createVAR(name, goal, hyps, discharged, desc, kids)
		}
		if(type == "WWD") {
			def p = new EventB(elementLabel)
			eventBased[eventLabel]['witness'] << createWWD(p, name, goal, hyps, discharged, desc, kids)
		}
		if(type == "WFIS") {
			def p = new EventB(elementLabel)
			eventBased[eventLabel]['witness'] << createWFIS(p, name, goal, hyps, discharged, desc, kids)
		}
	}

	private createEQL(variable, proofName, goal, hyps, discharged, desc, tree) {
		// EventBVariables are equivalent based on their name, so we just recreate the variable based on its label beforehand rather than searching for the equivalent object later
		return { event ->
			EQL eql = new EQL(proofName, event, variable, goal, hyps, discharged, desc)
			eql.addChildrenNodes(tree)
			return eql
		}
	}

	private createFIN(proofName, goal, hyps, discharged, desc, tree) {
		return { variant ->
			FIN fin = new FIN(proofName, variant, goal, hyps, discharged, desc)
			fin.addChildrenNodes(tree)
			return fin
		}
	}

	private createFIS(proofName, goal, hyps, discharged, desc, tree) {
		return { action ->
			FIS fis = new FIS(proofName, action, goal, hyps, discharged, desc)
			fis.addChildrenNodes(tree)
			return fis
		}
	}

	private createGRD(proofName, goal, hyps, discharged, desc, tree) {
		return { event, guard ->
			GRD grd = new GRD(proofName, event, guard, goal, hyps, discharged, desc)
			grd.addChildrenNodes(tree)
			return grd
		}
	}

	private createINV(proofName, goal, hyps, discharged, desc, tree) {
		return { event, invariant ->
			INV inv = new INV(proofName, event, invariant, goal, hyps, discharged, desc)
			inv.addChildrenNodes(tree)
			return inv
		}
	}

	private createMRG(proofName, goal, hyps, discharged, desc, tree) {
		return { event ->
			MRG mrg = new MRG(proofName, event, goal, hyps, discharged, desc)
			mrg.addChildrenNodes(tree)
			return mrg
		}
	}

	private createNAT(proofName, goal, hyps, discharged, desc, tree) {
		return { event, variant ->
			NAT nat = new NAT(proofName, event, variant, goal, hyps, discharged, desc)
			nat.addChildrenNodes(tree)
			return nat
		}
	}

	private createSIM(proofName, goal, hyps, discharged, desc, tree) {
		return { event, action ->
			SIM sim = new SIM(proofName, event, action, goal, hyps, discharged, desc)
			sim.addChildrenNodes(tree)
			return sim
		}
	}

	private createTHM(proofName, goal, hyps, discharged, desc, tree) {
		return { e ->
			THM thm = new THM(proofName, e, goal, hyps, discharged, desc)
			thm.addChildrenNodes(tree)
			return thm
		}
	}

	private createVAR(proofName, goal, hyps, discharged, desc, tree) {
		return { event, variant ->
			VAR v = new VAR(proofName, event, variant, goal, hyps, discharged, desc)
			v.addChildrenNodes(tree)
			return v
		}
	}

	private createVWD(proofName, goal, hyps, discharged, desc, tree) {
		return { variant ->
			VWD v = new VWD(proofName, variant, goal, hyps, discharged, desc)
			v.addChildrenNodes(tree)
			return v
		}
	}

	private createWD(proofName, goal, hyps, discharged, desc, tree) {
		return { e ->
			WD wd = new WD(proofName, e, goal, hyps, discharged, desc)
			wd.addChildrenNodes(tree)
			return wd
		}
	}

	private createWWD(param, proofName, goal, hyps, discharged, desc, tree) {
		return { witness ->
			WWD wwd = new WWD(proofName, witness, param, goal, hyps, discharged, desc)
			wwd.addChildrenNodes(tree)
			return wwd
		}
	}

	private createWFIS(param, proofName, goal, hyps, discharged, desc, tree) {
		return { witness ->
			WFIS wfis= new WFIS(proofName, witness, param, goal, hyps, discharged, desc)
			wfis.addChildrenNodes(tree)
			return wfis
		}
	}

	def addProofsFromContext(Context c) {
		// ADD THM AND WD
		c.getAxioms().getKeys().each {
			def label = it.getKey()
			def axiom = it.getValue()
			global[label].each { fkt ->
				proofs << fkt(axiom)
			}
		}
	}

	def addProofsFromMachine(EventBMachine m) {
		def invariants = m.getInvariants()
		// ADD THM AND WD
		invariants.getKeys().each {
			def label = it.getKey()
			def inv = it.getValue()

			global[label].each { fkt ->
				proofs << fkt(inv)
			}
		}

		// ADD VWD AND FIN
		def variant = m.getVariant()
		if(variant != null) {
			global['variant'].each { fkt ->
				proofs << fkt(variant)
			}
		}

		m.getEvents().getKeys().each {
			def label = it.getKey()
			def Event event = it.getValue()

			def closures = eventBased[label] == null ? [:] : eventBased[label]

			// ADD MRG AND EQL
			closures['event'].each { fkt ->
				proofs << fkt(event)
			}

			// ADD INV
			invariants.getKeys().each {
				def iLabel = it.getKey()
				def inv = it.getValue()

				closures[iLabel].each { fkt ->
					proofs << fkt(event, inv)
				}
			}

			// ADD NAT AND VAR
			if(variant != null) {
				closures['variant'].each { fkt ->
					proofs << fkt(event, variant)
				}
			}

			// ADD SIM AND GRD
			event.getRefines().getKeys().each {
				def refL = it.getKey()
				def refined = it.getValue()

				// ADD SIM
				refined.getActions().getKeys().each {
					def actL = it.getKey()
					def action = it.getValue()

					closures[actL].each { fkt ->
						proofs << fkt(event, action)
					}
				}

				// ADD GRD
				refined.getGuards().getKeys().each {
					def guardL = it.getKey()
					def guard = it.getValue()

					closures[guardL].each { fkt ->
						proofs << fkt(event, guard)
					}
				}
			}

			// ADD FIS
			event.getActions().getKeys().each {
				def actL = it.getKey()
				def action = it.getValue()

				closures[actL].each { fkt->
					proofs << fkt(action)
				}
			}

			// ADD WFIS AND WWD
			event.getWitnesses().getKeys().each {
				def witness = it.getValue()

				closures['witness'].each { fkt ->
					proofs << fkt(witness)
				}
			}
		}
	}

}
