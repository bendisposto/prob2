package de.prob.check.ltl;

public class Hint implements Comparable<Hint> {

	private String text;
	private String type;

	public Hint(String text, String type) {
		this.text = text;
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public String getType() {
		return type;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Hint other = (Hint) obj;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public int compareTo(Hint o) {
		return text.compareTo(o.getText());
	}

}
