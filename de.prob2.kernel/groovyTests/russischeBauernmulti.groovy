import de.prob.animator.domainobjects.*
import de.prob.model.eventb.ModelModifier
import de.prob.model.eventb.algorithm.AlgorithmTranslator
import de.prob.model.eventb.algorithm.graph.GraphMerge
import de.prob.model.eventb.algorithm.graph.OptimizedGenerationAlgorithm
import de.prob.model.eventb.translate.*
import de.prob.statespace.*


mm = new ModelModifier().make {
	

	
	machine(name: "multiplication") {
		variables "m","n"
		var_block name: "l", invariant: "l : NAT", init: "l,m :| l' : NAT1 & l'=m'"
		var_block name: "r", invariant: "r : NAT", init: "r,n :| r' : NAT1 & r'=n'"
		var_block name: "product", invariant: "product : NAT", init: "product := 0"
		
		invariants "m : NAT", "n : NAT"
		
		algorithm {
			While("l > 0", invariant: "product + (l*r) = m*n") {
				If("l mod 2 /= 0") {
					Then {
						Assume("l / 2 * 2 = l - 1")
						Assign("l := l / 2", "r := r * 2", "product := product + r")
					}
					Else("l := l / 2", "r := r * 2")
				}
			}
			Assert("product = m * n")
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