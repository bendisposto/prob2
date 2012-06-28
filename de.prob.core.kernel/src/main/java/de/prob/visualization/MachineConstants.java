package de.prob.visualization;

import java.util.List;

import de.prob.model.representation.AbstractElement;

public class MachineConstants {
	private final String name;
	private final List<String> variables;
	private final List<String> constants;
	private final List<String> operations;
	private final int width;
	private int lines;
	private final String html;
	private final int height;

	public MachineConstants(final AbstractElement abstractElement) {
		this.name = abstractElement.getName();
		this.variables = abstractElement.getVariables();
		this.constants = abstractElement.getConstants();
		this.operations = abstractElement.getOperations();
		width = calculateWidth();
		html = generateHTML();
		height = calculateHeight();
	}

	public String getName() {
		return name;
	}

	public int getLargestVar() {
		int max = 0;
		for (String entity : variables) {
			if (entity.length() > max) {
				max = entity.length();
			}
		}
		return max * 15;
	}

	public int getLargestConstant() {
		int max = 0;
		for (String entity : constants) {
			if (entity.length() > max) {
				max = entity.length();
			}
		}
		return max * 15;
	}

	public int getLargestOp() {
		int max = 0;
		for (String op : operations) {
			if (op.length() > max) {
				max = op.length();
			}
		}
		return max * 15;
	}

	public int getLargestLine() {
		int max = getLargestVar();
		int c = getLargestConstant();
		if (c > max) {
			max = c;
		}
		c = getLargestOp();
		if (c > max) {
			max = c;
		}
		return c;
	}

	public int calculateWidth() {
		int l = getLargestLine();
		if (l < 100)
			return 100;
		return l;
	}

	public int calculateHeight() {
		return lines * 16;
	}

	public String generateHTML() {
		HTMLgenerator htmlGenerator = new HTMLgenerator(width);
		lines = 1;
		htmlGenerator.writeLine(name);
		if (!variables.isEmpty()) {
			htmlGenerator.writeHeading("Variables");
			lines++;
			lines += htmlGenerator.writeList(variables);
		}
		if (!constants.isEmpty()) {
			htmlGenerator.writeHeading("Constants");
			lines++;
			lines += htmlGenerator.writeList(constants);
		}
		if (!operations.isEmpty()) {
			htmlGenerator.writeHeading("Operations");
			lines++;
			for (String op : operations) {
				htmlGenerator.writeLine(op);
				lines++;
			}
		}
		htmlGenerator.end();
		return htmlGenerator.get();
	}

	public int getHeight() {
		return height;
	}

	public String getHtml() {
		return html;
	}

	public int getWidth() {
		return width;
	}
}
