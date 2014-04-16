package de.prob.bmotion;

import com.github.mustachejava.DefaultMustacheFactory
import com.github.mustachejava.Mustache
import com.google.common.base.Function
import com.google.gson.JsonArray
import com.google.gson.JsonObject;

import de.prob.statespace.OpInfo
import de.prob.statespace.StateSpace
import de.prob.statespace.Trace

class Observer implements IBMotionScript {

	private BMotionStudioSession bmssession
	def mf = new DefaultMustacheFactory()
	def builder = new groovy.json.JsonBuilder()
	
	def Observer(bmssession) {
		this.bmssession = bmssession
	}

	private void EvalObserver(JsonObject observer, Trace trace) {
		def m = []
		observer.objs.each { obj ->
			def fselector = obj.selector.getAsString()
			m = m + obj.bindings.collect { item ->
				def fpredicate = true
				if(item.type.getAsString() == "predicate" && !item.formula.getAsString().isEmpty())
					fpredicate = bmssession.eval(item.formula.getAsString())
				def fattr = item.attr.getAsString()
				def fvalue = item.value.getAsString()
				if(item.type.getAsString() == "expression" && !item.formula.getAsString().isEmpty())
					fvalue = bmssession.eval(item.formula.getAsString())
				if(fpredicate) {
					def data = [
						selector: fselector,
						attr: fattr,
						value: fvalue.toString()
					]
					return data
				}
			}
		}
		bmssession.toGui(builder {
			cmd 'bms.triggerObserverActions'
			actions builder.call(m.findAll { item -> item != null })
		})
	}

	def ExecuteOperation(observer, trace) {
		def jsonObserver = new com.google.gson.Gson().toJson(observer.objs)
		bmssession.toGui(["initExecuteOperationObserver("+jsonObserver+")"])
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

	def mustacheRender(s,scope) {
		def writer = new StringWriter()
		Mustache mustache = mf.compile(new StringReader(s.toString()), "bms");
		mustache.execute(writer, scope);
		writer.toString()
	}

	public void modelChanged(StateSpace statespace) {
	}
	
}