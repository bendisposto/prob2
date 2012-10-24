package de.prob.animator.domainobjects

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
			case "fd": [term.getArgument(2),term.getArgument(1)] 	
				break	
			default: []	
		}
	}
	
	
	
}
