import de.prob.model.eventb.ModelModifier
import de.prob.model.eventb.algorithm.AlgorithmGenerationOptions
import de.prob.model.eventb.algorithm.AlgorithmTranslator

final mm = new ModelModifier().make {
	procedure(name: "mult") {
		argument "x", "NAT"
		argument "y", "NAT"
		result "product", "NAT"
		
		precondition "x >= 0 & y >= 0"
		postcondition "product = x * y"
			
		implementation {
			var "x0", "x0 : NAT", "x0 := x"
			var "y0", "y0 : NAT", "y0 := y"
			var "p", "p : NAT", "p := 0"
			theorem "x0 mod 2 /= 0 => (x0 / 2) * y0 * 2 = (x0 - 1) * y0"
			theorem "x mod 2 /= 0 => x / 2 * 2 = x - 1"
			theorem "x / 2*y*2 = x / 2*2*y"
			theorem "x0 <= 0 => x0 = 0"
			
			//variant "x0"
			
			algorithm {
				While("x0 > 0", invariant: "p + (x0*y0) = x*y", variant: "x0") {
					If("x0 mod 2 /= 0") {
						Then {
							Assign("p := p + y0")
						}
					}
					Assign("x0,y0 := x0 / 2, y0 * 2")
				}
				Assert("p = x*y")
				Return("p")
			}
		}
	}
}

final m = new AlgorithmTranslator(mm.model, new AlgorithmGenerationOptions().propagateAssertions(true).terminationAnalysis(true)).run()

"generate and animate a model"
