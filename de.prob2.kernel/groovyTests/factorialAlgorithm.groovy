import de.prob.animator.domainobjects.*
import de.prob.model.eventb.ModelModifier
import de.prob.model.eventb.algorithm.AlgorithmGenerationOptions
import de.prob.model.eventb.algorithm.AlgorithmTranslator
import de.prob.model.eventb.translate.*
import de.prob.statespace.*


mm = new ModelModifier().make {
	
	context(name: "definitions") {
		constant "fac"
		axioms "fac : NAT --> NAT",
		       "fac(0) = 0",
			   "!m,n.(m > 0 & n > 0 => fac(m) = fac(m - 1)* m)"
	}
	
	context(name: "fac_ctx", extends: "definitions") {
		constants "n", "factorial"
		axioms "n : NAT & factorial : NAT",
		       "n >= 0",
			   "factorial = fac(n)"
		
	}
	
	machine(name: "fac", sees: ["fac_ctx"]) {
		var "v", "v : NAT", "v := 1"
		var "r", "r : NAT", "r := 0"
		var "s", "s : NAT", "s := 0"
		var "u", "u : NAT", "u := 1"
		
		algorithm {
			While("r < n") {
				While("s < r") {
					Assign("u,s := u+v, s+1")
				}
				Assign("u,r,s := u,r+1,0")
			}
		}
	}
}

m = mm.getModel()
m = new AlgorithmTranslator(m, new AlgorithmGenerationOptions().DEFAULT).run()

mtx = new ModelToXML()
d = mtx.writeToRodin(m, "Factorial", "/tmp")
//d.deleteDir()

//s.animator.cli.shutdown();
"generate and animate a model"