package de.prob.bmotion;

import com.github.mustachejava.DefaultMustacheFactory
import com.github.mustachejava.Mustache
import com.google.common.base.Function

import de.prob.statespace.OpInfo
import de.prob.statespace.StateSpace
import de.prob.statespace.Trace

class Observer implements IBMotionScript {

	def bmssession
	def formulas
	def mf = new DefaultMustacheFactory()
	def scope = [:]

	def Observer(bmssession) {
		this.bmssession = bmssession
		scope.put("eval", new EvalExpression());
	}

	def evalObserver(observer, formulas, trace) {
		def objects = observer.get("objects")
		def m = []
		for(o in objects) {
			def predicate = o.get("predicate") == null ? true : translateValue(mustacheRender(o.get("predicate"),scope))
			if(predicate) {
				def triggers = o.get("trigger")
				def t = triggers.collect {
					def parameter = it.get("parameters")
					def parsedParameter = parameter.collect { return translateValue(mustacheRender(it,scope)) }
					return "\$('"+it.get("selector")+"')."+it.get("call")+"("+parsedParameter.join(",")+")"
				}
				m = m + t
			}
		}
		bmssession.toVisualization(m)
	}

	def listenOperation(observer, formulas, trace) {
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

	def executeOperation(observer, formulas, trace) {
		def jsonObserver = new com.google.gson.Gson().toJson(observer)
		def jsonFormulas = new com.google.gson.Gson().toJson(formulas)
		bmssession.toVisualization(["executeOperation("+jsonObserver+","+jsonFormulas+")"])
	}

	def getCharForNumber(i) {
		return i > 0 && i < 27 ? String.valueOf((char)(i + 64)) : null;
	}

	def cspEventObserver(observer, formulas, trace) {

		def observerCall = observer.get("cmd")
		def jsonObserver = new com.google.gson.Gson().toJson(observer)
		def jsonFormulas = new com.google.gson.Gson().toJson(formulas)

		def objects = observer.get("objects")
		trace.ensureOpInfosEvaluated()
		def opList = trace.getCurrent().getOpList();

		def m = []

		for(op in opList) {
			def fullOp = getOpString(op)
			for(obj in objects) {
				def events = mustacheRender(obj.get("events"),scope)
				if(events != null) {
					events = events.replace("{","").replace("}", "")
					events = events.split(",")
					if(events.contains(fullOp)) {
						def pmap = [:]
						op.getParams().eachWithIndex() { v, i -> pmap.put(getCharForNumber(i+1),v) };
						def triggers = obj.get("trigger")
						def t = triggers.collect {
							def parameter = it.get("parameters")
							def parsedParameter = parameter.collect {
								return translateValue(mustacheRender(it,pmap+scope))
							}
							return "\$('"+mustacheRender(it.get("selector"),pmap)+"')."+mustacheRender(it.get("call"),pmap)+"("+parsedParameter.join(",")+")"
						}
						m = m + t
					}
				}
			}
		}

		m = ["resetCSP()"]+ m
		bmssession.toVisualization(m);
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

	def void traceChanged(Trace trace, Map<String, Object> formulas) {
		this.formulas = formulas
		def json = bmssession.getJsonData()
		if(json != null) {
			json.each {
				def ol = it.get("observer");
				if(ol != null) {
					ol.each {
						def methodCall = it.get("cmd")
						if(this.metaClass.respondsTo(this, methodCall, Object, Object, Object)) {
							this."$methodCall"(it, formulas, trace)
						}
					}
				}
			}
		}
	}

	private class EvalExpression implements Function<String, Object> {
		@Override
		public Object apply(final String input) {
			return formulas.get(input);
		}
	}

	def translateValue(input) {
		switch (input) {
			case 'true':
				return true;
			case 'false':
				return false;
			default:
				return "\""+input+"\""
		}
	}

	def mustacheRender(s,scope) {
		def writer = new StringWriter()
		Mustache mustache = mf.compile(new StringReader(s.toString()), "bms");
		mustache.execute(writer, scope);
		writer.toString()
	}

	def void modelChanged(StateSpace statespace) {
	}
}