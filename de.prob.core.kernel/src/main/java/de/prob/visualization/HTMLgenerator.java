package de.prob.visualization;

import java.util.List;

public class HTMLgenerator {

	StringBuilder sb = new StringBuilder();
	private final int rightBoundary;

	public HTMLgenerator(final int rightBoundary) {
		sb.append("<html>");
		this.rightBoundary = rightBoundary;
	}

	public void writeHeading(final String heading) {
		sb.append("<b>");
		sb.append(heading);
		sb.append("</b><br>");
	}

	public void writeList(final List<String> strings) {
		int i = 0;
		for (int j = 0; j < strings.size(); j++) {

			String a = strings.get(j);
			i += a.length() * 15;
			if (!(i < rightBoundary)) {
				sb.append("<br>");
				i = 0;
			}
			System.out.println("a: " + a + " i:" + i);
			sb.append(a);

			if (j < strings.size() - 1) {
				i += 10;
				sb.append(", ");
			}

		}
	}

	public void end() {
		sb.append("</html>");
	}

	public String get() {
		return sb.toString();
	}
}
