package de.prob.model.eventb.translate

import org.eventb.core.ast.extension.IFormulaExtension

import spock.lang.Specification
import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.Event
import de.prob.model.eventb.EventBInvariant
import de.prob.model.eventb.EventBMachine
import de.prob.model.eventb.Event.EventType
import de.prob.model.eventb.proof.INV
import de.prob.model.eventb.proof.SimpleProofNode

class ProofFactoryTest extends Specification {

	def ProofFactory proofFactory
	def setup() {
		proofFactory = new ProofFactory()
		proofFactory.typeEnv = []
	}

	def "can parse solved rule true"() {
		when:
		def String s = '<prRule name="r2" confidence="1000" prDisplay="⊤ goal" prGoal="p7" prHyps=""/>'
		def xml = new XmlParser().parseText(s)
		def proof = proofFactory.extractProof(new HashSet<EventB>(), new EventB("⊤"), [:], xml)

		then:
		proof != null
		proof instanceof SimpleProofNode
		proof.isDischarged()
		proof.getGoal() == new EventB("⊤")
		proof.getHypotheses().isEmpty()
		proof.getChildrenNodes().isEmpty()
	}

	def "can extract cached predicates"() {
		when:
		def String s = '''<prProof name="INITIALISATION/inv4/INV" confidence="1000" prFresh="" prGoal="p0" prHyps="">
							<prRule name="r0" confidence="1000" prDisplay="Partition rewrites in hyp (partition(ProcID,{P1},{P2},{P3}))" prHyps="">
								<prAnte name="'">
									<prHypAction name="FORWARD_INF0" prHyps="p1" prInfHyps="p2,p3,p4,p5"/>
									<prHypAction name="HIDE1" prHyps="p1"/>
									<prHypAction name="SELECT2" prHyps="p2,p3,p4,p5"/>
									<prRule name="r1" confidence="1000" prDisplay="simplification rewrites" prGoal="p0" prHyps="">
										<prAnte name="'" prGoal="p7">
											<prHypAction name="HIDE0" prHyps="p6"/>
											<prRule name="r2" confidence="1000" prDisplay="⊤ goal" prGoal="p7" prHyps=""/>
										</prAnte>
									</prRule>
								</prAnte>
							</prRule>
							<prPred name="p7" predicate="⊤"/>
							<prPred name="p4" predicate="¬P1=P3"></prPred>
							<prPred name="p5" predicate="¬P2=P3"></prPred>
							<prPred name="p2" predicate="ProcID={P1,P2,P3}"></prPred>
							<prPred name="p0" predicate="card(∅ ⦂ ℙ(ProcID))≤1"/>
							<prPred name="p6" predicate="finite(∅ ⦂ ℙ(ProcID))"/>
							<prPred name="p3" predicate="¬P1=P2"></prPred>
							<prPred name="p1" predicate="partition(ProcID,{P1},{P2},{P3})"></prPred>
						</prProof>'''
		def xml = new XmlParser().parseText(s)
		def preds = [:]
		xml.prPred.each {
			preds[it.@name] = new EventB(it.@predicate,new HashSet<IFormulaExtension>())
		}

		then:
		preds != null
		!preds.isEmpty()
		preds.size() == 8
		preds["p0"] == new EventB("card(∅ ⦂ ℙ(ProcID))≤1")
		preds["p1"] == new EventB("partition(ProcID,{P1},{P2},{P3})")
		preds["p2"] == new EventB("ProcID={P1,P2,P3}")
		preds["p3"] == new EventB("¬P1=P2")
		preds["p4"] == new EventB("¬P1=P3")
		preds["p5"] == new EventB("¬P2=P3")
		preds["p6"] == new EventB("finite(∅ ⦂ ℙ(ProcID))")
		preds["p7"] == new EventB("⊤")
	}

