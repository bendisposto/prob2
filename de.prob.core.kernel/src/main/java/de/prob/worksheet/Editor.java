package de.prob.worksheet;

public class Editor {
	public final String id;
	private String text;
	public final String lang;

	public Editor(String id, String lang, String text) {
		this.id = id;
		this.lang = lang;
		this.text = text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

}
