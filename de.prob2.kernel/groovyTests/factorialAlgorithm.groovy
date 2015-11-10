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
		       "fac(0) = 1",
			   "!m.(m > 0 & m : dom(fac) & m - 1 : dom(fac) => fac(m) = fac(m - 1) * m)"
		theorem "dom(fac) = NAT"
		theorem "!m.(m : dom(fac) & m + 1 : dom(fac) => fac(m + 1) = fac(m) * (m + 1))"
		theorem "fac(0) = fac(1)"
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
			Assert("u = v")
			Assert("u = (s+1) * v")
			While("r < n", invariant: "r : dom(fac) & v = fac(r)") {
				Assert("v = fac(r)")
				Assert("r < n")
				While("s < r", invariant: "u = (s + 1) ∗ v") {
					Assign("u,s := u+v, s+1")
				}
				Assert("r : dom(fac) => u = fac(r + 1)")
				Assert("r < n")
				Assign("v,r,s := u,r+1,0")	
			}
			Assert("v = factorial")
		}
	}
}

m = mm.getModel()
m = new AlgorithmTranslator(m, new AlgorithmGenerationOptions().DEFAULT.propagateAssertions(true)).run()

mtx = new ModelToXML()
d = mtx.writeToRodin(m, "Factorial", "/tmp")
//d.deleteDir()

//s.animator.cli.shutdown();
"generate and animate a model"