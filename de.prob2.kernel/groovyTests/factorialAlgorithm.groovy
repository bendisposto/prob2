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
	
	procedure(name: "fac", seen: "definitions") {
		argument "n", "NAT"
		result "factorial", "NAT"
		
		precondition "n >= 0"
		postcondition "factorial = fac(n)"
		
		implementation {
			var "v", "v : NAT", "v := 1"
			var "r", "r : NAT", "r := 0"
			var "s", "s : NAT", "s := 0"
			var "u", "u : NAT", "u := 1"
			
			variant "(n - r) * (n + 1) + (r - s)"
			
			algorithm {
				Assert("u = (s+1) * v")
				While("r < n", invariant: "v = fac(r)") {
					Assert("r < n & s <= r")
					While("s < r", invariant: "u = (s + 1) ∗ v") {
						Assert("v = fac(r)")
						Assert("s <= r")
						Assert("r < n")
						Assign("u,s := u+v, s+1")
					}
					Assert("r < n")
					Assign("v,r,s := u,r+1,0")
				}
				Assert("r = n")
				Return("v")
			}
		}
	}
}

m = mm.getModel()
m = new AlgorithmTranslator(m, new AlgorithmGenerationOptions().terminationAnalysis(true).DEFAULT).run()

mtx = new ModelToXML()
//d = mtx.writeToRodin(m, "FactorialProc", "/tmp")
//d.deleteDir()

//s.animator.cli.shutdown();
"generate and animate a model"