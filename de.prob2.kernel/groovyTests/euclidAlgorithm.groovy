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
		       "GCD = {x|->y|->res | res|->x : Divides & res|->y : Divides & (!r. r : (0..x \\/ 0..y) => (r|->x : Divides & r|->y : Divides => r|->res : Divides) ) }",
			   "∀x,y·x↦x↦y ∈ GCD ⇒ x = y",
			   "∀v·GCD[{0↦v}] = {v}",
			   "∀v·GCD[{v↦v}] = {v}",
			   "∀x,y·y−x>0 ⇒ GCD[{x↦y}] = GCD[{x↦y−x}]",
			   "∀x,y·GCD[{x↦y}] = GCD[{y↦x}]"
	    theorem "!x,y.x <= y => GCD[{y|->x}]=GCD[{y-x|->x}]"
		theorem "!x,y.x <= y => GCD[{x|->y}]=GCD[{y-x|->x}]"

	}
	
	context(name: "limits") {
		constants "m", "n", "k"
		axioms "m : 0..k",
		       "n : 0..k",
			   "k : NAT"
			  // "k = 100",
			  // "m = 50",
			  // "n = 20"
	}
	
	machine(name: "euclid", sees: ["definitions", "limits"]) {
		var name: "u", invariant: "u : 0..k", init: "u := m"
		var name: "v", invariant: "v : 0..k", init: "v := n"
		
		algorithm {
			While("u /= 0", invariant: "GCD[{m|->n}] = GCD[{u|->v}]") {
				If("u < v") {
					Then("u,v := v,u")
				}
				Assert("u >= v")
				Assign("u := u - v")
			}
			Assert("m|->n|->v : GCD")
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
m = new AlgorithmTranslator(m, new AlgorithmGenerationOptions().DEFAULT).run()


mtx = new ModelToXML()
//d = mtx.writeToRodin(m, "Euclid", "/tmp")
//d.deleteDir()

"generate and animate a model"