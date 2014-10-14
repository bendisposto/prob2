package de.prob.bmotion;

import java.util.List;

import com.github.mustachejava.DefaultMustacheFactory
import com.github.mustachejava.Mustache
import com.google.gson.JsonElement

import de.prob.animator.domainobjects.EvalResult;
import de.prob.statespace.OpInfo
import de.prob.ui.api.ITool
import de.prob.bmotion.BMotionObserver
import de.prob.bmotion.Transform

class CSPAnimationObserver implements BMotionObserver {

	def JsonElement json
	def mf = new DefaultMustacheFactory()
	def builder = new groovy.json.JsonBuilder()

	def CSPAnimationObserver(json) {
		this.json = json
	}

	def getOpString(OpInfo op) {
		String opName = op.getName();
		String AsImplodedString = "";
		List<String> opParameter = op.getParams();
		if (opParameter.size() > 0) {
			String[] inputArray = opParameter.toArray(new String[opParameter
						.size()]);
			StringBuffer sb = new StringBuffer();
			sb.append(inputArray[0]);
			for (int i = 1; i < inputArray.length; i++) {
				sb.append(".");
				sb.append(inputArray[i]);
			}
			AsImplodedString = "." + sb.toString();
		}
		String opNameWithParameter = opName + AsImplodedString;
		return opNameWithParameter;
	}

	def mustacheRender(s,scope) {
		def writer = new StringWriter()
		Mustache mustache = mf.compile(new StringReader(s.toString()), "bms");
		mustache.execute(writer, scope);
		writer.toString()
	}

	@Override
	public List<Transform> update(BMotion bms) {
		def transformers = []
		def tool = bms.getTool()
		if(json != null) {
			json.observers.each { o ->
				if(o?.type?.getAsString() == "CspEventObserver") {
					def factions = []
					def trace = tool.getTrace()
					trace.ensureOpInfosEvaluated()
					def opList = trace.getCurrent().getOpList()
					opList.each { op ->
						def fullOp = getOpString(op)
						o.objs.each { obj ->
							def events = tool.evaluate(tool.getCurrentState(), obj.exp.getAsString())
							if(events instanceof EvalResult) {
								events = events.value.replace("{","").replace("}", "")
								def event_names = events.split(",")
								if(event_names.contains(fullOp)) {
									def pmap = [:]
									op.getParams().eachWithIndex() { v, i -> pmap.put("a"+(i+1),v) };
									pmap.put("Event", op.getName())
									factions = factions + obj.actions.collect { item ->
										def fselector = mustacheRender(item.selector.getAsString(),pmap)
										def fvalue = mustacheRender(item.value.getAsString(),pmap)
										def fattr = mustacheRender(item.attr.getAsString(),pmap)
										if(fattr == "style") {
											def t = new Transform(fselector)
											def sattrs = fvalue.split(";")
											sattrs.collect { it -> 
												def kv = it.split(":")
												t.style(kv[0],kv[1])
											}
											transformers << t
										} else {
											transformers << new Transform(fselector).set(fattr,fvalue)
										}
									}
								}
							}
						}
					}
				}
			}
		}
		transformers
	}
}