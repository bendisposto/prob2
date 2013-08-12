package de.prob.check.ltl;

public class Marker {

	private String type;
	private int line;
	private int pos;
	private int length;
	private String msg;

	public Marker(String type, int line, int pos, int length, String msg) {
		this.type = type;
		this.line = line;
		this.pos = pos;
		this.length = length;
		this.msg = msg;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
