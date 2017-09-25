import de.prob.animator.domainobjects.*
import de.prob.model.eventb.ModelModifier
import de.prob.model.eventb.algorithm.AlgorithmGenerationOptions
import de.prob.model.eventb.algorithm.AlgorithmTranslator
import de.prob.model.eventb.translate.*
import de.prob.statespace.*


final workspace = dir + File.separator + "TheoryExamples"
mm = new ModelModifier().make {
	
	procedure(name: "mult") {
		argument "x", "NAT"
		argument "y", "NAT"
		result "product", "NAT"
		
		precondition "x >= 0 & y >= 0"
		postcondition "product = x * y"
			
		implementation {
			var "x0", "x0 : NAT", "x0 := x"
			var "res", "res : NAT", "res := 0"
			algorithm {
				While("x0 > 0", invariant: "res = (x - x0) * y") {
					Assert("x0 > 0")
					Assign("x0 := x0 - 1")
					Assign("res := res + y")
				}
				Assert("res = x * y")
				Return("res")
			}
		}
	}
}

m = mm.getModel()
m = new AlgorithmTranslator(m, new AlgorithmGenerationOptions().propagateAssertions(true)).run()

"generate and animate a model"