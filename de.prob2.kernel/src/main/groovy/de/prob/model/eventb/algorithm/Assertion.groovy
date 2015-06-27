package de.prob.model.eventb.algorithm

class Assertion implements Statement {
	def String assertion

	def Assertion(String assertion) {
		this.assertion = assertion
	}
}
