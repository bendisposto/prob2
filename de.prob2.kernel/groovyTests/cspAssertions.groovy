import java.nio.file.Paths

import de.prob.animator.command.CSPAssertionsCommand
import de.prob.animator.domainobjects.CSP
import de.prob.model.representation.CSPModel
import de.prob.prolog.term.ListPrologTerm
import de.prob.statespace.Trace

// Groovy script example for checking CSP assertion:

final s = api.csp_load(Paths.get(dir, "machines", "csp", "Deterministic1.csp").toString())
final t = s as Trace
final m = t as CSPModel
final x = new CSP("assert not NonDeterm3 :[ deterministic [F] ]", m)
final y = new CSP("assert not NDet :[deterministic [FD]]", m)
final z = new CSP("assert not NDet1 :[deterministic [F]]", m)

final command = new CSPAssertionsCommand([x, y, z])
s.execute(command) 
final res1 = command.results // getting the list of results
assert !res1.empty
assert res1.each { it == "true" }
final traces = command.resultTraces // getting the list of possible counter example
assert !traces.empty
traces.each {
	assert it instanceof ListPrologTerm
	assert !it.empty
}

// csp evaluation
final res2 = t.evalCurrent(new CSP("NDet", m))
assert res2.value == "(a.1->STOP) [] (a.1->NDet)"

assert m.checkSyntax("NDet")
assert !m.checkSyntax("Foo")

"csp assertions tested correctly"
