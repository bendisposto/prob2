package de.prob.model.representation

import org.apache.commons.lang.StringEscapeUtils

import de.prob.animator.domainobjects.AbstractEvalElement
import de.prob.animator.domainobjects.ClassicalB
import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.Context
import de.prob.model.eventb.EventParameter
import de.prob.model.eventb.Variant
import de.prob.model.eventb.Witness
import de.prob.statespace.StateSpace
import de.prob.unicode.UnicodeTranslator

class ModelRep {

	def label
	def formulaId
	def ofInterest = false
	def formula
	def children = []

	def static List<ModelRep> translate(AbstractModel m) {
		def reps = []
		m.getChildrenOfType(Machine.class).each {
			reps << translate(it, m.getStatespace())
		}
		m.getChildrenOfType(Context.class).each {
			reps << translate(it, m.getStatespace())
		}
		reps
	}

	def static ModelRep translate(AbstractElement e, StateSpace s) {
		def mRep = new ModelRep()
		mRep.label = e.getMetaClass().respondsTo(e, "getName") ? e.getName() : "None"
		if(e instanceof AbstractFormulaElement) {
			extractFormula(e, s, mRep)
		}

		def kids = e.getChildren()
		kids.each {
			if(!(it.key == Machine.class || it.key == Context.class)) {
				def child = translate(it.key, it.value, s)
				mRep.children << child
				if(child.ofInterest) {
					mRep.ofInterest = true
				}
			}
		}
		mRep
	}

	def static extractFormula(AbstractFormulaElement e, StateSpace s, ModelRep mRep) {
		def AbstractEvalElement formula = e.getFormula()
		mRep.formulaId = formula.getFormulaId().uuid
		mRep.ofInterest = e.isSubscribed(s)
		if(formula instanceof EventB || formula instanceof ClassicalB) {
			mRep.formula = StringEscapeUtils.escapeHtml(UnicodeTranslator.toUnicode(formula.getCode()))
		}
	}

	def static ModelRep translate(Class<? extends AbstractElement> c, ModelElementList<? extends AbstractElement> kids, StateSpace s) {
		def ModelRep r = new ModelRep()
		r.label = getLabelName(c)
		r.ofInterest = false
		kids.each {
			def child = translate(it, s)
			r.children << child
			if (child.ofInterest) {
				r.ofInterest = true
			}
		}
		r
	}

	def static String getLabelName(Class<? extends AbstractElement> c) {
		def result = ""
		switch(c) {
			case BEvent:
				result = "events"
				break
			case BSet:
				result = "sets"
				break
			case EventParameter:
				result = "parameters"
				break
			case Witness:
				result = "witnesses"
				break
			case Variant:
				result = "variant"
				break
			default:
				result = c.getSimpleName().toLowerCase() + "s"
		}
		return result
	}
}
