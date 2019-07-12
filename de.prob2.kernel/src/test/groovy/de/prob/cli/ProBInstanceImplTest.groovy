package de.prob.cli

import java.util.concurrent.atomic.AtomicInteger

import spock.lang.Specification 

class ProBInstanceImplTest extends Specification {
	def testCliShutdown() {
		given:
		final Process process = Mock()
		final BufferedReader reader = new BufferedReader(new StringReader(""))
		final userInterruptRef = 1234L
		final ProBConnection connection = Mock()
		final OsSpecificInfo osinfo = Mock()
		final ProBInstance cli = new ProBInstance(process, reader, userInterruptRef, connection, "", osinfo, new AtomicInteger())

		expect:
		!cli.shuttingDown

		when:
		cli.shutdown()

		then:
		cli.shuttingDown
		1 * connection.disconnect()
	}
}
