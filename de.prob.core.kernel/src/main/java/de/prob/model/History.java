package de.prob.model;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.ProBException;

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
		if (current>=0)
			current--;
	}
	
	// forward
	public void forward() throws ProBException {
		if (current < history.size()-1)
			current++;
	}

	// getCurrentTransition
	public String getCurrentTransition() {
		if(current == -1)
			return null;
		return history.get(current);
	}

	public boolean isPreviousTransition(String id) {
		if (current>0)
			return history.get(current - 1).equals(id);
		return false;
	}

	public boolean isNextTransition(String id) {
		if (current < history.size()-1)
			return history.get(current + 1).equals(id);
		return false;
	}

}