package de.prob.web.data;

public class Message {
	public final int id;
	public final Object[] content;

	public Message(int id, Object... content) {
		this.id = id;
		this.content = content;
	}

}