
import spock.lang.Specification

import com.google.inject.Guice

import de.prob.MainModule
import de.prob.scripting.FactoryProvider


class ClassicalBMachineConstructionTest extends Specification{

	def machine;

	def setup() {
		String testmachine = """
		  MACHINE SimplyStructure
		  VARIABLES aa, b, Cc
		  INVARIANT aa : NAT
		  INITIALISATION aa:=1
		  CONSTANTS dd, e, Ff
		  PROPERTIES dd : NAT
		  SETS GGG; Hhh; JJ = {dada, dudu, TUTUT}; iII; kkk = {LLL}
		  END
		"""
		def FactoryProvider fp   = Guice.createInjector(new MainModule()).getInstance(FactoryProvider.class)
		machine = fp.getClassicalBFactory().load(testmachine);
	}

	def "test invariant"() {
		expect:
		machine.getInvariant() == "aa : NAT"
	}
}
