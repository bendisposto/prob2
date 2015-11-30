import de.prob.animator.domainobjects.*
import de.prob.model.eventb.ModelModifier
import de.prob.model.eventb.algorithm.AlgorithmGenerationOptions
import de.prob.model.eventb.algorithm.AlgorithmTranslator
import de.prob.model.eventb.translate.*
import de.prob.statespace.*

/*
 * x≔ 10
   while (x > 0)
     x≔ x + 1
     x ≔ x − 2
     variant x
   end
 */

mm = new ModelModifier().make {
	machine(name: "MyLoop") {
		var_block "x", "x : NAT", "x := 10"
		algorithm {
			While("x > 0", invariant: "x >= 0 & x <= 11", variant: "x") {
				Assign("x := x + 1")
				Assign("x := x - 2")
			}
		}
	}
}


m = new AlgorithmTranslator(mm.getModel(), new AlgorithmGenerationOptions().propagateAssertions(true).terminationAnalysis(true)).run()

mtx = new ModelToXML()
d = mtx.writeToRodin(m, "MyLoopProject", "/tmp")

"it is possible to generate models to check the termination of loops"