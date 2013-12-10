package de.prob.bmotion;

import groovy.json.JsonBuilder;

import java.util.Map;

import de.prob.bmotion.IBMotionScript
import de.prob.statespace.OpInfo
import de.prob.statespace.Trace

class Observer implements IBMotionScript {

	def bmssession

	def Observer(bmssession) {
		this.bmssession = bmssession
	}

	def evalObserver(observer, formulas, trace) {
		defaultObserver(observer, formulas)
	}

	def executeOperation(observer, formulas, trace) {
		defaultObserver(observer, formulas)
	}

	def defaultObserver(observer, formulas) {
		def observerCall = observer.get("cmd")
		def jsonObserver = new com.google.gson.Gson().toJson(observer)
		def jsonFormulas = new com.google.gson.Gson().toJson(formulas)
		bmssession.toVisualization(observerCall+"("+jsonObserver+","+jsonFormulas+")")
	}

	def cspEventObserver(observer, formulas, trace) {
		def observerCall = observer.get("cmd")
		def jsonObserver = new com.google.gson.Gson().toJson(observer)
		def jsonFormulas = new com.google.gson.Gson().toJson(formulas)
		// Add trace
		def opList = trace.getCurrent().getOpList();
		def m = opList.collect {
			return [name : it.getName(), parameter : it.getParams(), full: getOpString(it)];
		}
		def jsonTrace = new com.google.gson.Gson().toJson(m)
		bmssession.toVisualization(observerCall+"("+jsonObserver+","+jsonFormulas+","+jsonTrace+")")
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

	@Override
	public void traceChange(Trace trace, Map<String, Object> formulas) {

		def json = bmssession.getJson()
		if(json != null) {
			Object[] ol = json.get("observer");
			for(Object o : ol) {
				def methodCall = o.get("cmd")
				this."$methodCall"(o, formulas, trace)
			}
		}

	}

}