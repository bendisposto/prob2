import de.prob.animator.domainobjects.*
import de.prob.model.eventb.ModelModifier
import de.prob.model.eventb.algorithm.AlgorithmTranslator
import de.prob.model.eventb.algorithm.graph.GraphMerge
import de.prob.model.eventb.algorithm.graph.OptimizedGenerationAlgorithm
import de.prob.model.eventb.translate.*
import de.prob.statespace.*


mm = new ModelModifier().make {
	
	machine(name: "multiplication") {
		var_block name: "x", invariant: "x : NAT", init: "x :: NAT"
		var_block name: "y", invariant: "y : NAT", init: "y :: NAT"
		var_block name: "product", invariant: "product : NAT", init: "product := 0"
		var_block name: "ctr", invariant: "ctr : NAT", init: "ctr := 0"
		
		procedure(name: "mult", arguments: [x0: "x", y0: "y"], result: [p: "product"],
			precondition: "x >= 0 & y >= 0", abstraction: "product := x * y") {
			While("x0 > 0", invariant: "product + (x0*y0) = x*y") {
				If("x0 mod 2 /= 0") {
					Then {
						Assume("x0 / 2 * 2 = x0 - 1")
						Assign("x0 := x0 / 2", "y0 := y0 * 2", "p := p + y0")
					}
					Else("x0 := x0 / 2", "y0 := y0 * 2")
				}
			}
			Return("p")
		}
		
		algorithm {
			While("ctr < 10") {
				Call("mult", ["ctr", "x"], ["product"])
				Assert("product = ctr * x")
				Assign("ctr := ctr + 1")
			}
		}
	}
}

m = mm.getModel()
//m = new AlgorithmTranslator(m, new OptimizedGenerationAlgorithm([new GraphMerge()])).run()

//mtx = new ModelToXML()
//d = mtx.writeToRodin(m, "Bauern", "/tmp")
//d.deleteDir()

//s.animator.cli.shutdown();
"generate and animate a model"