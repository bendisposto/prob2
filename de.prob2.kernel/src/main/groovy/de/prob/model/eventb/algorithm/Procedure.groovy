package de.prob.model.eventb.algorithm

import de.be4.ltl.core.parser.node.THistorically;
import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.AbstractModifier
import de.prob.model.eventb.ModelGenerationException;

class Procedure extends AbstractModifier {
	String name
	Map<EventB, EventB> arguments
	Map<EventB, EventB> result
	EventB precondition
	EventB abstraction
	Block algorithm

	def Procedure(String name, Map<String, String> arguments, Map<String, String> result, String precondition, String abstraction, Block algorithm) throws ModelGenerationException{
		super(algorithm.typeEnvironment)
		this.name = name
		this.arguments = arguments.collectEntries { k,v ->
			[
				parseIdentifier(k),
				parseIdentifier(v)
			]
		}
		this.result = result.collectEntries {k,v ->
			[
				parseIdentifier(k),
				parseIdentifier(v)
			]
		}
		this.precondition = parsePredicate(precondition)
		this.abstraction = parseAssignment(abstraction)
		this.algorithm = algorithm
	}
}
