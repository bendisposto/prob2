package de.prob.statespace


import static org.junit.Assert.*
import static org.mockito.Mockito.*
import spock.lang.Specification
import de.prob.Main
import de.prob.animator.domainobjects.AbstractEvalResult
import de.prob.animator.domainobjects.CSP
import de.prob.animator.domainobjects.ClassicalB
import de.prob.animator.domainobjects.IEvalElement
import de.prob.model.representation.CSPModel
import de.prob.scripting.ClassicalBFactory


class StateSpaceEvaluationTest extends Specification {

	static StateSpace s
	static State root
	static State firstState

	def setupSpec() {
		def path = System.getProperties().get("user.dir")+"/groovyTests/machines/scheduler.mch"
		ClassicalBFactory factory = Main.getInjector().getInstance(ClassicalBFactory.class)
		s = factory.extract(path).load([:])
		root = s.getRoot()
		firstState = root.$initialise_machine()
	}

	def setup() {
		s.formulaRegistry.clear()
		s.subscribedFormulas.clear()
	}

	def "it is possible to evaluate formulas in a state"() {
		expect:
		s.eval(firstState, [
			new ClassicalB("waiting"),
			new ClassicalB("ready")
		]).collect { it.getValue() } == ["{}", "{}"]
	}

	def "it is possible for someone to subscribe to a formula"() {
		when:
		def formula = new ClassicalB("waiting /\\ ready")
		boolean before = s.formulaRegistry.containsKey(formula)
		def subscriber = "I am a subscriber!"
		def success = s.subscribe(subscriber, formula)

		then:
		!before
		success
		s.formulaRegistry.containsKey(formula)
		s.formulaRegistry[formula].containsKey(subscriber)
		s.subscribedFormulas.contains(formula)
	}

	def "it is possible for multiple people to subscribe to the same formula"() {
		when:
		def formula = new ClassicalB("waiting \\/ ready")
		boolean before = s.formulaRegistry.containsKey(formula)
		def subscriber1 = "I am a subscriber!"
		def subscriber2 = "I am also a subscriber!"
		def success = s.subscribe(subscriber1, formula)
		def success2 = s.subscribe(subscriber2, formula)

		then:
		!before // it didn't have it before
		success
		success2
		s.formulaRegistry.containsKey(formula)
		s.formulaRegistry[formula].containsKey(subscriber1)
		s.formulaRegistry[formula].containsKey(subscriber2)
		s.subscribedFormulas.contains(formula)
	}

	def "csp formulas cannot be subscribed"() {
		when:
		CSPModel m = new CSPModel(null);
		m.init("some content", new File("somedir"+File.separator+"someotherdir"+File.separator+"myfile.csp"))
		CSP csp = new CSP("some formula", m)
		def subscriber1 = "subscriber1"
		def success = s.subscribe(subscriber1, csp)

		then:
		!success
		!s.formulaRegistry.containsKey(csp)
		!s.subscribedFormulas.contains(csp)
	}

	def "it is possible for someone to subscribe to multiple formulas"() {
		when:
		def formula = new ClassicalB("card(waiting)")
		def formula2 = new ClassicalB("card(ready)")
		boolean before = s.formulaRegistry.containsKey(formula)
		boolean before2 = s.formulaRegistry.containsKey(formula2)
		def subscriber = "I am a subscriber!"
		def success = s.subscribe(subscriber, [formula, formula2])

		then:
		!before
		!before2
		success
		s.formulaRegistry.containsKey(formula)
		s.formulaRegistry[formula].containsKey(subscriber)
		s.subscribedFormulas.contains(formula)
		s.formulaRegistry.containsKey(formula2)
		s.formulaRegistry[formula2].containsKey(subscriber)
		s.subscribedFormulas.contains(formula2)
	}

	def "it is possible for multiple people to subscribe to the same multiple formulas"() {
		when:
		def formula = new ClassicalB("card(ready)+card(waiting)")
		def formula2 = new ClassicalB("card(active)")
		boolean before = s.formulaRegistry.containsKey(formula)
		boolean before2 = s.formulaRegistry.containsKey(formula2)
		def subscriber1 = "I am a subscriber!"
		def subscriber2 = "I am also a subscriber!"
		def success = s.subscribe(subscriber1, [formula, formula2])
		def success2 = s.subscribe(subscriber2, [formula, formula2])

		then:
		!before
		!before2
		success
		success2
		s.formulaRegistry.containsKey(formula)
		s.formulaRegistry[formula].containsKey(subscriber1)
		s.formulaRegistry[formula].containsKey(subscriber2)
		s.subscribedFormulas.contains(formula)

		s.formulaRegistry.containsKey(formula2)
		s.formulaRegistry[formula2].containsKey(subscriber1)
		s.formulaRegistry[formula2].containsKey(subscriber2)
		s.subscribedFormulas.contains(formula2)
	}

