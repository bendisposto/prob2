package de.prob.animator.domainobjects;

import de.be4.classicalb.core.parser.node.*;

import de.hhu.stups.prob.translator.BValue;
import de.prob.animator.command.EvaluationCommand;
import de.prob.model.representation.IFormulaUUID;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.statespace.State;

import de.tla2b.exceptions.ExpressionTranslationException;

import de.tla2bAst.Translator;

import util.ToolIO;

public class TLA extends AbstractEvalElement implements IBEvalElement {

	private final Start ast;
	private final ClassicalB classicalB;

	/**
	 * @deprecated Use {@link #TLA(String, FormulaExpand)} with an explicit {@link FormulaExpand} argument instead
	 */
	@Deprecated
	public TLA(String code) {
		this(code, FormulaExpand.EXPAND);
	}

	public TLA(String code, FormulaExpand expand) {
		super(code, expand);
		ast = fromTLA(code);
		classicalB = new ClassicalB(ast, expand);
	}

	private Start fromTLA(String code) {
		ToolIO.setMode(ToolIO.TOOL);
		Start start;
		try {
			start = Translator.translateTlaExpression(code);
			return start;
		} catch (ExpressionTranslationException e) {
			throw new EvaluationException(e.getMessage());
		}
	}

	@Override
	public void printProlog(IPrologTermOutput pout) {
		classicalB.printProlog(pout);
	}

	@Override
	public EvalElementType getKind() {
		return classicalB.getKind();
	}

	@Override
	public String serialized() {
		return "#TLA" + getCode();
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
	public BValue translate() {
		return classicalB.translate();
	}

	@Override
	public Node getAst() {
		return ast;
	}

}
