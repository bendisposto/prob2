package de.prob.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import javax.servlet.AsyncContext;

public class SessionQueue implements Runnable {

	private final Queue<String> q = new ArrayBlockingQueue<String>(100);
	private volatile AsyncContext context;
	int dozer;

	public void submit(String json) {
		q.offer(json);
	}

	@Override
	public void run() {
		while (q.isEmpty() || context == null) {
			dozer++;
			doze();
		}
		String json = q.poll();
		PrintWriter writer;
		try {
			writer = context.getResponse().getWriter();
			writer.write(json);
			writer.flush();
			writer.close();
			context.complete();
			context = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setContext(AsyncContext context) {
		this.context = context;
	}

	private void doze() {
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
