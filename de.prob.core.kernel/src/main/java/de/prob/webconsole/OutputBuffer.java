package de.prob.webconsole;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.gson.Gson;
import com.google.inject.Singleton;

@Singleton
public class OutputBuffer {

	private static final String SORRY = " lines were droped because they were not retrieved on time. Most likely this is a bug in the display code. Sorry.";

	@SuppressWarnings("unused")
	// Fields are used by JSon serialisation
	private static class Entry {

		private final int nr;
		private final String content;
		private final List<String> extra;
		private final String msgtype;

		public Entry(final int nr, final String content, final String msgtype,
				final List<String> extra) {
			this.nr = nr;
			this.content = content;
			this.extra = extra;
			this.msgtype = msgtype;
		}

		public Entry(final int nr, final String content, final String msgtype) {
			this(nr, content, msgtype, null);
		}

		public int getNr() {
			return nr;
		}

		public boolean isNewer(final int pos) {
			return nr > pos;
		}

	}

	private static final long GC_STORE_TRIGGER = 500;
	private static final long GC_TIME_TRIGGER = 10000;
	private final long lastGC = 0;
	private int minLine = 0;
	private int maxLine = 0;
	private int lastHighestNumber = 0;

	private final Queue<Entry> buffer = new ConcurrentLinkedQueue<Entry>();

	public void append(final String s) {
		buffer.add(new Entry(++maxLine, s + "\n", "output"));
	}

	public void error(final String s) {
		buffer.add(new Entry(++maxLine, s + "\n", "error"));
	}

	public void error(final String s, final List<String> trace) {
		buffer.add(new Entry(++maxLine, s + "\n", "trace", trace));
	}

	public void add(final String content, final String msgtype) {
		buffer.add(new Entry(++maxLine, content + "\n", msgtype));
		if (gcNecessary()) {
			gc();
		}
	}

	private void gc() {
		while (!buffer.peek().isNewer(lastHighestNumber)) {
			buffer.poll();
		}
		minLine = buffer.peek().getNr();
	}

	private boolean gcNecessary() {
		return (System.currentTimeMillis() - lastGC) > GC_TIME_TRIGGER
				&& (maxLine - minLine) > GC_STORE_TRIGGER;
	}

	public String getTextAsJSon(final int pos) {
		ArrayList<Entry> res = new ArrayList<Entry>();
		if (pos < minLine) {
			String content = (minLine - pos) + SORRY;
			res.add(new Entry(pos, content, "error"));
		}
		Iterator<Entry> iterator = buffer.iterator();
		while (iterator.hasNext()) {
			Entry e = iterator.next();
			if (e.isNewer(pos)) {
				res.add(e);
			}
		}
		if (!res.isEmpty()) {
			lastHighestNumber = res.get(res.size() - 1).getNr();
		}
		return new Gson().toJson(res);
	}

	public void newline() {
		append("\n");
	}

}
