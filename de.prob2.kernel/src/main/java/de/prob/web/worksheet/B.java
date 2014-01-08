package de.prob.web.worksheet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;

import de.prob.animator.domainobjects.EventB;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.animator.domainobjects.IEvalResult;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.StateId;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;
import de.prob.web.WebUtils;

public class B extends AbstractBox {

	private String content = "";
	private String trace;

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> render(BindingsSnapshot snapshot) {
		List<Object> res = new ArrayList<Object>();
		if (this.trace == null) {
			Map<String, String> renderMap = makeHtml(id,
					"Kein Trace ausgew&auml;lt");
			res.add(renderMap);
		} else {
			ScriptEngine groovy = owner.getGroovy();
			Map<String, String> renderMap;
			if (snapshot != null)
				snapshot.restoreBindings(groovy);
			if (this.content != null) {
				Trace trace;
				if (this.trace.equals("rodin_current"))

					trace = ((AnimationSelector) groovy.getBindings(
							ScriptContext.GLOBAL_SCOPE).get("animations"))
							.getCurrentTrace();
				else
					trace = (Trace) groovy.get(this.trace);
				StateSpace space = trace.getStateSpace();
				StateId curStateId = trace.getCurrentState();
				//
				if (!space.canBeEvaluated(curStateId)) {
					return pack(makeHtml(id,
							"*Current State can not be evaluated*"));
				}
				ArrayList<IEvalElement> evalElementList = new ArrayList<IEvalElement>();

				evalElementList.add(new EventB(content));
				List<IEvalResult> evalResultList = space.eval(curStateId,
						evalElementList);
				if (evalResultList.size() > 1) {
					return pack(makeHtml(id,
							"*ProB returned multiple Results.*"));
				}
				if (evalResultList.size() == 0) {
					return pack(makeHtml(id, "*ProB returned no Results.*"));
				}
				Object evalResult = (Object) evalResultList.get(0);

				res.add(makeHtml(id, WebUtils.render(
						"ui/worksheet/groovy_box.html", WebUtils.wrap("id", id,
								"result", evalResult, "output", ""))));
			} else {
				return pack(makeHtml(id, ""));
			}
		}
		ArrayList<String> traces = getTraceList();
		String traceList = WebUtils.toJson(getTraceList());
		Map<String, String> traceDropdownMap = WebUtils.wrap("cmd",
				"Worksheet.setDropdown", "id", id, "dropdownName",
				"trace-selection", "items", traceList);
		res.add(traceDropdownMap);
		return res;
	}

	private ArrayList<String> getTraceList() {
		ScriptEngine groovy = owner.getGroovy();
		Set<Entry<String, Object>> engineBindings = groovy.getBindings(
				ScriptContext.ENGINE_SCOPE).entrySet();
		ArrayList<String> traceKeys = new ArrayList<String>();
		Iterator<Entry<String, Object>> it = engineBindings.iterator();
		while (it.hasNext()) {
			Entry<String, Object> next = it.next();
			if (next.getValue() instanceof de.prob.statespace.Trace)
				traceKeys.add(next.getKey());
		}
		Collections.sort(traceKeys);
		traceKeys.add(0, "rodin_current");
		if (this.trace != null) {
			traceKeys.remove(this.trace);
			traceKeys.add(0, this.trace);
		}
		return traceKeys;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<? extends Object> additionalMessages() {
		String traceList = WebUtils.toJson(getTraceList());
		Map<String, String> traceDropdownMap = WebUtils.wrap("cmd",
				"Worksheet.setDropdown", "id", id, "dropdownName",
				"trace-selection", "items", traceList);
		return pack(traceDropdownMap);
	}

	@Override
	public void setContent(final Map<String, String[]> data) {
		this.content = data.get("text")[0];
		if (this.content.equals(""))
			this.content = null;
		this.trace = data.get("additionalData")[0];
		if (this.trace.equals(""))
			this.trace = null;
	}

	@Override
	protected String getContentAsJson() {
		return content;
	}

	@Override
	public EChangeEffect changeEffect() {
		return EChangeEffect.EVERYTHING_BELOW;
	}
}
