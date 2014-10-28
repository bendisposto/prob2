package de.prob.animator.command;

import java.util.ArrayList;
import java.util.List;

import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.OpInfo;
import de.prob.util.StringUtil;

public class GetOpFromId extends AbstractCommand {

	private final OpInfo op;
	private final String TERM = "JavaOpTerm";

	private String src;
	private String dest;
	private String name;
	private String targetState;
	private final List<String> params = new ArrayList<String>();
	private final List<String> returnValues = new ArrayList<String>();

	public GetOpFromId(final OpInfo opInfo) {
		op = opInfo;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("get_op_from_id").printAtomOrNumber(op.getId())
				.printVariable(TERM).closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		PrologTerm prologTerm = bindings.get(TERM);
		CompoundPrologTerm cpt = BindingGenerator
				.getCompoundTerm(prologTerm, 7);

		src = StringUtil.generateString(OpInfo.getIdFromPrologTerm(cpt
				.getArgument(2)));
		dest = StringUtil.generateString(OpInfo.getIdFromPrologTerm(cpt
				.getArgument(3)));
		name = StringUtil.generateString(PrologTerm.atomicString(cpt
				.getArgument(4)));

		CompoundPrologTerm paramTerm = BindingGenerator.getCompoundTerm(
				cpt.getArgument(5), 2);
		// TODO:
		// ListPrologTerm paramS = BindingGenerator.getList(paramTerm
		// .getArgument(1));
		ListPrologTerm paramP = BindingGenerator.getList(paramTerm
				.getArgument(2));

		for (PrologTerm pt : paramP) {
			String v = StringUtil.generateString(pt.getFunctor());
			params.add(v);
			/*
			 * paramsSource.add(new EvalResult("", v, paramS.get(i), new
			 * HashMap<String, String>(), new HashMap<String, PrologTerm>()));
			 */
		}

		CompoundPrologTerm retTerm = BindingGenerator.getCompoundTerm(
				cpt.getArgument(6), 2);
		// ListPrologTerm retS =
		// BindingGenerator.getList(retTerm.getArgument(1));
		ListPrologTerm retP = BindingGenerator.getList(retTerm.getArgument(2));

		for (PrologTerm pt : retP) {
			String v = StringUtil.generateString(pt.getFunctor());
			returnValues.add(v);
			/*
			 * returnValueSource.add(new EvalResult("", v, retS.get(i), new
			 * HashMap<String, String>(), new HashMap<String, PrologTerm>()));
			 */
		}

		targetState = StringUtil.generateString(OpInfo.getIdFromPrologTerm(cpt
				.getArgument(7)));
		// TODO: Move this command into de.prob.statespace.* so that it can be
		// package private
		op.setInfo(params, returnValues);
	}

	public String getSrc() {
		return src;
	}

	public String getDest() {
		return dest;
	}

	public String getName() {
		return name;
	}

	public List<String> getParams() {
		return params;
	}

	public String getTargetState() {
		return targetState;
	}

}
