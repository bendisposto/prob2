package de.prob.check.ltl;

public class Marker {

	private String type;
	private Mark mark;
	private String msg;

	public Marker(String type, int line, int pos, int length, String msg) {
		this.type = type;
		this.mark = new Mark(line, pos, length);
		this.msg = msg;
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

}
