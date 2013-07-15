package de.prob.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.AsyncContext;

import com.google.gson.Gson;

public class SessionQueue {

	private int id = 0;

	@SuppressWarnings("unused")
	// Data serialization object. Usage of field is on client side
	private static class DataObject {
		public final int id;
		public final Object[] content;

		public DataObject(Object[] content, int id) {
			this.content = content;
			this.id = id;
		}
	}

	private final List<Message> q = new ArrayList<Message>();
	private final Map<String, Request> reqs = new HashMap<String, Request>();

	private static final Gson GSON = new Gson();

	public void submit(Object json) {
		q.add(new Message(id++, json));
		deliverAll();
	}

	private void deliverAll() {
		System.out.println(reqs.size());
		for (Request r : reqs.values()) {
			ArrayList<Object> result = new ArrayList<Object>();
			System.out.println("Processiong request " + r.index);
			for (int i = r.index + 1; i < q.size(); i++) {
				Message m = q.get(i);
				result.add(m.content);
			}

			Object[] array = result.toArray(new Object[result.size()]);
			DataObject dataObject = new DataObject(array, q.size() - 1);

			deliver(r.context, dataObject);
		}
	}

	private void deliver(AsyncContext context, Object array) {
		PrintWriter writer;
		try {
			writer = context.getResponse().getWriter();
			writer.write(GSON.toJson(array));
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		context.complete();
	}

	public void updates(String client, int index, AsyncContext context) {
		if (client == null) {

		}
		reqs.put(client, new Request(context, index));
	}

	private static class Request {
		public final int index;
		public final AsyncContext context;

		public Request(AsyncContext context, int index) {
			this.context = context;
			this.index = index;
		}

	}

}
