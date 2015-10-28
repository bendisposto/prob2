import de.prob.animator.domainobjects.*
import de.prob.model.eventb.ModelModifier
import de.prob.model.eventb.algorithm.AlgorithmGenerationOptions
import de.prob.model.eventb.algorithm.AlgorithmTranslator
import de.prob.model.eventb.translate.*
import de.prob.statespace.*


mm = new ModelModifier().make {
	
	procedure(name: "mult") {
		argument "x", "NAT", "x :: NAT1"
		argument "y", "NAT", "y :: NAT1"
		result "product", "NAT", "product := 0"
		
		abstraction pre: "x >= 0 & y >= 0", 
			post: "product := x * y"
			
		algorithm([x0: "x", "y0": "y", "p": "product"]) {
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
	}
	
	machine(name: "apply_mult") {
		var "product", "product : NAT", "product := 0"
		var "ctr", "ctr : NAT", "ctr := 0"
		var "s", "s : NAT +-> INT", "s := {}"
		
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
m = new AlgorithmTranslator(m, new AlgorithmGenerationOptions().DEFAULT).run()

//mtx = new ModelToXML()
//d = mtx.writeToRodin(m, "MultWithProcedures", "/tmp")
//d.deleteDir()

//s.animator.cli.shutdown();
"generate and animate a model"