import de.prob.model.eventb.ModelModifier
import de.prob.model.eventb.algorithm.AlgorithmGenerationOptions
import de.prob.model.eventb.algorithm.AlgorithmTranslator

final mm = new ModelModifier().make {
	machine(name: "multiplication") {
		variables "x", "y"
		var name: "x0", invariant: "x0 : NAT", init: "x0,x :| x0' : NAT1 & x0'=x'"
		var name: "y0", invariant: "y0 : NAT", init: "y0,y :| y0' : NAT1 & y0'=y'"
		var name: "product", invariant: "product : NAT", init: "product := 0"
		
		invariants "x : NAT", "y : NAT"
		theorem "x0 / 2 * 2 = x0 - 1"
		algorithm {
			While("x0 > 0", invariant: "product + (x0*y0) = x*y") {
				If("x0 mod 2 /= 0") {
					Then {
						Assign("x0,y0,product := x0 / 2, y0 * 2, product + y0")
					}
					Else("x0,y0 := x0 / 2, y0 * 2")
				}
			}
			Assert("product = x * y")
		}
	}
	
	machine(name: "multiplication2") {
		variables "x", "y"
		var name: "x0", invariant: "x0 : NAT", init: "x0,x :| x0' : NAT1 & x0'=x'"
		var name: "y0", invariant: "y0 : NAT", init: "y0,y :| y0' : NAT1 & y0'=y'"
		var name: "product", invariant: "product : NAT", init: "product := 0"
		
		invariants "x : NAT", "y : NAT"
		theorem "x0 mod 2 /= 0 => (x0 / 2) * y0 * 2 = (x0 - 1) * y0"
		theorem "x mod 2 /= 0 => x / 2 * 2 = x - 1"
		theorem "x / 2*y*2 = x / 2*2*y"
		algorithm {
			While("x0 > 0", invariant: "product + (x0*y0) = x*y") {
				If("x0 mod 2 /= 0") {
					Then {
						Assign("product := product + y0")
					}
				}
				Assign("x0,y0 := x0 / 2, y0 * 2")
			}
			Assert("product = x * y")
		}
	}
}

final m = new AlgorithmTranslator(mm.model, new AlgorithmGenerationOptions().optimize(true).propagateAssertions(true)).run()

"generate and animate a model"
