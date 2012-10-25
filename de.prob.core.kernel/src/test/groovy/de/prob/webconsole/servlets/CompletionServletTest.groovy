package de.prob.webconsole.servlets;

import static org.junit.Assert.*
import static org.mockito.Mockito.*
import de.prob.webconsole.GroovyExecution;
import de.prob.webconsole.ShellCommands;
import spock.lang.Specification

class CompletionServletTest extends Specification {
	def servlet
	
	def setup() {
		def executorMock = mock(GroovyExecution.class)
		def shellCmdMock = mock(ShellCommands.class)
		Binding b = new Binding()
		b.setVariable("api", null)
		b.setVariable("foo", "baz")
		b.setVariable("foobar", "baz")
		b.setVariable("update", null)
		when(executorMock.getBindings()).thenReturn(b)
		when(executorMock.tryevaluate(anyString())).thenReturn("baz")
		when(shellCmdMock.getSpecialCommands()).thenReturn(["upgrade", "load", "import"] as Set)
		servlet = new CompletionServlet(executorMock, shellCmdMock)
	}

	def "camelCase gets split into correct substrings"() {
		when:
		def result = servlet.camelSplit("fooBar");
		then:
		result == ["foo", "Bar"]
	}

	def "camelCase gets split into correct substrings2"() {
		when:
		def result = servlet.camelSplit("FooBar");
		then:
		result == ["Foo", "Bar"]
	}
	
	def "splitting camelCase works on more than one uppercase occurence"() {
		when:
		def result = servlet.camelSplit("fooBarBazBlubbBla");
		then:
		result == ["foo", "Bar", "Baz", "Blubb", "Bla"]
	}

	def "camelCase splitting works with substrings of length one"() {
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
	
	def "proper underscore handling"() {
		when:
		List<String> completions = ["b_load(", "b_def()"]
		List<String> result = servlet.camelMatch(completions, "b_l")
		then:
		List<String> expected = ["b_load("]
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
	
	def "get special command completion"() {
		expect:
		servlet.getCompletions(col as String, fulltext) as Set == list as Set
				
		where:
		col		| fulltext		| list
		1		| "u"			| ["up"]
		2		| "up"			| ["upgrade ", "update"]
		3		| "upglatest"	| ["upgrade latest"]
		3		| "imp"			| ["import "]
	}
	
	def "completion on bindings"() {
		expect:
		servlet.getCompletions(col as String, fulltext) as Set == list as Set
				
		where:
		col		| fulltext		| list
		2		| "ap"			| ["api"]
		2		| "ap api"		| ["api api"]
		3		| "a a a"		| ["api", "al"]
		6		| "a a ap"		| ["a a api"]
		2		| "fo"			| ["foo"]
		3		| "foo"			| ["foo", "foobar"]
		6		| "api fo"		| ["api foo"]
		7		| "api foo"		| ["foo", "foobar"]
		6		| "api fo api"	| ["api foo api"]
		7		| "api foo api"	| ["foo", "foobar"]
	}
	
	def "method completion"() {
		expect:
		servlet.getCompletions(col as String, fulltext) as Set == list as Set
				
		where:
		col		| fulltext		| list
		5 		| "foo.l"		| ["lastIndexOf(", "leftShift(", "length()"]
		6 		| "foo.la"		| ["foo.lastIndexOf("]
		10 		| "api foo.la 3"| ["api foo.lastIndexOf( 3"]
		6 		| "foo.le"		| ["leftShift(", "length()"]
		6 		| "foo.lS"		| ["foo.leftShift("]
		8 		| "1 foo.tB 3"	| ["1 foo.toB 3"]
		9 		| "1 foo.toB 3"	| ["toBigDecimal()", "toBoolean()", "toBigInteger()"]
		9 		| "1 foo.tBD 3"	| ["1 foo.toBigDecimal() 3"]
		9 		| "1 foo.toBD 3"| ["toBigDecimal()", "toBoolean()", "toBigInteger()"]
		5		| "foo.C"		| ["foo.CASE_INSENSITIVE_ORDER"]
	}
	
	def "split input"() {
		expect:
		servlet.splitInput(text, col - 1) == arr
		
		where:
		text				| col	| arr
		""					| 0 	| ["", "", ""]
		"foo bar baz"		| 2		| ["", "fo", "o bar baz"]
		"foo bar baz"		| 7		| ["foo ", "bar", " baz"]
		"foo bart baz"		| 7		| ["foo ", "bar", "t baz"]
		"foo bar. baz"		| 8		| ["foo ", "bar.", " baz"]
		"foo bar. baz"		| 10	| ["foo bar. ", "b", "az"]
		"foo foo bar. baz"	| 6		| ["foo ", "fo", "o bar. baz"]
		"foo foo bar. baz"	| 10	| ["foo foo ", "ba", "r. baz"]
		"foo bar. baz"		| 12	| ["foo bar. ", "baz", ""]
	}
	
}
