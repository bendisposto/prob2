package org.codehaus.groovy.tools.shell

import spock.lang.Specification

class CompletorTest extends Specification {

	def "Is the identifier start what I expect?"() {
		setup:
		def completor = new PReflectionCompletor(null)

		expect:
		completor.findIdentifierStart(a,c) == b

		where:
		a 					|	b	|	c
		"api.println(api"	|	12	|	15
		"api.println(a"		|	12	|	13
		"api.println(api."	|   12	|	15 //find substring api
		"api.println(api.pr"|	12	| 	15
	}
}
