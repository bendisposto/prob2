package de.prob.web;

import java.util.ArrayList;

import de.prob.web.data.Message;

/**
 * Used for debugging purposes only.
 * 
 * @author bendisposto
 * 
 */
public class Responses {

	private ArrayList<Message> responses = new ArrayList<Message>();

	public int size() {
		return responses.size();
	}

	public boolean isEmpty() {
		return responses.isEmpty();
	}

	public Message get(int i) {
		return responses.get(i);
	}

	public void add(Message message) {
		responses.add(message);
	}

	public void clear() {
		responses.clear();
	}

}
