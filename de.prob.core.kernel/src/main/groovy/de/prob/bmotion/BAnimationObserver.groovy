package de.prob.bmotion;

import com.github.mustachejava.DefaultMustacheFactory
import com.github.mustachejava.Mustache
import com.google.common.base.Function
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject

import de.prob.statespace.OpInfo
import de.prob.ui.api.ITool

class BAnimationObserver implements IBMotionGroovyObserver {

	def BMotionStudioSession session
	def JsonElement json
	def mf = new DefaultMustacheFactory()
	def builder = new groovy.json.JsonBuilder()

	def BAnimationObserver(session,json) {
		this.session = session
		this.json = json
	}

	def expression(tool, item, fselector) {
		def formula = item?.formula?.getAsString()
		def factions = []
		if (tool.getErrors(tool.getCurrentState(), formula).isEmpty()) {
			def fvalue = ""
			def fpredicate = true
			def type = item?.type?.getAsString()
			if(type == "predicate") {
				fpredicate = tool.evaluate(tool.getCurrentState(), formula) == "TRUE"
			} else if(type == "expression") {
				fvalue = tool.evaluate(tool.getCurrentState(), formula)
			}
			if(fpredicate) {
				factions = factions + item.actions.collect { act ->
					def fattr = act?.attr?.getAsString()
					if(type == "predicate") {
						fvalue = act?.value?.getAsString()
					}
					return [ selector: fselector, attr: fattr, value: fvalue.toString() ]
				}
			}
		} else {
			// Somehow inform the user??
		}
		return factions
	}
	
	public void update(ITool tool) {
		json.observers.each { o ->
			if(o?.type?.getAsString() == "EvalObserver") {
				def factions = []
				o.objs.each { obj ->
					def fselector = obj?.selector?.getAsString()
					obj.bindings.each { item ->
						switch (item?.type?.getAsString()) {
							case ['expression', 'predicate']:
								factions = factions + expression(tool,item,fselector)
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
				session.toGui('bms.triggerObserverActions', [actions : factions.findAll { i -> i != null }])
			}
		}
	}
	
}