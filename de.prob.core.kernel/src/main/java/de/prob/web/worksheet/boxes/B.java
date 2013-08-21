package de.prob.web.worksheet.boxes;

import java.util.List;
import java.util.Map;

public class B extends AbstractBox {

	private String content = "";

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> render() {
		return pack(makeHtml(id, "B sayz " + content));
	}

	@Override
	public void setContent(Map<String, String[]> data) {
		this.content = data.get("text")[0];
	}

	@Override
	protected String getContentAsJson() {
		return content;
	}

	@Override
	public EReorderEffect reorderEffect() {
		return EReorderEffect.EVERYTHING_BELOW;
	}
}
