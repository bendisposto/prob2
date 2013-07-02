package de.prob.web.views

import javax.script.ScriptEngine

import com.github.mustachejava.DefaultMustacheFactory
import com.github.mustachejava.Mustache
import com.github.mustachejava.MustacheFactory
import com.google.common.collect.ImmutableMap
import com.google.inject.Inject

import de.prob.web.AbstractSession
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
			Object eval = engine.eval(line);
			return ["cmd": "groovyResult","result": eval.toString()]
		} catch (e) {
			return ["cmd":"groovyError", "message":e.getMessage()]
		}
	}

	@Override
	public String requestHtml(Map<String, String[]> parameterMap) {
		String uuid = getUuid().toString();
		MustacheFactory mf = new DefaultMustacheFactory();
		Mustache mustache = mf.compile("ui/templates/console.html");
		StringWriter sw = new StringWriter();
		try {
			mustache.execute(sw, ImmutableMap.of("uuid", uuid)).flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sw.toString();
	}
}