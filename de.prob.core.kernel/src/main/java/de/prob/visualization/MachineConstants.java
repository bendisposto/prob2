package de.prob.visualization;

import java.util.ArrayList;
import java.util.List;

import de.prob.model.eventb.Context;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.BEvent;
import de.prob.model.representation.Constant;
import de.prob.model.representation.Machine;
import de.prob.model.representation.Variable;

public class MachineConstants {
	private String name;
	private List<String> variables;
	private List<String> constants;
	private List<String> operations;
	private final int width;
	private int lines;
	private final String html;
	private final int height;

	public MachineConstants(final AbstractElement abstractElement) {
		if (abstractElement instanceof Machine) {
			final Machine machine = (Machine) abstractElement;
			this.name = machine.getName();
			variables = new ArrayList<String>();
			for (Variable variable : machine.getChildrenOfType(Variable.class)) {
				variables.add(variable.getName());
			}
			constants = new ArrayList<String>();
			operations = new ArrayList<String>();
			for (BEvent event : machine.getChildrenOfType(BEvent.class)) {
				operations.add(event.getName());
				// TODO: Create to string for BEvent
			}
		} else if (abstractElement instanceof Context) {
			Context context = (Context) abstractElement;
			constants = new ArrayList<String>();
			for (Constant constant : context.getChildrenOfType(Constant.class)) {
				constants.add(constant.getExpression().getCode());
			}
			variables = new ArrayList<String>();
			operations = new ArrayList<String>();
		}
		width = calculateWidth();
		html = generateHTML();
		height = calculateHeight();
	}

	public String getName() {
		return name;
	}

	public int getLargestVar() {
		int max = 0;
		for (final String entity : variables) {
			if (entity.length() > max) {
				max = entity.length();
			}
		}
		return max * 15;
	}

	public int getLargestConstant() {
		int max = 0;
		for (final String entity : constants) {
			if (entity.length() > max) {
				max = entity.length();
			}
		}
		return max * 15;
	}

	public int getLargestOp() {
		int max = 0;
		for (final String op : operations) {
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
		final int l = getLargestLine();
		if (l < 100) {
			return 100;
		}
		return l;
	}

	public int calculateHeight() {
		return lines * 16;
	}

	public String generateHTML() {
		final HTMLgenerator htmlGenerator = new HTMLgenerator(width);
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
			for (final String op : operations) {
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
