package de.prob.model;

import java.util.ArrayList;
import java.util.List;

public class History {

	// Logger logger = LoggerFactory.getLogger(History.class);
	public List<HistoryElement> history = new ArrayList<HistoryElement>();
	private int current = -1;

	// add
	public void add(final String dest, final String op) {
		if(isLastTransition(null))
		{
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
		if (current > 0 && id == null)
			return history.get(current).getOp() == null;
		
		if (current > 0)
			return history.get(current).getOp().equals(id);
		return false;
	}

	public boolean isNextTransition(final String id) {
		if (id == null && canGoForward())
		{
			return history.get(current + 1).getOp() == null;
		}
		if (canGoForward())
			return history.get(current + 1).getOp().equals(id);
		return false;
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
		for(int i = 0; i < history.size(); i++)
		{
			sb.append((history.get(i).getOp()) + ", ");
		}
		String content = sb.toString();

		// delete the last ", "
		content = content.substring(0, content.length() - 2);
		
		return "[" + content + "] " + "current Transition: "
				+ getCurrentTransition();
	}
	
	public String getCurrentState()
	{
		return history.get(current ).getDest();
	}
}