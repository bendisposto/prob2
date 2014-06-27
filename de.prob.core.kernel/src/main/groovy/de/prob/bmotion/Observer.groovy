package de.prob.bmotion;

import com.google.common.base.Function
import com.google.gson.JsonArray
import com.google.gson.JsonObject;

import de.prob.statespace.OpInfo
import de.prob.statespace.StateSpace
import de.prob.statespace.Trace

class Observer implements IBMotionScript {

	private BMotionStudioSession bmssession
	def builder = new groovy.json.JsonBuilder()

	def Observer(bmssession) {
		this.bmssession = bmssession
	}

	private void EvalObserver(JsonObject observer, Trace trace) {

		def factions = []
		observer.objs.each { obj ->

			def fselector = obj.selector.getAsString()

			obj.bindings.each { item ->

				def fpredicate = true
				def fvalue = ""

				if(item.type.getAsString() == "predicate") {
					fpredicate = bmssession.eval(item.formula.getAsString())
				} else if(item.type.getAsString() == "expression" && item.formula != null) {
					fvalue = bmssession.eval(item.formula.getAsString())
				}

				if(fpredicate) {

					item.actions.each { act ->
						def fattr = act.attr == null ? "" : act.attr.getAsString()
						if(item.type.getAsString() == "predicate")
							fvalue = act.value.getAsString()
						def data = [
							selector: fselector,
							attr: fattr,
							value: fvalue.toString()
						]
						factions = factions + data
					}
				}
			}
		}
		bmssession.toGui(builder {
			cmd 'bms.triggerObserverActions'
			actions builder.call(factions.findAll { item -> item != null })
		})
	}

	def ExecuteOperation(observer, trace) {
		def jsonObserver = new com.google.gson.Gson().toJson(observer.objs)
		bmssession.toGui([
			"initExecuteOperationObserver("+jsonObserver+")"
		])
	}

	private void CspEventObserver(JsonObject observer, Trace trace) {
		trace.ensureOpInfosEvaluated()
		def opList = trace.getCurrent().getOpList()
		def m = []
		opList.each { op ->
			def fullOp = getOpString(op)
			observer.objs.each { obj ->
				def events = bmssession.eval(obj.exp.getAsString())
				if(events != null) {
					events = events.replace("{","").replace("}", "")
					def event_names = events.split(",")
					if(event_names.contains(fullOp)) {
						def pmap = [:]
						op.getParams().eachWithIndex() { v, i -> pmap.put("a"+(i+1),v) };
						pmap.put("Event", op.getName())
						m = m + obj.actions.collect { item ->
							def fselector = mustacheRender(item.selector.getAsString(),pmap)
							def fvalue = mustacheRender(item.value.getAsString(),pmap)
							def fattr = mustacheRender(item.attr.getAsString(),pmap)
							def data = [
								selector: fselector,
								attr: fattr,
								value: fvalue
							]
							return data
						}
					}
				}
			}
		}
		bmssession.toGui(builder {
			cmd 'bms.triggerObserverActions'
			actions builder.call(m.findAll { item -> item != null })
		})
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

	public void traceChanged(Trace trace) {
		def json = bmssession.getJson()
		if(json != null) {
			JsonArray asJsonArray = json.getAsJsonObject().get("observers").getAsJsonArray();
			asJsonArray.each {
				def methodCall = it.getAsJsonObject().get("type").getAsString()
				if(this.metaClass.respondsTo(this, methodCall, JsonObject, Trace)) {
					this."$methodCall"(it, trace)
				}
			}
		}
	}

	private class EvalExpression implements Function<String, Object> {
		@Override
		public Object apply(final String input) {
			return bmssession.eval(input)
		}
	}



	public void modelChanged(StateSpace statespace) {
	}
}