package de.prob.check.ltl;

import java.util.LinkedList;
import java.util.List;

public class Expression {

	private Mark operator;
	private List<Mark> operands;

	public Expression(Mark operator, Mark operand) {
		this.operator = operator;
		operands = new LinkedList<Mark>();
		operands.add(operand);
	}

	public Expression(Mark operator, List<Mark> operands) {
		this.operator = operator;
		this.operands = operands;
	}

	public Mark getOperator() {
		return operator;
	}

	public void setOperator(Mark operator) {
		this.operator = operator;
	}

	public List<Mark> getOperands() {
		return operands;
	}

	public void setOperands(List<Mark> operands) {
		this.operands = operands;
	}

}
