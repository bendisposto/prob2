package de.prob.model.eventb.algorithm

import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.AbstractModifier
import de.prob.model.eventb.ModelGenerationException;

class Procedure extends AbstractModifier {
	String name
	List<EventB> variables
	EventB precondition
	EventB abstraction
	Map<EventB, EventB> locals
	Block algorithm

	def Procedure(String name, List<String> variables, String precondition, String abstraction, Map<String, String> locals, Block algorithm) throws ModelGenerationException{
		this.name = name
		this.variables = variables.collect { parseIdentifier(it) }
		this.precondition = parsePredicate(precondition)
		this.abstraction = parseAssignment(abstraction)
		this.locals = locals.collectEntries { k,v ->
			[
				parseIdentifier(k),
				parseIdentifier(v)
			]
		}
		this.algorithm = algorithm
	}
}
