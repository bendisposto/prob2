package de.prob.web;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;

public class SessionRunnable implements Runnable {

	private final Map<String, String[]> parameterMap;
	private final ISession session;
	private static final Gson GSON = new Gson();
	private final SessionQueue realizer;

	public SessionRunnable(Map<String, String[]> parameterMap, ISession session) {
		this.parameterMap = parameterMap;
		this.session = session;
		realizer = session.getQueue();
	}

	@Override
	public void run() {
		ListenableFuture<Object> result = session.requestJson(parameterMap);
		while (!result.isDone()) {
			doze();
		}
		String json = "{}";
		try {
			json = GSON.toJson(result.get());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		realizer.submit(json);
	}

	private void doze() {
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