	def "can extract simple proof using cached predicates"() {
		when:
		def String s = '''<prProof name="INITIALISATION/inv4/INV" confidence="1000" prFresh="" prGoal="p0" prHyps="">
							<prRule name="r0" confidence="1000" prDisplay="Partition rewrites in hyp (partition(ProcID,{P1},{P2},{P3}))" prHyps="">
								<prAnte name="'">
									<prHypAction name="FORWARD_INF0" prHyps="p1" prInfHyps="p2,p3,p4,p5"/>
									<prHypAction name="HIDE1" prHyps="p1"/>
									<prHypAction name="SELECT2" prHyps="p2,p3,p4,p5"/>
									<prRule name="r1" confidence="1000" prDisplay="simplification rewrites" prGoal="p0" prHyps="">
										<prAnte name="'" prGoal="p7">
											<prHypAction name="HIDE0" prHyps="p6"/>
											<prRule name="r2" confidence="1000" prDisplay="⊤ goal" prGoal="p7" prHyps=""/>
										</prAnte>
									</prRule>
								</prAnte>
							</prRule>
							<prPred name="p7" predicate="⊤"/>
							<prPred name="p4" predicate="¬P1=P3"></prPred>
							<prPred name="p5" predicate="¬P2=P3"></prPred>
							<prPred name="p2" predicate="ProcID={P1,P2,P3}"></prPred>
							<prPred name="p0" predicate="card(∅ ⦂ ℙ(ProcID))≤1"/>
							<prPred name="p6" predicate="finite(∅ ⦂ ℙ(ProcID))"/>
							<prPred name="p3" predicate="¬P1=P2"></prPred>
							<prPred name="p1" predicate="partition(ProcID,{P1},{P2},{P3})"></prPred>
						</prProof>'''
		def xml = new XmlParser().parseText(s)
		def cachedPreds = [:]
		xml.prPred.each {
			cachedPreds[it.@name] = new EventB(it.@predicate,new HashSet<IFormulaExtension>())
		}
		def SimpleProofNode proof = proofFactory.extractProof(new HashSet<EventB>(), new EventB("card(∅ ⦂ ℙ(ProcID))≤1"), cachedPreds, xml.prRule[0])

		then:
		proof != null
		proof.description == "Partition rewrites in hyp (partition(ProcID,{P1},{P2},{P3}))"
		proof.getHypotheses().isEmpty()
		proof.isDischarged()
		def kids = proof.getChildrenNodes()
		kids.size() == 1
		def kid = kids.first()
		kid.isDischarged()
		kid.description == "simplification rewrites"
		kid.getHypotheses().size() == 4
		kid.getGoal() == proof.getGoal()
		def kidskids = kid.getChildrenNodes()
		kidskids.size() == 1
		def kidskid = kidskids.first()
		kidskid.isDischarged()
		kidskid.description == "⊤ goal"
		kidskid.getHypotheses().size() == 4
		kidskid.getGoal() == new EventB("⊤")
		kidskid.getChildrenNodes().size() == 0
	}

