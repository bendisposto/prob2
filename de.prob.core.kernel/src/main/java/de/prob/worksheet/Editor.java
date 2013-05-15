package de.prob.worksheet;

public class Editor {
	public final String id;
	public final EBoxTypes type;
	private String text;

	public Editor(String id, String lang, String text) {
		this.id = id;
		this.text = text;
		this.type = EBoxTypes.valueOf(lang);
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

}
