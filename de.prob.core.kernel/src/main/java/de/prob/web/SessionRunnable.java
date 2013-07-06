package de.prob.web;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.google.gson.Gson;

public class SessionRunnable implements Runnable {

	private final Map<String, String[]> parameterMap;
	private final ISession session;
	private static final Gson GSON = new Gson();
	private final SessionQueue realizer = null;
	private final ExecutorService executor;

	public SessionRunnable(Map<String, String[]> parameterMap,
			ISession session, ExecutorService executor) {
		this.parameterMap = parameterMap;
		this.session = session;
		this.executor = executor;
	}

	@Override
	public void run() {
		Callable<Object> task = null;// session.requestJson(parameterMap);
		Future<Object> result = executor.submit(task);
		System.out.println(System.currentTimeMillis());
		while (!result.isDone()) {
			doze();
		}
		System.out.println(System.currentTimeMillis());
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
