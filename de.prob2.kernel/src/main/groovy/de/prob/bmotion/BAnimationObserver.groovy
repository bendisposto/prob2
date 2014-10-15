package de.prob.bmotion

import com.github.mustachejava.DefaultMustacheFactory
import com.google.gson.JsonElement
import de.prob.animator.domainobjects.EvalResult

class BAnimationObserver implements IBMotionObserver {

	def JsonElement json
	def mf = new DefaultMustacheFactory()
	def builder = new groovy.json.JsonBuilder()

	def BAnimationObserver(json) {
		this.json = json
	}

	def expression(tool, item, fselector) {
		def formula = item?.formula?.getAsString()
		def factions = []
		def fvalue = ""
		def fpredicate = true
		def type = item?.type?.getAsString()
		def attrs = [:]
		if(type == "predicate") {
			def fres = tool.evaluate(tool.getCurrentState(), formula)
			fpredicate = fres instanceof EvalResult ? (fres.value == "TRUE") : false
		} else if(type == "expression") {
			fvalue = tool.evaluate(tool.getCurrentState(), formula)
		}
		if(fpredicate) {
			item.actions.each { act ->
				def fattr = act?.attr?.getAsString()
				if(type == "predicate") {
					fvalue = act?.value?.getAsString()
				}
				attrs.put(fattr,fvalue.toString())
			}
		}
		return new TransformerObject(fselector,attrs)
	}

	public List<TransformerObject> update(BMotion bms) {
		def transformers = []
		if(json != null) {
			json.observers.each { o ->
				if(o?.type?.getAsString() == "BAnimation") {
					o.objs.each { obj ->
						def fselector = obj?.selector?.getAsString()
						obj.bindings.each { item ->
							switch (item?.type?.getAsString()) {
								case ['expression', 'predicate']:
									transformers << expression(bms.getTool(),item,fselector)
									break
								case 'variable':
									break
								case 'operation':
									break
								default:
									println "Type unknown!"
							}
						}
					}
				}
			}
		}
		transformers
	}

    @Override
    def apply(BMotion bms) {
        return null
    }
}