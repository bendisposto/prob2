import de.prob.animator.domainobjects.*
import de.prob.statespace.*
import de.prob.model.eventb.translate.*

mm = new ModelModifier()
mm.make {
	
	machine(name: "bowl1") {
		var_block name: "size",
		          invariant: "size : NAT",
				  init: "size := 0"
		invariant "size <= 50"
		
		event(name: "put") {
			parameter "x"
			guards "x : 1..5",
			       "size + x <= 50"
			action "size := size + x"
		}
		
		event(name: "take") {
			parameter "x"
			guards "x : 1..5",
			       "size - x >= 0"
			action "size := size - x"
		}
	}
	
	context(name: "Cherries") {
		enumerated_set name: "cherries",
		               constants: (1..50).collect { it < 10 ? "c0$it" : "c$it" }
	}
	
	machine(name: "bowl2", sees: ["Cherries"], refines: ["bowl1"]) {
		var_block name: "bowl",
		          invariant: "bowl <: cherries",
				  init: "bowl := {}"
		invariant gluing: "size = card(bowl)"

		refine(name: "put") {
			parameter "handfull"
			guards    "handfull <: cherries",
			          "card(handfull) : 1..5",
					 "handfull /\\ bowl = {}"
			witness   for: "x", with: "card(handfull) = x"
			action    "bowl := bowl \\/ handfull"
		}
			
		refine(name: "take") {
			parameter "handfull"
			guards    "handfull <: bowl",
					  "card(handfull) : 1..5"
			witness   "x", "card(handfull) = x"
			action    "bowl := bowl \\ handfull"
		}
	}
}

m = mm.getModifiedModel("bowl2")
s = m as StateSpace
t = m as Trace

t = t.randomAnimation(10)

//mtx = new ModelToXML()
//d = mtx.writeToRodin(m, "BowlOfCherries", dir)
//d.deleteDir()

s.animator.cli.shutdown();
"generate and animate a model"