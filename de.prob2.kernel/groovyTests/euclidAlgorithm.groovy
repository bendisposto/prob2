import de.prob.model.eventb.ModelModifier
import de.prob.model.eventb.algorithm.AlgorithmGenerationOptions
import de.prob.model.eventb.algorithm.AlgorithmTranslator

final mm = new ModelModifier().make {
	context(name: "definitions") {
		constants "Divides", "GCD"
		
		axioms "Divides = {i|->j | #k.k:0..j & j = i*k}",
			"GCD = {x|->y|->res | res|->x : Divides & res|->y : Divides & (!r. r : (0..x \\/ 0..y) => (r|->x : Divides & r|->y : Divides => r|->res : Divides) ) }",
			"GCD : (NAT ** NAT) +-> NAT",
			"!x,y.x|->x|->y : GCD => x = y",
			"!v.GCD[{0|->v}] = {v}",
			"!v.GCD[{v|->v}] = {v}",
			"!x,y.y-x>0 => GCD[{x|->y}] = GCD[{x|->y-x}]",
			"!x,y.GCD[{x|->y}] = GCD[{y|->x}]"
		theorem "4|->2|->2 : GCD"
		theorem "!x,y.x <= y => GCD[{y|->x}]=GCD[{y-x|->x}]"
		theorem "!x,y.x <= y => GCD[{x|->y}]=GCD[{y-x|->x}]"
	}
	
	procedure(name: "euclid", seen: "definitions") {
		argument "m", "NAT"
		argument "n", "NAT"
		result "res", "ran(GCD)"
		precondition "m|->n : dom(GCD)"
		postcondition "res = GCD(m|->n) "
		
		implementation {
			var "u", "u : NAT", "u := m"
			var "v", "v : NAT", "v := n"
			algorithm {
				Assert("u = m & v = n")
				If("v = 0") {
					Then {
						Assert("GCD(m|->n)=u")
						Return("u")
					}
				}
				While("u /= 0", invariant: "GCD[{m|->n}] = GCD[{u|->v}]", variant: "u + v") {
					If("u < v") {
						Then("u,v := v,u")
					}
					Assert("u >= v")
					Assign("u := u - v")
					Assert("v > 0")
				}
				Assert("GCD(m|->n) = v")
				Return("v")
			}
		}
	}
}

final m1 = new AlgorithmTranslator(mm.model, new AlgorithmGenerationOptions().propagateAssertions(true).terminationAnalysis(true)).run()
final m2 = new AlgorithmTranslator(mm.model, new AlgorithmGenerationOptions().DEFAULT.terminationAnalysis(true)).run()


"generate a model of a multiplication algorithm"
