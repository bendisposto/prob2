package de.prob.visualization;

import java.util.List;

public class HTMLgenerator {

	StringBuilder sb = new StringBuilder();
	private final int rightBoundary;

	public HTMLgenerator(final int rightBoundary) {
		sb.append("<html>");
		this.rightBoundary = rightBoundary;
	}

	public void writeLine(final String line) {
		sb.append(line);
		sb.append("<br>");
	}

	public void writeHeading(final String heading) {
		sb.append("<u>");
		sb.append(heading);
		sb.append("</u><br>");
	}

	public int writeList(final List<String> strings) {
		int lines = 1;
		int i = 0;
		for (int j = 0; j < strings.size(); j++) {

			String a = strings.get(j);
			i += a.length() * 15;
			if (!(i < rightBoundary)) {
				i = 0;
				lines++;
			}
			sb.append(a);

			if (j < strings.size() - 1) {
				i += 10;
				sb.append(", ");
			}
		}
		lines++;
		sb.append("<br>");
		return lines;
	}

	public void end() {
		sb.append("</html>");
	}

	public String get() {
		return sb.toString();
	}
}