	def "extracting hypotheses works"() {
		when:
		// xml is simplified from Scheduler0.bpo. Some of the predicate sets are edited so that only the relevant sets (the ones that include predicate) are considered.
		// The elements from the sequent that are not used (poSource, poSelHint) are also deleted
		def importantFromBPO = '''<poFile poStamp="0"><poSequent name="del/inv7/INV" accurate="true" poDesc="Invariant  preservation" poStamp="0">
									<poPredicateSet name="SEQHYP" parentSet="/Scheduler/Scheduler0.bpo|poFile#Scheduler0|poPredicateSet#EVTALLHYPProcessf'"/>
									<poPredicate name="SEQHYQ" predicate="active∩(waiting ∖ {p})=(∅ ⦂ ℙ(ProcID))" source="/Scheduler/Scheduler0.bum|machineFile#Scheduler0|invariant#internal_inv7I"/>
								  </poSequent>
								  <poPredicateSet name="EVTALLHYPProcessf'" parentSet="/Scheduler/Scheduler0.bpo|poFile#Scheduler0|poPredicateSet#ALLHYP" poStamp="0">
								  	<poPredicate name="PRD0" predicate="p∈waiting" source="/Scheduler/Scheduler0.bum|machineFile#Scheduler0|event#internal_evt13|guard#internal_grd1"/>
								  </poPredicateSet>
								  <poPredicateSet name="ALLHYP" parentSet="/Scheduler/Scheduler0.bpo|poFile#Scheduler0|poPredicateSet#HYPProcessev" poStamp="0">
									<poPredicate name="PRD3" predicate="card(active)≤1" source="/Scheduler/Scheduler0.bum|machineFile#Scheduler0|invariant#internal_inv4I"/>
									<poPredicate name="PRD4" predicate="active∩ready=(∅ ⦂ ℙ(ProcID))" source="/Scheduler/Scheduler0.bum|machineFile#Scheduler0|invariant#internal_inv5I"/>
									<poPredicate name="PRD5" predicate="ready∩waiting=(∅ ⦂ ℙ(ProcID))" source="/Scheduler/Scheduler0.bum|machineFile#Scheduler0|invariant#internal_inv6I"/>
									<poPredicate name="PRD6" predicate="active∩waiting=(∅ ⦂ ℙ(ProcID))" source="/Scheduler/Scheduler0.bum|machineFile#Scheduler0|invariant#internal_inv7I"/>
									<poPredicate name="PRD7" predicate="active=(∅ ⦂ ℙ(ProcID))⇒ready=(∅ ⦂ ℙ(ProcID))" source="/Scheduler/Scheduler0.bum|machineFile#Scheduler0|invariant#internal_inv8I"/>
									<poPredicate name="PRD8" predicate="deadlock∈BOOL" source="/Scheduler/Scheduler0.bum|machineFile#Scheduler0|invariant#internal_inv8M"/>
								  </poPredicateSet>
								  <poPredicateSet name="HYPProcessev" parentSet="/Scheduler/Scheduler0.bpo|poFile#Scheduler0|poPredicateSet#CTXHYP" poStamp="0">
									<poPredicate name="PRD0" predicate="active∈ℙ(ProcID)" source="/Scheduler/Scheduler0.bum|machineFile#Scheduler0|invariant#internal_inv1I"/>
									<poPredicate name="PRD1" predicate="ready∈ℙ(ProcID)" source="/Scheduler/Scheduler0.bum|machineFile#Scheduler0|invariant#internal_inv2I"/>
									<poPredicate name="PRD2" predicate="waiting∈ℙ(ProcID)" source="/Scheduler/Scheduler0.bum|machineFile#Scheduler0|invariant#internal_inv3I"/>
								  </poPredicateSet>
								  <poPredicateSet name="CTXHYP" poStamp="0">
									<poPredicate name="ProcIE" predicate="partition(ProcID,{P1},{P2},{P3})" source="/Scheduler/Processes.buc|contextFile#Processes|axiom#+"/>
								  </poPredicateSet></poFile>'''
		def xml = new XmlParser().parseText(importantFromBPO)
		def predSetsXML = proofFactory.cachePredSetXML(xml)
		def sequentsXML = proofFactory.cacheSequentXML(xml)

		then:
		sequentsXML != null
		sequentsXML.size() == 1
		predSetsXML.size() == 4
		proofFactory.extractPredicateSet("EVTALLHYPProcessf'",predSetsXML).size() == 11
	}

