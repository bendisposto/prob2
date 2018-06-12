package de.prob.animator.command;

import java.util.ArrayList;
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

	@Override
	public void processResult(final ISimplifiedROMap<String, PrologTerm> bindings) {
		for (PrologTerm prologTerm : BindingGenerator.getList(bindings, RESULT_VARIABLE)) {
			final String opName = prologTerm.getArgument(1).getFunctor();
			final List<String> outputParameterNames = ((ListPrologTerm)prologTerm.getArgument(2)).stream()
				.map(PrologTerm::getFunctor)
				.collect(Collectors.toList());
			final List<String> parameterNames = ((ListPrologTerm)prologTerm.getArgument(3)).stream()
				.map(PrologTerm::getFunctor)
				.collect(Collectors.toList());
			operationInfos.add(new OperationInfo(opName, parameterNames, outputParameterNames));
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
