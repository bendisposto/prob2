import de.prob.animator.domainobjects.*
import de.prob.model.eventb.ModelModifier
import de.prob.model.eventb.algorithm.AlgorithmTranslator
import de.prob.model.eventb.algorithm.graph.GraphMerge
import de.prob.model.eventb.algorithm.graph.OptimizedGenerationAlgorithm
import de.prob.model.eventb.translate.*
import de.prob.statespace.*


mm = new ModelModifier().make {
	

	
	machine(name: "multiplication") {
		variables "x","y"
		var_block name: "x0", invariant: "x0 : NAT", init: "x0,x :| x0' : NAT1 & x0'=x'"
		var_block name: "y0", invariant: "y0 : NAT", init: "y0,y :| y0' : NAT1 & y0'=y'"
		var_block name: "product", invariant: "product : NAT", init: "product := 0"
		
		invariants "x : NAT", "y : NAT"
		
		algorithm {
			While("x0 > 0", invariant: "product + (x0*y0) = x*y") {
				If("x0 mod 2 /= 0") {
					Then {
						Assume("x0 / 2 * 2 = x0 - 1")
						Assign("x0 := x0 / 2", "y0 := y0 * 2", "product := product + y0")
					}
					Else("x0 := x0 / 2", "y0 := y0 * 2")
				}
			}
			Assert("product = x * y")
		}
	}
}

m = mm.getModel()
m = new AlgorithmTranslator(m, new OptimizedGenerationAlgorithm([new GraphMerge()])).run()

//mtx = new ModelToXML()
//d = mtx.writeToRodin(m, "Bauern", "/tmp")
//d.deleteDir()

//s.animator.cli.shutdown();
"generate and animate a model"