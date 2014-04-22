package de.prob.model.representation

import de.prob.animator.domainobjects.AbstractEvalElement
import de.prob.animator.domainobjects.ClassicalB
import de.prob.animator.domainobjects.EventB
import de.prob.statespace.StateSpace
import de.prob.unicode.UnicodeTranslator

class ModelRep {

	def label
	def formulaId
	def ofInterest = false
	def formula
	def children = []

	def static ModelRep translate(AbstractElement e, StateSpace s) {
		def mRep = new ModelRep()
		mRep.label = e.getMetaClass().respondsTo(e, "getName") ? e.getName() : "None"
		if(e instanceof AbstractFormulaElement) {
			extractFormula(e, s, mRep)
		}

		def kids = e.getChildren()
		kids.each {
			def child = translate(it.key, it.value, s)
			mRep.children << child
			if(child.ofInterest) {
				mRep.ofInterest = true
			}
		}
		mRep
	}

	def static extractFormula(AbstractFormulaElement e, StateSpace s, ModelRep mRep) {
		def AbstractEvalElement formula = e.getFormula()
		mRep.formulaId = formula.getFormulaId().uuid
		mRep.ofInterest = e.isSubscribed(s)
		if(formula instanceof EventB || formula instanceof ClassicalB) {
			mRep.formula = UnicodeTranslator.toUnicode(formula.getCode())
		}
	}

	def static ModelRep translate(Class<? extends AbstractElement> c, ModelElementList<? extends AbstractElement> kids, StateSpace s) {
		def ModelRep r = new ModelRep()
		r.label = c.getSimpleName()
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
}
