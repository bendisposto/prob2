package de.prob.check.ltl;

import com.google.common.base.Objects;

public class Hint implements Comparable<Hint> {

	private final String text;
	private final String type;

	public Hint(final String text, final String type) {
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
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Hint other = (Hint) obj;
		if (text == null) {
			if (other.text != null) {
				return false;
			}
		} else if (!text.equals(other.text)) {
			return false;
		}
		if (type == null) {
			if (other.type != null) {
				return false;
			}
		} else if (!type.equals(other.type)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(text, type);
	}

	@Override
	public int compareTo(final Hint o) {
		return text.compareTo(o.getText());
	}

}
