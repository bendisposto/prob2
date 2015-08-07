package de.prob.model.eventb

import spock.lang.Specification
import de.prob.model.representation.CSPElement

/**
 *
 * Test some of the util classes that are provided by AbstractModifier
 * @author joy
 *
 */
class AbstractModifierTest extends Specification {

	def am = new AbstractModifier()

	def "find correct counter"() {
		when:
		def list = [new CSPElement("inv1"), new CSPElement("inv2"), new CSPElement("inv4")]

		then:
		am.extractCounter("inv",list) == 4
	}

	def "find default counter"() {
		when:
		def list = [new CSPElement("inv1"), new CSPElement("inv2"), new CSPElement("inv4")]

		then:
		am.extractCounter("invx",list) == -1
	}

	def "ignore unmatchable elements"() {
		when:
		def list = [new CSPElement("inv1"), new CSPElement("inv2"), new CSPElement("inv4"), new CSPElement("inv056"), new CSPElement("inv92e")]

		then:
		am.extractCounter("inv",list) == 56
	}

}