	def "extracting simple proof works"() {
		when:
		//All text is simplified from the Scheduler0 examples to include only the elements that are actually considered by the proof factory.
		def bpoFile = '''<poFile poStamp="0">
							  <poSequent name="del/inv7/INV" accurate="true" poDesc="Invariant  preservation" poStamp="0">
								<poPredicateSet name="SEQHYP" parentSet="/Scheduler/Scheduler0.bpo|poFile#Scheduler0|poPredicateSet#EVTALLHYPProcessf'"/>
								<poPredicate name="SEQHYQ" predicate="active∩(waiting ∖ {p})=(∅ ⦂ ℙ(ProcID))" source="/Scheduler/Scheduler0.bum|machineFile#Scheduler0|invariant#internal_inv7I"/>
							  </poSequent>
							  <poPredicateSet name="EVTALLHYPProcessf'" parentSet="/Scheduler/Scheduler0.bpo|poFile#Scheduler0|poPredicateSet#ALLHYP" poStamp="0">
							  	<poPredicate name="PRD0" predicate="p∈waiting" source="/Scheduler/Scheduler0.bum|machineFile#Scheduler0|event#internal_evt13|guard#internal_grd1"/>
							  </poPredicateSet>
							  <poPredicateSet name="ALLHYP" parentSet="/Scheduler/Scheduler0.bpo|poFile#Scheduler0|poPredicateSet#HYPProcessev" poStamp="0">
								<poPredicate name="PRD3" predicate="card(active)≤1" source="/Scheduler/Scheduler0.bum|machineFile#Scheduler0|invariant#internal_inv4I"/>
								<poPredicate name="PRD4" predicate="active∩ready=(∅ ⦂ ℙ(ProcID))" source="/Scheduler/Scheduler0.bum|machineFile#Scheduler0|invariant#internal_inv5I"/>
								<poPredicate name="PRD5" predicate="ready∩waiting=(∅ ⦂ ℙ(ProcID))" source="/Scheduler/Scheduler0.bum|machineFile#Scheduler0|invariant#internal_inv6I"/>
								<poPredicate name="PRD6" predicate="active∩waiting=(∅ ⦂ ℙ(ProcID))" source="/Scheduler/Scheduler0.bum|machineFile#Scheduler0|invariant#internal_inv7I"/>
								<poPredicate name="PRD7" predicate="active=(∅ ⦂ ℙ(ProcID))⇒ready=(∅ ⦂ ℙ(ProcID))" source="/Scheduler/Scheduler0.bum|machineFile#Scheduler0|invariant#internal_inv8I"/>
								<poPredicate name="PRD8" predicate="deadlock∈BOOL" source="/Scheduler/Scheduler0.bum|machineFile#Scheduler0|invariant#internal_inv8M"/>
							  </poPredicateSet>
							  <poPredicateSet name="HYPProcessev" parentSet="/Scheduler/Scheduler0.bpo|poFile#Scheduler0|poPredicateSet#CTXHYP" poStamp="0">
								<poPredicate name="PRD0" predicate="active∈ℙ(ProcID)" source="/Scheduler/Scheduler0.bum|machineFile#Scheduler0|invariant#internal_inv1I"/>
								<poPredicate name="PRD1" predicate="ready∈ℙ(ProcID)" source="/Scheduler/Scheduler0.bum|machineFile#Scheduler0|invariant#internal_inv2I"/>
								<poPredicate name="PRD2" predicate="waiting∈ℙ(ProcID)" source="/Scheduler/Scheduler0.bum|machineFile#Scheduler0|invariant#internal_inv3I"/>
							  </poPredicateSet>
							  <poPredicateSet name="CTXHYP" poStamp="0">
								<poPredicate name="ProcIE" predicate="partition(ProcID,{P1},{P2},{P3})" source="/Scheduler/Processes.buc|contextFile#Processes|axiom#+"/>
						  	  </poPredicateSet>
						</poFile>'''
		def bprFile = '''<prFile version="1"><prProof name="del/inv7/INV" confidence="0" prFresh="" prGoal="p0" prHyps="" psManual="true">
							<prRule name="r0" confidence="1000" prDisplay="Partition rewrites in hyp (partition(ProcID,{P1},{P2},{P3}))" prHyps="">
								<prAnte name="'">
									<prHypAction name="FORWARD_INF0" prHyps="p1" prInfHyps="p2,p3,p4,p5"/>
									<prRule name="r1" confidence="1000" prDisplay="rewrites set equality in goal" prGoal="p0" prHyps="">
										<prAnte name="'" prGoal="p6">
											<prRule name="r2" confidence="1000" prDisplay="dc (waiting=∅)" prHyps="">
												<prAnte name="'" prGoal="p7"/>
												<prAnte name="(" prHyps="p8"/>
												<prAnte name=")" prHyps="p9"/>
											</prRule>
										</prAnte>
										<prAnte name="(" prGoal="p10">
											<prRule name="r3" confidence="1000" prDisplay="simplification rewrites" prGoal="p10" prHyps="">
												<prAnte name="'" prGoal="p7">
													<prRule name="r4" confidence="1000" prDisplay="⊤ goal" prGoal="p7" prHyps=""/>
												</prAnte>
											</prRule>
										</prAnte>
									</prRule>
								</prAnte>
							</prRule>
							<prPred name="p7" predicate="⊤"/>
							<prPred name="p9" predicate="¬waiting=(∅ ⦂ ℙ(ProcID))"/>
							<prPred name="p2" predicate="¬P1=P3"></prPred>
							<prPred name="p0" predicate="active∩(waiting ∖ {p})=(∅ ⦂ ℙ(ProcID))"/>
							<prPred name="p3" predicate="¬P2=P3"></prPred>
							<prPred name="p4" predicate="ProcID={P1,P2,P3}"></prPred>
							<prPred name="p10" predicate="(∅ ⦂ ℙ(ProcID))⊆active∩(waiting ∖ {p})"/>
							<prPred name="p6" predicate="active∩(waiting ∖ {p})⊆(∅ ⦂ ℙ(ProcID))"/>
							<prPred name="p8" predicate="waiting=(∅ ⦂ ℙ(ProcID))"/>
							<prPred name="p5" predicate="¬P1=P2"></prPred>
							<prPred name="p1" predicate="partition(ProcID,{P1},{P2},{P3})"></prPred>
						</prProof></prFile>'''
		def bpoXML = new XmlParser().parseText(bpoFile)
		def predSetsXML = proofFactory.cachePredSetXML(bpoXML)
		def sequentsXML = proofFactory.cacheSequentXML(bpoXML)

		def bprXML = new XmlParser().parseText(bprFile)
		def proofXML = proofFactory.cacheProofXML(bprXML)

		def invariant = new EventBInvariant("inv7", "active ∩ waiting = ∅", false, new HashSet<IFormulaExtension>())
		def event = new Event("del", EventType.ORDINARY)
		def EventBMachine machine = new EventBMachine("testMachine")
		machine.addInvariants([invariant])
		machine.addEvents([event])
		def proofs = proofFactory.addMachineProofs(machine, predSetsXML, sequentsXML, proofXML)

		then:
		!proofs.isEmpty()
		def inv = proofs[0]
		inv != null
		inv instanceof INV
		inv.getName() == "del/inv7/INV"
		!inv.isDischarged()
		inv.getHypotheses().size() == 11

		// r0
		def r0 = inv.getChildrenNodes().first()
		r0.isDischarged()
		r0.getGoal() == inv.getGoal()
		r0.getHypotheses().size() == 11
		r0.getChildrenNodes().size() == 1

		// r1
		def r1 = r0.getChildrenNodes().first()
		r1.getGoal() == r0.getGoal()
		r1.getHypotheses().size() == 15
		r1.isDischarged()

		// r2 & r3
		def r1kids = []
		r1kids.addAll(r1.getChildrenNodes())
		r1kids.size() == 2
		def r2
		def r3
		r1kids.each {
			if(it.getDescription() == "dc (waiting=∅)") {
				r2 = it
			} else {
				r3 = it
			}
		}

		r2.getDescription() == "dc (waiting=∅)"
		r2.getGoal() == new EventB("active∩(waiting ∖ {p})⊆(∅ ⦂ ℙ(ProcID))")
		r2.isDischarged()

		r2.getChildrenNodes().size() == 3
		r2.getChildrenNodes().each {
			!it.isDischarged()
			if(it.getGoal() == new EventB("⊤")) {
				it.getHypotheses().size() == 15
			} else {
				it.getHypotheses().size() == 16
			}
		}

		r3.getGoal() == new EventB("(∅ ⦂ ℙ(ProcID))⊆active∩(waiting ∖ {p})")
		r3.isDischarged()
		r3.getHypotheses().size() == 15

		// r4
		def r4 = r3.getChildrenNodes().first()
		r4.getGoal() == new EventB("⊤")
		r4.isDischarged()
		r4.getChildrenNodes().isEmpty()
		r4.getHypotheses().size() == 15
	}
}
