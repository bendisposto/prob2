package de.prob.model.eventb.algorithm

import de.be4.ltl.core.parser.node.THistorically;
import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.AbstractModifier
import de.prob.model.eventb.ModelGenerationException;

class Procedure extends AbstractModifier {
	String name
	List<EventB> arguments
	List<EventB> results
	Map<EventB, EventB> locals // the name of the local variable is the key, the abstract variable the value
	EventB precondition
	EventB abstraction
	Block algorithm

	def Procedure(String name, List<String> arguments, List<String> results, Map<String, String> locals, String precondition, String abstraction, Block algorithm) throws ModelGenerationException{
		super(algorithm.typeEnvironment)
		this.name = name
		this.arguments = arguments.collect { parseIdentifier(it) }
		this.results = results.collect { parseIdentifier(it) }
		this.locals = locals.collectEntries { k,v ->
			[
				parseIdentifier(k),
				parseIdentifier(v)
			]
		}
		this.precondition = parsePredicate(precondition)
		this.abstraction = parseAssignment(abstraction)
		this.algorithm = algorithm
	}

	@Override
	public String toString() {
		def res = results.collect { v -> "$v"}.iterator().join(",")
		res = res + " := " + name + "("
		res = res + arguments.collect { v -> "$v" }.iterator().join(",")+ "):"
		res
	}
}
