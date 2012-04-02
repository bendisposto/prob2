package de.prob.model;

import static org.junit.Assert.*;
import spock.lang.Ignore;
import spock.lang.Specification

class HistoryTest extends Specification {
	def History h

	def setup() {
		h = new History()
		/*h.add("1")
		h.add("2")
		h.add("3")
		h.add("4")
		h.add("5")*/
		// numbers are states, characters are ops here
		h.add("1", "a")
		h.add("2", "b")
		h.add("3", "c")
		h.add("4", "d")
		h.add("5", "e")
	}

	def "test add method via toString() method "() {
		expect:
		h.toString() == "[a, b, c, d, e] current Transition: e" 
	}

	def "test add method when current state not on end"() {
		when:
		h.goToPos(a)
		h.add(b, "bar")
		then:
		h.history.size() == c

		where:
		a 	| b 	| c
		2 	| "6"   | 4
		0 	| "10" 	| 2
		-1 	| "10" 	| 1
		4 	| "6" 	| 6
		42 	| "7" 	| 6
	}

	def "test goToPos"() {

		when:
		h.goToPos(a)

		then:
		h.current == b

		where:
		a 	| b
		-1 	| -1
		-2 	| 4
		4 	| 4
		5 	| 4
	}
	
	def "test back"() {
		when:
		h.back()
		
		then:
		h.current == 3
	}
	
	def "test back 2"() {
		when:
		h.goToPos(-1)
		h.back()
		
		then:
		h.current == -1
	}
	
	def "test forward"() {
		when:
		h.forward()
		
		then:
		h.current == 4
	}
	
	def "test forward 2"() {
		when:
		h.goToPos(2)
		h.forward()
		
		then:
		h.current == 3
	}
	
	def "test get current transition"() {
		expect:
		h.getCurrentTransition() == "e"
	}
	
	def "test get current transition 2"() {
		when:
		h.back()
		
		then:
		h.getCurrentTransition() == "d"
	}
	
	def "test get current transition for pos -1"() {
		when:
		h.goToPos(-1)
		
		then:
		h.getCurrentTransition() == null
	}
	
	def "test isPreviousTransition"() {
		expect:
		h.isLastTransition("e") == true
	}
	
	def "test isPreviousTransition 2"() {
		when:
		h.goToPos(-1)
		
		then:
		h.isLastTransition("") == false
	}
	
	def "test isPreviousTransition 3"() {
		when:
		h.goToPos(0)
		
		then:
		h.isLastTransition("") == false
	}
	
	def "test isNextTransition"() {
		expect:
		h.isNextTransition("any") == false
	}
	
	def "test isNextTransition 1"() {
		when:
		h.goToPos(2)
		
		then:
		h.isNextTransition("d") == true
		h.isNextTransition("b") == false
	}
	
	def "test isNextTransition 2"() {
		when:
		h.goToPos(-1)
		
		then:
		h.isNextTransition("a") == true
		h.isNextTransition("e") == false
	}
	
	def "test toString method"() {
		expect:
		h.toString() == "[a, b, c, d, e] current Transition: e"
	}
	
	def "test null as transition"()
	{
		when:
		h.add("6", null)
		h.add("7", "bla")
		
		then:
		h.history.size() == 7;
		h.getCurrentState() == "7";
		h.getCurrentTransition() == "bla"
	}
	
	def "test null with isLastTransition"()
	{
		when:
		h.add("6", null)
		
		then:
		h.isLastTransition("any") == false
	}
	
	def "test overwriting null transition"()
	{
		when:
		h.add("6", null)
		h.back()
		h.add("7", "op")
		
		then:
		h.isNextTransition(null) == false
		h.isLastTransition(null) == false
		h.history.size() == 6
		h.getCurrentState() == "7"
		h.getCurrentTransition() == "op"
	}
}
