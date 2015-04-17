package de.prob.animator.domainobjects;

import de.be4.classicalb.core.parser.node.Node;
import de.be4.classicalb.core.parser.node.Start;
import de.prob.animator.command.EvaluationCommand;
import de.prob.model.representation.IFormulaUUID;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.statespace.State;
import de.prob.translator.types.BObject;

public class TLA extends AbstractEvalElement implements IBEvalElement {

	private ClassicalB classicalB;
	private Start ast;

	public TLA(String code) {
		this.code = code;
		ast = fromTLA(code);
		classicalB = new ClassicalB(ast);
	}

	private Start fromTLA(String code) {
		return null;
	}

	@Override
	public void printProlog(IPrologTermOutput pout) {
		classicalB.printProlog(pout);
	}

	@Override
	public String getKind() {
		return classicalB.getKind();
	}

	@Override
	public String serialized() {
		return "#TLA" + code;
	}

	@Override
	public IFormulaUUID getFormulaId() {
		return classicalB.getFormulaId();
	}

	@Override
	public EvaluationCommand getCommand(State stateid) {
		return classicalB.getCommand(stateid);
	}

	@Override
	public FormulaExpand expansion() {
		return classicalB.expansion();
	}

	@Override
	public BObject translate() {
		return classicalB.translate();
	}

	@Override
	public Node getAst() {
		return ast;
	}

}
