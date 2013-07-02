package de.prob.web.views;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import de.prob.web.AbstractSession;

public class ReflectorDebugServlet extends AbstractSession {

	private static class Result {

		private final String cmd;

		public Result(String string) {
			this.cmd = string;
		}

	}

	@Override
	public ListenableFuture<Object> requestJson(
			Map<String, String[]> parameterMap) {

		String cmd = parameterMap.get("cmd")[0];

		if ("bar".equals(cmd)) {
			Result result = new Result("Ok");
			return Futures.immediateFuture((Object) result);

		}
		return Futures.immediateFuture((Object) new Result("fail"));
	}

	@Override
	public String requestHtml(Map<String, String[]> parameterMap) {
		String uuid = getUuid().toString();
		MustacheFactory mf = new DefaultMustacheFactory();
		Mustache mustache = mf.compile("ui/templates/debug.html");
		StringWriter sw = new StringWriter();
		try {
			mustache.execute(sw, ImmutableMap.of("uuid", uuid)).flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sw.toString();
	}

}
