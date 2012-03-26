package de.prob.model;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

public class History {

	Logger logger = LoggerFactory.getLogger(History.class);
	public List<String> history = new LinkedList<String>();
	public int current = -1;

	// add
	public void add(String id) {
		if( current == -1 ) {
			history.clear();
			history.add(id);
			current++;
		} else {
			if (history.size() != current )
				history = history.subList(0, current+1);
			history.add(id);
			current++;
		}
	}

	// goToPos
	public void goToPos(int pos) {
		if (pos >= -1 && pos < history.size())
			current = pos;
	}

	// back
	public void back() {
		if (canGoBack())
			current--;
	}
	
	// forward
	public void forward(){
		if (canGoForward())
			current++;
	}

	// getCurrentTransition
	public String getCurrentTransition() {
		if(current == -1)
			return null;
		return history.get(current);
	}

	public boolean isLastTransition(String id) {
		if (current>0)
			return history.get(current).equals(id);
		return false;
	}

	public boolean isNextTransition(String id) {
		if (canGoForward())
			return history.get(current + 1).equals(id);
		return false;
	}
	
	public boolean canGoForward() {
		return current < history.size()-1;
	}
	
	public boolean canGoBack() {
		return current >= 0;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String list = Joiner.on(", ").join(history);
		sb.append("[");
		sb.append(list);
		sb.append("] ");
		sb.append("current Transition: ");
		sb.append(getCurrentTransition());
		return sb.toString();
	}

}