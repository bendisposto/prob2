package de.prob.visualization;

import java.util.ArrayList;
import java.util.List;

import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.classicalb.ClassicalBMachine;
import de.prob.model.eventb.EBContext;
import de.prob.model.eventb.newdom.EventBMachine;
import de.prob.model.representation.IEntity;
import de.prob.model.representation.Label;

public class MachineConstants {
	private final String name;
	private List<String> variables;
	private List<String> constants;
	private List<String> operations;
	private final int width;
	private int lines;
	private final String html;
	private final int height;

	public MachineConstants(final Label abstractElement) {
		this.name = abstractElement.getName();
		if (abstractElement instanceof ClassicalBMachine) {
			final ClassicalBMachine machine = (ClassicalBMachine) abstractElement;
			variables = extractStrings(machine.variables.getChildren());
			constants = new ArrayList<String>();
			operations = extractStrings(machine.operations.getChildren());
		} else if (abstractElement instanceof EBContext) {
			constants = extractStrings(((EBContext) abstractElement).constants
					.getChildren());
			variables = new ArrayList<String>();
			operations = new ArrayList<String>();
		} else if (abstractElement instanceof EventBMachine) {
			final EventBMachine machine = (EventBMachine) abstractElement;
			variables = extractStrings(machine.variables.getChildren());
			operations = extractStrings(machine.events.getChildren());
			constants = new ArrayList<String>();
		}
		width = calculateWidth();
		html = generateHTML();
		height = calculateHeight();
	}

	private List<String> extractStrings(final List<IEntity> entities) {
		final List<String> names = new ArrayList<String>();
		for (final IEntity entity : entities) {
			if (entity instanceof IEvalElement) {
				names.add(((IEvalElement) entity).getCode());
			}
			if (entity instanceof Label) {
				names.add(entity.toString());
			}
		}
		return names;
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
