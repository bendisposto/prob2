package de.prob.check.ltl;

public class PatternInfo {

	private String name;
	private String description;
	private String code;
	private boolean builtin;

	public PatternInfo() {

	}

	public PatternInfo(String name, String description, String code, boolean builtin) {
		this.name = name;
		this.description = description;
		this.code = code;
		this.builtin = builtin;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public boolean isBuiltin() {
		return builtin;
	}

	public void setBuiltin(boolean builtin) {
		this.builtin = builtin;
	}

}
