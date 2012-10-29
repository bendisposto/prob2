package de.prob.animator.domainobjects

import com.google.common.base.Joiner;

import de.prob.prolog.term.IntegerPrologTerm
import de.prob.prolog.term.PrologTerm

class ValueTranslator {

	def makeSet(node, set) {
		if (node.getFunctor() == "empty") set
		else {
			def value = translate(node.getArgument(1))
			set.add(value)
			def left = makeSet(node.getArgument(4),set)
			makeSet(node.getArgument(5),left)
		}
	}

	def translate(PrologTerm term) {
		if (term instanceof IntegerPrologTerm) 
			term.getValue() else  {
		def termFunctor = term.functor
		switch (termFunctor) {
			case "int": term.getArgument(1).getValue()
			 break
			case "pred_true": true
			break
			case "pred_false": false
			break
			case "avl_set": makeSet(term.getArgument(1),new HashSet())
			break
			case ",": [1:translate(term.getArgument(1)),2:translate(term.getArgument(2))]
			break
			case "fd":  [term.getArgument(2).functor,term.getArgument(1)] 
			break
			default: []
		}}
	}
	


	def String asString(groovy_state) {
		switch (groovy_state) {
			case HashSet: "{"+ Joiner.on(",").join(groovy_state.collect {asString(it)}) +"}"
				break
			case LinkedHashMap: "("+asString(groovy_state.get(1))+","+asString(groovy_state.get(2))+")"
				break
			case ArrayList: if (groovy_state.isEmpty()) "{}" else  asString(groovy_state.get(0))+asString(groovy_state.get(1))
			    break
			default: groovy_state.toString();
		}
	}
}
