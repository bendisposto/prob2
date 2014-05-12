package de.prob.model.representation

import org.apache.commons.lang.StringEscapeUtils

import de.prob.animator.domainobjects.AbstractEvalElement
import de.prob.animator.domainobjects.ClassicalB
import de.prob.animator.domainobjects.EventB
import de.prob.model.classicalb.Property
import de.prob.model.eventb.Context
import de.prob.model.eventb.Variant
import de.prob.model.eventb.proof.ProofObligation
import de.prob.statespace.StateSpace
import de.prob.unicode.UnicodeTranslator

class ModelRep {

	def label
	def formulaId
	def ofInterest = false
	def hasSubformula = false
	def nrOfInterest = 0
	def formula
	def parentName
	def children = []
	def path = []

	def static List<ModelRep> translate(AbstractModel m) {
		def reps = []
		m.getChildrenOfType(Machine.class).each {
			reps << translate(it, [], m.getStatespace())
		}
		m.getChildrenOfType(Context.class).each {
			reps << translate(it, [], m.getStatespace())
		}
		reps
	}

	def static ModelRep translate(AbstractElement e, path, StateSpace s) {
		def mRep = new ModelRep()
		mRep.label = e.getMetaClass().respondsTo(e, "getName") ? e.getName() : false
		if(e instanceof AbstractFormulaElement) {
			extractFormula(e, s, path, mRep)
		} else {
			mRep.path = new ArrayList<String>(path)
			mRep.path << mRep.label
		}

		def kids = e.getChildren()
		kids.each {
			if(!(it.key == Machine.class || it.key == Context.class || it.key == ProofObligation.class )) {
				if(it.key == BEvent.class) {
					def child = translateEvents(it.value, mRep.path, s)
					if(child.ofInterest) {
						mRep.ofInterest = true
					}
					if(child.hasSubformula) {
						mRep.hasSubformula = true
						mRep.children << child
					}
					mRep.nrOfInterest += child.nrOfInterest
				} else {
					def child = translate(it.key, it.value, mRep.path, s)
					if(child.ofInterest) {
						mRep.ofInterest = true
					}
					if(child.hasSubformula) {
						mRep.hasSubformula = true
						mRep.children << child
					}
					mRep.nrOfInterest += child.nrOfInterest
				}
			}
		}
		mRep
	}

	def static extractFormula(AbstractFormulaElement e, StateSpace s, path, ModelRep mRep) {
		def AbstractEvalElement formula = e.getFormula()
		mRep.formulaId = formula.getFormulaId().uuid
		mRep.ofInterest = e.isSubscribed(s)
		mRep.nrOfInterest = mRep.ofInterest ? 1 : 0
		mRep.hasSubformula = true
		mRep.path = new ArrayList<String>(path)
		mRep.path << "_" + mRep.formulaId
		if(formula instanceof EventB || formula instanceof ClassicalB) {
			mRep.formula = StringEscapeUtils.escapeHtml(UnicodeTranslator.toUnicode(formula.getCode()))
			if(mRep.formula == mRep.label) {
				mRep.label = false
			}
		}
	}

	def static ModelRep translate(Class<? extends AbstractElement> c, ModelElementList<? extends AbstractElement> kids, path, StateSpace s) {
		def ModelRep r = new ModelRep()
		r.label = getLabelName(c)
		r.path = new ArrayList<String>(path)
		r.path << r.label
		r.ofInterest = false
		kids.each {
			def child = translate(it, r.path, s)
			if (child.ofInterest) {
				r.ofInterest = true
			}
			if (child.hasSubformula) {
				r.hasSubformula = true
				r.children << child
			}
			r.nrOfInterest += child.nrOfInterest
		}
		r
	}

	def static ModelRep translateEvents(ModelElementList<BEvent> events, path, StateSpace s) {
		def ModelRep r = new ModelRep()
		r.path = new ArrayList<String>(path)
		r.path << "guards"
		r.label = "guards"

		events.each { BEvent event ->
			def guards = event.getChildrenOfType(Guard.class)
			def p = new ArrayList<String>(path)
			p << "events"
			p << event.getName()
			p << "guards"
			guards.each {
				def g = translate(it, p, s)
				g.parentName = event.getName()
				if(g.ofInterest) {
					r.ofInterest = true
				}
				if(g.hasSubformula) {
					r.hasSubformula = true
					r.children << g
				}
				r.nrOfInterest += g.nrOfInterest
			}
		}
		r
	}

	def static String getLabelName(Class<? extends AbstractElement> c) {
		def result = ""
		switch(c) {
			case BSet:
				result = "sets"
				break
			case Variant:
				result = "variant"
				break
			case Property:
				result = "properties"
				break
			default:
				result = c.getSimpleName().toLowerCase() + "s"
		}
		return result
	}
}
