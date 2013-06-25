package de.prob.web.views;

import javax.script.ScriptEngine;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;

import de.prob.web.ISession;
import de.prob.worksheet.ScriptEngineProvider;

public class GroovySession implements ISession {

	private final ScriptEngine engine;

	@Inject
	public GroovySession(ScriptEngineProvider sep) {
		this.engine = sep.get();
	}

	@Override
	public void doGet(String session, HttpServletRequest request,
			HttpServletResponse response) {
		System.out.println(engine);

	}

}
