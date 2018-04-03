import de.prob.model.eventb.ModelModifier
import de.prob.statespace.Trace

final mm = new ModelModifier().make {
	machine(name: "bowl1") {
		var name: "size",
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
			constants: (1..50).collect {it < 10 ? "c0$it" : "c$it"}
	}
	
	machine(name: "bowl2", sees: ["Cherries"], refines: "bowl1") {
		var name: "bowl",
			invariant: "bowl <: cherries",
			init: "bowl := {}"
		invariant gluing: "size = card(bowl)"
		
		refine(name: "put") {
			parameter "handfull"
			guards "handfull <: cherries",
				"card(handfull) : 1..5",
				"handfull /\\ bowl = {}"
			witness for: "x", with: "card(handfull) = x"
			action "bowl := bowl \\/ handfull"
		}
		
		refine(name: "take") {
			parameter "handfull"
			guards "handfull <: bowl",
				"card(handfull) : 1..5"
			witness "x", "card(handfull) = x"
			action "bowl := bowl \\ handfull"
		}
	}
}

final m = mm.model
final s = m.load(m.bowl2)
def t = s as Trace

t = t.randomAnimation(10)

"generate and animate a model"
