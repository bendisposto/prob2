package de.prob.worksheet.api.evalStore;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JNodeData {
	private String color;

	@JsonProperty(value = "color")
	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

}
