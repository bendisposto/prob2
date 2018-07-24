import de.prob.model.eventb.ModelModifier
import de.prob.model.eventb.algorithm.AlgorithmGenerationOptions
import de.prob.model.eventb.algorithm.AlgorithmTranslator

final mm = new ModelModifier().make {
	machine(name: "MyLoop") {
		var "x", "x : NAT", "x := 10"
		algorithm {
			While("x > 0", invariant: "x >= 0 & x <= 11", variant: "x") {
				Assign("x := x + 1")
				Assign("x := x - 2")
			}
		}
	}
}

final m = new AlgorithmTranslator(mm.model, new AlgorithmGenerationOptions().propagateAssertions(true).terminationAnalysis(true)).run()

"it is possible to generate models to check the termination of loops"
