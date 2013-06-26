package de.prob.web.views;

import java.util.Map;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import de.prob.web.AbstractSession;

public class ReflectorDebugServlet extends AbstractSession {

	@Override
	public ListenableFuture<Object> requestJson(
			Map<String, String[]> parameterMap) {
		return Futures.immediateFuture((Object) "Done");
	}

	@Override
	public String requestHtml(Map<String, String[]> parameterMap) {
		return "<h1>Do'h</h1>";
	}

}
