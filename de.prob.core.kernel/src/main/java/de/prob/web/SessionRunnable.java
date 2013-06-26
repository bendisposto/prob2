package de.prob.web;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.servlet.AsyncContext;

import com.google.gson.Gson;

public class SessionRunnable implements Runnable {

	private final AsyncContext context;
	private final Map<String, String[]> parameterMap;
	private final ISession session;
	private static final Gson GSON = new Gson();

	public SessionRunnable(AsyncContext context,
			Map<String, String[]> parameterMap, ISession session) {
		this.context = context;
		this.parameterMap = parameterMap;
		this.session = session;
	}

	@Override
	public void run() {
		Future<Object> result = session.requestJson(parameterMap);
		while (!result.isDone()) {
			doze();
		}
		try {
			String json = GSON.toJson(result.get());
			context.getResponse().getWriter().write(json);
			context.complete();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void doze() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
