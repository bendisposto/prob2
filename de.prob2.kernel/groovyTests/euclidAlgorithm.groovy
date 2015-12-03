import de.prob.animator.domainobjects.*
import de.prob.model.eventb.ModelModifier
import de.prob.model.eventb.algorithm.AlgorithmGenerationOptions
import de.prob.model.eventb.algorithm.AlgorithmTranslator
import de.prob.model.eventb.translate.*
import de.prob.statespace.*

/*
context Context

constants Divides IsGCD

axioms
  @axm Divides = {i↦j ∣ ∃k·k∈0‥j ∧ j = i∗k}
  @axm2 IsGCD = {i↦j↦k ∣ i↦j ∈ Divides ∧ i↦k ∈ Divides ∧
        (∀r· r ∈ (0‥j ∪ 0‥k)
        ⇒  (r↦j ∈ Divides ∧ r↦k ∈ Divides ⇒ r↦i ∈ Divides)
        )
        }
  @axm3 (3↦9↦6) ∈ IsGCD
  @axm4 (5↦15↦25) ∈ IsGCD
end

Further axioms:
			   "!x,y.x|->x|->y : GCD => x = y",
			   "!v.GCD[{v|->v}] = {v}",
			   "!x,y.y-x>0 => GCD[{x|->y}] = GCD[{x|->y-x}]",
			   "!x,y.GCD[{x|->y}] = GCD[{y|->x}]"
 * 
 */

mm = new ModelModifier().make {
	
	context(name: "definitions") {
		constants "Divides", "GCD"
		
		axioms "Divides = {i|->j | #k.k:0..j & j = i*k}",
		       "GCD = {x|->y|->res | res|->x : Divides & res|->y : Divides & (!r. r : (0..x \\/ 0..y) => (r|->x : Divides & r|->y : Divides => r|->res : Divides) ) }"

	}
	
	context(name: "limits") {
		constants "m", "n", "k"
		axioms "m : 1..k",
		       "n : 1..k",
			   "k = 100",
			   "m = 50",
			   "n = 20"
	}
	
	machine(name: "euclid", sees: ["definitions", "limits"]) {
		var name: "u", invariant: "u : 0..k", init: "u := m"
		var name: "v", invariant: "v : 0..k", init: "v := n"
		invariant "GCD[{m|->n}] = GCD[{u|->v}]"
		
		algorithm {
			While("u /= v", variant: "u + v") {
				If("u < v") {
					Then("v := v - u")
					Else("u := u - v")
				}
			}
			Assert("m|->n|->v : GCD")//"TRUE = TRUE")
		}
	}
}

def guards(evt) {
	evt.guards.collect { it.getPredicate().getCode() }
}

def actions(evt) {
	evt.actions.collect { it.getCode().getCode() }
}

m = mm.getModel()
m = new AlgorithmTranslator(m, new AlgorithmGenerationOptions()).run()

//m = new AlgorithmTranslator(m, new NaiveAlgorithmPrototype()).run()
//m = new NaiveTerminationAnalysis(m).run()

/*
e = m.euclid

evt0_enter_while = e.events.evt0_enter_while
assert evt0_enter_while != null
assert guards(evt0_enter_while) == ["pc = 0", "u /= 0"]
assert actions(evt0_enter_while) == ["pc := 1"]

evt0_exit_while = e.events.evt0_exit_while
assert evt0_exit_while != null
assert guards(evt0_exit_while) == ["pc = 0", "not(u /= 0)"]
assert actions(evt0_exit_while) == ["pc := 5"]

evt1_if = e.events.evt1_if
assert evt1_if != null
assert guards(evt1_if) == ["pc = 1", "u < v"]
assert actions(evt1_if) == ["pc := 2"]

evt2 = e.events.evt2
assert evt2 != null
assert guards(evt2) == ["pc = 2"]
assert actions(evt2) == ["pc := 3", "u := v", "v := u"]

evt3 = e.events.evt3
assert evt3 != null
assert guards(evt3) == ["pc = 3"]
assert actions(evt3) == ["pc := 4", "u := u - v"]

evt4_loop = e.events.evt4_loop
assert evt4_loop != null
assert guards(evt4_loop) == ["pc = 4"]
assert actions(evt4_loop) == ["pc := 0"]

evt5 = e.events.evt5
assert evt5 != null
assert guards(evt5) == ["pc = 5", "v|->m|->n : IsGCD"]//"TRUE = TRUE"] 
assert evt5.guards[1].isTheorem()
assert actions(evt5) == ["pc := 6"]

evt6 = e.events.evt6
assert evt6 != null
assert guards(evt6) == ["pc = 6"]
assert actions(evt6) == []

//m = api.eventb_load("/tmp/Euclid/euclid.bcm")
s = m.load(m.euclid)
t = s as Trace
t = t.$setup_constants().$initialise_machine()
//t = t.$initialise_machine()
t = t.evt0_enter_while()
assert t.evalCurrent("u").value == "50" && t.evalCurrent("v").value == "20"
t = t.evt1_else()
assert t.evalCurrent("u").value == "50" && t.evalCurrent("v").value == "20"
t = t.evt3()
assert t.evalCurrent("u").value == "30" && t.evalCurrent("v").value == "20"
t = t.evt4_loop()
assert t.evalCurrent("u").value == "30" && t.evalCurrent("v").value == "20"
t = t.evt0_enter_while()
assert t.evalCurrent("u").value == "30" && t.evalCurrent("v").value == "20"
t = t.evt1_else()
assert t.evalCurrent("u").value == "30" && t.evalCurrent("v").value == "20"
t = t.evt3()
assert t.evalCurrent("u").value == "10" && t.evalCurrent("v").value == "20"
t = t.evt4_loop()
assert t.evalCurrent("u").value == "10" && t.evalCurrent("v").value == "20"
t = t.evt0_enter_while()
assert t.evalCurrent("u").value == "10" && t.evalCurrent("v").value == "20"
t = t.evt1_if()
assert t.evalCurrent("u").value == "10" && t.evalCurrent("v").value == "20"
t = t.evt2()
assert t.evalCurrent("u").value == "20" && t.evalCurrent("v").value == "10"
t = t.evt3()
assert t.evalCurrent("u").value == "10" && t.evalCurrent("v").value == "10"
t = t.evt4_loop()
assert t.evalCurrent("u").value == "10" && t.evalCurrent("v").value == "10"
t = t.evt0_enter_while()
assert t.evalCurrent("u").value == "10" && t.evalCurrent("v").value == "10"
t = t.evt1_else()
assert t.evalCurrent("u").value == "10" && t.evalCurrent("v").value == "10"
t = t.evt3()
assert t.evalCurrent("u").value == "0" && t.evalCurrent("v").value == "10"
t = t.evt4_loop()
assert t.evalCurrent("u").value == "0" && t.evalCurrent("v").value == "10"
t = t.evt0_exit_while()
assert t.evalCurrent("u").value == "0" && t.evalCurrent("v").value == "10"
assert t.evalCurrent("v|->m|->n : IsGCD").value == "TRUE"
t = t.evt5()
assert t.evalCurrent("u").value == "0" && t.evalCurrent("v").value == "10"
*/
//mtx = new ModelToXML()
//d = mtx.writeToRodin(m, "Euclid", "/tmp")
//d.deleteDir()

"generate and animate a model"