package de.prob.model;

import static org.junit.Assert.*;
import spock.lang.Specification

class HistoryTest extends Specification {
	def History h

	def setup() {
		h = new History()
		h.add("1")
		h.add("2")
		h.add("3")
		h.add("4")
		h.add("5")
	}

	def "test add method"() {
		expect:
		h.history == ["1", "2", "3", "4", "5"]
	}

	def "test add method when current state not on end"() {
		when:
		h.goToPos(a)
		h.add(b)
		then:
		h.history == c

		where:
		a 	| b 	| c
		2 	| "6" 	| ["1", "2", "3", "6"]
		0 	| "10" 	| ["1", "10"]
		-1 	| "10" 	| ["10"]
		4 	| "6" 	| ["1", "2", "3", "4", "5", "6"]
		42 	| "7" 	| ["1", "2", "3", "4", "5", "7"]
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
		h.getCurrentTransition() == "5"
	}
	
	def "test get current transition 2"() {
		when:
		h.back()
		
		then:
		h.getCurrentTransition() == "4"
	}
	
	def "test get current transition for pos -1"() {
		when:
		h.goToPos(-1)
		
		then:
		h.getCurrentTransition() == null
	}
	
	def "test isPreviousTransition"() {
		expect:
		h.isLastTransition("5") == true
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
		h.isNextTransition("4") == true
		h.isNextTransition("2") == false
	}
	
	def "test isNextTransition 2"() {
		when:
		h.goToPos(-1)
		
		then:
		h.isNextTransition("1") == true
		h.isNextTransition("5") == false
	}
	
	def "test toString method"() {
		expect:
		h.toString() == "[1, 2, 3, 4, 5] current Transition: 5"
	}
}
