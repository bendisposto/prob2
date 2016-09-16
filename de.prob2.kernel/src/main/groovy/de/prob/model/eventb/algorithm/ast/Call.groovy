package de.prob.model.eventb.algorithm.ast

import org.eventb.core.ast.extension.IFormulaExtension

import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.ModelGenerationException
import de.prob.model.eventb.algorithm.Procedure

class Call extends Statement implements IAssignment {

	String name
	List<EventB> arguments
	List<EventB> results

	def Call(String name, List<String> arguments, List<String> results, Set<IFormulaExtension> typeEnv) throws ModelGenerationException {
		super(typeEnv)
		this.name = name
		this.arguments = arguments.collect { parseIdentifier(it) }
		this.results = results.collect { parseIdentifier(it) }
	}

	@Override
	public String toString() {
		results.collect { it.getCode() }.iterator().join(",") + " := "+name+"("+arguments.collect { it.getCode() }.iterator().join(",")+")"
	}

	public Map<String, EventB> getSubstitutions(Procedure procedure) {
		validate('procedure', procedure)
		assert procedure.arguments.size() == arguments.size()
		assert procedure.results.size() == results.size()
		Map<String, EventB> subs = [:]
		[
			procedure.arguments,
			arguments
		].transpose().each { e ->
			subs[e[0]] = e[1]
		}
		[
			procedure.results,
			results
		].transpose().each { e ->
			subs[e[0]] = e[1]
		}
		subs
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
