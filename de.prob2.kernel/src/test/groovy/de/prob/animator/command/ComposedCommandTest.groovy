package de.prob.animator.command

import de.prob.parser.ISimplifiedROMap
import de.prob.prolog.output.IPrologTermOutput
import de.prob.prolog.term.PrologTerm

import spock.lang.Specification 

class ComposedCommandTest extends Specification {
	private static final char[] LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()

	def "ComposedCommand created from varargs is written correctly to Prolog"() {
		given:
		final AbstractCommand foo = Mock()
		final AbstractCommand bar = Mock()
		final cmd = new ComposedCommand(foo, bar)

		when:
		cmd.writeCommand(Mock(IPrologTermOutput.class))

		then:
		1 * foo.writeCommand(_)
		1 * bar.writeCommand(_)
	}

	def "ComposedCommand created from List is written correctly to Prolog"() {
		given:
		final AbstractCommand foo = Mock()
		final AbstractCommand bar = Mock()
		final cmd = new ComposedCommand([foo, bar])

		when:
		cmd.writeCommand(Mock(IPrologTermOutput.class))

		then:
		1 * foo.writeCommand(_)
		1 * bar.writeCommand(_)
	}

	def "the results of subcommands are processed when the ComposedCommand's result is processed"() {
		given:
		final AbstractCommand foo = Mock()
		final AbstractCommand bar = Mock()
		final cmd = new ComposedCommand(foo, bar)
		final ISimplifiedROMap<String, PrologTerm> map = Mock()

		when:
		cmd.processResult(map)

		then:
		1 * foo.processResult(_)
		1 * bar.processResult(_)
	}

	def "ComposedCommand.createPrefix works for i < 26"() {
		given:
		final command = new ComposedCommand()

		when:
		final prefix = command.createPrefix(i)

		then:
		prefix.charAt(0) == c
		prefix.length() == 1

		where:
		c << LETTERS
		i << (0..<LETTERS.length)
	}

	def "ComposedCommand.createPrefix works for i >= 26"() {
		given:
		final command = new ComposedCommand()

		when:
		final prefix = command.createPrefix(i)

		then:
		prefix.charAt(0) == c
		prefix.charAt(1) == '1' as char
		prefix.length() == 2

		where:
		c << LETTERS
		i << (LETTERS.length..<LETTERS.length*2)
	}
	
	def "getting the result of an unknown command throws an exception"() {
		given:
		final AbstractCommand foo = Mock()
		final AbstractCommand bar = Mock()
		final cmd = new ComposedCommand(foo, bar)

		final AbstractCommand baz = Mock()
		final ISimplifiedROMap<String, PrologTerm> map = Mock()

		when:
		cmd.writeCommand(Mock(IPrologTermOutput.class))
		cmd.getResultForCommand(baz, map)

		then:
		thrown(IllegalArgumentException)
	}
}
