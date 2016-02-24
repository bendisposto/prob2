import de.prob.animator.domainobjects.*
import de.prob.model.eventb.ModelModifier
import de.prob.model.eventb.algorithm.AlgorithmGenerationOptions
import de.prob.model.eventb.algorithm.AlgorithmTranslator
import de.prob.model.eventb.translate.*
import de.prob.statespace.*


mm = new ModelModifier().make {
	
	machine(name: "multiplication") {
		variables "x", "x0"
		var "y", "y : NAT1", "y :: NAT1"
		var "res0", "res0 : NAT", "res0 := 0"
		
		invariants "x : NAT", "x0 : NAT", "x0 <= x"
		
		initialisation {
			then "x,x0 :| x' : NAT1 & x' = x0'"
		}
		
		algorithm {
			While("x0 > 0", variant: "x0", invariant: "res0=(x-x0)*y") {
				Assign("x0 := x0 - 1")
				Assign("res0 := res0 + y")
			}
			Assert("res0 = x*y")
		}
	}
}

m = mm.getModel()
m = new AlgorithmTranslator(m, new AlgorithmGenerationOptions().DEFAULT).run()

//mtx = new ModelToXML()
//d = mtx.writeToRodin(m, "Multiplication", "/tmp")
//d.deleteDir()

"generating a model from an algorithm"