package de.prob.web.views

import javax.script.ScriptEngine

import com.google.inject.Inject

import de.prob.web.AbstractSession
import de.prob.web.WebUtils
import de.prob.worksheet.ScriptEngineProvider


class GroovyConsole extends AbstractSession{

	def ScriptEngine engine;

	@Inject
	def GroovyConsole(ScriptEngineProvider sep) {
		engine = sep.get();
	}

	def exec(Map<String,String[]>params) {

		def line =get(params,"line");
		try {
			def console = new StringBuffer()
			engine.put("__console", console);
			def eval = engine.eval(line);
			return ["cmd": "Console.groovyResult","result": eval.toString(), "output": console.toString()]
		} catch (e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			def trace = e.getStackTrace().collect {it.toString() };
			return ["cmd":"Console.groovyError", "message":e.getMessage(), "trace": sw.toString()]
		}
	}

	@Override
	public String requestHtml(Map<String, String[]> parameterMap) {
		String uuid = getUuid().toString();
		def template = "ui/console/index.html"
		def scope = ["uuid": uuid]
		return WebUtils.render(template, scope);
	}
}