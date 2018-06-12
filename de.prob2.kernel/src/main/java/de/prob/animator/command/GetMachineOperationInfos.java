package de.prob.animator.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.OperationInfo;

public class GetMachineOperationInfos extends AbstractCommand {
	private static final String PROLOG_COMMAND_NAME = "get_machine_operation_infos";
	private static final String RESULT_VARIABLE = "MachineOperationInfos";

	private final List<OperationInfo> operationInfos = new ArrayList<>();

	public GetMachineOperationInfos() {
		super();
	}

	private static List<String> convertAtomicStringList(final PrologTerm list) {
		if (list instanceof ListPrologTerm) {
			return ((ListPrologTerm)list).stream().map(PrologTerm::atomicString).collect(Collectors.toList());
		} else if ("unknown".equals(PrologTerm.atomicString(list))) {
			return Collections.emptyList();
		} else {
			throw new AssertionError("Not a list or 'unknown': " + list);
		}
	}

	@Override
	public void processResult(final ISimplifiedROMap<String, PrologTerm> bindings) {
		for (PrologTerm prologTerm : BindingGenerator.getList(bindings, RESULT_VARIABLE)) {
			final String opName = prologTerm.getArgument(1).getFunctor();
			final List<String> outputParameterNames = convertAtomicStringList(prologTerm.getArgument(2));
			final List<String> parameterNames = convertAtomicStringList(prologTerm.getArgument(3));
			final List<String> readVariables = convertAtomicStringList(prologTerm.getArgument(6));
			final List<String> writtenVariables = convertAtomicStringList(prologTerm.getArgument(7));
			final List<String> nonDetWrittenVariables = convertAtomicStringList(prologTerm.getArgument(8));
			operationInfos.add(new OperationInfo(opName, parameterNames, outputParameterNames, readVariables, writtenVariables, nonDetWrittenVariables));
		}
	}

	public List<OperationInfo> getOperationInfos() {
		return new ArrayList<>(this.operationInfos);
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME).printVariable(RESULT_VARIABLE).closeTerm();
	}
}
