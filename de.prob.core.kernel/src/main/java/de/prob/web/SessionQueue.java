package de.prob.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.servlet.AsyncContext;

import com.google.gson.Gson;

public class SessionQueue {

	private int id = 0;

	private static class DataObject {
		public final int id;
		public final Object[] content;

		public DataObject(Object[] content, int id) {
			this.content = content;
			this.id = id;
		}
	}

	private final List<Message> q = new ArrayList<Message>();
	private final List<Request> reqs = new Vector<Request>();

	private static final Gson GSON = new Gson();

	public void submit(Object json) {
		q.add(new Message(id++, json));
		deliverAll();
	}

	private void deliverAll() {
		for (Request r : reqs) {
			ArrayList<Object> result = new ArrayList<Object>();
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

	public void updates(int index, AsyncContext context) {
		reqs.add(new Request(context, index));
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
