import static org.junit.Assert.*
import spock.lang.Specification
import de.be4.classicalb.core.parser.BParser
import de.be4.classicalb.core.parser.exceptions.BCompoundException
import de.be4.classicalb.core.parser.node.Start
import de.prob.model.classicalb.ClassicalBMachine
import de.prob.model.classicalb.DomBuilder

class SpockDomWalkerTest extends Specification {


	def static serialVersionUID = -9047892808993422222L;

	private Start parse(final String testMachine) throws BCompoundException {
		final BParser parser = new BParser("testcase");
		return parser.parse(testMachine, false);
	}

	def ClassicalBMachine machine;

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
		def ast = parse(testmachine)
		machine = new DomBuilder(null).build(ast)
	}

	def "testing that variables are handled correctly"() {
		when:
		def r = machine.variables.collect { it.getName() }
		then:
		r == ['aa', 'b', 'Cc']
	}

	def "testing that the name is handled correctly"() {
		expect:
		machine.name == 'SimplyStructure'
	}

	def "test if there are any constants"() {
		when:
		def r = machine.constants.collect { it.getName() }
		then:
		r == ['dd', 'e', 'Ff']
	}

	def "test if there are any invariants"() {
		when:
		def r = machine.invariants.collect { it.getPredicate().getCode() }
		then:
		r == ["aa:NAT"]
	}
}
