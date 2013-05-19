package de.prob.worksheet;

public class DefaultEditor {
	public final String id;
	protected EBoxTypes type;
	private String text;

	public DefaultEditor(String id, String text) {
		this.id = id;
		this.text = text;
		type = EBoxTypes.unknown;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	protected RenderResult evaluate(WorkSheet ws) {
		return new RenderResult(WorkSheet.RENDERER_TEMPLATE_HTML, "DIE!");
	};

}
