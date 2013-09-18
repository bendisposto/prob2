package de.prob.web.views;


import geb.spock.GebSpec
import spock.lang.Ignore



class CurrentAnimationsTest extends GebSpec {

	@Ignore
	def "go to login"() {
		when:
		go "http://localhost:8080/sessions/CurrentAnimations"

		then:
		title == "ProB Current Trace"
	}
}
