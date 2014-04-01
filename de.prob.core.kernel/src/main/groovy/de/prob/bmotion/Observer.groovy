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
	def scope = [:]
	def builder = new groovy.json.JsonBuilder()
	
	def Observer(bmssession) {
		this.bmssession = bmssession
		scope.put("eval", new EvalExpression());
	}

	private void EvalObserver(JsonObject observer, Trace trace) {
		def m = []
		observer.objs.each { obj ->
			def fselector = translateValue(mustacheRender(obj.selector.getAsString(),scope))
			m = m + obj.actions.collect { item ->
				def fattr = translateValue(mustacheRender(item.attr.getAsString(),scope))
				def fvalue = translateValue(mustacheRender(item.value.getAsString(),scope))
				def fpredicate = true
				if(!item.predicate.getAsString().isEmpty())
					fpredicate = bmssession.eval(item.predicate.getAsString())	
				if(fpredicate) {
					def data = [
						selector: fselector,
						attr: fattr,
						value: fvalue
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

	def listenOperation(observer, trace) {
		def statespace = trace.getStateSpace()
		def objects = observer.get("objects")
		def m = []
		for(o in objects) {
			def op = getOp(trace, o.get("operation"), o.get("predicate") == null ? "TRUE=TRUE" : o.get("predicate"))
			def triggers = o.get("trigger")
			def t = triggers.collect {
				def parameter = op == null ? it.get("disabled") : it.get("enabled")
				def parsedParameter = parameter.collect { return translateValue(mustacheRender(it,scope)) }
				return "\$('"+it.get("selector")+"')."+it.get("call")+"("+parsedParameter.join(",")+")"
			}
			m = m + t
		}
		bmssession.toVisualization(m)
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

	def getOp(trace,name,pred) {
		try {
			def ops = trace.getStateSpace().opFromPredicate(trace.getCurrentState(),name,pred,1)
			ops[0]
		} catch (Exception e) {
			null
		}
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

	def translateValue(input) {
		switch (input) {
			case 'true':
				return true
			case 'false':
				return false
			default:
				return input
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