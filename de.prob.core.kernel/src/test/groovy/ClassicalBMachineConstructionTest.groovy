
import spock.lang.Specification

import com.google.inject.Provider

import de.prob.model.StateSpace
import de.prob.model.languages.ClassicalBFactory


class ClassicalBMachineConstructionTest extends Specification{

	def machine;

	Provider<StateSpace> p = new Provider<StateSpace>(){
		def get() {
			return new StateSpace();
		}
	};

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
		ClassicalBFactory f = new ClassicalBFactory(p);
		machine = f.load(testmachine);
	}

	def "test invariant"() {
		expect:
		machine.getInvariant() == "aa : NAT"
	}
}
