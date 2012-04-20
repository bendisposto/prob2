import static org.junit.Assert.*
import spock.lang.Ignore
import spock.lang.Specification
import de.be4.classicalb.core.parser.BParser
import de.be4.classicalb.core.parser.exceptions.BException
import de.be4.classicalb.core.parser.node.Start
import de.prob.model.classicalb.ClassicalBMachine;
import de.prob.model.representation.DomBuilder

class SpockDomWalkerTest extends Specification {


	def static serialVersionUID = -9047892808993422222L;

	private Start parse(final String testMachine) throws BException {
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
		machine = new DomBuilder().build(ast)
	}

	@Ignore
	def "testing that variables are handled correctly"() {
		when:
		def r = machine.getVariables().collect { it.getIdentifier() }
		then:
		r == ['aa', 'b', 'Cc']
	}

	@Ignore
	def "testing that the name is handled correctly"() {
		expect:
		machine.name == 'SimplyStructure'
	}

	@Ignore
	def "test if there are any constants"() {
		when:
		def r = machine.getConstants().collect { it.getIdentifier() }
		then:
		r == ['dd', 'e', 'Ff']
	}
}
