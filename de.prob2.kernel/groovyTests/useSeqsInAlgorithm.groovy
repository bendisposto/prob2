import java.nio.file.Paths

import de.prob.model.eventb.ModelModifier
import de.prob.model.eventb.algorithm.AlgorithmGenerationOptions
import de.prob.model.eventb.algorithm.AlgorithmTranslator

final workspace = Paths.get(dir, "TheoryExamples").toString()
def m = new ModelModifier().make {
	loadTheories workspace: workspace,
		project: "BasicTheory",
		theories: ["Seq"]
	
	machine(name: "UseSeq") {
		var "s", "s : seq(INT)", "s := emptySeq"
		var "x", "x : NAT", "x := 0"
		invariant "seqSize(s) = x"
		
		algorithm {
			While("x < 10", variant: "10 - x") {
				Assign("s := seqAppend(s,x)")
				Assign("x := x + 1")
			}
		}
	}
}.model
m = new AlgorithmTranslator(m, new AlgorithmGenerationOptions().mergeBranches(true)).run()

"it is possible to load or create and animate Event-B models that use the theory plugin"
