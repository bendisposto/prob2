package de.prob.visualization;

import java.util.List;

import de.prob.model.classicalb.ClassicalBEntity;
import de.prob.model.classicalb.ClassicalBMachine;
import de.prob.model.representation.Operation;

public class MachineConstants {
	private final String name;
	private final List<ClassicalBEntity> variables;
	private final List<ClassicalBEntity> constants;
	private final List<Operation> operations;

	public MachineConstants(final ClassicalBMachine m) {
		this.name = m.name();
		this.variables = m.variables();
		this.constants = m.constants();
		this.operations = m.operations();
	}

	public String getName() {
		return name;
	}

	public int calculateLines() {
		int i = 1;
		if (!variables.isEmpty()) {
			i++;
			int sum = 0;
			for (ClassicalBEntity var : variables) {
				sum += var.getIdentifier().length() + 1;
			}
			i += sum / 100 + 1;
		}
		if (!constants.isEmpty()) {
			i++;
			int sum = 0;
			for (ClassicalBEntity c : constants) {
				sum += c.getIdentifier().length() + 1;
			}
			i += sum / 100 + 1;
		}
		if (!operations.isEmpty()) {
			i++;
			int sum = 0;
			for (Operation op : operations) {
				sum += op.toString().length() + 1;
			}
			i += sum / 100 + 1;
		}
		return i;
	}

	public int getLargestVar() {
		int max = 0;
		for (ClassicalBEntity entity : variables) {
			if (entity.getIdentifier().length() > max) {
				max = entity.getIdentifier().length();
			}
		}
		return max * 15;
	}

	public int getLargestConstant() {
		int max = 0;
		for (ClassicalBEntity entity : constants) {
			if (entity.getIdentifier().length() > max) {
				max = entity.getIdentifier().length();
			}
		}
		return max * 15;
	}

	public int getLargestOp() {
		int max = 0;
		for (Operation op : operations) {
			if (op.toString().length() > max) {
				max = op.toString().length();
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
		int width = calculateWidth();
		// Add 20 for name and each label
		int height = 20;
		if (!variables.isEmpty()) {
			height += 20;
			int i = 0;
			int maxVar = getLargestVar();
			for (int j = 0; j < variables.size(); j++) {
				i++;
				if (i * maxVar >= width) {
					i = 0;
					height += 25;
				}
			}
			height += 25;
		}
		if (!constants.isEmpty()) {
			height += 20;
			int i = 0;
			int maxVar = getLargestConstant();
			for (int j = 0; j < constants.size(); j++) {
				i++;
				if (i * maxVar >= width) {
					i = 0;
					height += 25;
				}
			}
			height += 25;
		}
		if (!operations.isEmpty()) {
			height += 20;
			int i = 0;
			int maxVar = getLargestOp();
			for (int j = 0; j < operations.size(); j++) {
				i++;
				if (i * maxVar >= width) {
					i = 0;
					height += 25;
				}
			}
			height += 25;
		}
		return height;
	}
}