	def "multiple csp formulas cannot be subscribed"() {
		when:
		CSPModel m = new CSPModel(null);
		m.init("some content", new File("somedir"+File.separator+"someotherdir"+File.separator+"myfile.csp"))
		CSP csp = new CSP("some formula", m)
		CSP csp2 = new CSP("some formula2", m)
		def subscriber1 = "subscriber1"
		def success = s.subscribe(subscriber1, [csp, csp2])

		then:
		!success
		!s.formulaRegistry.containsKey(csp)
		!s.subscribedFormulas.contains(csp)
		!s.formulaRegistry.containsKey(csp2)
		!s.subscribedFormulas.contains(csp2)
	}

	def "formulas should not be evaluated in the root state"() {
		expect: !s.canBeEvaluated(root)
	}

	def "after subscribing a formula, its values can be retrieved using valuesAt"() {
		when:
		def formula = new ClassicalB("card(waiting) + 1")
		s.subscribe("mmm",formula)
		def values = s.valuesAt(firstState)

		then:
		values.containsKey(formula)
		values[formula].getValue() == "1"
	}

	class DummyObject {
		def field = "I don't do much!"
	}

	def "the garbage collector should automatically remove subscribers if their references no longer exist"() {
		when:
		def subscriber = new DummyObject()
		def formula = new ClassicalB('card(ready) + 1')
		def success = s.subscribe(subscriber, formula)
		def before = s.formulaRegistry.containsKey(formula)
		def before2 = s.formulaRegistry[formula].containsKey(subscriber)
		subscriber = null
		System.gc()

		then:
		success
		before
		before2
		s.formulaRegistry.containsKey(formula)
		!s.formulaRegistry[formula].containsKey(subscriber)
	}

	def "a formula that has not yet been subscribed should be recognized as subscribed"() {
		expect: !s.isSubscribed(new ClassicalB("card(waiting)+10"))
	}

	def "if there are no longer any subscribers who are interested in a formula, it is recognized as subscribed"() {
		when:
		def subscriber = new DummyObject()
		def formula = new ClassicalB('card(ready) + 77')
		def success = s.subscribe(subscriber, formula)
		def before = s.isSubscribed(formula)
		s.formulaRegistry[formula].remove(subscriber) // this will happen at some point if the subscriber is cleaned up by the garbage collector

		then:
		success
		before
		!s.isSubscribed(formula)
	}

	def "it is possible to unsubscribe a formula after subscribing it"() {
		when:
		def subscriber = "I'm a subscriber!"
		def formula = new ClassicalB("card(waiting) + 5")
		def success = s.subscribe(subscriber, formula)
		def before = s.isSubscribed(formula)
		def success2 = s.unsubscribe(subscriber, formula)

		then:
		success
		success2
		!s.isSubscribed(formula)
	}

	def "it is possible for someone to unsubscribe even if someone else is still subscribed"() {
		when:
		def subscriber = "hi!"
		def subscriber2 = "hi again!"
		def formula = new ClassicalB("card(ready) + card(active) + 7")
		def success = s.subscribe(subscriber, formula)
		def success2 = s.subscribe(subscriber2, formula)

		then:
		success
		success2
		s.unsubscribe(subscriber, formula)
	}

	def "it is not possible to unsubscribe a formula that is not subscribed (nothing will happen)"() {
		expect:
		!s.unsubscribe("I'm not a subscriber", new ClassicalB("1+24"))
	}

	def "getting subscribed formulas also removes formulas that aren't there any more"() {
		when:
		def subscriber = "hi!"
		def formula = new ClassicalB("card(active) + 9")
		def success = s.subscribe(subscriber, formula)
		def before = s.getSubscribedFormulas() == [formula] as Set
		s.formulaRegistry.remove(formula)

		then:
		success
		before
		!s.getSubscribedFormulas().contains(formula)
	}

	def "getting subscribed formulas also removes formulas that aren't there any more 2"() {
		when:
		def subscriber = "hi!"
		def formula = new ClassicalB("card(active) + 10")
		def success = s.subscribe(subscriber, formula)
		def before = s.getSubscribedFormulas() == [formula] as Set
		s.formulaRegistry[formula].remove(subscriber)

		then:
		success
		before
		!s.getSubscribedFormulas().contains(formula)
	}

	def "it is possible to evaluate multiple formulas in multiple states"() {
		when:
		def waiting = new ClassicalB("waiting")
		def ready = new ClassicalB("ready")
		def active = new ClassicalB("active")
		def state2 = firstState.new("pp=PID1")
		def state3 = firstState.new("pp=PID2")
		def state4 = firstState.new("pp=PID3")
		def states = [
			root,
			firstState,
			state2,
			state3,
			state4
		]
		s.subscribe("I'm a subscriber!",[ready])
		Map<State, Map<IEvalElement, AbstractEvalResult>> result = s.evaluateForGivenStates(states, [waiting, ready, active])

		then:
		result[root] == null // ignored because it is not initialised
		def statesWOroot = states.findAll { it != root}
		statesWOroot.collect { result[it][ready].getValue() }.inject(true) {acc, i -> acc && i == "{}"}
		statesWOroot.collect { result[it][active].getValue() }.inject(true) {acc, i -> acc && i == "{}"}
		result[firstState][waiting].getValue() == "{}"
		result[state2][waiting].getValue() == "{PID1}"
		result[state3][waiting].getValue() == "{PID2}"
		result[state4][waiting].getValue() == "{PID3}"
	}
}
