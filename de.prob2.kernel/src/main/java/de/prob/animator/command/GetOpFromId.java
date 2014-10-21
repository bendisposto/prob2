package de.prob.animator.command;

import java.util.ArrayList;
import java.util.List;

import de.prob.animator.domainobjects.EvalResult;
import de.prob.animator.domainobjects.SimpleEvalResult;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.OpInfo;

public class GetOpFromId extends AbstractCommand {

	private final OpInfo op;
	private final String TERM = "JavaOpTerm";

	private String src;
	private String dest;
	private String name;
	private String targetState;
	private final List<String> params = new ArrayList<String>();
	private final List<String> returnValues = new ArrayList<String>();
	private final List<EvalResult> paramsSource = new ArrayList<EvalResult>();
	private final List<EvalResult> returnValueSource = new ArrayList<EvalResult>();

	public GetOpFromId(final OpInfo opInfo) {
		this.op = opInfo;
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

		src = SimpleEvalResult.generateString(OpInfo.getIdFromPrologTerm(cpt
				.getArgument(2)));
		dest = SimpleEvalResult.generateString(OpInfo.getIdFromPrologTerm(cpt
				.getArgument(3)));
		name = SimpleEvalResult.generateString(PrologTerm.atomicString(cpt
				.getArgument(4)));

		CompoundPrologTerm paramTerm = BindingGenerator.getCompoundTerm(
				cpt.getArgument(5), 2);
		ListPrologTerm paramS = BindingGenerator.getList(paramTerm
				.getArgument(1));
		ListPrologTerm paramP = BindingGenerator.getList(paramTerm
				.getArgument(2));

		for (int i = 0; i < paramS.size(); i++) {
			String v = SimpleEvalResult.generateString(paramP.get(i)
					.getFunctor());
			params.add(v);
			/*
			 * paramsSource.add(new EvalResult("", v, paramS.get(i), new
			 * HashMap<String, String>(), new HashMap<String, PrologTerm>()));
			 */
		}

		CompoundPrologTerm retTerm = BindingGenerator.getCompoundTerm(
				cpt.getArgument(6), 2);
		ListPrologTerm retS = BindingGenerator.getList(retTerm.getArgument(1));
		ListPrologTerm retP = BindingGenerator.getList(retTerm.getArgument(2));

		for (int i = 0; i < retS.size(); i++) {
			String v = SimpleEvalResult
					.generateString(retP.get(i).getFunctor());
			returnValues.add(v);
			/*
			 * returnValueSource.add(new EvalResult("", v, retS.get(i), new
			 * HashMap<String, String>(), new HashMap<String, PrologTerm>()));
			 */
		}

		targetState = SimpleEvalResult.generateString(OpInfo
				.getIdFromPrologTerm(cpt.getArgument(7)));
		op.setInfo(name, params, returnValues, paramsSource, returnValueSource,
				targetState);
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
