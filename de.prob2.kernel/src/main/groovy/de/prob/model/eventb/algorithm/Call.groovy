package de.prob.model.eventb.algorithm

import org.eventb.core.ast.extension.IFormulaExtension

import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.ModelGenerationException

class Call extends Statement {

	String name
	List<EventB> arguments
	List<EventB> results

	def Call(String name, List<String> arguments, List<String> results, Set<IFormulaExtension> typeEnv) throws ModelGenerationException {
		super(typeEnv)
		this.name = name
		this.arguments = arguments.collect { parseIdentifier(it) }
		this.results = results.collect { parseIdentifier(it) }
	}

	//	def EventB renamedAssignment(Procedure procedure) {
	//		if (procedure.getArguments().size() != arguments.size() || procedure.getResult().size() != results.size()) {
	//			throw new IllegalArgumentException("Expected ${arguments.size()} arguments")
	//		}
	//		EventB assignment = procedure.abstraction
	//		Assignment evtb = assignment.getRodinParsedResult().getParsedAssignment()
	//		FormulaFactory f = FormulaFactory.getInstance(typeEnvironment)
	//		Map<FreeIdentifier, Expression> renameMap = procedure.getArguments()
	//	}
	//	evtb.substituteFreeIdents()}

}
