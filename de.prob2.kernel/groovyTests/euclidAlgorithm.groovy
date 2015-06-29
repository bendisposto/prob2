import de.prob.animator.domainobjects.*
import de.prob.model.eventb.ModelModifier
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
 * 
 */

mm = new ModelModifier()
//mm.startProB = false
mm.make {
	
	context(name: "definitions") {
		constants "Divides", "IsGCD"
		
		axioms "Divides = {i|->j | #k.(k:0..j & j = i*k)}",
		       """IsGCD = {i↦j↦k ∣ i↦j ∈ Divides ∧ i↦k ∈ Divides ∧
			   (∀r· r ∈ (0‥j ∪ 0‥k)
			   ⇒  (r↦j ∈ Divides ∧ r↦k ∈ Divides ⇒ r↦i ∈ Divides)
			   )
			   }"""
		}
	
	context(name: "limits") {
		constants "m", "n", "k"
		axioms "m : 1..k",
		       "n : 1..k",
			   "k = 100"
	}
	
	machine(name: "euclid") { //, sees: ["definitions"]) { //, sees: ["limits"]) {
		var_block name: "u", invariant: "u : 1..100", init: "u := 50"
		var_block name: "v", invariant: "v : 1..100", init: "v := 20"
		
		algorithm {
			While("u /= 0") {
				If("u < v") {
					Then("u := v", "v := u")
				}
				Assign("u := u - v")
			}
			Assert("TRUE = TRUE")//"v|->50|->20 : IsGCD")
		}
	}
}

def guards(evt) {
	evt.guards.collect { it.getPredicate().getCode() }
}

def actions(evt) {
	evt.actions.collect { it.getCode().getCode() }
}

m = mm.getModifiedModel("euclid")

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
//assert guards(evt5) == ["pc = 5", "v|->50|->20 : IsGCD"]//"TRUE = TRUE"] 
//assert evt5.guards[1].isTheorem()
assert actions(evt5) == ["pc := 6"]

evt6 = e.events.evt6
assert evt6 != null
assert guards(evt6) == ["pc = 6"]
assert actions(evt6) == []

s = m as StateSpace
t = s as Trace
t = t.$initialise_machine()
println t.getNextTransitions()
t = t.evt0_enter_while()

//t = t.randomAnimation(10)

mtx = new ModelToXML()
d = mtx.writeToRodin(m, "Euclid", "/tmp")
//d.deleteDir()

//s.animator.cli.shutdown();
"generate and animate a model"