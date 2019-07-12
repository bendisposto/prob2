package de.prob.cli

import org.slf4j.Logger

import spock.lang.Specification 

class ConsoleListenerTest extends Specification {
	def "multiple lines are read and logged"() {
		given:
		final ProBInstance proBInstance = Mock()
		final reader = new BufferedReader(new StringReader("foo\nbar"))
		final Logger logger = Mock()
		final listener = new ConsoleListener(proBInstance, reader, logger)

		when:
		final line1 = listener.readAndLog()
		final line2 = listener.readAndLog()

		then:
		line1 == "foo"
		1 * logger.info("{}\u001b[0m", "foo")
		line2 == "bar"
		1 * logger.info("{}\u001b[0m", "bar")

		cleanup:
		reader.close()
	}

	def "no more lines are logged once the ProBInstance is shutting down"() {
		given:
		final ProBInstance proBInstance = Mock() {
			isShuttingDown() >>> [false, false, true]
		}
		final reader = new BufferedReader(new StringReader("foo\nbar\ngoo"))
		final Logger logger = Mock()
		final listener = new ConsoleListener(proBInstance, reader, logger)

		when:
		listener.logLines()

		then:
		1 * logger.info("{}\u001b[0m", "foo")
		1 * logger.info("{}\u001b[0m", "bar")
		0 * logger._

		cleanup:
		reader.close()
	}

	def "a single line is read and logged"() {
		given:
		final ProBInstance proBInstance = Mock()
		final reader = new BufferedReader(new StringReader("foo"))
		final Logger logger = Mock()
		final listener = new ConsoleListener(proBInstance, reader, logger)

		when:
		final line = listener.readAndLog()

		then:
		line == "foo"
		1 * logger.info("{}\u001b[0m", "foo")

		cleanup:
		reader.close()
	}

	def "nothing is logged if the ProBInstance is shutting down"() {
		given:
		final ProBInstance proBInstance = Mock() {
			isShuttingDown() >> true
		}
		final reader = new BufferedReader(new StringReader("foo"))
		final Logger logger = Mock()
		final listener = new ConsoleListener(proBInstance, reader, logger)

		when:
		listener.logLines()

		then:
		0 * logger._

		cleanup:
		reader.close()
	}

	def "reading when the ProBInstance is shutting down returns null and logs nothing"() {
		given:
		final ProBInstance proBInstance = Mock() {
			isShuttingDown() >> true
		}
		final reader = new BufferedReader(new StringReader(""))
		final Logger logger = Mock()
		final listener = new ConsoleListener(proBInstance, reader, logger)

		when:
		final line = listener.readAndLog()

		then:
		line == null
		0 * logger._

		cleanup:
		reader.close()
	}
}
