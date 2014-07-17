import de.prob.animator.domainobjects.*
import de.prob.statespace.*
import de.prob.animator.command.*
import de.prob.prolog.term.*

// Groovy script example for checking CSP assertion:

m = api.csp_load("/home/joy/code/prob_examples/public_examples/CSP/other/Ivo/Deterministic1.csp")
t = m as Trace
s = t as StateSpace 
x = new CSP("assert not NonDeterm3 :[ deterministic [F] ]",m) 
y = new CSP("assert not NDet :[deterministic [FD]]",m)
z = new CSP("assert not NDet1 :[deterministic [F]]",m)

command = new CSPAssertionsCommand([x,y,z])
s.execute(command) 
res = command.getResults() // getting the list of results
assert !res.isEmpty()
assert res.each { it == "true" }
traces = command.getResultTraces() // getting the list of possible counter example
assert !traces.isEmpty()
traces.each { assert it instanceof ListPrologTerm; assert !it.isEmpty() }


s.animator.cli.shutdown();
"csp assertions tested correctly"