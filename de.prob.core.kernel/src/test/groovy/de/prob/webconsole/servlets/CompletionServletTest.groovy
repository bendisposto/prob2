package de.prob.webconsole.servlets;

import static org.junit.Assert.*
import spock.lang.Specification

class CompletionServletTest extends Specification {
	def servlet
	
	def setup() {
		servlet = new CompletionServlet(null)
	}

	def "camelCase gets split into correct substrings"() {
		when:
		def result = servlet.camelSplit("fooBar");
		then:
		result == ["foo", "Bar"]
	}

	def "splitting camelCase works on more than one uppercase occurence"() {
		when:
		def result = servlet.camelSplit("fooBarBazBlubbBla");
		then:
		result == ["foo", "Bar", "Baz", "Blubb", "Bla"]
	}

	def "camcelCase splitting works with substrings of length one"() {
		when:
		def result = servlet.camelSplit("mahnaMBDBDB");
		then:
		result == ["mahna", "M", "B", "D", "B", "D", "B"]
	}

	def "camelCase splitting works even on strings of length one"() {
		when:
		def result = servlet.camelSplit("f")
		then:
		result == ["f"]
	}

	def "trying to split an empty string result in an empty list"() {
		expect:
		[] == servlet.camelSplit("")
	}
	
	def "filtering the correct completions works"() {
		when:
		List<String> completions = ["bla", "foo", "fooBar", "fooBahn", "fooTran", "fantaBar"]
		List<String> result = servlet.camelMatch(completions, "foB")
		then:
		List<String> expected = ["fooBar", "fooBahn"]
		result == expected
	}
	
	def "filtering the correct completions works with substrings of length one"() {
		when:
		List<String> completions = ["bla", "fooClownBus", "fooBarBar", "fooBahnBaah", "fooTran", "fantaBar"]
		List<String> result = servlet.camelMatch(completions, "foBB")
		then:
		List<String> expected = ["fooBarBar", "fooBahnBaah"]
		result == expected
	}
	
	def "checking common prefixes"() {
		expect:
		servlet.getCommonPrefix(input) == output

		where:
		input                   | output
		["fool","fook","foom"]  | "foo"
		["a"]                   | "a"
		[]                      | ""
		["aaaa","baaa"]         | ""
	}
	
}
