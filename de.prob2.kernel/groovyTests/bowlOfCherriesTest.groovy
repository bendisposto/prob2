import de.prob.animator.domainobjects.*
import de.prob.statespace.*

mm = new ModelModifier("BowlOfCherries",dir)
mm.make {
	
	machine(name: "bowl1") {
		var_block name: "size",
		          invariant: [type_size: "size : NAT"],
				  init: [init_size: "size := 0"]
		invariant inv1: "size <= 50"
		
		event(name: "put") {
			parameter "x"
			guards xdef: "x : 1..5",
			        xconst: "size + x <= 50"
			action put: "size := size + x"
		}
		
		event(name: "take") {
			parameter "x"
			guards xdef: "x : 1..5",
			        xconst: "size - x >= 0"
			action take: "size := size - x"
		}
	}
	
	context(name: "Cherries") {
		enumerated_set name: "cherries",
		               constants: (1..50).collect { it < 10 ? "c0$it" : "c$it" }
	}
	
	machine(name: "bowl2", sees: ["Cherries"], refines: ["bowl1"]) {
		var_block name: "bowl",
		          invariant: [type_bowl: "bowl <: cherries"],
				  init: [act_bowl: "bowl := {}"]
		invariant gluing: "size = card(bowl)"

		refine(name: "put") {
			parameter "handfull"
			guards    define: "handfull <: cherries",
			          constrain: "card(handfull) : 1..5",
					  unique: "handfull /\\ bowl = {}"
			witness   x: "card(handfull) = x"
			action    put: "bowl := bowl \\/ handfull"
		}
			
		refine(name: "take") {
			parameter "handfull"
			guards    define: "handfull <: bowl",
					  constrain: "card(handfull) : 1..5"
			witness   x: "card(handfull) = x"
			action    take: "bowl := bowl \\ handfull"
		}
	}
}

//File dir = mm.writeToRodin("BowlOfCherries",dir)
//dir.deleteDir()

m = mm.getModifiedModel("bowl2")
s = m as StateSpace
t = m as Trace

t = t.randomAnimation(10)


s.animator.cli.shutdown();
"add a description of the test here"