import de.prob.animator.domainobjects.*
import de.prob.model.eventb.ModelModifier
import de.prob.model.eventb.algorithm.AlgorithmTranslator
import de.prob.model.eventb.algorithm.graph.GraphMerge
import de.prob.model.eventb.algorithm.graph.OptimizedGenerationAlgorithm
import de.prob.model.eventb.translate.*
import de.prob.statespace.*


mm = new ModelModifier().make {
	
	machine(name: "multiplication") {
		var_block name: "x", invariant: "x : NAT", init: "x :: NAT1"
		var_block name: "y", invariant: "y : NAT", init: "y :: NAT1"
		var_block name: "product", invariant: "product : NAT", init: "product := 0"
		var_block name: "ctr", invariant: "ctr : NAT", init: "ctr := 0"
		var_block name: "s", invariant: "s : NAT +-> INT", init: "s := {}"
		
		procedure(name: "mult", arguments: ["x", "y"], results: ["product"],
			locals: [x0: "x", y0: "y", p: "product"],
			precondition: "x >= 0 & y >= 0", abstraction: "product := x * y") {
			While("x0 > 0", invariant: "p + (x0*y0) = x*y") {
				If("x0 mod 2 /= 0") {
					Then {
						Assume("x0 / 2 * 2 = x0 - 1")
						Assign("x0 := x0 / 2", "y0 := y0 * 2", "p := p + y0")
					}
					Else("x0 := x0 / 2", "y0 := y0 * 2")
				}
			}
			Assert("p = x * y")
			Return("p")
		}
		
		algorithm {
			While("ctr < 10", invariant: "!i.i : dom(s) => s(i) = i*i") {
				Call("mult", ["ctr", "ctr"], ["product"])
				Assert("product = ctr*ctr")
				Assign("s(ctr) := product","ctr := ctr + 1")
			}
		}
	}
}

m = mm.getModel()
m = new AlgorithmTranslator(m, new OptimizedGenerationAlgorithm([new GraphMerge()])).run()

//mtx = new ModelToXML()
//d = mtx.writeToRodin(m, "ProcMult", "/tmp")
//d.deleteDir()

//s.animator.cli.shutdown();
"generate and animate a model"