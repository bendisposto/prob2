package de.prob.check.ltl;

import org.antlr.v4.runtime.Token;

public class Marker {

	private String type;
	private Mark mark;
	private String msg;
	private String name;
	private Mark stop;

	public Marker(String type, int line, int pos, int length, String msg) {
		this.type = type;
		this.mark = new Mark(line, pos, length);
		this.msg = msg;
	}

	public Marker(String type, Token token, int length, String msg) {
		this.type = type;
		this.mark = new Mark(token, length);
		this.msg = msg;
	}

	public Marker(String type, Token start, Token stop, String name, String msg) {
		this.type = type;
		this.mark = new Mark(start, 1);
		this.msg = msg;
		this.stop = new Mark(stop, stop.getStopIndex() - stop.getStartIndex() + 1);
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Mark getMark() {
		return mark;
	}

	public void setMark(Mark mark) {
		this.mark = mark;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Mark getStop() {
		return stop;
	}

	public void setStop(Mark stop) {
		this.stop = stop;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
