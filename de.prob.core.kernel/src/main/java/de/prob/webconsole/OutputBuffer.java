package de.prob.webconsole;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.gson.Gson;
import com.google.inject.Singleton;

@Singleton
public class OutputBuffer {

	private static final String SORRY = " lines were droped because they were not retrieved on time. Most likely this is a bug in the display code. Sorry.";

	private static class Entry {

		private final int nr;
		@SuppressWarnings("unused")
		private final String content; // is actually used by JSon serialisation

		@SuppressWarnings("unused")
		private final String style; // is actually used by JSon serialisation

		public Entry(int nr, boolean error, String content) {
			this.nr = nr;
			this.style = error ? "groovy_error" : "";
			this.content = content;
		}

		public int getNr() {
			return nr;
		}

		public boolean isNewer(int pos) {
			return nr > pos;
		}

	}

	private static final long GC_STORE_TRIGGER = 500;
	private static final long GC_TIME_TRIGGER = 10000;
	private long lastGC = 0;
	private int minLine = 0;
	private int maxLine = 0;
	private int lastHighestNumber = 0;

	private Queue<Entry> buffer = new ConcurrentLinkedQueue<Entry>();

	public void append(String s) {
		buffer.add(new Entry(++maxLine, false, s));
		if (gcNecessary())
			gc();
	}

	public void error(String s) {
		buffer.add(new Entry(++maxLine, true, s));
		if (gcNecessary())
			gc();
	}

	private void gc() {
		while (!buffer.peek().isNewer(lastHighestNumber))
			buffer.poll();
		minLine = buffer.peek().getNr();
	}

	private boolean gcNecessary() {
		return (System.currentTimeMillis() - lastGC) > GC_TIME_TRIGGER
				&& (maxLine - minLine) > GC_STORE_TRIGGER;
	}

	public String getTextAsJSon(int pos) {
		ArrayList<Entry> res = new ArrayList<Entry>();
		if (pos < minLine)
			res.add(new Entry(pos, true, (minLine - pos) + SORRY));
		Iterator<Entry> iterator = buffer.iterator();
		while (iterator.hasNext()) {
			Entry e = iterator.next();
			if (e.isNewer(pos))
				res.add(e);
		}
		if (!res.isEmpty())
			lastHighestNumber = res.get(res.size() - 1).getNr();
		return new Gson().toJson(res);
	}

	public void newline() {
		append("\n");
	}

}
