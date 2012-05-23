package de.prob.statespace;

import java.util.ArrayList;
import java.util.List;

public class History {

	private List<HistoryElement> history = new ArrayList<HistoryElement>();
	private int current = -1;

	/**
	 * Adds a new transition (History Element) to the History object. Takes the
	 * operation id and adds it to the list. If the current position in the
	 * history is the end of the list, it simply appends the new element to the
	 * list. If it is not at the end of the list, the new History Element
	 * overwrites the element at the current position.
	 * 
	 * @param dest
	 * @param op
	 */
	public void add(final String dest, final String op) {
		if (op == null && isLastTransition(null)) {
			// if current state is "root", we can't go back anyway
			// hence we will not add another case for this
			back();
		}

		if (current == -1) {
			String src = "root";
			final HistoryElement elem = new HistoryElement(src, dest, op);
			history.clear();
			history.add(elem);
			current++;
		} else {
			if (history.size() != current) {
				history = history.subList(0, current + 1);
			}
			String src = getCurrentState();
			final HistoryElement elem = new HistoryElement(src, dest, op);

			history.add(elem);
			current++;
		}
	}

	// goToPos
	public void goToPos(final int pos) {
		if (pos >= -1 && pos < history.size()) {
			current = pos;
		}
	}

	// back
	public void back() {
		if (canGoBack()) {
			current--;
		}
	}

	// forward
	public void forward() {
		if (canGoForward()) {
			current++;
		}
	}

	// getCurrentTransition
	public String getCurrentTransition() {
		if (current == -1)
			return null;
		return history.get(current).getOp();
	}

	public boolean isLastTransition(final String id) {
		if (current <= 0)
			return false;

		// String currentOp = history.get(current).getOp();
		String currentOp = getCurrentTransition();
		if (id == null)
			return currentOp == null;
		if (currentOp == null)
			return id == null;

		return currentOp.equals(id);
	}

	public boolean isNextTransition(final String id) {
		if (!canGoForward())
			return false;

		String nextOp = history.get(current + 1).getOp();
		if (id == null)
			return nextOp == null;
		if (nextOp == null)
			return id == null;

		return nextOp.equals(id);
	}

	public boolean canGoForward() {
		return current < history.size() - 1;
	}

	public boolean canGoBack() {
		return current >= 0;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < history.size() - 1; i++) {
			sb.append((history.get(i).getOp()) + ", ");
		}
		if (history.size() != 0) {
			sb.append(history.get(history.size() - 1).getOp());
		}
		String content = sb.toString();
		return "[" + content + "] " + "current Transition: "
				+ getCurrentTransition();
	}

	public String getCurrentState() {
		if (current == -1)
			return "root";
		return history.get(current).getDest();
	}
}