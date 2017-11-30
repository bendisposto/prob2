package de.prob.animator.command;

import java.util.ArrayList;
import java.util.List;

import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class GetMachineOperationInfos extends AbstractCommand {



	private static final String PROLOG_COMMAND_NAME = "get_machine_operation_infos";
	private static final String RESULT_VARIABLE = "MachineOperationInfos";
	private final List<OperationInfo> operationInfos = new ArrayList<>();

	public GetMachineOperationInfos() {

	}

	@Override
	public void processResult(final ISimplifiedROMap<String, PrologTerm> bindings) {
		ListPrologTerm list = BindingGenerator.getList(bindings, RESULT_VARIABLE);
		for (PrologTerm prologTerm : list) {
			final String opName = prologTerm.getArgument(1).getFunctor();
			final List<String> outputParameterNames = new ArrayList<>();
			for (PrologTerm param : (ListPrologTerm) prologTerm.getArgument(2)) {
				outputParameterNames.add(param.getFunctor());
			}
			final List<String> parameterNames = new ArrayList<>();
			for (PrologTerm param : (ListPrologTerm) prologTerm.getArgument(3)) {
				parameterNames.add(param.getFunctor());
			}
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

	public class OperationInfo {
		private final String operationName;
		private final List<String> parameterNames;
		private final List<String> outputParameterNames;

		private OperationInfo(String opName, List<String> parameterNames, List<String> outputParameterNames) {
			this.operationName = opName;
			this.parameterNames = parameterNames;
			this.outputParameterNames = outputParameterNames;
		}

		public String getOperationName() {
			return operationName;
		}

		public List<String> getParameterNames() {
			return parameterNames;
		}

		public List<String> getOutputParameterNames() {
			return outputParameterNames;
		}

		@Override
		public String toString() {
			return String.format("[opName: %s, params: [%s], outputParams: [%s]]", operationName,
					String.join(", ", parameterNames), String.join(", ", outputParameterNames));

		}

	}

}
